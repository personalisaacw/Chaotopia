package com.example.chaotopia;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Manages animation sequences for Chao characters in the Chaotopia application.
 * This class handles the automatic cycling through a series of sprite images
 * to create smooth animation effects for characters in various states.
 *
 * <p>The animation cycles through frames at a configurable speed, displaying each frame
 * before advancing to the next one.</p>
 */
public class ChaoAnimation {

    /** The ImageView component that will display the animated character */
    private ImageView characterView;

    /** Tracks the current frame number in the animation sequence */
    private int currentFrame = 1;

    /** The total number of frames in the animation sequence */
    private int totalFrames;

    /** The type of Chao being animated */
    private ChaoType chaoType;

    /** The type of animation being performed */
    private AnimationState animationState;

    /** The Timeline that controls the animation sequence */
    private Timeline timeline;

    /** The animation speed in seconds per frame */
    private double frameSpeed = 0.2;

    /**
     * Constructs a new ChaoAnimation instance.
     * Initializes the animation timeline but does not start it automatically.
     *
     * @param characterView The ImageView component that will display the animated character
     * @param chaoType The type of Chao being animated
     * @param animationState The type of animation to perform
     */
    public ChaoAnimation(ImageView characterView, ChaoType chaoType, AnimationState animationState) {
        this.characterView = characterView;
        this.chaoType = chaoType;
        this.animationState = animationState;
        this.totalFrames = animationState.getFrameCount();

        // Initialize the timeline with the appropriate frame update logic
        initializeTimeline();

        //Apply scaling for Hero and Dark Chao
        applyChaoTypeScaling();
    }

    /**
     * Constructs a new ChaoAnimation instance with a custom frame count.
     * Use this constructor when the animation has a non-standard number of frames.
     *
     * @param characterView The ImageView component that will display the animated character
     * @param chaoType The type of Chao being animated
     * @param animationState The type of animation to perform
     * @param totalFrames The total number of frames in the animation sequence
     */
    public ChaoAnimation(ImageView characterView, ChaoType chaoType, AnimationState animationState, int totalFrames) {
        this.characterView = characterView;
        this.chaoType = chaoType;
        this.animationState = animationState;
        this.totalFrames = totalFrames;

        // Initialize the timeline with the appropriate frame update logic
        initializeTimeline();

        //Apply scaling for Hero and Dark Chao
        applyChaoTypeScaling();
    }

    /**
     * Initializes the animation timeline.
     */
    private void initializeTimeline() {
        this.timeline = new Timeline(new KeyFrame(Duration.seconds(frameSpeed), event -> updateFrame()));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    /**
     * Updates the frame displayed in the ImageView.
     * This method is called by the timeline at regular intervals.
     */
    private void updateFrame() {
        try {
            // Generate the path to the current frame's image using a folder structure
            // Format: /sprites/CHAOTYPE/ANIMATIONSTATE1.png
            String imagePath = String.format("/com/example/chaotopia/sprites/%s/%s%d.png",
                    chaoType.getResourceName(),
                    animationState.getResourceName(),
                    currentFrame);

            // Load the image
            Image frameImage = new Image(getClass().getResourceAsStream(imagePath));

            // Update the image view
            characterView.setImage(frameImage);

            // Move to the next frame
            currentFrame = (currentFrame % totalFrames) + 1;
        } catch (Exception e) {
            System.err.println("Error loading animation frame: " + e.getMessage());
            System.err.println("Attempted path: " +
                    "/com/example/chaotopia/sprites/" +
                    chaoType.getResourceName() + "/" +
                    animationState.getResourceName() +
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
     * Changes the current animation.
     * This allows switching between different animation states (like from NORMAL to SLEEPING)
     * without creating a new ChaoAnimation instance.
     *
     * @param animationState The new animation state
     */
    public void changeAnimation(AnimationState animationState) {
        // Stop the current animation
        timeline.stop();

        // Update the animation parameters
        this.animationState = animationState;
        this.totalFrames = animationState.getFrameCount();
        this.currentFrame = 1;

        // Restart the animation
        timeline.play();
    }

    /**
     * Changes the animation based on the Chao's logical state.
     * This provides a convenient way to sync the animation with the Chao's game state.
     *
     * @param state The Chao's logical state
     */
    public void changeState(State state) {
        changeAnimation(AnimationState.fromState(state));
    }

    /**
     * Changes the current animation with a custom frame count.
     * Use this method when the animation has a non-standard number of frames.
     *
     * @param animationState The new animation state
     * @param totalFrames The total number of frames in the new animation
     */
    public void changeAnimation(AnimationState animationState, int totalFrames) {
        // Stop the current animation
        timeline.stop();

        // Update the animation parameters
        this.animationState = animationState;
        this.totalFrames = totalFrames;
        this.currentFrame = 1;

        // Restart the animation
        timeline.play();
    }

    /**
     * Changes the Chao type being animated.
     * This allows switching between different Chao types without creating a new ChaoAnimation instance.
     *
     * @param chaoType The new Chao type
     */
    public void changeChaoType(ChaoType chaoType) {
        // Stop the current animation
        timeline.stop();

        // Update the Chao type
        this.chaoType = chaoType;
        this.currentFrame = 1;

        //Apply scaling to Hero and Dark Chao
        applyChaoTypeScaling();

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
     * Gets the current animation state.
     *
     * @return The current animation state
     */
    public AnimationState getAnimationState() {
        return animationState;
    }

    /**
     * Gets the current Chao type.
     *
     * @return The current Chao type
     */
    public ChaoType getChaoType() {
        return chaoType;
    }

    /**
     * Applies scaling to the ImageView based on the Chao type.
     * Different Chao types can have different scaling factors.
     */
    private void applyChaoTypeScaling() {
        // Apply scaling based on Chao type
        switch (chaoType) {
            case DARK:
            case HERO:
                // Scale up by 75%
                characterView.setScaleX(1.60);
                characterView.setScaleY(1.60);
                break;
            case RED:
            case GREEN:
            case BLUE:
            default:
                // Normal scale
                characterView.setScaleX(1.0);
                characterView.setScaleY(1.0);
                break;
        }
    }

}