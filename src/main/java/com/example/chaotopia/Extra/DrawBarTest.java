package com.example.chaotopia.Extra;

import com.example.chaotopia.Model.DrawBar;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Test class for the DrawBar component.
 * This class allows manual testing of the status bars.
 */
public class DrawBarTest extends Application {

    private DrawBar happinessBar;
    private DrawBar healthBar;
    private DrawBar fullnessBar;
    private DrawBar sleepBar;

    @Override
    public void start(Stage primaryStage) {
        // Create the DrawBar instances
        happinessBar = new DrawBar(200, 30, 100, "Happiness", Color.YELLOW);
        healthBar = new DrawBar(200, 30, 100, "Health", Color.RED);
        fullnessBar = new DrawBar(200, 30, 80, "Fullness", Color.GREEN);
        sleepBar = new DrawBar(200, 30, 90, "Sleep", Color.BLUE);

        // Create buttons to test increasing and decreasing values
        Button decreaseAllButton = new Button("Decrease All (-10)");
        decreaseAllButton.setOnAction(e -> {
            happinessBar.updateValue(happinessBar.getValue() - 10);
            healthBar.updateValue(healthBar.getValue() - 10);
            fullnessBar.updateValue(fullnessBar.getValue() - 10);
            sleepBar.updateValue(sleepBar.getValue() - 10);
        });

        Button increaseAllButton = new Button("Increase All (+10)");
        increaseAllButton.setOnAction(e -> {
            happinessBar.updateValue(happinessBar.getValue() + 10);
            healthBar.updateValue(healthBar.getValue() + 10);
            fullnessBar.updateValue(fullnessBar.getValue() + 10);
            sleepBar.updateValue(sleepBar.getValue() + 10);
        });

        // Create buttons for individual stat adjustments
        Button feedButton = new Button("Feed (+15 Fullness)");
        feedButton.setOnAction(e -> fullnessBar.updateValue(fullnessBar.getValue() + 15));

        Button playButton = new Button("Play (+20 Happiness, -5 Others)");
        playButton.setOnAction(e -> {
            happinessBar.updateValue(happinessBar.getValue() + 20);
            fullnessBar.updateValue(fullnessBar.getValue() - 5);
            sleepBar.updateValue(sleepBar.getValue() - 5);
        });

        Button sleepButton = new Button("Sleep (+25 Sleep, +5 Health)");
        sleepButton.setOnAction(e -> {
            sleepBar.updateValue(sleepBar.getValue() + 25);
            healthBar.updateValue(healthBar.getValue() + 5);
        });

        // Layout the components
        VBox barsContainer = new VBox(10,
                new Label("Status Bars Test"),
                happinessBar,
                healthBar,
                fullnessBar,
                sleepBar
        );
        barsContainer.setPadding(new Insets(20));

        HBox buttonContainer = new HBox(10,
                decreaseAllButton,
                increaseAllButton,
                feedButton,
                playButton,
                sleepButton
        );
        buttonContainer.setPadding(new Insets(10));

        VBox root = new VBox(20, barsContainer, buttonContainer);
        root.setStyle("-fx-background-color: #e8d0aa;");

        // Create and show the scene
        Scene scene = new Scene(root, 600, 300);
        primaryStage.setTitle("DrawBar Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Main method to launch the test application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}