package com.balatro_lite;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.balatro_lite.HandEvaluator.HandResult;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainViewController {

	private int totalScore = 0;
	private int discardsRemaining = 4;
	private int handsRemaining = 4;
	private int blindScore = 300;
	private int cashOnHand = 5;
	private int handSize = 8;
	private int roundNumber = 0;
	private int currentAnte = 1;
	private int roundScore = 0; // 💥 tracks score within current round only
	private final int MAX_JOKERS = 3;
	private int nextJokerSlot = 1;
	private Integer jokerSlot1Id = null;
	private Integer jokerSlot2Id = null;
	private Integer jokerSlot3Id = null;
	private int bossBlindsDefeated = 0;
	private final int MAX_BOSS_BLINDS = 4;
	private boolean enforceFiveCardPlays = false;

	private String selectedBossName; // e.g., "TheHead", "TheWindow", etc.

	// === Hand Cards ===
	@FXML
	private ImageView imgHandCard1;
	@FXML
	private ImageView imgHandCard2;
	@FXML
	private ImageView imgHandCard3;
	@FXML
	private ImageView imgHandCard4;
	@FXML
	private ImageView imgHandCard5;
	@FXML
	private ImageView imgHandCard6;
	@FXML
	private ImageView imgHandCard7;
	@FXML
	private ImageView imgHandCard8;

	// === Main UI Elements ===
	@FXML
	private ImageView imgMainBackground;
	@FXML
	private ImageView imgJokerThoughtBubble;
	@FXML
	private ImageView imgSortHandPane;
	@FXML
	private ImageView imgDiscard;
	@FXML
	private ImageView imgPlayHand;
	@FXML
	private ImageView imgShopSign;

	// === Jokers ===
	@FXML
	private ImageView imgJoker1;
	@FXML
	private ImageView imgJoker2;
	@FXML
	private ImageView imgJoker3;

	// === Shop Cards ===
	@FXML
	private ImageView imgShopPane;
	@FXML
	private ImageView imgShopCard1;
	@FXML
	private ImageView imgShopCard2;
	@FXML
	private ImageView imgShopCard3;
	@FXML
	private ImageView imgShopCard4;
	@FXML
	private ImageView imgShopCard5;
	@FXML
	private ImageView imgShopCard6;

	// === Blind States ===
	@FXML
	private ImageView imgSmallBlindCurrent;
	@FXML
	private ImageView imgSmallBlindGameScore;
	@FXML
	private ImageView imgSmallBlindDefeated;
	@FXML
	private ImageView imgBigBlindCurrent;
	@FXML
	private ImageView imgBigBlindGameScore;
	@FXML
	private ImageView imgBigBlindDefeated;
	@FXML
	private ImageView imgBigBlindUpcoming;
	@FXML
	private ImageView imgBossBlindGameScore;
	@FXML
	private ImageView selectedBossBlind;

	// === Boss Enemies (Upcoming & Current) ===
	@FXML
	private ImageView imgTheClubCurrent;
	@FXML
	private ImageView imgTheClubUpcoming;
	@FXML
	private ImageView imgTheGoadCurrent;
	@FXML
	private ImageView imgTheGoadUpcoming;
	@FXML
	private ImageView imgTheHeadCurrent;
	@FXML
	private ImageView imgTheHeadUpcoming;
	@FXML
	private ImageView imgTheManacleCurrent;
	@FXML
	private ImageView imgTheManacleUpcoming;
	@FXML
	private ImageView imgThePsychicCurrent;
	@FXML
	private ImageView imgThePsychicUpcoming;
	@FXML
	private ImageView imgTheWindowCurrent;
	@FXML
	private ImageView imgTheWindowUpcoming;

	// === Labels: Blind Scores ===
	@FXML
	private Label lblSmallBlindScore;
	@FXML
	private Label lblBigBlindScore;
	@FXML
	private Label lblBossBlindScore;
	@FXML
	private Label lblBlindHealth;

	// === Labels: Score + Multiplier ===
	@FXML
	private Label lblChips;
	@FXML
	private Label lblMult;
	@FXML
	private Label lblRoundScore;
	@FXML
	private Label lblHandType;

	// === Labels: Game State & Progress ===
	@FXML
	private Label lblHandsCounter;
	@FXML
	private Label lblDiscardsCounter;
	@FXML
	private Label lblRound;
	@FXML
	private Label lblCurrentDeckCounter;
	@FXML
	private Label lblRunInfo;

	// === Labels: Economy & Ante Info ===
	@FXML
	private Label lblCashOnHand;
	@FXML
	private Label lblCurrentAnte;
	@FXML
	private Label lblAnteCap;

	// === Labels: Jokers ===
	@FXML
	private Label lblJokerSpeech;
	@FXML
	private Label lblJokersInHand;

	// === Layout Containers ===
	@FXML
	private Pane paneContainer;
	@FXML
	private HBox hboxJokersPane;
	@FXML
	private HBox hboxShopCardDeck;
	@FXML
	private HBox hboxShopCardDeck2;
	@FXML
	private HBox hboxHand;

	// === Buttons ===
	@FXML
	private Button btnMainMenu;
	@FXML
	private Button btnNextRound;
	@FXML
	private Button btnSelectSmallBlind;
	@FXML
	private Button btnSelectBigBlind;
	@FXML
	private Button btnSelectBossBlind;
	@FXML
	private Button btnPlayHand;
	@FXML
	private Button btnDiscard;
	@FXML
	private Button btnSortRank;
	@FXML
	private Button btnSortSuit;
	@FXML
	private Button btnReroll;

	private final List<ImageView> imageViews = new ArrayList<>();
	private final HashSet<ImageView> selectedImages = new HashSet<>();
	private final HashSet<Integer> ownedJokers = new HashSet<>();
	private final Map<Integer, Integer> ownedPlanets = new HashMap<>();
	List<ImageView> toReplace = new ArrayList<>();
	private final List<Integer> currentCardIds = new ArrayList<>();
	private final List<Integer> deck = new ArrayList<>();
	private List<Boss> bossList;
	private Boss selectedBoss;
	private enum SortMode { NONE, RANK, SUIT }
	private SortMode currentSortMode = SortMode.NONE;
	private enum BlindPhase { SMALL, BIG, BOSS }
	private String getCurrentBlindPhase() {
		int phase = roundNumber % 3;
		return switch (phase) {
		case 1 -> "SMALL";
		case 2 -> "BIG";
		case 0 -> "BOSS";
		default -> "SMALL"; // fallback
		};
	}


	private final Map<Integer, String> jokerEffects = Map.of(1, "Loker: +15 mult", 2,
			"Theoker: Gives +5 mult on Two Pairs", 3, "Jimbo: Gives +5 mult on High Card", 4,
			"Peter: Gives +5 mult on Pairs", 5, "Friggin Packet Yoker: Gives +5 mult on Three of a Kind", 6,
			"Nikola Joker: Gives +5 mult on Full Houses", 7, "Sal Vulcanoker?: Gives +5 mult on Flushes", 8,
			"LeBroker: Gives +5 mult on Four of a Kinds", 9, "This guy: Gives +5 mult on Straights", 10,
			"You: Gives +5 mult on Straight Flushes");

	private final Map<Integer, String> planetEffects = Map.of(1,
			"Pluto: Level up High card by 1. +10 Chips and +1 Mult", 2,
			"Mercury: Level up Pair by 1. +15 Chips and +1 Mult", 3,
			"Uranus: Level up Two Pair by 1. +20 Chips and +1 Mult", 4,
			"Venus: Level up Three of a Kind by 1. +20 Chips and +2 Mult", 5,
			"Saturn: Level up Straight by 1. +30 Chips and +3 Mult", 6,
			"Jupiter: Level up Flush by 1. +15 Chips and +2 Mult", 7,
			"Earth: Level up Full House by 1. +25 Chips and +2 Mult", 8,
			"Mars: Level up Four of a Kind by 1. +30 Chips and +3 Mult", 9,
			"Neptune: Level up Straight Flush by 1. +40 Chips and +4 Mult");

	private final String imageDir = "/image/card"; // relative to resources

	public void initialize() {
		imageViews.addAll(List.of(imgHandCard1, imgHandCard2, imgHandCard3, imgHandCard4, imgHandCard5, imgHandCard6,
				imgHandCard7, imgHandCard8));
	}

	private void initDeck() {
		deck.clear();
		for (int i = 1; i <= 52; i++)
			deck.add(i);
		Collections.shuffle(deck);
	}

	private void dealNewHand() {
		if (deck.size() < handSize) {
			System.out.println("❌ Not enough cards to deal a new hand!");
			return;
		}

		currentCardIds.clear();
		selectedImages.clear();

		for (int i = 0; i < imageViews.size(); i++) {
			if (i < handSize) {
				int cardId = deck.remove(0);
				currentCardIds.add(cardId);

				String path = imageDir + "/" + cardId + ".png";
				Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path),
						"❌ Image not found: " + path));
				ImageView view = imageViews.get(i);
				view.setImage(img);
				view.setVisible(true);
				view.setStyle("");
				view.setTranslateY(0);

				final ImageView target = view;
				view.setOnMouseClicked(e -> handleCardSelect(target));
			} else {
				// Hide extra slots
				imageViews.get(i).setImage(null);
				imageViews.get(i).setVisible(false);
			}
		}
	}

	@FXML
	private void handleDiscard() {
		if (discardsRemaining <= 0) {
			System.out.println("⛔ You have no discards left.");
			return;
		}

		if (selectedImages.isEmpty()) {
			System.out.println("❗ No cards selected to discard.");
			return;
		}

		if (deck.isEmpty()) {
			System.out.println("❗ Deck is empty. No cards to draw.");
			return;
		}
		// Replace selected cards with new ones from deck
		for (ImageView img : new ArrayList<>(selectedImages)) {
			int index = imageViews.indexOf(img); // use your tracked 8 cards
			//	        if (index == -1) {
			//	            System.out.println("⚠️ ImageView not found in tracked imageViews!");
			//	            continue;
			//	        }

			if (deck.isEmpty()) {
				System.out.println("🃏 Deck empty, can't draw more cards.");
				break;
			}

			int newCardId = deck.remove(0);
			currentCardIds.set(index, newCardId);

			String path = imageDir + "/" + newCardId + ".png";
			Image newImage = new Image(
					Objects.requireNonNull(getClass().getResourceAsStream(path), "❌ Image not found: " + path));
			img.setImage(newImage);
			img.setStyle("");
			img.setTranslateY(0);
		}

		// Reapply last sort
		switch (currentSortMode) {
		case RANK -> handleSortByRank();
		case SUIT -> handleSortBySuit();
		default -> {}
		}

		selectedImages.clear();
		resetLivePreview();
		lblCurrentDeckCounter.setText(String.valueOf(deck.size()));

		if (discardsRemaining > 0) {
			discardsRemaining--;
			if (lblDiscardsCounter != null) {
				lblDiscardsCounter.setText(String.valueOf(discardsRemaining));
			}
		} else {
			System.out.println("⚠️ No discards remaining! Cannot decrement further.");
		}
		System.out.println("✅ Discard attempt finished. Deck size: " + deck.size());
	}

	@FXML
	private void handleSortByRank() {
		System.out.println("🔃 Sorting hand by RANK...");
		currentSortMode = SortMode.RANK;

		if (currentCardIds.size() != imageViews.size()) {
			System.out.println("❌ Mismatch: " + currentCardIds.size() + " card IDs vs " + imageViews.size());
			return;
		}

		List<Integer> sorted = new ArrayList<>(currentCardIds);
		sorted.sort(Comparator.comparingInt(id -> ((id - 1) % 13) + 1));

		for (int i = 0; i < imageViews.size(); i++) {
			int cardId = sorted.get(i);
			currentCardIds.set(i, cardId);
			String path = imageDir + "/" + cardId + ".png";
			Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path), "❌ Image not found: " + path));
			imageViews.get(i).setImage(img);
			imageViews.get(i).setStyle("");
			imageViews.get(i).setTranslateY(0);
		}

		selectedImages.clear();
	}

	@FXML
	private void handleSortBySuit() {
		System.out.println("🔃 Sorting hand by SUIT...");
		currentSortMode = SortMode.SUIT;

		if (currentCardIds.size() != imageViews.size()) {
			System.out.println("❌ Mismatch: " + currentCardIds.size() + " card IDs vs " + imageViews.size());
			return;
		}

		List<Integer> sorted = new ArrayList<>(currentCardIds);
		sorted.sort(Comparator.comparingInt(id -> (id - 1) / 13));

		for (int i = 0; i < imageViews.size(); i++) {
			int cardId = sorted.get(i);
			currentCardIds.set(i, cardId);
			String path = imageDir + "/" + cardId + ".png";
			Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path), "❌ Image not found: " + path));
			imageViews.get(i).setImage(img);
			imageViews.get(i).setStyle("");
			imageViews.get(i).setTranslateY(0);
		}

		selectedImages.clear();
	}



	@FXML
	private void handlePlayHand() {
		String phase = getCurrentBlindPhase();
		System.out.println("Current blind phase" + phase);

		if (selectedImages.isEmpty()) {
			System.out.println("⚠️ Select at least one card to play.");
			return;
		}

		List<HeldHand.Card> playedCards = new ArrayList<>();

		for (ImageView img : selectedImages) {
			int index = imageViews.indexOf(img);
			int cardId = currentCardIds.get(index);

			System.out.println(index);
			System.out.println(cardId);

			int rank = ((cardId - 1) % 13) + 1;
			String suit = switch ((cardId - 1) / 13) {
			case 0 -> "Spades";
			case 1 -> "Hearts";
			case 2 -> "Diamonds";
			case 3 -> "Clubs";
			default -> "Unknown";
			};
			int score = rank;

			playedCards.add(new HeldHand.Card(rank, suit, score));
		}




		if (enforceFiveCardPlays && selectedImages.size() != 5) {
			System.out.println("🔮 The Psychic's power compels you to play exactly 5 cards.");
			lblJokerSpeech.setText("You must play exactly 5 cards!");
			return;
		}

		if (selectedImages.isEmpty()) {
			System.out.println("⚠️ Select at least one card to play.");
			return;
		}

		List<HeldHand.Card> filteredCards = playedCards.stream()
				.filter(c -> !c.isDisabled())
				.collect(Collectors.toList());

		// 👇 Apply boss debuff effects AFTER populating playedCards
		if (phase.equals("BOSS")) {
			System.out.println("YOU ARE HERE");
			System.out.println("🔥 Boss Blind active against: " + selectedBossName);
			switch (selectedBossName) {
			case "TheClub" -> applyTheClubEffect(filteredCards);
			case "TheGoad" -> applyTheGoadEffect(filteredCards);
			case "TheManacle" -> applyTheManacleEffect(); // already applied via handSize
			case "TheHead" -> applyTheHeadEffect(filteredCards);
			case "TheWindow" -> applyTheWindowEffect(filteredCards); 
			case "ThePsychic" -> applyThePsychicEffect(); // this is a gate check, not a debuff
			}
		}


		System.out.println("🔎 Hand cards after boss effects:");
		filteredCards.forEach(c -> System.out.println(" - " + c));

		// 👇 THIS must go to scoring
		HandEvaluator.HandResult result = HandEvaluator.evaluateHandDetailed(filteredCards);

		// Base from evaluation
		int bonusChips = result.chips;
		int bonusMult = result.multiplier;
		int bonusRank = result.rankBonus;

		// Apply Joker effects (new version)
		for (Integer jokerId : ownedJokers) {
			switch (jokerId) {
			case 1 -> {
				bonusMult += 5;
				System.out.println("🃏 Joker 1 (Loker): +15 mult");
			}
			case 2 -> {
				if ("Two Pair".equals(result.name)) {
					bonusMult += 5;
					System.out.println("🃏 Joker 2: +5 mult for Two Pair");
				}
			}
			case 3 -> {
				if ("High Card".equals(result.name)) {
					bonusMult += 5;
					System.out.println("🃏 Joker 3: +5 mult for High Card");
				}
			}
			case 4 -> {
				if ("Pair".equals(result.name)) {
					bonusMult += 5;
					System.out.println("🃏 Joker 4: +5 mult for Pair");
				}
			}
			case 5 -> {
				if ("Three of a Kind".equals(result.name)) {
					bonusMult += 5;
					System.out.println("🃏 Joker 5: +5 mult for Three of a Kind");
				}
			}
			case 6 -> {
				if ("Full House".equals(result.name)) {
					bonusMult += 5;
					System.out.println("🃏 Joker 6: +5 mult for Full House");
				}
			}
			case 7 -> {
				if ("Flush".equals(result.name)) {
					bonusMult += 5;
					System.out.println("🃏 Joker 7: +5 mult for Flush");
				}
			}
			case 8 -> {
				if ("Four of a Kind".equals(result.name)) {
					bonusMult += 5;
					System.out.println("🃏 Joker 8: +5 mult for Four of a Kind");
				}
			}
			case 9 -> {
				if ("Straight".equals(result.name)) {
					bonusMult += 5;
					System.out.println("🃏 Joker 9: +5 mult for Straight");
				}
			}
			case 10 -> {
				if ("Straight Flush".equals(result.name)) {
					bonusMult += 5;
					System.out.println("🃏 Joker 10: +5 mult for Straight Flush");
				}
			}
			}
		}

		switch (result.name) {
		case "High Card" -> {
			int count = ownedPlanets.getOrDefault(1, 0);
			bonusChips += 10 * count;
			bonusMult += 1 * count;
			if (count > 0)
				System.out.println("🌍 Pluto effect ×" + count);
		}
		case "Pair" -> {
			int count = ownedPlanets.getOrDefault(2, 0);
			bonusChips += 15 * count;
			bonusMult += 1 * count;
			if (count > 0)
				System.out.println("☿ Mercury effect ×" + count);
		}
		case "Two Pair" -> {
			int count = ownedPlanets.getOrDefault(3, 0);
			bonusChips += 20 * count;
			bonusMult += 1 * count;
			if (count > 0)
				System.out.println("🌍 Uranus effect ×" + count);
		}
		case "Three of a Kind" -> {
			int count = ownedPlanets.getOrDefault(4, 0);
			bonusChips += 20 * count;
			bonusMult += 2 * count;
			if (count > 0)
				System.out.println("🌍 Venus effect ×" + count);
		}
		case "Straight" -> {
			int count = ownedPlanets.getOrDefault(5, 0);
			bonusChips += 30 * count;
			bonusMult += 3 * count;
			if (count > 0)
				System.out.println("🌍 Saturn effect ×" + count);
		}
		case "Flush" -> {
			int count = ownedPlanets.getOrDefault(6, 0);
			bonusChips += 15 * count;
			bonusMult += 2 * count;
			if (count > 0)
				System.out.println("🌍 Jupiter effect ×" + count);
		}
		case "Full House" -> {
			int count = ownedPlanets.getOrDefault(7, 0);
			bonusChips += 25 * count;
			bonusMult += 2 * count;
			if (count > 0)
				System.out.println("🌍 Earth effect ×" + count);
		}
		case "Four of a Kind" -> {
			int count = ownedPlanets.getOrDefault(8, 0);
			bonusChips += 30 * count;
			bonusMult += 3 * count;
			if (count > 0)
				System.out.println("🌍 Mars effect ×" + count);
		}
		case "Straight Flush" -> {
			int count = ownedPlanets.getOrDefault(9, 0);
			bonusChips += 40 * count;
			bonusMult += 4 * count;
			if (count > 0)
				System.out.println("🌍 Neptune effect ×" + count);
		}
		}

		int finalScore = (bonusChips + bonusRank) * bonusMult;
		System.out.println(String.valueOf(finalScore));

		bonusChips += bonusChips;
		bonusMult += bonusMult;
		roundScore += finalScore;
		totalScore += finalScore;
		lblRoundScore.setText(String.valueOf(roundScore));
		lblChips.setText(String.valueOf(bonusChips));
		lblMult.setText(String.valueOf(bonusMult));
		lblHandType.setText(result.name);

		for (ImageView img : new ArrayList<>(selectedImages)) {
			int index = imageViews.indexOf(img);
			if (deck.isEmpty()) {
				System.out.println("🃏 Deck empty, can't draw more cards.");
				break;
			}

			int newCardId = deck.remove(0);
			currentCardIds.set(index, newCardId);
			String path = imageDir + "/" + newCardId + ".png";
			Image newImage = new Image(
					Objects.requireNonNull(getClass().getResourceAsStream(path), "❌ Image not found: " + path));
			img.setImage(newImage);
			img.setStyle("");
			img.setTranslateY(0);

		}

		// Reapply last sort
		switch (currentSortMode) {
		case RANK -> handleSortByRank();
		case SUIT -> handleSortBySuit();
		default -> {}
		}

		selectedImages.clear();
		resetLivePreview();
		lblCurrentDeckCounter.setText(String.valueOf(deck.size()));
		System.out.println("✅ Hand played with " + playedCards.size() + " cards: " + result);

		if (handsRemaining > 0) {
			handsRemaining--;
			if (lblHandsCounter != null) {
				lblHandsCounter.setText(String.valueOf(handsRemaining));
			}
		}

		// ✅ Game over check
		if (handsRemaining >= 0) {
			if (totalScore >= blindScore) {
				System.out.println("✅ You beat the blind!");
				endRound(true);
			} else if (handsRemaining < 1) {
				System.out.println("❌ You failed to beat the blind.");
				endRound(false);
			}
		}
	}

	private void applyTheClubEffect(List<HeldHand.Card> cards) {
		System.out.println("♣ The Club is active: Club cards are disabled.");
		for (HeldHand.Card card : cards) {
			if ("Clubs".equals(card.getSuit())) {
				card.disable();
				System.out.println("🃏 Disabled (Club): " + card);
			}
		}
	}

	private void applyTheWindowEffect(List<HeldHand.Card> cards) {
		System.out.println("💎 The Window is active: Diamond cards are disabled.");
		for (HeldHand.Card card : cards) {
			if ("Diamonds".equals(card.getSuit())) {
				card.disable();
				System.out.println("💠 Disabled (Diamond): " + card);
			}
		}
	}

	private void applyTheGoadEffect(List<HeldHand.Card> cards) {
		System.out.println("♠ The Goad is active: Spade cards will be disabled.");
		for (HeldHand.Card card : cards) {
			System.out.println("⏳ Checking: " + card);
			if ("Spades".equals(card.getSuit())) {
				card.disable();
				card.setRank(0);
				card.setScore(0);
				System.out.println("💀 Goad Disabled: " + card);
			}
		}
	}

	private void applyTheHeadEffect(List<HeldHand.Card> cards) {
		System.out.println("🧠 The Head is active: Heart cards are disabled.");
		for (HeldHand.Card card : cards) {
			if ("Hearts".equals(card.getSuit())) {
				System.out.println("YOU ARE HERE");
				card.disable();
				System.out.println("💔 Disabled (Heart): " + card);
			}
		}
	}

	private void applyTheManacleEffect() {
		handSize = 7; // Deal only 7 cards this round
		System.out.println("⛓️ The Manacle effect active: Only 7 cards will be dealt.");
		imgTheManacleCurrent.setVisible(false);
	}

	private void applyThePsychicEffect() {
		enforceFiveCardPlays = true;
	}




	private void returnToHomePage() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/homepage-view.fxml"));
			Parent root = loader.load();

			// Optional: reset state here if needed
			Stage stage = (Stage) paneContainer.getScene().getWindow(); // get current window
			stage.setScene(new Scene(root));
			stage.setTitle("Balatro Lite - Login");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("❌ Failed to load login view.");
		}
	}


	private void endRound(boolean won) {
		btnPlayHand.setDisable(true);
		btnDiscard.setDisable(true);
		btnSortRank.setDisable(true);
		btnSortSuit.setDisable(true);

		if (won) {
			System.out.println("🏆 You win! Proceeding to next scene...");

			int phase = roundNumber % 3;
			int cashReward = 0;

			if (phase == 1) {
				cashReward = 4; // Small Blind
			} else if (phase == 2) {
				cashReward = 6; // Big Blind
			} else if (phase == 0) {
				cashReward = 8; // Boss Blind
				bossBlindsDefeated++;
				System.out.println("🧠 Boss Blinds Defeated: " + bossBlindsDefeated);

				if (bossBlindsDefeated >= MAX_BOSS_BLINDS) {
					showVictoryScreen();
					return;
				}

				currentAnte++;
				lblCurrentAnte.setText(String.valueOf(currentAnte));
				adjustFontSize(lblCurrentAnte);
				resetBossBlind();
			}

			// Add cash and update UI
			cashOnHand += cashReward;
			lblCashOnHand.setText("$" + cashOnHand);
			System.out.println("💵 You earned $" + cashReward + " from this win.");

			showShopScene();
		} else {
			System.out.println("💀 You lose! Game over.");
			returnToHomePage();
		}
	}

	private void showVictoryScreen() {
		System.out.println("🎉 You have defeated all Boss Blinds! Game complete.");
		returnToHomePage();
	}

	private int calculateBlindScore() {
		return 300 + (currentAnte * 150);
	}


	private void buttonsToFront() {
		// Push button arrangements to the front to avoid images blocking them
		btnMainMenu.toFront();
		btnNextRound.toFront();
		btnSelectSmallBlind.toFront();
		btnSelectBigBlind.toFront();
		btnSelectBossBlind.toFront();
		btnPlayHand.toFront();
		btnDiscard.toFront();
		btnSortRank.toFront();
		btnSortSuit.toFront();
		btnReroll.toFront();
	}

	@FXML
	private void handleDealNewHand(ActionEvent event) {
		dealNewHand(); // 👈 triggers your main logic
	}

	@FXML
	private void handleMainMenu() {
		returnToHomePage();
	}

	private void handleCardSelect(ImageView img) {
		if (selectedImages.contains(img)) {
			selectedImages.remove(img);
			img.setStyle("");
			img.setTranslateY(0);
		} else if (selectedImages.size() < 5) {
			selectedImages.add(img);
			img.setStyle("-fx-effect: dropshadow(gaussian, black, 10, 0.5, 0, 0);");
			img.setTranslateY(-20);
		}

		evaluateLiveHand(); // 🔥 call this after selection changes
	}

	private void resetLivePreview() {
		lblHandType.setText("");
		lblChips.setText("");
		lblMult.setText("");
	}

	private void evaluateLiveHand() {
		if (selectedImages.isEmpty()) {
			lblHandType.setText("");
			lblChips.setText("");
			lblMult.setText("");
			return;
		}

		List<HeldHand.Card> selectedCards = new ArrayList<>();
		for (ImageView img : selectedImages) {
			int index = imageViews.indexOf(img);
			int cardId = currentCardIds.get(index);

			int rank = ((cardId - 1) % 13) + 1;
			String suit = switch ((cardId - 1) / 13) {
			case 0 -> "Spades";
			case 1 -> "Hearts";
			case 2 -> "Diamonds";
			case 3 -> "Clubs";
			default -> "Unknown";
			};
			selectedCards.add(new HeldHand.Card(rank, suit, rank));
		}

		if (selectedCards.size() < 1) return;

		HandEvaluator.HandResult result = HandEvaluator.evaluateHandDetailed(selectedCards);
		int bonusChips = result.chips;
		int bonusMult = result.multiplier;
		int rankBonus = result.rankBonus;

		// Planet Buffs
		switch (result.name) {
		case "High Card" -> {
			int count = ownedPlanets.getOrDefault(1, 0);
			bonusChips += 10 * count;
			bonusMult += 1 * count;
		}
		case "Pair" -> {
			int count = ownedPlanets.getOrDefault(2, 0);
			bonusChips += 15 * count;
			bonusMult += 1 * count;
		}
		case "Two Pair" -> {
			int count = ownedPlanets.getOrDefault(3, 0);
			bonusChips += 20 * count;
			bonusMult += 1 * count;
		}
		case "Three of a Kind" -> {
			int count = ownedPlanets.getOrDefault(4, 0);
			bonusChips += 20 * count;
			bonusMult += 2 * count;
		}
		case "Straight" -> {
			int count = ownedPlanets.getOrDefault(5, 0);
			bonusChips += 30 * count;
			bonusMult += 3 * count;
		}
		case "Flush" -> {
			int count = ownedPlanets.getOrDefault(6, 0);
			bonusChips += 15 * count;
			bonusMult += 2 * count;
		}
		case "Full House" -> {
			int count = ownedPlanets.getOrDefault(7, 0);
			bonusChips += 25 * count;
			bonusMult += 2 * count;
		}
		case "Four of a Kind" -> {
			int count = ownedPlanets.getOrDefault(8, 0);
			bonusChips += 30 * count;
			bonusMult += 3 * count;
		}
		case "Straight Flush" -> {
			int count = ownedPlanets.getOrDefault(9, 0);
			bonusChips += 40 * count;
			bonusMult += 4 * count;
		}
		}

		int finalScore = (bonusChips + rankBonus) * bonusMult;

		lblHandType.setText(result.name);
		lblChips.setText(String.valueOf(bonusChips));
		lblMult.setText(String.valueOf(bonusMult));
	}


	private void resetUI() {
		// Hide all ImageViews
		imgSmallBlindCurrent.setVisible(false);
		imgMainBackground.setVisible(false);
		imgJokerThoughtBubble.setVisible(false);
		imgBigBlindCurrent.setVisible(false);
		imgBigBlindDefeated.setVisible(false);
		imgSmallBlindGameScore.setVisible(false);
		imgBigBlindGameScore.setVisible(false);
		imgBossBlindGameScore.setVisible(false);
		imgDiscard.setVisible(false);
		imgPlayHand.setVisible(false);
		imgShopSign.setVisible(false);
		imgSmallBlindDefeated.setVisible(false);
		imgSortHandPane.setVisible(false);
		imgTheClubCurrent.setVisible(false);
		imgTheClubUpcoming.setVisible(false);
		imgBigBlindUpcoming.setVisible(false);
		imgTheGoadCurrent.setVisible(false);
		imgTheGoadUpcoming.setVisible(false);
		imgTheHeadCurrent.setVisible(false);
		imgTheHeadUpcoming.setVisible(false);
		imgTheManacleUpcoming.setVisible(false);
		imgThePsychicCurrent.setVisible(false);
		imgThePsychicUpcoming.setVisible(false);
		imgTheWindowCurrent.setVisible(false);
		imgTheWindowUpcoming.setVisible(false);
		imgShopPane.setVisible(false);
		imgShopCard1.setVisible(false);
		imgShopCard2.setVisible(false);
		imgShopCard3.setVisible(false);
		imgShopCard4.setVisible(false);
		imgShopCard5.setVisible(false);
		imgShopCard6.setVisible(false);
		imgJoker1.setVisible(false);
		imgJoker2.setVisible(false);
		imgJoker3.setVisible(false);
		imgHandCard1.setVisible(false);
		imgHandCard2.setVisible(false);
		imgHandCard3.setVisible(false);
		imgHandCard4.setVisible(false);
		imgHandCard5.setVisible(false);
		imgHandCard6.setVisible(false);
		imgHandCard7.setVisible(false);
		imgHandCard8.setVisible(false);

		// Hide all Labels
		lblSmallBlindScore.setVisible(false);
		lblBigBlindScore.setVisible(false);
		lblBossBlindScore.setVisible(false);
		lblChips.setVisible(false);
		lblMult.setVisible(false);
		lblRoundScore.setVisible(false);
		lblHandsCounter.setVisible(false);
		lblDiscardsCounter.setVisible(false);
		lblRunInfo.setVisible(false);
		lblRound.setVisible(false);
		lblCurrentAnte.setVisible(false);
		lblAnteCap.setVisible(false);
		lblCashOnHand.setVisible(false);
		lblJokerSpeech.setVisible(false);
		lblJokersInHand.setVisible(false);
		lblHandType.setVisible(false);
		lblBlindHealth.setVisible(false);

		// Hide containers
		paneContainer.setVisible(false);
		hboxJokersPane.setVisible(false);
		hboxShopCardDeck.setVisible(false);
		hboxHand.setVisible(false);

		// Disable buttons
		btnMainMenu.setDisable(true);
		btnNextRound.setDisable(true);
		btnSelectSmallBlind.setDisable(true);
		btnSelectBigBlind.setDisable(true);
		btnSelectBossBlind.setDisable(true);
		btnPlayHand.setDisable(true);
		btnDiscard.setDisable(true);
		btnSortRank.setDisable(true);
		btnSortSuit.setDisable(true);
		btnReroll.setDisable(true);
	}

	// rename this to showItemsShop or somethin
	private void showJokerShop() {
		// All possible joker IDs (1 to 10)
		List<Integer> jokerPool = new ArrayList<>();
		for (int i = 1; i <= 10; i++) {
			jokerPool.add(i);
		}

		List<Integer> planetPool = new ArrayList<>();
		for (int i = 1; i <= 9; i++) {
			planetPool.add(i);
		}

		Collections.shuffle(jokerPool);
		Collections.shuffle(planetPool);

		// Pick first 2 jokers randomly
		int joker1Id = jokerPool.get(0);
		int joker2Id = jokerPool.get(1);

		// Pick first 4 olanet cards randomly
		int planet1Id = planetPool.get(0);
		int planet2Id = planetPool.get(1);
		int planet3Id = planetPool.get(2);
		int planet4Id = planetPool.get(3);

		// Build image paths
		String joker1Path = "/image/card/Joker" + joker1Id + ".png";
		String joker2Path = "/image/card/Joker" + joker2Id + ".png";

		String planet1Path = getPlanetImagePath(planet1Id);
		String planet2Path = getPlanetImagePath(planet2Id);
		String planet3Path = getPlanetImagePath(planet3Id);
		String planet4Path = getPlanetImagePath(planet4Id);

		// Load images
		Image img1 = new Image(Objects.requireNonNull(getClass().getResourceAsStream(joker1Path)));
		Image img2 = new Image(Objects.requireNonNull(getClass().getResourceAsStream(joker2Path)));

		System.out.println("🌍 Trying to load: " + planet1Path);
		InputStream testStream = getClass().getResourceAsStream(planet1Path);
		System.out.println("📥 Stream for Planet1 is null? " + (testStream == null));
		Image planetImg1 = new Image(
				Objects.requireNonNull(getClass().getResourceAsStream(planet1Path), "❌ Not found: " + planet1Path));
		Image planetImg2 = new Image(
				Objects.requireNonNull(getClass().getResourceAsStream(planet2Path), "❌ Not found: " + planet2Path));
		Image planetImg3 = new Image(
				Objects.requireNonNull(getClass().getResourceAsStream(planet3Path), "❌ Not found: " + planet3Path));
		Image planetImg4 = new Image(
				Objects.requireNonNull(getClass().getResourceAsStream(planet4Path), "❌ Not found: " + planet4Path));

		// Display in shop image views
		imgShopCard1.setImage(img1);
		imgShopCard2.setImage(img2);

		imgShopCard3.setImage(planetImg1);
		imgShopCard4.setImage(planetImg2);
		imgShopCard5.setImage(planetImg3);
		imgShopCard6.setImage(planetImg4);

		System.out.println("💡 imgShopCard1 is null? " + (imgShopCard1 == null));
		imgShopCard3.setImage(planetImg1);
		System.out.println("💡 imgShopCard2 is null? " + (imgShopCard2 == null));
		imgShopCard4.setImage(planetImg2);
		System.out.println("💡 imgShopCard3 is null? " + (imgShopCard3 == null));
		imgShopCard5.setImage(planetImg3);
		System.out.println("💡 imgShopCard4 is null? " + (imgShopCard4 == null));
		imgShopCard6.setImage(planetImg4);

		// Set click events to purchase
		imgShopCard1.setOnMouseClicked(e -> handleBuyJoker(joker1Id, joker1Path, imgShopCard1));
		imgShopCard2.setOnMouseClicked(e -> handleBuyJoker(joker2Id, joker2Path, imgShopCard2));

		imgShopCard3.setOnMouseClicked(e -> handleBuyPlanet(planet1Id, planet1Path, imgShopCard3));
		imgShopCard4.setOnMouseClicked(e -> handleBuyPlanet(planet2Id, planet2Path, imgShopCard4));
		imgShopCard5.setOnMouseClicked(e -> handleBuyPlanet(planet3Id, planet3Path, imgShopCard5));
		imgShopCard6.setOnMouseClicked(e -> handleBuyPlanet(planet4Id, planet4Path, imgShopCard6));

		// Set hover effect descriptions
		imgShopCard1.setOnMouseEntered(
				e -> lblJokerSpeech.setText(jokerEffects.getOrDefault(joker1Id, "Mystery Joker...")));
		imgShopCard1.setOnMouseExited(e -> lblJokerSpeech.setText("Jokers are 5, Planets are 3. Go nuts."));

		imgShopCard2.setOnMouseEntered(
				e -> lblJokerSpeech.setText(jokerEffects.getOrDefault(joker2Id, "Mystery Joker...")));
		imgShopCard2.setOnMouseExited(e -> lblJokerSpeech.setText("Jokers are 5, Planets are 3. Go nuts."));

		imgShopCard3.setOnMouseEntered(
				e -> lblJokerSpeech.setText(planetEffects.getOrDefault(planet1Id, "Mystery Planet...")));
		imgShopCard3.setOnMouseExited(e -> lblJokerSpeech.setText("Jokers are 5, Planets are 3. Go nuts."));

		imgShopCard4.setOnMouseEntered(
				e -> lblJokerSpeech.setText(planetEffects.getOrDefault(planet2Id, "Mystery Planet...")));
		imgShopCard4.setOnMouseExited(e -> lblJokerSpeech.setText("Jokers are 5, Planets are 3. Go nuts."));

		imgShopCard5.setOnMouseEntered(
				e -> lblJokerSpeech.setText(planetEffects.getOrDefault(planet3Id, "Mystery Planet...")));
		imgShopCard5.setOnMouseExited(e -> lblJokerSpeech.setText("Jokers are 5, Planets are 3. Go nuts."));

		imgShopCard6.setOnMouseEntered(
				e -> lblJokerSpeech.setText(planetEffects.getOrDefault(planet4Id, "Mystery Planet...")));
		imgShopCard6.setOnMouseExited(e -> lblJokerSpeech.setText("Jokers are 5, Planets are 3. Go nuts."));

		imgJoker1.setOnMouseEntered(e -> {
			if (jokerSlot1Id != null)
				lblJokerSpeech.setText(jokerEffects.getOrDefault(jokerSlot1Id, "Mystery Joker..."));
		});
		imgJoker1.setOnMouseExited(e -> lblJokerSpeech.setText(""));

		imgJoker2.setOnMouseEntered(e -> {
			if (jokerSlot2Id != null)
				lblJokerSpeech.setText(jokerEffects.getOrDefault(jokerSlot2Id, "Mystery Joker..."));
		});
		imgJoker2.setOnMouseExited(e -> lblJokerSpeech.setText(""));

		imgJoker3.setOnMouseEntered(e -> {
			if (jokerSlot3Id != null)
				lblJokerSpeech.setText(jokerEffects.getOrDefault(jokerSlot3Id, "Mystery Joker..."));
		});
		imgJoker3.setOnMouseExited(e -> lblJokerSpeech.setText(""));

		lblJokerSpeech.setText(jokerEffects.getOrDefault(joker1Id, "Mystery Joker..."));

		// Optional: reset shop visuals
		imgShopCard1.setOpacity(1.0);
		imgShopCard1.setDisable(false);
		imgShopCard2.setOpacity(1.0);
		imgShopCard2.setDisable(false);

		System.out.println("🃏 Shop Jokers: " + joker1Id + " and " + joker2Id);
	}

	//	private void setPlanetShopCard(int planetId, ImageView targetImageView) {
	//	    String path = getPlanetImagePath(planetId);
	//	    InputStream stream = getClass().getResourceAsStream(path);
	//	    Image image = new Image(Objects.requireNonNull(stream, "❌ Couldn't load planet image: " + path));
	//	    targetImageView.setImage(image);
	//	}

	private String getPlanetImagePath(int planetId) {
		return "/image/card/Planet" + planetId + ".png";
	}

	public static class PlanetEffect {
		public final String handType;
		public final int bonusChips;
		public final int bonusMult;

		public PlanetEffect(String handType, int bonusChips, int bonusMult) {
			this.handType = handType;
			this.bonusChips = bonusChips;
			this.bonusMult = bonusMult;
		}
	}

	public HandEvaluator.HandResult applyPlanetEffects(HandEvaluator.HandResult result, Set<Integer> ownedPlanets) {
		int bonusChips = 0;
		int bonusMult = 0;

		for (int planetId : ownedPlanets) {
			PlanetEffect effect = planetEffectMap.get(planetId);
			if (effect != null && effect.handType.equals(result.name)) {
				bonusChips += effect.bonusChips;
				bonusMult += effect.bonusMult;
			}
		}

		int finalChips = result.chips + bonusChips;
		int finalMult = result.multiplier + bonusMult;

		return new HandEvaluator.HandResult(result.name, finalChips, finalMult, result.rankBonus);
	}

	private final Map<Integer, PlanetEffect> planetEffectMap = Map.of(1, new PlanetEffect("High Card", 10, 1), 2,
			new PlanetEffect("Pair", 15, 1), 3, new PlanetEffect("Two Pair", 20, 1), 4,
			new PlanetEffect("Three of a Kind", 20, 2), 5, new PlanetEffect("Straight", 30, 3), 6,
			new PlanetEffect("Flush", 15, 2), 7, new PlanetEffect("Full House", 25, 2), 8,
			new PlanetEffect("Four of a Kind", 30, 3), 9, new PlanetEffect("Straight Flush", 40, 4));



	private void handleBuyJoker(int jokerId, String imagePath, ImageView shopImageView) {

		// 🪙 Not enough money
		if (cashOnHand < 4) {
			System.out.println("❌ Not enough cash! Joker costs $4.");
			return;
		}

		// ⛔ Joker limit
		if (ownedJokers.size() >= MAX_JOKERS) {
			System.out.println("⛔ Joker limit reached (max 3).");
			return;
		}

		// Deduct money
		cashOnHand -= 4;
		lblCashOnHand.setText("$" + cashOnHand);

		// Track joker
		ownedJokers.add(jokerId);
		lblJokersInHand.setText(ownedJokers.size() + "/3");

		// Assign image to joker slot
		Image jokerImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));

		if (imgJoker1.getImage() == null) {
			imgJoker1.setImage(jokerImage);
			jokerSlot1Id = jokerId;
		} else if (imgJoker2.getImage() == null) {
			imgJoker2.setImage(jokerImage);
			jokerSlot2Id = jokerId;
		} else if (imgJoker3.getImage() == null) {
			imgJoker3.setImage(jokerImage);
			jokerSlot3Id = jokerId;
		}

		// Dim the shop image to indicate it's purchased
		shopImageView.setOpacity(0.3); // 👈 Dim the image visually

		System.out.println("🎴 Joker " + jokerId + " purchased and equipped!");
	}

	private void handleBuyPlanet(int planetId, String imagePath, ImageView shopImageView) {
		// 💸 Check if you have enough cash
		if (cashOnHand < 3) {
			System.out.println("❌ Not enough cash! Planet costs $3.");
			return;
		}

		// ⛔ Optional: You can add a max per-type if you want (not required here)
		int currentLevel = ownedPlanets.getOrDefault(planetId, 0);

		// 💰 Deduct cost and update label
		cashOnHand -= 3;
		lblCashOnHand.setText("$" + cashOnHand);

		// 🌍 Add or increment the planet level
		ownedPlanets.put(planetId, currentLevel + 1);

		// 🖼️ Dim and disable the image
		shopImageView.setOpacity(0.3);
		shopImageView.setDisable(true);
		shopImageView.setOnMouseClicked(null); // Prevent multiple purchases

		// ✅ Confirm
		System.out.println("🪐 Bought planet " + planetId + " → New Level: " + (currentLevel + 1));
	}

	private List<Integer> getRandomJokerIds(int count) {
		List<Integer> jokerPool = IntStream.rangeClosed(1, 10).filter(id -> !ownedJokers.contains(id)).boxed()
				.collect(Collectors.toList());

		Collections.shuffle(jokerPool);
		return jokerPool.stream().limit(count).toList();
	}

	private List<Integer> getRandomPlanetIds(int count) {
		List<Integer> planetPool = IntStream.rangeClosed(1, 9)
				.filter(id -> !ownedPlanets.containsKey(id)) // ✅ Check keys, not values
				.boxed()
				.collect(Collectors.toList());

		Collections.shuffle(planetPool);
		return planetPool.stream().limit(count).toList();
	}

	private void updateShopWithJokers(List<Integer> jokerIds) {
		if (jokerIds.size() < 2) return;

		int id1 = jokerIds.get(0);
		int id2 = jokerIds.get(1);

		setShopCard(id1, imgShopCard1);
		setShopCard(id2, imgShopCard2);

		// 👇 Update hover descriptions for shop jokers
		imgShopCard1.setOnMouseEntered(e -> lblJokerSpeech.setText(jokerEffects.getOrDefault(id1, "Mystery Joker...")));
		imgShopCard1.setOnMouseExited(e -> lblJokerSpeech.setText("Jokers are 5, Planets are 3. Go nuts."));

		imgShopCard2.setOnMouseEntered(e -> lblJokerSpeech.setText(jokerEffects.getOrDefault(id2, "Mystery Joker...")));
		imgShopCard2.setOnMouseExited(e -> lblJokerSpeech.setText("Jokers are 5, Planets are 3. Go nuts."));
	}

	private void setPlanetCard(int planetId, ImageView targetImageView) {
		String path = "/image/card/Planet" + planetId + ".png";
		InputStream stream = getClass().getResourceAsStream(path);

		if (stream == null) {
			System.out.println("❌ Planet image not found: " + path);
			return;
		}

		Image image = new Image(stream);
		targetImageView.setImage(image);
		targetImageView.setOpacity(1.0);
		targetImageView.setDisable(false);
		System.out.println("🌍 Planet ID " + planetId + " loaded into slot");
	}


	private void updateShopWithPlanets(List<Integer> planetIds) {
		if (planetIds.size() < 4) return;

		int id1 = planetIds.get(0);
		int id2 = planetIds.get(1);
		int id3 = planetIds.get(2);
		int id4 = planetIds.get(3);

		setPlanetCard(id1, imgShopCard3);
		setPlanetCard(id2, imgShopCard4);
		setPlanetCard(id3, imgShopCard5);
		setPlanetCard(id4, imgShopCard6);

		// Hover effect (if desired)
		imgShopCard3.setOnMouseEntered(e -> lblJokerSpeech.setText(planetEffects.getOrDefault(id1, "Mystery Planet...")));
		imgShopCard3.setOnMouseExited(e -> lblJokerSpeech.setText("Jokers are 5, Planets are 3. Go nuts."));

		imgShopCard4.setOnMouseEntered(e -> lblJokerSpeech.setText(planetEffects.getOrDefault(id2, "Mystery Planet...")));
		imgShopCard4.setOnMouseExited(e -> lblJokerSpeech.setText("Jokers are 5, Planets are 3. Go nuts."));

		imgShopCard5.setOnMouseEntered(e -> lblJokerSpeech.setText(planetEffects.getOrDefault(id3, "Mystery Planet...")));
		imgShopCard5.setOnMouseExited(e -> lblJokerSpeech.setText("Jokers are 5, Planets are 3. Go nuts."));

		imgShopCard6.setOnMouseEntered(e -> lblJokerSpeech.setText(planetEffects.getOrDefault(id4, "Mystery Planet...")));
		imgShopCard6.setOnMouseExited(e -> lblJokerSpeech.setText("Jokers are 5, Planets are 3. Go nuts."));
	}


	private void setShopCard(int jokerId, ImageView targetImageView) {
		String path = "/image/card/Joker" + jokerId + ".png";

		InputStream stream = getClass().getResourceAsStream(path);
		if (stream == null) {
			System.out.println("❌ Image not found: " + path);
			return;
		}

		Image image = new Image(stream);
		targetImageView.setImage(image);

		if (ownedJokers.contains(jokerId)) {
			// Joker already owned → gray out and disable
			targetImageView.setOpacity(0.3);
			targetImageView.setDisable(true);
			targetImageView.setOnMouseClicked(null); // Prevent click
		} else {
			// Joker available → fully visible and clickable
			targetImageView.setOpacity(1.0);
			targetImageView.setDisable(false);
			targetImageView.setOnMouseClicked(e -> handleBuyJoker(jokerId, path, targetImageView));
		}
	}


	private void rerollJokers() {
		List<Integer> newJokerIds = getRandomJokerIds(2);
		updateShopWithJokers(newJokerIds);
	}

	private void rerollPlanets() {
		List<Integer> newPlanetIds = getRandomPlanetIds(4); // ✅ uses filtered + shuffled list
		updateShopWithPlanets(newPlanetIds);                // ✅ updates UI
	}



	@FXML
	private void handleRerollShop() {
		System.out.println("🔁 Reroll shop triggered.");

		if (cashOnHand < 5) {
			System.out.println("❌ Not enough cash to reroll.");
			return;
		}

		cashOnHand -= 5;
		lblCashOnHand.setText("$" + cashOnHand);

		// Separate reroll logic
		rerollJokers();
		rerollPlanets();
	}

	private void adjustFontSize(Label label) {
		int length = label.getText().length();

		String baseStyle;
		if (length <= 3) {
			baseStyle = "-fx-font-size: 36px; -fx-font-family: 'Balatro';";
		} else if (length == 4) {
			baseStyle = "-fx-font-size: 30px; -fx-font-family: 'Balatro';";
		} else if (length == 5) {
			baseStyle = "-fx-font-size: 20px; -fx-font-family: 'Balatro';";
		}else {
			baseStyle = "-fx-font-size: 10px; -fx-font-family: 'Balatro';";
		}

		label.setStyle(baseStyle);
	}

	public void showShopScene() {
		resetUI();
		resetPlanetShopCards();

		// === Core UI ===
		imgMainBackground.setVisible(true);
		imgJokerThoughtBubble.setVisible(true);
		imgShopPane.setVisible(true);
		imgShopCard1.setVisible(true);
		imgShopCard2.setVisible(true);
		imgShopCard3.setVisible(true);
		imgShopCard4.setVisible(true);
		imgShopCard5.setVisible(true);
		imgShopCard6.setVisible(true);
		imgJoker1.setVisible(true);
		imgJoker2.setVisible(true);
		imgJoker3.setVisible(true);

		// === Labels ===
		lblHandsCounter.setVisible(true);
		lblDiscardsCounter.setVisible(true);
		lblRunInfo.setVisible(true);
		lblRound.setVisible(true);
		lblCurrentAnte.setVisible(true);
		lblAnteCap.setVisible(true);
		lblCashOnHand.setVisible(true);
		lblJokerSpeech.setVisible(true);
		lblJokersInHand.setVisible(true);
		lblJokerSpeech.setText("Jokers are 5, Planets are 3. Go nuts.");

		// === Containers ===
		paneContainer.setVisible(true);
		hboxJokersPane.setVisible(true);
		hboxShopCardDeck.setVisible(true);
		hboxShopCardDeck2.setVisible(true);

		// === Buttons ===
		btnNextRound.setDisable(false);
		btnMainMenu.setDisable(false);
		btnReroll.setDisable(false);

		showJokerShop();
		btnReroll.toFront();
		btnNextRound.toFront();
		System.out.println("Shop till you drop!");
	}

	private void resetPlanetShopCards() {
		for (ImageView view : List.of(imgShopCard3, imgShopCard4, imgShopCard5, imgShopCard6)) {
			view.setDisable(false);
			view.setOpacity(1.0);
			view.setImage(null); // Optional: clear old image
			view.setOnMouseClicked(null); // Clear old click events
		}
	}

	private int calculateBlindScore(int roundNumber) {
		// Each ante includes 3 phases (small, big, boss)
		int anteNumber = (roundNumber - 1) / 3; // Starts at 0
		int phase = (roundNumber - 1) % 3;      // 0: small, 1: big, 2: boss

		int baseScore = 300;     // Starting score for Small Blind
		int increment = 150;     // Score increase per phase

		return baseScore + (anteNumber * 3 + phase) * increment;
	}

	public void showSmallBlindScene() {
		resetUI();

		// Core UI
		paneContainer.setVisible(true);
		imgSmallBlindCurrent.setVisible(true);
		imgMainBackground.setVisible(true);
		imgBigBlindUpcoming.setVisible(true);
		imgJokerThoughtBubble.setVisible(true);
		imgJoker1.setVisible(true);
		imgJoker2.setVisible(true);
		imgJoker3.setVisible(true);

		initializeBossBlindIfNeeded();
		showBossImage("upcoming");

		// UI labels
		lblSmallBlindScore.setVisible(true);
		lblSmallBlindScore.setText(String.valueOf(calculateBlindScore(roundNumber + 1))); // when showing small
		lblSmallBlindScore.setLayoutY(640);
		adjustFontSize(lblSmallBlindScore);
		lblBigBlindScore.setVisible(true);
		lblBigBlindScore.setText(String.valueOf(calculateBlindScore(roundNumber + 2)));   // etc
		lblBigBlindScore.setLayoutY(700);
		adjustFontSize(lblBigBlindScore);
		lblBossBlindScore.setText(String.valueOf(calculateBlindScore(roundNumber + 3)));
		lblBossBlindScore.setVisible(true);
		lblBossBlindScore.setLayoutY(760);
		adjustFontSize(lblBossBlindScore);
		lblJokersInHand.setVisible(true);
		lblJokerSpeech.setVisible(true);
		lblJokerSpeech.setText("Ready to lose? LOL (That's short for laugh out loud btw)");

		// Gameplay info
		lblChips.setVisible(true);
		lblMult.setVisible(true);
		lblHandsCounter.setVisible(true);
		lblDiscardsCounter.setVisible(true);
		lblRunInfo.setVisible(true);
		lblCashOnHand.setVisible(true);
		lblCurrentAnte.setVisible(true);
		lblAnteCap.setVisible(true);
		lblRound.setVisible(true);
		hboxJokersPane.setVisible(true);

		// Buttons
		btnMainMenu.setDisable(false);
		btnSelectSmallBlind.setDisable(false);
		buttonsToFront();
	}

	public void showBigBlindScene() {
		resetUI(); // Always start with a clean slate

		paneContainer.setVisible(true);
		imgSmallBlindDefeated.setVisible(true);
		imgMainBackground.setVisible(true);
		imgBigBlindCurrent.setVisible(true);
		imgJokerThoughtBubble.setVisible(true);
		imgJoker1.setVisible(true);
		imgJoker2.setVisible(true);
		imgJoker3.setVisible(true);

		// ✅ Use previously stored boss, not reselect
		if (selectedBossBlind != null) {
			selectedBossBlind.setVisible(true);
		}

		showBossImage("upcoming");

		lblSmallBlindScore.setVisible(true);
		lblSmallBlindScore.setText(String.valueOf(calculateBlindScore(roundNumber))); // when showing small
		lblSmallBlindScore.setLayoutY(700);
		adjustFontSize(lblSmallBlindScore);
		lblBigBlindScore.setVisible(true);
		lblBigBlindScore.setText(String.valueOf(calculateBlindScore(roundNumber + 1))); // when showing big
		lblBigBlindScore.setLayoutY(640);
		adjustFontSize(lblBigBlindScore);
		lblBossBlindScore.setVisible(true);
		lblBossBlindScore.setText(String.valueOf(calculateBlindScore(roundNumber + 2))); // when showing boss
		lblBossBlindScore.setLayoutY(760);
		adjustFontSize(lblBossBlindScore);
		lblJokersInHand.setVisible(true);
		lblJokerSpeech.setVisible(true);
		lblJokerSpeech.setText("Welcome back! MUAHAHAHHAHAHA");

		lblChips.setVisible(true);
		lblMult.setVisible(true);
		lblHandsCounter.setVisible(true);
		lblDiscardsCounter.setVisible(true);
		lblRunInfo.setVisible(true);
		lblCashOnHand.setVisible(true);
		lblCurrentAnte.setVisible(true);
		lblAnteCap.setVisible(true);
		lblRound.setVisible(true);

		hboxJokersPane.setVisible(true);

		btnMainMenu.setDisable(false);
		btnSelectBigBlind.setDisable(false);
		buttonsToFront();
	}

	public void showBossBlindScene() {
		resetUI(); // Clean start

		paneContainer.setVisible(true);
		imgMainBackground.setVisible(true);
		imgJokerThoughtBubble.setVisible(true);
		imgBigBlindDefeated.setVisible(true);
		imgSmallBlindDefeated.setVisible(true);
		imgJoker1.setVisible(true);
		imgJoker2.setVisible(true);
		imgJoker3.setVisible(true);

		// 🧠 Display preselected boss CURRENT image (not upcoming)
		showBossImage("current");

		// 🔢 Score labels and layout
		lblSmallBlindScore.setVisible(true);
		lblSmallBlindScore.setText(String.valueOf(calculateBlindScore(roundNumber - 1))); // when showing small
		lblSmallBlindScore.setLayoutY(700);
		adjustFontSize(lblSmallBlindScore);
		lblBigBlindScore.setVisible(true);
		lblBigBlindScore.setText(String.valueOf(calculateBlindScore(roundNumber))); // when showing big
		lblBigBlindScore.setLayoutY(700);
		adjustFontSize(lblBigBlindScore);
		lblBossBlindScore.setVisible(true);
		lblBossBlindScore.setText(String.valueOf(calculateBlindScore(roundNumber + 1))); // when showing boss
		lblBossBlindScore.setLayoutY(700);
		adjustFontSize(lblBossBlindScore);

		// 🃏 Joker + Game info
		lblJokersInHand.setVisible(true);
		lblJokerSpeech.setText("Make sure to read that Boss Blind rule! Or else you'll lose. LOL!");
		lblJokerSpeech.setVisible(true);

		lblChips.setVisible(true);
		lblMult.setVisible(true);
		lblHandsCounter.setVisible(true);
		lblDiscardsCounter.setVisible(true);
		lblRunInfo.setVisible(true);
		lblCashOnHand.setVisible(true);
		lblCurrentAnte.setVisible(true);
		lblAnteCap.setVisible(true);
		lblRound.setVisible(true);

		hboxJokersPane.setVisible(true);

		// 🔘 Button control (disable others if needed)
		btnMainMenu.setDisable(false);
		btnSelectBossBlind.setDisable(false); // disable to avoid re-selecting

		buttonsToFront(); // Make sure buttons are clickable
	}

	@FXML
	private void handleNextRound() {
		try {
			System.out.println("🔁 Reroll shop triggered.");

			// 👀 Determine which scene to show
			int blindPhase = roundNumber % 3;
			if (blindPhase == 1) {
				System.out.println("➡️ Moving to SMALL blind scene.");
				showBigBlindScene();
			} else if (blindPhase == 2) {
				System.out.println("➡️ Moving to BIG blind scene.");
				showBossBlindScene();
			} else {
				System.out.println("➡️ Moving to BOSS blind scene.");
				showSmallBlindScene();
			}

		} catch (Exception e) {
			System.out.println("❌ Error advancing round: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void showCurrentBlindPanel() {
		// Parse current round number from label
		try {
			roundNumber = Integer.parseInt(lblRound.getText());
		} catch (NumberFormatException e) {
			System.out.println("❌ Invalid round number in lblRound: " + lblRound.getText());
			return;
		}

		int phase = roundNumber % 3;

		if (phase == 1) {
			imgSmallBlindGameScore.getParent().setVisible(true);
			imgSmallBlindGameScore.setVisible(true);
		} else if (phase == 2) {
			imgBigBlindGameScore.setVisible(true);
			System.out.println("🔁 Round " + roundNumber + ": Big Blind");
		} else {
			imgBossBlindGameScore.setVisible(true);
			System.out.println("🔁 Round " + roundNumber + ": Boss Blind");
		}
	}

	private String incrementRound() {
		roundNumber++;
		return String.valueOf(roundNumber);
	}

	private void resetRound() {
		roundNumber = 1;
		lblRound.setText("0");
	}

	private boolean isBossBlindPhase() {
		return roundNumber % 3 == 0;
	}


	public void showGameScreen() {
		resetUI(); // Always start with a clean slate
		// update UI
		totalScore = 0;
		roundScore = 0;
		handsRemaining = 4;
		discardsRemaining = 4;
		lblRoundScore.setText("");
		lblChips.setText("");
		lblMult.setText("");
		lblRound.setText(incrementRound());
		adjustFontSize(lblRound);
		showCurrentBlindPanel();
		blindScore = calculateBlindScore(roundNumber);
		lblBlindHealth.setText(String.valueOf(blindScore));
		adjustFontSize(lblBlindHealth);
		if ("TheManacle".equals(selectedBossName) && isBossBlindPhase()) {
			applyTheManacleEffect();
		} else {
			handSize = 8;
		}
		if ("ThePsychic".equals(selectedBossName) && isBossBlindPhase()) {
			applyThePsychicEffect();
		} else {
			enforceFiveCardPlays = false;
		}
		imgMainBackground.setVisible(true);
		imgJokerThoughtBubble.setVisible(true);
		imgDiscard.setVisible(true);
		imgPlayHand.setVisible(true);
		imgSortHandPane.setVisible(true);
		imgJoker1.setVisible(true);
		imgJoker2.setVisible(true);
		imgJoker3.setVisible(true);
		imgHandCard1.setVisible(true);
		imgHandCard2.setVisible(true);
		imgHandCard3.setVisible(true);
		imgHandCard4.setVisible(true);
		imgHandCard5.setVisible(true);
		imgHandCard6.setVisible(true);
		imgHandCard7.setVisible(true);
		imgHandCard8.setVisible(true);

		lblChips.setVisible(true);
		lblMult.setVisible(true);
		lblRoundScore.setVisible(true);
		lblHandsCounter.setVisible(true);
		lblDiscardsCounter.setVisible(true);
		lblRunInfo.setVisible(true);
		lblRound.setVisible(true);
		lblCurrentAnte.setVisible(true);
		lblAnteCap.setVisible(true);
		lblCashOnHand.setVisible(true);
		lblJokerSpeech.setVisible(true);
		lblJokersInHand.setVisible(true);
		lblHandType.setVisible(true);
		lblBlindHealth.setVisible(true);
		lblBlindHealth.setText((String.valueOf(blindScore)));

		paneContainer.setVisible(true);
		hboxJokersPane.setVisible(true);
		hboxHand.setVisible(true);

		btnMainMenu.setDisable(true);
		btnPlayHand.setDisable(false);
		btnDiscard.setDisable(false);
		btnSortRank.setDisable(false);
		btnSortSuit.setDisable(false);

		initDeck();
		dealNewHand();
		lblCurrentDeckCounter.setText(String.valueOf(deck.size()));
		buttonsToFront();
		hboxHand.toFront();
		btnPlayHand.toFront();
	}

	private void showBossImage(String state) {
		// state = "current", "upcoming"

		switch (selectedBossName) {
		case "TheHead" -> {
			if (state.equals("current"))
				imgTheHeadCurrent.setVisible(true);
			else if (state.equals("upcoming"))
				imgTheHeadUpcoming.setVisible(true);
		}
		case "TheWindow" -> {
			if (state.equals("current"))
				imgTheWindowCurrent.setVisible(true);
			else if (state.equals("upcoming"))
				imgTheWindowUpcoming.setVisible(true);
		}
		case "TheClub" -> {
			if (state.equals("current"))
				imgTheClubCurrent.setVisible(true);
			else if (state.equals("upcoming"))
				imgTheClubUpcoming.setVisible(true);
		}
		case "TheGoad" -> {
			if (state.equals("current"))
				imgTheGoadCurrent.setVisible(true);
			else if (state.equals("upcoming"))
				imgTheGoadUpcoming.setVisible(true);
		}
		case "TheManacle" -> {
			if (state.equals("current"))
				imgTheManacleCurrent.setVisible(true);
			else if (state.equals("upcoming"))
				imgTheManacleUpcoming.setVisible(true);
		}
		case "ThePsychic" -> {
			if (state.equals("current"))
				imgThePsychicCurrent.setVisible(true);
			else if (state.equals("upcoming"))
				imgThePsychicUpcoming.setVisible(true);
		}
		}
	}

	private void resetBossBlind() {
		selectedBossName = null;
		selectedBossBlind = null;
	}


	private void initializeBossBlindIfNeeded() {
		if (selectedBossName == null) {
			selectedBossName = chooseRandomBossName();
			selectedBossBlind = getUpcomingBossImage(selectedBossName);
			System.out.println("🎲 Selected Boss: " + selectedBossName);
		}
	}

	private String chooseRandomBossName() {
		List<String> bosses = List.of("TheHead", "TheWindow", "TheClub", "TheGoad", "TheManacle", "ThePsychic");
		return bosses.get(new Random().nextInt(bosses.size()));
	}

	private ImageView getUpcomingBossImage(String bossName) {
		return switch (bossName) {
		case "TheHead" -> imgTheHeadUpcoming;
		case "TheWindow" -> imgTheWindowUpcoming;
		case "TheClub" -> imgTheClubUpcoming;
		case "TheGoad" -> imgTheGoadUpcoming;
		case "TheManacle" -> imgTheManacleUpcoming;
		case "ThePsychic" -> imgThePsychicUpcoming;
		default -> throw new IllegalArgumentException("Unknown boss: " + bossName);
		};
	}

	private static class Boss {
		String name;
		ImageView currentImage;
		ImageView upcomingImage;

		Boss(String name, ImageView current, ImageView upcoming) {
			this.name = name;
			this.currentImage = current;
			this.upcomingImage = upcoming;
		}
	}

}
