// HeldHand.java

// JavaFX imports for UI components
package com.balatro_lite;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

// Main class extending JavaFX's Application class
public class HeldHand extends Application {

	// Path to card images (local directory)
	private static final String imageDir = "file:/C:/Users/logan/Downloads/image";

	// Deck of card IDs (1-52)
	private List<Integer> deck = new ArrayList<>();
	private int totalScore = 0;

	// Stores selected card images for actions like discard/evaluate
	private final Set<ImageView> selectedImages = new HashSet<>();
	// Stores current card IDs displayed on screen
	private final List<Integer> currentCardIds = new ArrayList<>();

	// Game state variables
	private int discardsRemaining = 3;
	private int handsRemaining = 4;
	private int currentRound = 1;
	private int blindScore = 300;

	// UI Elements
	private Label scoreLabel;
	private Label turnsLabel;
	private Label blindLabel;
	private TextFlow lblGameTitle;
	private Button btnEvaluate;
	private Button btnDiscard;
	private Button btnShow;
	private ImageView[] imageViews;
	private HBox cardBox;
	private Scene cardScene;

	@Override
	public void start(Stage stage) throws IOException {
		
		// ------------------ LOGIN UI SETUP ------------------

		// ✅ Load FXML login screen
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
		Parent root = loader.load();

		// ✅ Get controller and inject HeldHand into it
		LoginController controller = loader.getController();
		controller.setMainApp(this); // give controller access to this HeldHand instance

		// ✅ Set the login scene
		Scene loginScene = new Scene(root);
		stage.setScene(loginScene);
		stage.setTitle("Login - Balatro Lite");
		stage.setResizable(false);
		stage.show();


		// ------------------ GAME UI SETUP ------------------

		// Labels for score, hand and blind display
		scoreLabel = new Label("Score: 0");
		turnsLabel = new Label("Hands Left: 4 | Discards Left: 3");
		blindLabel = new Label("Blind: 300");

		// Center-aligned top text area
		lblGameTitle = new TextFlow();
		lblGameTitle.setStyle("-fx-font-size: 14pt;");

		// HBox to hold card images horizontally
		cardBox = new HBox(4);
		cardBox.setAlignment(Pos.CENTER);

		// Create 8 card image slots, default to card back image
		imageViews = new ImageView[8];
		for (int i = 0; i < imageViews.length; i++) {
			imageViews[i] = new ImageView(getImageCover());
			imageViews[i].setFitWidth(72);
			imageViews[i].setFitHeight(96);
			cardBox.getChildren().add(imageViews[i]);

			// Add click event to allow selection/deselection of cards
			ImageView img = imageViews[i];
			img.setOnMouseClicked(e -> {
				if (selectedImages.contains(img)) {
					selectedImages.remove(img);        // Deselect
					img.setStyle("");
					img.setTranslateY(0);
				} else if (selectedImages.size() < 5) {
					selectedImages.add(img);          // Select
					img.setStyle("-fx-effect: dropshadow(gaussian, black, 10, 0.5, 0, 0);");
					img.setTranslateY(-20);
				}
				// Live preview of hand result for current selection
				evaluateCurrentSelection(lblGameTitle, currentCardIds, cardBox);
			});
		}

		// Action buttons
		btnShow = new Button("Show");
		btnEvaluate = new Button("Evaluate");
		btnDiscard = new Button("Discard");

		// Event listeners for each action button
		btnShow.setOnAction(e -> dealNewHand());
		btnEvaluate.setOnAction(e -> evaluateSelectedCards());
		btnDiscard.setOnAction(e -> discardSelectedCards());

		// Layouts
		HBox buttonBox = new HBox(10, btnEvaluate, btnDiscard);
		VBox topBox = new VBox(lblGameTitle, scoreLabel, blindLabel, turnsLabel);
		BorderPane cardPane = new BorderPane();
		cardPane.setTop(topBox);
		cardPane.setCenter(cardBox);
		cardPane.setBottom(buttonBox);

		cardScene = new Scene(cardPane, 800, 400);
	}

	// Resets and shuffles the full 52-card deck
	private void resetDeck() {
		deck.clear();
		for (int i = 1; i <= 52; i++) deck.add(i);
		Collections.shuffle(deck);
	}

	// Deals 8 new cards to the player
	private void dealNewHand() {
		if (deck.size() < 8) {
			lblGameTitle.getChildren().setAll(new Text("Deck is out of cards!"));
			btnShow.setDisable(true);
			return;
		}
		currentCardIds.clear();
		List<Integer> ids = new ArrayList<>(deck.subList(0, 8));
		deck.subList(0, 8).clear();  // Remove dealt cards from deck
		currentCardIds.addAll(ids);

		for (int i = 0; i < imageViews.length; i++) {
			imageViews[i].setImage(new Image(imageDir + "/card/" + ids.get(i) + ".png"));
			imageViews[i].setStyle("");
			imageViews[i].setTranslateY(0);
		}
		selectedImages.clear();
	}

