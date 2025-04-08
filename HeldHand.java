import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.util.*;

public class HeldHand extends Application {

	private static final String imageDir = "file:/C:/Users/logan/Downloads/image";
	private List<Integer> deck = new ArrayList<>();
	private int totalScore = 0;
	private final Set<ImageView> selectedImages = new HashSet<>();
	private final List<Integer> currentCardIds = new ArrayList<>();

	private int discardsRemaining = 10;
	private int handsRemaining = 4;
	private int currentRound = 1;
	private int blindScore = 300;

	private Label scoreLabel;
	private Label turnsLabel;
	private Label blindLabel;
	private TextFlow lblGameTitle;
	private Button btnEvaluate;
	private Button btnDiscard;
	private Button btnShow;
	private ImageView[] imageViews;
	private HBox cardBox;

	@Override
	public void start(Stage stage) {
		Label firstnameLabel = new Label("Enter Your First Name:");
		TextField firstnameField = new TextField();
		Label errorLabel = new Label();
		errorLabel.setStyle("-fx-text-fill: red;");

		Button startButton = new Button("Start Game");
		VBox nameLayout = new VBox(10, firstnameLabel, firstnameField, startButton, errorLabel);
		nameLayout.setAlignment(Pos.CENTER);
		nameLayout.setPadding(new Insets(15));

		Scene nameScene = new Scene(nameLayout, 300, 300);
		stage.setTitle("Balatro Lite");
		stage.setScene(nameScene);
		stage.setResizable(false);
		stage.show();

		scoreLabel = new Label("Score: 0");
		scoreLabel.setPadding(new Insets(10));
		scoreLabel.setAlignment(Pos.CENTER);

		turnsLabel = new Label("Hands Left: 4 | Discards Left: 3");
		turnsLabel.setPadding(new Insets(5));
		turnsLabel.setAlignment(Pos.CENTER);

		blindLabel = new Label("Blind: 300");
		blindLabel.setPadding(new Insets(5));
		blindLabel.setAlignment(Pos.CENTER);

		lblGameTitle = new TextFlow();
		lblGameTitle.setPadding(new Insets(10));
		lblGameTitle.setMaxWidth(Double.MAX_VALUE);
		lblGameTitle.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
		lblGameTitle.setStyle("-fx-font-size: 14pt;");

		cardBox = new HBox(4);
		cardBox.setAlignment(Pos.CENTER);

		imageViews = new ImageView[8];
		for (int i = 0; i < imageViews.length; i++) {
			imageViews[i] = new ImageView(getImageCover());
			imageViews[i].setFitWidth(72);
			imageViews[i].setFitHeight(96);
			cardBox.getChildren().add(imageViews[i]);

			ImageView img = imageViews[i];
			img.setOnMouseClicked(e -> {
				if (selectedImages.contains(img)) {
					selectedImages.remove(img);
					img.setStyle("");
					img.setTranslateY(0);
				} else {
					if (selectedImages.size() < 5) {
						selectedImages.add(img);
						img.setStyle("-fx-effect: dropshadow(gaussian, black, 10, 0.5, 0, 0);");
						img.setTranslateY(-20);
					}
				}
				evaluateCurrentSelection(lblGameTitle, currentCardIds, cardBox);
			});
		}

		btnShow = new Button("Show");
		btnEvaluate = new Button("Evaluate");
		btnDiscard = new Button("Discard");

		btnShow.setOnAction(e -> {
			if (deck.size() < 8) {
				lblGameTitle.getChildren().setAll(new Text("Deck is out of cards!"));
				btnShow.setDisable(true);
				return;
			}
			currentCardIds.clear();
			List<Integer> ids = new ArrayList<>(deck.subList(0, 8));
			deck.subList(0, 8).clear();
			currentCardIds.addAll(ids);

			for (int i = 0; i < imageViews.length; i++) {
				imageViews[i].setImage(new Image(imageDir + "/card/" + ids.get(i) + ".png"));
				imageViews[i].setStyle("");
				imageViews[i].setTranslateY(0);
			}
			selectedImages.clear();
		});

		btnEvaluate.setOnAction(e -> {
			if (selectedImages.isEmpty()) {
				lblGameTitle.getChildren().setAll(new Text("Please select cards to play."));
				return;
			}
			if (handsRemaining <= 0) {
				lblGameTitle.getChildren().setAll(new Text("No hands remaining!"));
				btnEvaluate.setDisable(true);
				return;
			}
			if (deck.size() < 8) {
				lblGameTitle.getChildren().setAll(new Text("Deck is out of cards!"));
				btnEvaluate.setDisable(true);
				return;
			}
			List<Card> selectedCards = new ArrayList<>();
			List<ImageView> toReplace = new ArrayList<>();
			for (ImageView img : selectedImages) {
				int index = cardBox.getChildren().indexOf(img);
				int cardId = currentCardIds.get(index);
				selectedCards.add(new Card(cardId));
				toReplace.add(img);
			}
			HandEvaluator.HandResult result = HandEvaluator.evaluateHandDetailed(selectedCards);
			totalScore += result.totalScore;
			scoreLabel.setText("Total Score: " + totalScore);
			displayHandResult(lblGameTitle, result);

			for (ImageView img : toReplace) {
				int index = cardBox.getChildren().indexOf(img);
				int newCardId = deck.remove(0);
				currentCardIds.set(index, newCardId);
				img.setImage(new Image(imageDir + "/card/" + newCardId + ".png"));
				img.setStyle("");
				img.setTranslateY(0);
			}

			selectedImages.clear();
			handsRemaining--;
			turnsLabel.setText("Hands Left: " + handsRemaining + " | Discards Left: " + discardsRemaining);
			if (handsRemaining == 0) btnEvaluate.setDisable(true);

			if (totalScore >= blindScore) {
				lblGameTitle.getChildren().setAll(new Text("Blind beaten! Proceeding to next round..."));
				nextRound();
			} else if (handsRemaining == 0 && discardsRemaining == 0) {
				lblGameTitle.getChildren().setAll(new Text("You failed to beat the blind. Game Over."));
				btnEvaluate.setDisable(true);
				btnDiscard.setDisable(true);
				btnShow.setDisable(true);
			}
		});

		btnDiscard.setOnAction(e -> {
			if (selectedImages.isEmpty()) {
				lblGameTitle.getChildren().setAll(new Text("Select cards to discard."));
				return;
			}
			if (discardsRemaining <= 0) {
				lblGameTitle.getChildren().setAll(new Text("No discards remaining!"));
				btnDiscard.setDisable(true);
				return;
			}
			if (deck.size() < 8) {
				lblGameTitle.getChildren().setAll(new Text("Deck is out of cards!"));
				btnDiscard.setDisable(true);
				return;
			}
			for (ImageView img : new ArrayList<>(selectedImages)) {
				int index = cardBox.getChildren().indexOf(img);
				int newCardId = deck.remove(0);
				currentCardIds.set(index, newCardId);
				img.setImage(new Image(imageDir + "/card/" + newCardId + ".png"));
				img.setStyle("");
				img.setTranslateY(0);
			}
			selectedImages.clear();
			discardsRemaining--;
			turnsLabel.setText("Hands Left: " + handsRemaining + " | Discards Left: " + discardsRemaining);
			if (discardsRemaining == 0) btnDiscard.setDisable(true);

			if (handsRemaining == 0 && discardsRemaining == 0 && totalScore < blindScore) {
				lblGameTitle.getChildren().setAll(new Text("You failed to beat the blind. Game Over."));
				btnEvaluate.setDisable(true);
				btnDiscard.setDisable(true);
				btnShow.setDisable(true);
			}
		});

		HBox buttonBox = new HBox(10, btnShow, btnEvaluate, btnDiscard);
		buttonBox.setPadding(new Insets(10));
		buttonBox.setAlignment(Pos.CENTER);

		VBox topBox = new VBox(lblGameTitle, scoreLabel, blindLabel, turnsLabel);
		topBox.setAlignment(Pos.CENTER);

		BorderPane cardPane = new BorderPane();
		cardPane.setTop(topBox);
		cardPane.setCenter(cardBox);
		cardPane.setBottom(buttonBox);

		Scene cardScene = new Scene(cardPane, 800, 400);

		startButton.setOnAction(e -> {
			String name = firstnameField.getText().trim();
			if (name.isEmpty()) {
				errorLabel.setText("Please enter your name.");
			} else {
				lblGameTitle.getChildren().setAll(new Text("Player Name: " + name));
				resetDeck();
				stage.setScene(cardScene);
				stage.setTitle("Balatro Lite");
			}
		});
	}

