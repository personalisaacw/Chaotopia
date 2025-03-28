package com.example.chaotopia.Controller;

import com.example.chaotopia.Model.FruitAnimation;
import com.example.chaotopia.Model.FruitType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

/**
 * Controller for the Fruit Animation testing screen.
 * This controller allows the user to select different fruit types and start/stop their animations.
 */
public class FruitAnimationController {

    @FXML
    private ImageView fruitImageView;

    @FXML
    private ComboBox<FruitType> fruitTypeComboBox;

    @FXML
    private Button startButton;

    @FXML
    private Button stopButton;

    @FXML
    private Label statusLabel;

    // The animation controller
    private FruitAnimation fruitAnimation;

    /**
     * Initializes the controller.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        // Populate the fruit type combo box
        fruitTypeComboBox.getItems().addAll(FruitType.values());
        fruitTypeComboBox.setValue(FruitType.RED); // Default selection

        // Create the fruit animation with the default fruit type
        fruitAnimation = new FruitAnimation(fruitImageView, fruitTypeComboBox.getValue());

        // Listen for changes to the combo box
        fruitTypeComboBox.setOnAction(event -> changeFruitType());

        // Initially disable the stop button
        stopButton.setDisable(true);
    }

    /**
     * Handles the "Start Animation" button click.
     */
    @FXML
    private void handleStartAnimation() {
        fruitAnimation.startAnimation();
        statusLabel.setText("Animating: " + fruitTypeComboBox.getValue().name() + " fruit");
        startButton.setDisable(true);
        stopButton.setDisable(false);
    }

    /**
     * Handles the "Stop Animation" button click.
     */
    @FXML
    private void handleStopAnimation() {
        fruitAnimation.stopAnimation();
        statusLabel.setText("Animation stopped. Select a fruit type and press Start");
        startButton.setDisable(false);
        stopButton.setDisable(true);
    }

    /**
     * Changes the fruit type being animated.
     */
    private void changeFruitType() {
        FruitType selectedType = fruitTypeComboBox.getValue();

        if (fruitAnimation != null) {
            // If animation is running, update it with the new fruit type
            boolean wasRunning = !startButton.isDisabled();

            if (wasRunning) {
                fruitAnimation.stopAnimation();
            }

            fruitAnimation.changeFruitType(selectedType);

            if (wasRunning) {
                fruitAnimation.startAnimation();
                statusLabel.setText("Animating: " + selectedType.name() + " fruit");
            } else {
                statusLabel.setText("Selected: " + selectedType.name() + " fruit. Press Start to animate");
            }
        }
    }
}