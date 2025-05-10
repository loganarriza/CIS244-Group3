package com.balatro_lite;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class HomepageController {

    @FXML private Pane pane;
    @FXML private ImageView imgMenu;

    @FXML private Button btnPlay;
    @FXML private Button btnQuit;
    @FXML private Button btnCollection;
    @FXML private Button btnRegister;

    @FXML
    public void initialize() {
        URL url = getClass().getResource("/image/BalaMenu.png");
        if (url == null) {
            System.out.println("❌ Could not find /image/BalaMenu.png");
        } else {
        	Image img = new Image(getClass().getResource("/image/BalaMenu.png").toExternalForm());
            imgMenu.setImage(img);
            System.out.println("✅ Image loaded successfully.");
        }

        // Bind background to full window
        imgMenu.fitWidthProperty().bind(pane.widthProperty());
        imgMenu.fitHeightProperty().bind(pane.heightProperty());
    }


    @FXML
    private void handlePlay(ActionEvent event) {
        try {
        	FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
        	Parent root = loader.load();

        	MainViewController controller = loader.getController();

        	Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        	Scene scene = new Scene(root); // ✅ ← make sure this is the same "root"
            scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());

            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
            
            controller.showSmallBlindScene(); // Load initial screen arrangement

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleQuit() {
        System.out.println("👋 Quit clicked. Exiting...");
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCollection() {
        System.out.println("📚 Collection clicked.");
        // TODO: Load collection scene
    }

    @FXML
    private void handleRegister() {
        System.out.println("🔄 Register clicked.");
        // TODO: Registration logic
    }
}
