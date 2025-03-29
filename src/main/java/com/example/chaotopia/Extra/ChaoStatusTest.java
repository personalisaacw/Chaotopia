package com.example.chaotopia.Extra;

import com.example.chaotopia.Model.Chao;
import com.example.chaotopia.Model.ChaoType;
import com.example.chaotopia.Model.State;
import com.example.chaotopia.Model.Status;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application class for the Chaotopia game.
 */
public class ChaoStatusTest extends Application {

    private GameplayAnimationController controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/chaotopia/View/ChaoStatusPane.fxml"));
        Parent root = loader.load();

        // Get the controller
        controller = loader.getController();

        // Create a test Chao
        Status initialStatus = new Status(100, 100, 100, 100);
        Chao testChao = new Chao(0, "Sonic Jr.", ChaoType.BLUE, State.NORMAL, initialStatus);

        // Set the Chao in the controller
        controller.setChao(testChao);

        // Set up the scene
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Chaotopia");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        // Properly shutdown timers when the application closes
        if (controller != null) {
            controller.shutdown();
        }
    }

    /**
     * Main method to launch the application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}