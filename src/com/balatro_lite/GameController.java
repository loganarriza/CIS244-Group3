// GameController.java
package com.balatro_lite;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextFlow;

public class GameController {
    @FXML private Label scoreLabel;
    @FXML private Label turnsLabel;
    @FXML private Label blindLabel;
    @FXML private TextFlow lblGameTitle;
    @FXML private Button btnEvaluate;
    @FXML private Button btnDiscard;
    @FXML private Button btnShow;
    @FXML private HBox cardBox;

    // Add any methods you want to bind to buttons here
    @FXML
    private void onShowPressed() {
        System.out.println("Show clicked!");
        // Call HeldHand.dealNewHand() or refactor logic into this class
    }

    @FXML
    private void onEvaluatePressed() {
        System.out.println("Evaluate clicked!");
    }

    @FXML
    private void onDiscardPressed() {
        System.out.println("Discard clicked!");
    }
}
