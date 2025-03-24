package com.example.chaotopia.Controller;

import com.example.chaotopia.Model.Chao;
import com.example.chaotopia.Model.Status;
import com.example.chaotopia.Model.DrawBar;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller class for managing the Chao status UI and game logic.
 */
public class ChaoStatusController implements Initializable {

    @FXML private VBox statusBarsContainer;
    @FXML private Label nameLabel;
    @FXML private Label scoreLabel;
    @FXML private ImageView chaoImageView;

    private Chao chao;
    private DrawBar happinessBar;
    private DrawBar healthBar;
    private DrawBar fullnessBar;
    private DrawBar sleepBar;

    private int score = 0;
    private Timeline statDecayTimeline;

    /**
     * Initializes the controller with required resources.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Create the status bars
        happinessBar = new DrawBar(200, 30, 100, "Happiness", Color.YELLOW);
        healthBar = new DrawBar(200, 30, 100, "Health", Color.RED);
        fullnessBar = new DrawBar(200, 30, 100, "Fullness", Color.GREEN);
        sleepBar = new DrawBar(200, 30, 100, "Sleep", Color.BLUE);

        // Add status bars to the container
        statusBarsContainer.getChildren().addAll(
                happinessBar, healthBar, fullnessBar, sleepBar
        );

        // Initialize score
        updateScore(0);

        // Set up the stat decay timeline (runs every 5 seconds)
        statDecayTimeline = new Timeline(
                new KeyFrame(Duration.seconds(5), e -> decreaseStats())
        );
        statDecayTimeline.setCycleCount(Timeline.INDEFINITE);
        statDecayTimeline.play();
    }

    /**
     * Sets the Chao and updates the UI to reflect its status.
     *
     * @param chao the Chao to display
     */
    public void setChao(Chao chao) {
        this.chao = chao;
        nameLabel.setText(chao.getName());
        updateStatusBars();

        // Load the appropriate Chao image based on its type
        // This is a placeholder - you would load the actual image based on your assets
        // String imageUrl = "images/" + chao.getType().toString().toLowerCase() + "_chao.png";
        // chaoImageView.setImage(new Image(imageUrl));
    }

    /**
     * Updates all status bars to reflect the current Chao status.
     */
    public void updateStatusBars() {
        if (chao != null) {
            Status status = chao.getStatus();
            happinessBar.updateValue(status.getHappiness());
            healthBar.updateValue(status.getHealth());
            fullnessBar.updateValue(status.getFullness());
            sleepBar.updateValue(status.getSleep());

            // Check if Chao is dead
            if (status.isDead()) {
                statDecayTimeline.stop();
                // Handle death (e.g., show game over screen)
            }
        }
    }

    /**
     * Decreases the stats over time to simulate natural decay.
     * Health does not decrease automatically.
     */
    private void decreaseStats() {
        if (chao != null && !chao.getStatus().isDead()) {
            // Decrease happiness, fullness, and sleep by 1-3 points
            int happinessDecrease = (int) (Math.random() * 3) + 1;
            int fullnessDecrease = (int) (Math.random() * 3) + 1;
            int sleepDecrease = (int) (Math.random() * 3) + 1;

            // Update the status
            chao.getStatus().updateStats(
                    -happinessDecrease,   // Decrease happiness
                    0,                    // Health doesn't decrease over time
                    -fullnessDecrease,    // Decrease fullness
                    -sleepDecrease        // Decrease sleep
            );

            // Update the UI
            updateStatusBars();

            // Increase score for keeping Chao alive
            updateScore(score + 1);
        }
    }

    /**
     * Updates the score display.
     *
     * @param newScore the new score value
     */
    private void updateScore(int newScore) {
        this.score = newScore;
        scoreLabel.setText("SCORE: " + score);
    }

    /**
     * Feeds the Chao, increasing fullness and slightly increasing happiness.
     */
    @FXML
    public void feedChao() {
        if (chao != null && !chao.getStatus().isDead()) {
            chao.getStatus().adjustFullness(15);
            chao.getStatus().adjustHappiness(5);
            updateStatusBars();
            updateScore(score + 5);
        }
    }

    /**
     * Plays with the Chao, increasing happiness.
     */
    @FXML
    public void playWithChao() {
        if (chao != null && !chao.getStatus().isDead()) {
            chao.getStatus().adjustHappiness(20);
            chao.getStatus().adjustFullness(-5); // Playing makes them hungry
            chao.getStatus().adjustSleep(-5);    // And tired
            updateStatusBars();
            updateScore(score + 10);
        }
    }

    /**
     * Lets the Chao sleep, increasing sleep and health.
     */
    @FXML
    public void letChaoSleep() {
        if (chao != null && !chao.getStatus().isDead()) {
            chao.getStatus().adjustSleep(25);
            chao.getStatus().adjustHealth(5);
            updateStatusBars();
            updateScore(score + 7);
        }
    }

    /**
     * Heals the Chao, increasing health but costing game points.
     */
    @FXML
    public void healChao() {
        if (chao != null && !chao.getStatus().isDead() && score >= 20) {
            chao.getStatus().adjustHealth(15);
            updateStatusBars();
            updateScore(score - 20); // Healing costs points
        }
    }

    /**
     * Stops the game timers when the application is closing.
     */
    public void shutdown() {
        if (statDecayTimeline != null) {
            statDecayTimeline.stop();
        }
    }
}