	// Evaluates the selected 5 cards and scores the hand
	private void evaluateSelectedCards() {
		if (selectedImages.isEmpty()) {
			lblGameTitle.getChildren().setAll(new Text("Please select 5 cards."));
			return;
		}
		if (handsRemaining <= 0) {
			lblGameTitle.getChildren().setAll(new Text("No hands remaining."));
			btnEvaluate.setDisable(true);
			return;
		}

		List<Card> selectedCards = new ArrayList<>();
		List<ImageView> toReplace = new ArrayList<>();

		// Convert selected image views into Card objects
		for (ImageView img : selectedImages) {
			int index = cardBox.getChildren().indexOf(img);
			int cardId = currentCardIds.get(index);
			selectedCards.add(new Card(cardId));
			toReplace.add(img);
		}

		// Call hand evaluator logic (external class)
		HandEvaluator.HandResult result = HandEvaluator.evaluateHandDetailed(selectedCards);

		// Update total score
		totalScore += result.totalScore;
		scoreLabel.setText("Total Score: " + totalScore);
		displayHandResult(lblGameTitle, result);

		// Replace selected cards with new ones from deck
		for (ImageView img : toReplace) {
			int index = cardBox.getChildren().indexOf(img);
			if (deck.isEmpty()) continue;
			int newCardId = deck.remove(0);
			currentCardIds.set(index, newCardId);
			img.setImage(new Image(imageDir + "/card/" + newCardId + ".png"));
			img.setStyle("");
			img.setTranslateY(0);
		}

		selectedImages.clear();
		handsRemaining--;
		turnsLabel.setText("Hands Left: " + handsRemaining + " | Discards Left: " + discardsRemaining);

		// Check if game ends
		if (handsRemaining == 0 && totalScore < blindScore) {
			lblGameTitle.getChildren().setAll(new Text("You failed to beat the blind. Game Over."));
			btnEvaluate.setDisable(true);
			btnDiscard.setDisable(true);
			btnShow.setDisable(true);
		}
	}

	// Discards selected cards and replaces them with new ones
	private void discardSelectedCards() {
		if (selectedImages.isEmpty()) {
			lblGameTitle.getChildren().setAll(new Text("Select cards to discard."));
			return;
		}
		if (discardsRemaining <= 0) {
			lblGameTitle.getChildren().setAll(new Text("No discards remaining!"));
			btnDiscard.setDisable(true);
			return;
		}

		for (ImageView img : new ArrayList<>(selectedImages)) {
			int index = cardBox.getChildren().indexOf(img);
			if (deck.isEmpty()) continue;
			int newCardId = deck.remove(0);
			currentCardIds.set(index, newCardId);
			img.setImage(new Image(imageDir + "/card/" + newCardId + ".png"));
			img.setStyle("");
			img.setTranslateY(0);
		}

		selectedImages.clear();
		discardsRemaining--;
		turnsLabel.setText("Hands Left: " + handsRemaining + " | Discards Left: " + discardsRemaining);
	}

	// Show live evaluation of current selected cards (preview only)
	private void evaluateCurrentSelection(TextFlow flow, List<Integer> currentCardIds, HBox cardBox) {
		if (selectedImages.isEmpty()) {
			flow.getChildren().setAll(new Text("No cards selected."));
			return;
		}
		List<Card> selectedCards = new ArrayList<>();
		for (ImageView img : selectedImages) {
			int index = cardBox.getChildren().indexOf(img);
			int cardId = currentCardIds.get(index);
			selectedCards.add(new Card(cardId));
		}
		HandEvaluator.HandResult result = HandEvaluator.evaluateHandDetailed(selectedCards);
		displayHandResult(flow, result);
	}

	// Display hand result in the TextFlow area
	private void displayHandResult(TextFlow flow, HandEvaluator.HandResult result) {
		Text textHandName = new Text("Hand: " + result.name + " (");
		Text textChips = new Text(String.valueOf(result.chips));
		textChips.setFill(Color.BLUE);
		Text textPlus = new Text(" + " + result.rankBonus + ") × ");
		Text textMult = new Text(String.valueOf(result.multiplier));
		textMult.setFill(Color.RED);
		Text textEquals = new Text(" = " + result.totalScore);

		flow.getChildren().setAll(textHandName, textChips, textPlus, textMult, textEquals);
	}
	
	public void launchGame(String username) {
		lblGameTitle.getChildren().setAll(new Text("Welcome, " + username + "!"));

		resetDeck();         // Shuffle new deck
		btnShow.fire();      // Deal first hand automatically

		// Get the current stage and switch to the main game scene
		Stage stage = (Stage) lblGameTitle.getScene().getWindow();
		stage.setScene(cardScene);
	}

	// Returns the default card back image
	private Image getImageCover() {
		return new Image(imageDir + "/card/b2fv.png");
	}

	// Launch the JavaFX app
	public static void main(String[] args) {
		launch(args);
	}

	// Nested class to represent a single playing card
	public static class Card {
		public final int cardId;

		public Card(int cardId) {
			this.cardId = cardId;
		}

		// 1–13 (Ace to King)
		public int getRank() {
			return (cardId - 1) % 13 + 1;
		}

		// Suit based on cardId
		public String getSuit() {
			switch ((cardId - 1) / 13) {
				case 0: return "Spades";
				case 1: return "Hearts";
				case 2: return "Diamonds";
				case 3: return "Clubs";
				default: return "Unknown";
			}
		}

		// Get score based on Blackjack-like rules
		public int getScore() {
			int rank = getRank();
			if (rank == 1) return 11;             // Ace
			if (rank >= 11 && rank <= 13) return 10; // Face cards
			return rank;
		}
	}
}
