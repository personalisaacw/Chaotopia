package com.example.chaotopia;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public class HelloController {

    @FXML
    private ImageView characterImageView;

    private AnimationIdle animationIdle;

    @FXML
    public void initialize() {
        // Set initial image
        characterImageView.setImage(new Image(getClass().getResourceAsStream("/com/example/chaotopia/sprites/DarkChaoIdle2.png")));

        // Create animation instance
        animationIdle = new AnimationIdle(characterImageView);

        // Start the animation
        animationIdle.startAnimation();
    }
}