	private void resetDeck() {
		deck.clear();
		for (int i = 1; i <= 52; i++) {
			deck.add(i);
		}
		Collections.shuffle(deck);
		btnShow.fire(); // auto-deal
	}

	private void nextRound() {
		currentRound++;
		totalScore = 0;
		discardsRemaining = 3;
		handsRemaining = 4;
		blindScore = 300 + (currentRound - 1) * 150;

		scoreLabel.setText("Total Score: " + totalScore);
		turnsLabel.setText("Hands Left: " + handsRemaining + " | Discards Left: " + discardsRemaining);
		blindLabel.setText("Blind: " + blindScore);
		btnEvaluate.setDisable(false);
		btnDiscard.setDisable(false);
		btnShow.setDisable(false);

		resetDeck();
		lblGameTitle.getChildren().setAll(new Text("Round " + currentRound + " started. Beat the blind!"));
	}

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

	private Image getImageCover() {
		return new Image(imageDir + "/card/b2fv.png");
	}

	public static void main(String[] args) {
		launch(args);
	}

	public static class Card {
		public final int cardId;

		public Card(int cardId) {
			this.cardId = cardId;
		}

		public int getRank() {
			return (cardId - 1) % 13 + 1;
		}

		public String getSuit() {
			switch ((cardId - 1) / 13) {
				case 0: return "Spades";
				case 1: return "Hearts";
				case 2: return "Diamonds";
				case 3: return "Clubs";
				default: return "Unknown";
			}
		}

		public int getScore() {
			int rank = getRank();
			if (rank == 1) return 11;
			if (rank >= 11 && rank <= 13) return 10;
			return rank;
		}
	}
}
