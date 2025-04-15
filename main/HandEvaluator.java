import java.util.*;

// Utility class for evaluating 5-card poker hands
public class HandEvaluator {

	// Evaluates a hand and returns a detailed HandResult
	public static HandResult evaluateHandDetailed(List<HeldHand.Card> cards) {
		Map<Integer, Integer> rankCounts = new HashMap<>();   // Rank → frequency (e.g., 10 → 3)
		Map<String, Integer> suitCounts = new HashMap<>();    // Suit → frequency (e.g., "Hearts" → 5)
		List<Integer> ranks = new ArrayList<>();              // All ranks in the hand

		// Count occurrences of each rank and suit
		for (HeldHand.Card card : cards) {
			int rank = card.getRank();
			String suit = card.getSuit();
			rankCounts.put(rank, rankCounts.getOrDefault(rank, 0) + 1);
			suitCounts.put(suit, suitCounts.getOrDefault(suit, 0) + 1);
			ranks.add(rank);
		}

		int rankBonus = 0;
		Collections.sort(ranks);                        // Sort for straight detection
		boolean isFlush = suitCounts.containsValue(5);  // All cards same suit?
		boolean isStraight = isSequential(ranks);       // Ranks form a sequence?
		boolean isRoyal = ranks.equals(Arrays.asList(1, 10, 11, 12, 13));  // A,10,J,Q,K?

		// === Evaluate Hand Type ===

		// Royal Flush
		if (isFlush && isRoyal) {
			rankBonus = cards.stream()
				.filter(c -> Arrays.asList(1, 10, 11, 12, 13).contains(c.getRank()))
				.mapToInt(HeldHand.Card::getScore)
				.sum();
			return new HandResult("Royal Flush", 100, 8, rankBonus);
		}

		// Straight Flush
		if (isFlush && isStraight) {
			rankBonus = cards.stream()
				.mapToInt(HeldHand.Card::getScore)
				.sum();
			return new HandResult("Straight Flush", 100, 8, rankBonus);
		}

		// Four of a Kind
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

		// Full House (3 of a kind + a pair)
		if (rankCounts.containsValue(3) && rankCounts.containsValue(2)) {
			int bonus = cards.stream()
				.filter(c -> rankCounts.get(c.getRank()) == 3 || rankCounts.get(c.getRank()) == 2)
				.mapToInt(HeldHand.Card::getScore)
				.sum();
			return new HandResult("Full House", 40, 4, bonus);
		}

		// Flush
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

		// Straight
		if (isStraight) {
			rankBonus = cards.stream()
				.mapToInt(HeldHand.Card::getScore)
				.sum();
			return new HandResult("Straight", 30, 4, rankBonus);
		}
		
		// Special case: Royal but not a flush → treat as regular straight
		if (isRoyal && !isFlush) {
			rankBonus = cards.stream().mapToInt(HeldHand.Card::getScore).sum();
			return new HandResult("Straight", 30, 4, rankBonus);
		}

		// Three of a Kind
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

		// Two Pair
		long pairCount = rankCounts.values().stream().filter(count -> count == 2).count();
		if (pairCount >= 2) {
			rankBonus = cards.stream()
				.filter(c -> rankCounts.get(c.getRank()) == 2)
				.mapToInt(HeldHand.Card::getScore)
				.sum();
			return new HandResult("Two Pair", 20, 2, rankBonus);
		}

		// One Pair
		if (pairCount == 1) {
			rankBonus = cards.stream()
				.filter(c -> rankCounts.get(c.getRank()) == 2)
				.mapToInt(HeldHand.Card::getScore)
				.sum();
			return new HandResult("One Pair", 5, 1, rankBonus);
		}

		// High Card (no matches)
		if (pairCount == 0) {
			rankBonus = cards.stream()
				.mapToInt(HeldHand.Card::getScore)
				.max()
				.orElse(0);
			return new HandResult("High Card", 10, 1, rankBonus);
		}

		// Should not reach here, fallback
		return new HandResult("Unknown", 0, 1, 0);
	}

	// Helper to check if ranks are sequential (i.e., a straight)
	public static boolean isSequential(List<Integer> ranks) {
		Set<Integer> uniqueRanks = new HashSet<>(ranks); // Avoid duplicates
		if (uniqueRanks.size() != 5) return false;

		Collections.sort(ranks);

		// Special case: Ace-low straight (A,2,3,4,5)
		if (ranks.equals(Arrays.asList(1, 2, 3, 4, 5))) return true;

		// Check if difference between ranks is always 1
		for (int i = 0; i < ranks.size() - 1; i++) {
			if (ranks.get(i + 1) - ranks.get(i) != 1) return false;
		}
		return true;
	}

	// Class to hold detailed result of hand evaluation
	public static class HandResult {
		public final String name;         // Hand type (e.g., "Flush")
		public final int chips;          // Base point value
		public final int multiplier;     // Multiplier for score
		public final int rankBonus;      // Sum of rank values involved
		public final int totalScore;     // Final score = (chips + bonus) * multiplier

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
