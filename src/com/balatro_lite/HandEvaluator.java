package com.balatro_lite;
import java.util.*;
import java.util.stream.Collectors;

// Utility class for evaluating 5-card poker hands
public class HandEvaluator {

    public static HandResult evaluateHandDetailed(List<HeldHand.Card> cards) {
    	Map<Integer, Integer> rankCounts = new HashMap<>();
        Map<String, Integer> suitCounts = new HashMap<>();
        List<Integer> ranks = new ArrayList<>();

        // Ignore disabled cards
        List<HeldHand.Card> usableCards = cards;

        System.out.println("🧠 Evaluating cards:");
        for (HeldHand.Card card : usableCards) {
            System.out.println(" - " + card);
        }
        
        for (HeldHand.Card card : usableCards) {
        	
        	int rank = card.getRank();
        		
            String suit = card.getSuit();
            rankCounts.put(rank, rankCounts.getOrDefault(rank, 0) + 1);
            suitCounts.put(suit, suitCounts.getOrDefault(suit, 0) + 1);
            
            if (card.isDisabled()) {
        		System.out.print("YOU ARE NOW HERE RANK THING");
        		rank = 0;
        	}
            
            ranks.add(rank);
        }

        int rankBonus = 0;
        Collections.sort(ranks);
        boolean isFlush = suitCounts.containsValue(5);
        boolean isStraight = isSequential(ranks);
        boolean isRoyal = ranks.equals(Arrays.asList(1, 10, 11, 12, 13));

        // === Hand Evaluation ===

        if (isFlush && isRoyal) {
            rankBonus = usableCards.stream()
                .filter(c -> Arrays.asList(1, 10, 11, 12, 13).contains(c.getRank()))
                .mapToInt(HeldHand.Card::getScore)
                .sum();
            return new HandResult("Royal Flush", 100, 8, rankBonus);
        }

        if (isFlush && isStraight) {
            rankBonus = usableCards.stream()
                .mapToInt(HeldHand.Card::getScore)
                .sum();
            return new HandResult("Straight Flush", 100, 8, rankBonus);
        }

        for (Map.Entry<Integer, Integer> entry : rankCounts.entrySet()) {
            if (entry.getValue() == 4) {
                int rank = entry.getKey();
                rankBonus = usableCards.stream()
                    .filter(c -> c.getRank() == rank)
                    .mapToInt(HeldHand.Card::getScore)
                    .sum();
                return new HandResult("Four of a Kind", 60, 7, rankBonus);
            }
        }

        if (rankCounts.containsValue(3) && rankCounts.containsValue(2)) {
            int tripleRank = rankCounts.entrySet().stream()
                .filter(e -> e.getValue() == 3)
                .map(Map.Entry::getKey)
                .findFirst().orElse(0);

            int pairRank = rankCounts.entrySet().stream()
                .filter(e -> e.getValue() == 2)
                .map(Map.Entry::getKey)
                .findFirst().orElse(0);

            rankBonus = usableCards.stream()
                .filter(c -> c.getRank() == tripleRank || c.getRank() == pairRank)
                .mapToInt(HeldHand.Card::getScore)
                .sum();

            return new HandResult("Full House", 40, 4, rankBonus);
        }

        if (isFlush) {
            String flushSuit = suitCounts.entrySet().stream()
                .filter(e -> e.getValue() >= 5)
                .map(Map.Entry::getKey)
                .findFirst().orElse("Unknown");

            rankBonus = usableCards.stream()
                .filter(c -> c.getSuit().equals(flushSuit))
                .mapToInt(HeldHand.Card::getScore)
                .sum();
            return new HandResult("Flush", 40, 4, rankBonus);
        }

        if (isStraight) {
            rankBonus = usableCards.stream()
                .mapToInt(HeldHand.Card::getScore)
                .sum();
            return new HandResult("Straight", 30, 4, rankBonus);
        }

        if (isRoyal && !isFlush) {
            rankBonus = usableCards.stream().mapToInt(HeldHand.Card::getScore).sum();
            return new HandResult("Straight", 30, 4, rankBonus);
        }

        for (Map.Entry<Integer, Integer> entry : rankCounts.entrySet()) {
            if (entry.getValue() == 3) {
                int rank = entry.getKey();
                rankBonus = usableCards.stream()
                    .filter(c -> c.getRank() == rank)
                    .mapToInt(HeldHand.Card::getScore)
                    .sum();
                return new HandResult("Three of a Kind", 30, 3, rankBonus);
            }
        }

        long pairCount = rankCounts.values().stream().filter(count -> count == 2).count();

        if (pairCount >= 2) {
            rankBonus = usableCards.stream()
                .filter(c -> rankCounts.get(c.getRank()) == 2)
                .mapToInt(HeldHand.Card::getScore)
                .sum();
            return new HandResult("Two Pair", 20, 2, rankBonus);
        }

        if (pairCount == 1) {
            rankBonus = usableCards.stream()
                .filter(c -> rankCounts.get(c.getRank()) == 2)
                .mapToInt(HeldHand.Card::getScore)
                .sum();
            return new HandResult("One Pair", 10, 2, rankBonus);
        }

        rankBonus = usableCards.stream()
            .mapToInt(HeldHand.Card::getScore)
            .max()
            .orElse(0);
        return new HandResult("High Card", 5, 1, rankBonus);
    }

    public static boolean isSequential(List<Integer> ranks) {
        Set<Integer> uniqueRanks = new HashSet<>(ranks);
        if (uniqueRanks.size() != 5) return false;

        Collections.sort(ranks);

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
