package com.balatro_lite;
import java.util.*;

public class TestHand {

    public static void main(String[] args) {
        System.out.println("Running Hand Evaluation Tests...\n");

        testRoyalStraight();
        testLowAceStraight();
        testInvalidStraight();
        testDuplicateRanks();
        testFullHouse();
        testFourOfAKind();
        testFlush();
        testThreeOfAKind();
        testTwoPair();
        testOnePair();
        testHighCard();

        System.out.println("\n✅ All tests completed.");
    }

    private static void testRoyalStraight() {
        System.out.println("Test: Royal Straight (A, 10, J, Q, K)");
        List<HeldHand.Card> cards = List.of(
            newCard(4, "Spades"),
            newCard(10, "Spades"),
            newCard(11, "Spades"),
            newCard(12, "Spades"),
            newCard(13, "Spades")
        );
        var result = HandEvaluator.evaluateHandDetailed(cards);
        assertResult(result.name, "Royal Flush");
    }

    private static void testLowAceStraight() {
        System.out.println("Test: Low Ace Straight (A, 2, 3, 4, 5)");
        List<HeldHand.Card> cards = List.of(
            newCard(1, "Hearts"),
            newCard(2, "Spades"),
            newCard(3, "Hearts"),
            newCard(4, "Clubs"),
            newCard(5, "Diamonds")
        );
        var result = HandEvaluator.evaluateHandDetailed(cards);
        assertResult(result.name, "Straight");
    }

    private static void testInvalidStraight() {
        System.out.println("Test: Invalid Straight (3, 4, 5, 6, Queen)");
        List<HeldHand.Card> cards = List.of(
            newCard(3, "Hearts"),
            newCard(4, "Hearts"),
            newCard(5, "Clubs"),
            newCard(6, "Diamonds"),
            newCard(12, "Spades")
        );
        var result = HandEvaluator.evaluateHandDetailed(cards);
        assertNotEqual(result.name, "Straight");
    }

    private static void testDuplicateRanks() {
        System.out.println("Test: Duplicate Ranks (1, 2, 2, 3, 4)");
        List<HeldHand.Card> cards = List.of(
            newCard(1, "Spades"),
            newCard(2, "Clubs"),
            newCard(2, "Diamonds"),
            newCard(3, "Hearts"),
            newCard(4, "Spades")
        );
        var result = HandEvaluator.evaluateHandDetailed(cards);
        assertNotEqual(result.name, "Straight");
    }

    private static void testFullHouse() {
        System.out.println("Test: Full House (7, 7, 7, 9, 9)");
        List<HeldHand.Card> cards = List.of(
            newCard(7, "Clubs"),
            newCard(7, "Hearts"),
            newCard(7, "Spades"),
            newCard(9, "Hearts"),
            newCard(9, "Clubs")
        );
        var result = HandEvaluator.evaluateHandDetailed(cards);
        assertResult(result.name, "Full House");
    }

    private static void testFourOfAKind() {
        System.out.println("Test: Four of a Kind (4x King)");
        List<HeldHand.Card> cards = List.of(
            newCard(12, "Spades"),
            newCard(13, "Clubs"),
            newCard(13, "Hearts"),
            newCard(13, "Diamonds"),
            newCard(7, "Spades")
        );
        var result = HandEvaluator.evaluateHandDetailed(cards);
        assertResult(result.name, "Four of a Kind");
    }

    private static void testFlush() {
        System.out.println("Test: Flush (all Hearts)");
        List<HeldHand.Card> cards = List.of(
            newCard(2, "Hearts"),
            newCard(5, "Hearts"),
            newCard(9, "Hearts"),
            newCard(11, "Hearts"),
            newCard(13, "Hearts")
        );
        var result = HandEvaluator.evaluateHandDetailed(cards);
        assertResult(result.name, "Flush");
    }

    private static void testThreeOfAKind() {
        System.out.println("Test: Three of a Kind (3x 8)");
        List<HeldHand.Card> cards = List.of(
            newCard(8, "Hearts"),
            newCard(8, "Clubs"),
            newCard(7, "Spades"),
            newCard(5, "Diamonds"),
            newCard(2, "Hearts")
        );
        var result = HandEvaluator.evaluateHandDetailed(cards);
        assertResult(result.name, "Three of a Kind");
    }

    private static void testTwoPair() {
        System.out.println("Test: Two Pair (6, 6, 9, 9)");
        List<HeldHand.Card> cards = List.of(
            newCard(6, "Hearts"),
            newCard(6, "Spades"),
            newCard(9, "Hearts"),
            newCard(9, "Clubs"),
            newCard(2, "Diamonds")
        );
        var result = HandEvaluator.evaluateHandDetailed(cards);
        assertResult(result.name, "Two Pair");
    }

    private static void testOnePair() {
        System.out.println("Test: One Pair (2x 5)");
        List<HeldHand.Card> cards = List.of(
            newCard(5, "Hearts"),
            newCard(5, "Clubs"),
            newCard(2, "Diamonds"),
            newCard(3, "Spades"),
            newCard(9, "Hearts")
        );
        var result = HandEvaluator.evaluateHandDetailed(cards);
        assertResult(result.name, "One Pair");
    }

    private static void testHighCard() {
        System.out.println("Test: High Card");
        List<HeldHand.Card> cards = List.of(
            newCard(2, "Spades"),
            newCard(5, "Hearts"),
            newCard(7, "Diamonds"),
            newCard(9, "Clubs"),
            newCard(11, "Hearts")
        );
        var result = HandEvaluator.evaluateHandDetailed(cards);
        assertResult(result.name, "High Card");
    }

    // === UTILITIES ===

    private static HeldHand.Card newCard(int rank, String suit) {
        return new HeldHand.Card(cardIdFromRankSuit(rank, suit));
    }

    private static int cardIdFromRankSuit(int rank, String suit) {
        int suitOffset = switch (suit) {
            case "Spades" -> 0;
            case "Hearts" -> 13;
            case "Diamonds" -> 26;
            case "Clubs" -> 39;
            default -> throw new IllegalArgumentException("Unknown suit: " + suit);
        };
        return suitOffset + (rank == 1 ? 1 : rank);
    }

    private static void assertResult(String actual, String expected) {
        if (!actual.equals(expected)) {
            System.err.println("❌ FAILED: Expected [" + expected + "] but got [" + actual + "]");
        } else {
            System.out.println("✅ PASS: " + expected);
        }
    }

    private static void assertNotEqual(String actual, String incorrect) {
        if (actual.equals(incorrect)) {
            System.err.println("❌ FAILED: Got incorrect match [" + incorrect + "]");
        } else {
            System.out.println("✅ PASS: Correctly did NOT match [" + incorrect + "]");
        }
    }
}
