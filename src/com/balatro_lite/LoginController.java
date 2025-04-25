package com.balatro_lite;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.scene.Node;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import javafx.event.ActionEvent;

public class LoginController {

    @FXML private TextField tfUsername;
    @FXML private PasswordField pfPassword;
    @FXML private Button btnLogin;
    @FXML private Button btnRegister;
    @FXML private Label loginErrorLabel;

    private HeldHand mainApp;

    public void setMainApp(HeldHand mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String user = tfUsername.getText().trim();
        String pass = pfPassword.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            loginErrorLabel.setText("Please fill in all fields.");
            return;
        }

        if (DBUtil.validateUser(user, pass)) {
            try {
                // Load the new FXML file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/homepage-view.fxml"));
                Parent root = loader.load();

                // Optionally: pass data to new controller
                HomepageController controller = loader.getController();
                controller.setUser(user);

                // Get current stage from the event
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // Set the new scene
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                loginErrorLabel.setText("Failed to load the homepage.");
            }

        } else {
            loginErrorLabel.setText("Invalid credentials.");
            loginErrorLabel.setStyle("-fx-text-fill: red;");
        }
    }


    @FXML
    private void handleRegister(ActionEvent event) {
        String user = tfUsername.getText().trim();
        String pass = pfPassword.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            loginErrorLabel.setText("Please fill in all fields to register.");
            return;
        }

        if (DBUtil.registerUser(user, pass)) {
            loginErrorLabel.setText("✅ Registered! Please log in.");
            loginErrorLabel.setStyle("-fx-text-fill: green;");
        } else {
            loginErrorLabel.setText("⚠️ Username already exists.");
            loginErrorLabel.setStyle("-fx-text-fill: red;");
        }
        
    }

}
