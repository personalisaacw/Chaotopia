package com.example.chaotopia.Model;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Manages animation sequences for Fruits in the Chaotopia application.
 * This class handles the automatic cycling through a series of sprite images
 * to create smooth animation effects for fruits.
 *
 * <p>The animation cycles through frames at a configurable speed, displaying each frame
 * before advancing to the next one.</p>
 */
public class FruitAnimation {

    /** The ImageView component that will display the animated fruit */
    private ImageView fruitView;

    /** Tracks the current frame number in the animation sequence */
    private int currentFrame = 1;

    /** The total number of frames in the animation sequence */
    private final int totalFrames = 6; // All fruits have 6 frames (6th is blank)

    /**
     * Gets the total number of frames in the animation.
     *
     * @return The total frame count
     */
    public int getTotalFrames() {
        return totalFrames;
    }

    /** The type of Fruit being animated */
    private FruitType fruitType;

    /** The Timeline that controls the animation sequence */
    private Timeline timeline;

    /** The animation speed in seconds per frame */
    private double frameSpeed = 0.2;

    /**
     * Constructs a new FruitAnimation instance.
     * Initializes the animation timeline but does not start it automatically.
     *
     * @param fruitView The ImageView component that will display the animated fruit
     * @param fruitType The type of Fruit being animated
     */
    public FruitAnimation(ImageView fruitView, FruitType fruitType) {
        this.fruitView = fruitView;
        this.fruitType = fruitType;

        // Initialize the timeline with the appropriate frame update logic
        initializeTimeline();

        // No scaling needed
    }

    /**
     * Initializes the animation timeline.
     */
    private void initializeTimeline() {
        this.timeline = new Timeline(new KeyFrame(Duration.seconds(frameSpeed), event -> updateFrame()));
        timeline.setCycleCount(totalFrames); // Only run through all frames once
    }

    /**
     * Updates the frame displayed in the ImageView.
     * This method is called by the timeline at regular intervals.
     */
    private void updateFrame() {
        try {
            // For the 6th frame, display nothing (clear the image)
            if (currentFrame == 6) {
                fruitView.setImage(null);
            } else {
                // Generate the path to the current frame's image using a folder structure
                // Format: /fruits/FRUITTYPE/frame1.png
                String imagePath = String.format("/com/example/chaotopia/fruits/%s/frame%d.png",
                        fruitType.getResourceName(),
                        currentFrame);

                // Load the image
                Image frameImage = new Image(getClass().getResourceAsStream(imagePath));

                // Update the image view
                fruitView.setImage(frameImage);
            }

            // Move to the next frame
            currentFrame++;
        } catch (Exception e) {
            System.err.println("Error loading fruit animation frame: " + e.getMessage());
            System.err.println("Attempted path: " +
                    "/com/example/chaotopia/fruits/" +
                    fruitType.getResourceName() + "/frame" +
                    currentFrame + ".png");
            e.printStackTrace();
        }
    }

    /**
     * Starts the animation.
     * The animation will cycle through all frames repeatedly until stopped.
     * If the animation is already running, calling this method has no effect.
     */
    public void startAnimation() {
        timeline.play();
    }

    /**
     * Stops the animation.
     * The current frame will remain displayed in the ImageView.
     * If the animation is already stopped, calling this method has no effect.
     */
    public void stopAnimation() {
        timeline.stop();
    }

    /**
     * Changes the Fruit type being animated.
     * This allows switching between different Fruit types without creating a new FruitAnimation instance.
     *
     * @param fruitType The new Fruit type
     */
    public void changeFruitType(FruitType fruitType) {
        // Stop the current animation
        timeline.stop();

        // Update the Fruit type
        this.fruitType = fruitType;
        this.currentFrame = 1;

        // Restart the animation
        timeline.play();
    }

    /**
     * Changes the animation speed.
     *
     * @param frameSpeed The new speed in seconds per frame
     */
    public void setAnimationSpeed(double frameSpeed) {
        this.frameSpeed = frameSpeed;

        // Recreate the timeline with the new speed
        boolean wasPlaying = timeline.getStatus() == Animation.Status.RUNNING;
        timeline.stop();
        initializeTimeline();

        // Resume the animation if it was playing
        if (wasPlaying) {
            timeline.play();
        }
    }

    /**
     * Gets the current Fruit type.
     *
     * @return The current Fruit type
     */
    public FruitType getFruitType() {
        return fruitType;
    }


}