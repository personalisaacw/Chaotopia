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

/**
 * The main application class for the Chaotopia game.
 * This class initializes and starts the JavaFX application, loading the main menu scene.
 */
public class MainMenu extends Application {
    /**
     * Starts the JavaFX application, loading the main menu scene and setting up the primary stage.
     *
     * @param primaryStage The primary stage for this application, onto which the application scene can be set.
     * Applications may create other stages, if needed, but they will not be primary stages.
     * @throws Exception If an error occurs during the loading of the FXML or setting up the stage.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/chaotopia/View/MainMenu.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        BaseController.addCSS(scene);

        try {
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/chaotopia/Assets/Icon/ChaoIcon.png")));
            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            System.out.println("Couldn't load window icon: " + e.getMessage());
        }

        primaryStage.setScene(scene);
        primaryStage.setTitle("Chaotopia");
        primaryStage.show();
    }

    /**
     * The main entry point for the Chaotopia game application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}