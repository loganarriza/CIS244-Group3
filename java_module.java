module balatro_lite {
requires javafx.controls;
requires javafx.fxml;
requires javafx.web;
requires javafx.base;
requires javafx.graphics;
requires java.sql;

opens heldhand.java to javafx.graphics, javafx.fxml; //Replace chapter15eventdemoinclass with your package name
