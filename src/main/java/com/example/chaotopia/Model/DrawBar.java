package com.example.chaotopia.Model;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * The DrawBar class represents a visual status bar that can display
 * a value between 0 and 100 as a filling bar.
 * @author Rosaline Scully
 */
public class DrawBar extends StackPane {
    private Canvas canvas;
    private GraphicsContext gc;
    private double width;
    private double height;
    private int value;
    private String label;
    private Color barColor;

    // For warning effects
    private Timeline warningTimeline;
    private boolean isFlashing = false;
    private boolean flashState = false;

    /**
     * Constructor to create a status bar with specific dimensions and color.
     *
     * @param width the width of the status bar
     * @param height the height of the status bar
     * @param initialValue the initial value of the status (0-100)
     * @param label the label for this status bar
     * @param barColor the color of the bar fill
     */
    public DrawBar(double width, double height, int initialValue, String label, Color barColor) {
        this.width = width;
        this.height = height;
        this.value = initialValue;
        this.label = label;
        this.barColor = barColor;

        // Create the canvas to draw on
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();

        // Add the canvas to this pane
        getChildren().add(canvas);

        // Draw the initial state
        draw();
    }

    /**
     * Updates the value of the status bar and redraws it.
     *
     * @param newValue the new value (0-100)
     */
    public void updateValue(int newValue) {
        // Ensure value is between 0 and 100
        this.value = Math.max(0, Math.min(100, newValue));
        draw();
    }

    /**
     * Draws the status bar with the current value.
     */
    private void draw() {
        // Clear the canvas
        gc.clearRect(0, 0, width, height);

        // Draw the border
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(0, 0, width, height);

        // Calculate the fill width based on the current value
        double fillWidth = (width - 4) * value / 100.0;

        // Determine if we should show a warning (below 25%)
        boolean showWarning = value < 25;

        // Start or stop warning animation
        if (showWarning && !isFlashing) {
            startWarningAnimation();
        } else if (!showWarning && isFlashing) {
            stopWarningAnimation();
        }

        // Draw the fill with appropriate color
        if (showWarning) {
            // Use warning color when below 25%
            if (flashState) {
                gc.setFill(Color.RED);
            } else {
                gc.setFill(Color.ORANGE);
            }
        } else {
            gc.setFill(barColor);
        }

        gc.fillRect(2, 2, fillWidth, height - 4);

        // Draw the label with appropriate color
        if (showWarning) {
            if (flashState) {
                gc.setFill(Color.RED);
            } else {
                gc.setFill(Color.BLACK);
            }
        } else {
            gc.setFill(Color.BLACK);
        }

        gc.fillText(label + ": " + value, 5, height / 2 + 5);
    }

    /**
     * Starts a flashing warning animation for low stats.
     */
    private void startWarningAnimation() {
        isFlashing = true;

        // Stop any existing timeline
        if (warningTimeline != null) {
            warningTimeline.stop();
        }

        // Create a new timeline that toggles flash state every 500ms
        warningTimeline = new Timeline(
                new KeyFrame(Duration.millis(500), e -> {
                    flashState = !flashState;
                    draw();
                })
        );
        warningTimeline.setCycleCount(Timeline.INDEFINITE);
        warningTimeline.play();
    }

    /**
     * Stops the warning animation.
     */
    private void stopWarningAnimation() {
        isFlashing = false;

        if (warningTimeline != null) {
            warningTimeline.stop();
            warningTimeline = null;
        }

        flashState = false;
    }

    /**
     * Stops any active animations when the component is no longer needed.
     */
    public void dispose() {
        if (warningTimeline != null) {
            warningTimeline.stop();
            warningTimeline = null;
        }
    }

    /**
     * Gets the current value of the status bar.
     *
     * @return the current value (0-100)
     */
    public int getValue() {
        return value;
    }

    /**
     * Gets the label of this status bar.
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the color of the bar fill.
     *
     * @param color the new color
     */
    public void setBarColor(Color color) {
        this.barColor = color;
        draw();
    }
}