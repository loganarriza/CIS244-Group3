package com.balatro_lite;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

public class LoginController {

	@FXML
    private AnchorPane anchorpane;
    @FXML
    private Button btnLogin;
    @FXML
    private Button btnRegister;
    @FXML
    private HBox hboxBtn;
    @FXML
    private ImageView imgBalatroLogo;
    @FXML
    private Label loginErrorLabel;
    @FXML
    private PasswordField pfPassword;

    @FXML
    private TextField tfUsername;
	
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
				// Load FXML
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/homepage-view.fxml"));
				Parent root = loader.load();

				// Get the controller
				HomepageController controller = loader.getController();

				// Switch scenes
				Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				Scene scene = new Scene(root);
				stage.setMaximized(true);
				scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
				
				// Then show it
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
	private void handleKeyPress(KeyEvent event) {
	    if (event.getCode() == KeyCode.ENTER) {
	        btnLogin.fire(); // ✅ triggers the real action from button
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
