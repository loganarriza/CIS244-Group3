import java.util.*;

public class HandEvaluator {

	public static HandResult evaluateHandDetailed(List<HeldHand.Card> cards) {
		Map<Integer, Integer> rankCounts = new HashMap<>();
		Map<String, Integer> suitCounts = new HashMap<>();
		List<Integer> ranks = new ArrayList<>();

		for (HeldHand.Card card : cards) {
			int rank = card.getRank();
			String suit = card.getSuit();
			rankCounts.put(rank, rankCounts.getOrDefault(rank, 0) + 1);
			suitCounts.put(suit, suitCounts.getOrDefault(suit, 0) + 1);
			ranks.add(rank);
		}

		int rankBonus = 0;
		Collections.sort(ranks);
		boolean isFlush = suitCounts.containsValue(5);
		boolean isStraight = isSequential(ranks);
		boolean isRoyal = ranks.equals(Arrays.asList(1, 10, 11, 12, 13));

		if (isFlush && isRoyal) {
			rankBonus = cards.stream()
				.filter(c -> Arrays.asList(1, 10, 11, 12, 13).contains(c.getRank()))
				.mapToInt(HeldHand.Card::getScore)
				.sum();
			return new HandResult("Royal Flush", 100, 8, rankBonus);
		}

		if (isFlush && isStraight) {
			rankBonus = cards.stream()
				.mapToInt(HeldHand.Card::getScore)
				.sum();
			return new HandResult("Straight Flush", 100, 8, rankBonus);
		}

		for (Map.Entry<Integer, Integer> entry : rankCounts.entrySet()) {
			if (entry.getValue() == 4) {
				int rank = entry.getKey();
				rankBonus = cards.stream()
					.filter(c -> c.getRank() == rank)
					.mapToInt(HeldHand.Card::getScore)
					.sum();
				return new HandResult("Four of a Kind", 60, 7, rankBonus);
			}
		}

		if (rankCounts.containsValue(3) && rankCounts.containsValue(2)) {
			int bonus = cards.stream()
				.filter(c -> rankCounts.get(c.getRank()) == 3 || rankCounts.get(c.getRank()) == 2)
				.mapToInt(HeldHand.Card::getScore)
				.sum();
			return new HandResult("Full House", 40, 4, bonus);
		}

		if (isFlush) {
			String flushSuit = suitCounts.entrySet().stream()
				.filter(e -> e.getValue() >= 5)
				.map(Map.Entry::getKey)
				.findFirst().orElse("Unknown");

			rankBonus = cards.stream()
				.filter(c -> c.getSuit().equals(flushSuit))
				.mapToInt(HeldHand.Card::getScore)
				.sum();
			return new HandResult("Flush", 40, 4, rankBonus);
		}

		if (isStraight) {
			rankBonus = cards.stream()
				.mapToInt(HeldHand.Card::getScore)
				.sum();
			return new HandResult("Straight", 30, 4, rankBonus);
		}
		
		if (isRoyal && !isFlush) {
			rankBonus = cards.stream().mapToInt(HeldHand.Card::getScore).sum();
			return new HandResult("Straight", 30, 4, rankBonus);
		}

		for (Map.Entry<Integer, Integer> entry : rankCounts.entrySet()) {
			if (entry.getValue() == 3) {
				int rank = entry.getKey();
				rankBonus = cards.stream()
					.filter(c -> c.getRank() == rank)
					.mapToInt(HeldHand.Card::getScore)
					.sum();
				return new HandResult("Three of a Kind", 30, 3, rankBonus);
			}
		}

		long pairCount = rankCounts.values().stream().filter(count -> count == 2).count();
		if (pairCount >= 2) {
			rankBonus = cards.stream()
				.filter(c -> rankCounts.get(c.getRank()) == 2)
				.mapToInt(HeldHand.Card::getScore)
				.sum();
			return new HandResult("Two Pair", 20, 2, rankBonus);
		}

		if (pairCount == 1) {
			rankBonus = cards.stream()
				.filter(c -> rankCounts.get(c.getRank()) == 2)
				.mapToInt(HeldHand.Card::getScore)
				.sum();
			return new HandResult("One Pair", 5, 1, rankBonus);
		}

		if (pairCount == 0) {
			rankBonus = cards.stream()
				.mapToInt(HeldHand.Card::getScore)
				.max()
				.orElse(0);
			return new HandResult("High Card", 10, 1, rankBonus);
		}

		return new HandResult("Unknown", 0, 1, 0); // fallback
	}

	public static boolean isSequential(List<Integer> ranks) {
		Set<Integer> uniqueRanks = new HashSet<>(ranks);
		if (uniqueRanks.size() != 5) return false;

		Collections.sort(ranks);

		// handle Ace-low straight
		if (ranks.equals(Arrays.asList(1, 2, 3, 4, 5))) return true;

		for (int i = 0; i < ranks.size() - 1; i++) {
			if (ranks.get(i + 1) - ranks.get(i) != 1) return false;
		}
		return true;
	}

	public static class HandResult {
		public final String name;
		public final int chips;
		public final int multiplier;
		public final int rankBonus;
		public final int totalScore;

		public HandResult(String name, int chips, int multiplier, int rankBonus) {
			this.name = name;
			this.chips = chips;
			this.multiplier = multiplier;
			this.rankBonus = rankBonus;
			this.totalScore = (chips + rankBonus) * multiplier;
		}

		@Override
		public String toString() {
			return name + " (" + chips + " + " + rankBonus + ") × " + multiplier + " = " + totalScore;
		}
	}
}
