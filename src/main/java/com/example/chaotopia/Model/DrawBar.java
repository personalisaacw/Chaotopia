package com.example.chaotopia.Model;

import javafx.application.Platform; // Import Platform
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font; // Optional: For text styling

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
    private boolean disposed = false; // Flag to prevent updates after disposal

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
        // Ensure initial value is valid
        this.value = Math.max(0, Math.min(100, initialValue));
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
     * Does nothing if the bar has been disposed.
     *
     * @param newValue the new value (0-100)
     */
    public void updateValue(int newValue) {
        if (disposed) {
            return; // Do nothing if disposed
        }
        // Ensure value is between 0 and 100
        this.value = Math.max(0, Math.min(100, newValue));
        // Ensure drawing happens on the JavaFX Application Thread
        Platform.runLater(this::draw);
    }

    /**
     * Draws the status bar with the current value.
     * Should only be called from the JavaFX Application Thread.
     */
    private void draw() {
        if (disposed || gc == null) { // Extra check
            return;
        }

        // Clear the canvas
        gc.clearRect(0, 0, width, height);

        // Draw the border
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(0, 0, width, height);

        // Calculate the fill width based on the current value
        double fillWidth = (width - 4) * value / 100.0; // Adjusted for border thickness

        // Draw the fill
        gc.setFill(barColor);
        gc.fillRect(2, 2, Math.max(0, fillWidth), height - 4); // Ensure fillWidth isn't negative

        // Draw the label (consider font settings for better appearance)
        gc.setFill(Color.BLACK);
        // gc.setFont(Font.font("System", 12)); // Example: Set font
        gc.fillText(label + ": " + value, 5, height / 2 + 5); // Adjust Y for baseline if needed
    }


    /**
     * Cleans up the DrawBar, making it inactive and removing it visually.
     * Ensures cleanup happens on the JavaFX Application Thread.
     */
    public void dispose() {
        // Ensure disposal happens on the JavaFX Application Thread
        Platform.runLater(() -> {
            if (disposed) {
                return; // Already disposed
            }
            disposed = true;

            // 1. Clear the graphics context visually (optional but good practice)
            if (gc != null) {
                gc.clearRect(0, 0, width, height);
                // Optional: Draw a disposed state
                gc.setFill(Color.LIGHTGRAY);
                gc.fillRect(0,0, width, height);
                gc.setStroke(Color.DARKGRAY);
                gc.strokeRect(0, 0, width, height);
                gc.setFill(Color.GRAY);
                gc.fillText(label + ": ---", 5, height / 2 + 5);
            }

            // 2. Remove the canvas from the children list of the StackPane
            if (canvas != null) {
                getChildren().remove(canvas);
            }

            // 3. Nullify references to help garbage collection (optional)
            gc = null;
            canvas = null;
        });
    }


    // --- Getters remain the same ---

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
     * Sets the color of the bar fill and redraws.
     * Does nothing if the bar has been disposed.
     *
     * @param color the new color
     */
    public void setBarColor(Color color) {
        if (disposed) {
            return;
        }
        this.barColor = color;
        // Ensure drawing happens on the JavaFX Application Thread
        Platform.runLater(this::draw);
    }
}