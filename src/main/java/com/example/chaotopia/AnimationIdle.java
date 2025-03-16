package com.example.chaotopia;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

//make this more general and that you can pass in the name of the sprite you want
/**
 * Manages an idle animation sequence for a character in the Chaotopia application.
 * This class handles the automatic cycling through a series of sprite images
 * to create a smooth animation effect for a character when it's in an idle state.
 *
 * <p>The animation cycles through 4 frames by default, displaying each frame for
 * 0.2 seconds before advancing to the next one.</p>
 *
 */
public class AnimationIdle {

    /** The ImageView component that will display the animated character */
    private ImageView characterView;

    /** Tracks the current frame number in the animation sequence */
    private int currentFrame = 1;

    /** The total number of frames in the animation sequence */
    private static final int TOTAL_FRAMES = 4;

    /**
     * The Timeline that controls the animation sequence.
     * This timeline updates the ImageView with a new sprite image every 0.2 seconds.
     * The animation cycles indefinitely until explicitly stopped.
     */
    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.2), event -> {
        // Update the image based on the current frame
        String imagePath = "/com/example/chaotopia/sprites/DarkChaoIdle" + currentFrame + ".png";
        characterView.setImage(new Image(getClass().getResourceAsStream(imagePath)));

        // Move to the next frame
        currentFrame = (currentFrame % TOTAL_FRAMES) + 1;
    }));

    /**
     * Constructs a new AnimationIdle instance.
     * Initializes the animation timeline but does not start it automatically.
     *
     * @param characterView The ImageView component that will display the animated character.
     *                     This component will have its image updated with each frame of the animation.
     */
    public AnimationIdle(ImageView characterView) {
        this.characterView = characterView;
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    /**
     * Starts the idle animation.
     * The animation will cycle through all frames repeatedly until stopped.
     * If the animation is already running, calling this method has no effect.
     */
    public void startAnimation() {
        timeline.play();
    }

    /**
     * Stops the idle animation.
     * The current frame will remain displayed in the ImageView.
     * If the animation is already stopped, calling this method has no effect.
     */
    public void stopAnimation() {
        timeline.stop();
    }
}