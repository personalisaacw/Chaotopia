package com.example.chaotopia.Application;

import com.example.chaotopia.Controller.BaseController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class MainMenu extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/chaotopia/View/MainMenu.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        BaseController.addCSS(scene);

        try {
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/chaotopia/Assets/Icon/ChaoIcon.png")));            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            System.out.println("Couldn't load window icon: " + e.getMessage());
        }

        primaryStage.setScene(scene);
        primaryStage.setTitle("ChaoTopia");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}