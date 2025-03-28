package com.example.chaotopia.Model;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;


public class FruitAnimation {


    private ImageView fruitView;
    private int currentFrame = 1;
    private final int totalFrames = 6;
    private FruitType fruitType;
    private Timeline timeline;
    private double frameSpeed = 0.2; // 0.2 seconds per frame

    /**
     * Constructs a new FruitAnimation instance.
     * Initializes the animation timeline but does not start it automatically.
     *
     * @param fruitView The ImageView component that will display the animated fruit
     * @param fruitType The type of Fruit being animated
     */
    public FruitAnimation(ImageView fruitView, FruitType fruitType) {
        if (fruitView == null) {
            throw new IllegalArgumentException("ImageView cannot be null for FruitAnimation");
        }
        this.fruitView = fruitView;
        this.fruitType = fruitType;
        initializeTimeline();
    }

    /**
     * Initializes the animation timeline and adds the onFinished handler.
     */
    private void initializeTimeline() {
        // Reset frame count for initialization/re-initialization
        this.currentFrame = 1;

        this.timeline = new Timeline(new KeyFrame(Duration.seconds(frameSpeed), event -> updateFrame()));
        timeline.setCycleCount(totalFrames); // Run exactly 6 times (for frames 1-6)


        timeline.setOnFinished(event -> {
            Platform.runLater(() -> {
                if (fruitView != null) {
                    fruitView.setVisible(false);
                    fruitView.setImage(null);
                }
                this.currentFrame = 1;
            });
        });

    }

    /**
     * Updates the frame displayed in the ImageView.
     * This method is called by the timeline at regular intervals.
     */
    private void updateFrame() {
        // Check if fruitView is still valid (might be nullified if disposed elsewhere)
        if (fruitView == null) {
            if(timeline != null) timeline.stop(); // Stop if view is gone
            return;
        }
        try {
            // Frame 6 is blank
            if (currentFrame == 6) {
                fruitView.setImage(null);
            } else {
                String imagePath = String.format("/com/example/chaotopia/fruits/%s/frame%d.png",
                        fruitType.getResourceName(),
                        currentFrame);
                Image frameImage = new Image(getClass().getResourceAsStream(imagePath));
                fruitView.setImage(frameImage);
            }
            currentFrame++; // Increment frame *after* using it
        } catch (NullPointerException npe) {
            System.err.println("Error loading fruit animation frame: Resource not found. Check path and image existence.");
            System.err.println("Attempted path: " +
                    "/com/example/chaotopia/fruits/" +
                    (fruitType != null ? fruitType.getResourceName() : "null") + "/frame" +
                    (currentFrame) + ".png"); // Use currentFrame before increment
            if(timeline != null) timeline.stop(); // Stop animation on error
        } catch (Exception e) {
            System.err.println("Error loading fruit animation frame: " + e.getMessage());
            e.printStackTrace();
            if(timeline != null) timeline.stop(); // Stop animation on error
        }
    }

    /**
     * Starts the animation from the beginning.
     * The animation will play once through all frames.
     * If the animation is already running, it might restart or behave unexpectedly depending on FX version,
     * it's safer to ensure it's stopped first if needed, but changeFruitType handles this.
     */
    public void startAnimation() {
        if (timeline != null && fruitView != null) {
            // Ensure starting from frame 1 and view is visible
            currentFrame = 1;
            fruitView.setVisible(true);
            timeline.playFromStart(); // Use playFromStart to ensure it begins at frame 1
        }
    }

    /**
     * Stops the animation immediately.
     * The current frame might remain displayed. Consider hiding manually if needed.
     */
    public void stopAnimation() {
        if (timeline != null) {
            timeline.stop();
        }
        // Optionally hide the view immediately when stopped externally
        // if (fruitView != null) {
        //     Platform.runLater(() -> fruitView.setVisible(false));
        // }
    }


    /**
     * Changes the Fruit type being animated and starts the animation from the beginning.
     *
     * @param newFruitType The new Fruit type
     */
    public void changeFruitType(FruitType newFruitType) {
        if (timeline == null || fruitView == null) return; // Safety check

        timeline.stop(); // Stop any current animation

        this.fruitType = newFruitType;
        this.currentFrame = 1; // Reset frame count

        fruitView.setVisible(true);
        timeline.playFromStart();
    }

    /**
     * Gets the total duration of one animation cycle.
     * @return Duration object
     */
    public Duration getAnimationDuration() {
        return Duration.seconds(totalFrames * frameSpeed);
    }
}