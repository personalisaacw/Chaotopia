package com.example.chaotopia;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class HelloController {

    @FXML
    private ImageView characterImageView;

    @FXML
    private HBox controlButtons; // Optional: You can add this to your FXML for UI controls

    private ChaoAnimation chaoAnimation;
    private ChaoType currentChaoType = ChaoType.DARK;
    private State currentState = State.NORMAL;

    @FXML
    public void initialize() {
        // Set initial image
        characterImageView.setImage(new Image(getClass().getResourceAsStream("/com/example/chaotopia/sprites/DARK/NORMAL1.png")));

        // Create animation instance with DARK Chao and NORMAL state
        chaoAnimation = new ChaoAnimation(characterImageView, ChaoType.DARK, AnimationState.NORMAL);

        // Start the animation
        chaoAnimation.startAnimation();

        // Optional: Create UI controls for testing different states
        setupControls();
    }

    /**
     * Sets up UI controls for testing different animations.
     * This is optional and can be removed if not needed.
     */
    private void setupControls() {
        // You can add these buttons to your FXML
        if (controlButtons != null) {
            // Add state buttons
            for (State state : State.values()) {
                Button stateButton = new Button(state.name());
                stateButton.setOnAction(e -> changeState(state));
                controlButtons.getChildren().add(stateButton);
            }

            // Add Chao type buttons
            for (ChaoType type : ChaoType.values()) {
                Button typeButton = new Button(type.name());
                typeButton.setOnAction(e -> changeChaoType(type));
                controlButtons.getChildren().add(typeButton);
            }
        }
    }

    /**
     * Changes the Chao's state and updates the animation.
     */
    public void changeState(State state) {
        currentState = state;
        chaoAnimation.changeState(state);
    }

    /**
     * Changes the Chao type and updates the animation.
     */
    public void changeChaoType(ChaoType type) {
        currentChaoType = type;
        chaoAnimation.changeChaoType(type);
    }
}