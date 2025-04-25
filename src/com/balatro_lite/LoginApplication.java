package com.balatro_lite;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoginApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        //step1: use FXMLloader to load the fxml file
    	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
        Parent root = fxmlLoader.load();

        //step2: create the reference of the Controller for the above fxml
        //cast correctly
        LoginController logCont = (LoginController)fxmlLoader.getController();

        //step3: create scene
        Scene scene = new Scene(root);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
