import java.util.*;

public class HandEvaluator {

	public static String evaluateHand(List<HeldHand.Card> cards) {
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

		Collections.sort(ranks);
		boolean isFlush = suitCounts.containsValue(5);
		boolean isStraight = isSequential(ranks);
		boolean isRoyal = ranks.equals(Arrays.asList(1, 10, 11, 12, 13)); // Ace-high

		if (isFlush && isRoyal) return "Royal Flush";
		if (isFlush && isStraight) return "Straight Flush";
		if (rankCounts.containsValue(4)) return "Four of a Kind";
		if (rankCounts.containsValue(3) && rankCounts.containsValue(2)) return "Full House";
		if (isFlush) return "Flush";
		if (isStraight) return "Straight";
		if (rankCounts.containsValue(3)) return "Three of a Kind";

		long pairCount = rankCounts.values().stream().filter(count -> count == 2).count();
		if (pairCount == 2) return "Two Pair";
		if (pairCount == 1) return "One Pair";

		return "High Card";
	}
	
	public static int getHandScore(List<HeldHand.Card> cards) {
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

		Collections.sort(ranks);
		boolean isFlush = suitCounts.containsValue(5);
		boolean isStraight = isSequential(ranks);
		boolean isRoyal = ranks.equals(Arrays.asList(1, 10, 11, 12, 13));

		if (isFlush && isRoyal) return 900;              // Royal Flush
		if (isFlush && isStraight) return 800;           // Straight Flush
		if (rankCounts.containsValue(4)) return 700;     // Four of a Kind
		if (rankCounts.containsValue(3) && rankCounts.containsValue(2)) return 600; // Full House
		if (isFlush) return 500;                         // Flush
		if (isStraight) return 400;                      // Straight
		if (rankCounts.containsValue(3)) return 300;     // Three of a Kind

		long pairCount = rankCounts.values().stream().filter(count -> count == 2).count();
		if (pairCount == 2) return 200;                  // Two Pair
		if (pairCount == 1) return 100;                  // One Pair

		// High Card base value = highest card rank
		return Collections.max(ranks);                   // High Card value
	}


	public static boolean isSequential(List<Integer> ranks) {
		Set<Integer> uniqueRanks = new HashSet<>(ranks);
		if (uniqueRanks.size() != 5) return false;

		Collections.sort(ranks);
		// handle Ace as low
		if (ranks.equals(Arrays.asList(1, 2, 3, 4, 5))) return true;

		for (int i = 0; i < ranks.size() - 1; i++) {
			if (ranks.get(i + 1) - ranks.get(i) != 1) {
				return false;
			}
		}
		return true;
	}
}

