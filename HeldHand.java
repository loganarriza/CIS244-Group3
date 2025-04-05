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
import javafx.stage.Stage;

import java.util.*;

public class HeldHand extends Application {

	private static final String imageDir = "file:/C:/Users/logan/Downloads/image";

	@Override
	public void start(Stage stage) {

		// name entry scene
		Label firstnameLabel = new Label("Enter Your First Name:");
		TextField firstnameField = new TextField();
		Label middleinitialLabel = new Label("Enter Your Middle Initial:");
		TextField middleinitialField = new TextField();
		Label lastnameLabel = new Label("Enter Your Last Name:");
		TextField lastnameField = new TextField();
		Label errorLabel = new Label();
		errorLabel.setStyle("-fx-text-fill: red;");

		Button startButton = new Button("Start Game");
		VBox nameLayout = new VBox(10, firstnameLabel, firstnameField,
				middleinitialLabel, middleinitialField,
				lastnameLabel, lastnameField,
				startButton, errorLabel);
		nameLayout.setAlignment(Pos.CENTER);
		nameLayout.setPadding(new Insets(15));

		Scene nameScene = new Scene(nameLayout, 300, 300);
		stage.setTitle("Midterm - Logan Arrizabalaga");
		stage.setScene(nameScene);
		stage.setResizable(false);
		stage.show();

		// card game scene elements
		HBox cardBox = new HBox(4);
		cardBox.setAlignment(Pos.CENTER);

		ImageView img1 = new ImageView(getImageCover());
		ImageView img2 = new ImageView(getImageCover());
		ImageView img3 = new ImageView(getImageCover());
		ImageView img4 = new ImageView(getImageCover());
		ImageView img5 = new ImageView(getImageCover());

		cardBox.getChildren().addAll(img1, img2, img3, img4, img5);

		Label lblGameTitle = new Label();
		lblGameTitle.setPadding(new Insets(10));
		lblGameTitle.setMaxWidth(Double.MAX_VALUE);
		lblGameTitle.setAlignment(Pos.CENTER);

		Label scoreLabel = new Label("Score: 0");
		scoreLabel.setPadding(new Insets(10));
		scoreLabel.setAlignment(Pos.CENTER);

		Button btnShow = new Button("Show");
		btnShow.setOnAction(e -> {
			// shuffle deck and draw 5 unique cards
			List<Integer> deck = new ArrayList<>();
			for (int i = 1; i <= 52; i++) {
				deck.add(i);
			}
			Collections.shuffle(deck);

			List<Integer> ids = deck.subList(0, 5); // first 5 unique cards

			img1.setImage(new Image(imageDir + "/card/" + ids.get(0) + ".png"));
			img2.setImage(new Image(imageDir + "/card/" + ids.get(1) + ".png"));
			img3.setImage(new Image(imageDir + "/card/" + ids.get(2) + ".png"));
			img4.setImage(new Image(imageDir + "/card/" + ids.get(3) + ".png"));
			img5.setImage(new Image(imageDir + "/card/" + ids.get(4) + ".png"));

			List<Card> cards = new ArrayList<>();
			for (int id : ids) {
				cards.add(new Card(id));
			}

			int totalScore = cards.stream().mapToInt(Card::getCardScore).sum() % 10;
			scoreLabel.setText("Score: " + totalScore);

			String handDescription = HandEvaluator.evaluateHand(cards);
			lblGameTitle.setText("Hand: " + handDescription);
		});

		HBox buttonBox = new HBox(btnShow);
		buttonBox.setPadding(new Insets(10));
		buttonBox.setAlignment(Pos.CENTER);

		VBox topBox = new VBox(lblGameTitle, scoreLabel);
		topBox.setAlignment(Pos.CENTER);

		BorderPane cardPane = new BorderPane();
		cardPane.setTop(topBox);
		cardPane.setCenter(cardBox);
		cardPane.setBottom(buttonBox);

		Scene cardScene = new Scene(cardPane, 600, 400);

		startButton.setOnAction(e -> {
			String name = firstnameField.getText().trim();
			if (name.isEmpty()) {
				errorLabel.setText("Please enter your name.");
			} else {
				lblGameTitle.setText("Player Name: " + name);
				stage.setScene(cardScene);
				stage.setTitle("Balatro Lite");
			}
		});
	}

	private Image getImageCover() {
		return new Image(imageDir + "/card/b2fv.png");
	}

	private int getRandomCardId() {
		return (int) (Math.random() * 52) + 1;
	}

	public static void main(String[] args) {
		launch(args);
	}

	// card class
	public static class Card {
		private final int cardId;

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

		public int getCardScore() {
			int rank = getRank();
			if (rank == 1) return 11;
			if (rank >= 11 && rank <= 13) return 10;
			return rank;
		}
	}
}
