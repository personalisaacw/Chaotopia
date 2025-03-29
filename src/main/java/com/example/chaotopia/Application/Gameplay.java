package com.example.chaotopia.Application;

import com.example.chaotopia.Controller.BaseController;
import com.example.chaotopia.Controller.GameplayController; // Import GameplayController
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Gameplay extends Application {

    private GameplayController gameplayController; // Keep reference for shutdown

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/chaotopia/View/Gameplay.fxml"));
        Parent root = loader.load();

        // Get the controller instance AFTER loading
        gameplayController = loader.getController();
        if (gameplayController == null) {
            throw new RuntimeException("Failed to load GameplayController from Gameplay.fxml");
        }

        Scene scene = new Scene(root); // Use the loaded root
        BaseController.addCSS(scene); // Apply global CSS if needed

        // Set up shutdown hook
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Main window closing...");
            if (gameplayController != null) {
                gameplayController.shutdown(); // Call controller's cleanup
            }
            System.out.println("Shutdown complete. Exiting.");
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("ChaoTopia - Play Game");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}