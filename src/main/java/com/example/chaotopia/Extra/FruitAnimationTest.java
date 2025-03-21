package com.example.chaotopia.Extra;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main application class for testing the Fruit Animation functionality.
 */
public class FruitAnimationTest extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Load the FXML file from resources/com/example/chaotopia/View/FruitAnimation.fxml
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/chaotopia/View/FruitAnimation.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);

        // Set up the stage
        stage.setTitle("Fruit Animation Tester");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Launch the application
        launch();
    }
}