package BalatroLite;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class HeldHand extends Application {
    private static final int CARD_COUNT = 8;
    private static final int TOTAL_CARDS = 52;
    private final Random random = new Random();
    private final HBox cardPane = new HBox(10);

    @Override
    public void start(Stage primaryStage) {
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> showRandomCards());

        cardPane.setPadding(new Insets(15));
        BorderPane pane = new BorderPane();
        pane.setCenter(cardPane);
        pane.setBottom(refreshButton);
        BorderPane.setMargin(refreshButton, new Insets(15));

        showRandomCards();

        Scene scene = new Scene(pane, 600, 250);
        primaryStage.setTitle("Pick Eight Cards");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showRandomCards() {
        cardPane.getChildren().clear();
        Set<Integer> cardNumbers = new HashSet<>();
        while (cardNumbers.size() < CARD_COUNT) {
            cardNumbers.add(random.nextInt(TOTAL_CARDS) + 1);
        }
        
        for (int number : cardNumbers) {
            // update the file path: note the trailing slash and file naming
            String imagePath = "file:///Users/loganarrizabalaga/Desktop/card/" + number + ".png";
            Image cardImage = new Image(imagePath, false);
            
            if (cardImage.isError()) {
                System.err.println("Failed to load image: " + imagePath);
                System.err.println("Error: " + cardImage.getException());
            }
            
            ImageView imageView = new ImageView(cardImage);
            cardPane.getChildren().add(imageView);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
