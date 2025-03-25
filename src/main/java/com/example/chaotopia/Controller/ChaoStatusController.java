package com.example.chaotopia.Controller;


/* notes:
 so for some reason when health is at 5, it's dead, but the health is still at 5.
 also the sleep button isn't working like it should. it doesn't do anything
 */
import com.example.chaotopia.Model.Chao;
import com.example.chaotopia.Model.ChaoType;
import com.example.chaotopia.Model.State;
import com.example.chaotopia.Model.Status;
import com.example.chaotopia.Model.DrawBar;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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
    @FXML private BorderPane mainContainer; // Add this FXML reference

    private VBox gameOverBox; // Reference to the game over screen
    private Chao chao;
    private DrawBar happinessBar;
    private DrawBar healthBar;
    private DrawBar fullnessBar;
    private DrawBar sleepBar;
    private Timeline tempAnimationTimer;
    private State previousState = State.NORMAL;

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
        updateChaoImage();
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
     * Health does not decrease automatically unless in special states.
     */
    private void decreaseStats() {
        if (chao != null && !chao.getStatus().isDead()) {
            Status status = chao.getStatus();
            State currentState = chao.getState();

            // Skip for temporary states like HAPPY that will revert on their own
            if (currentState == State.HAPPY && tempAnimationTimer != null &&
                    tempAnimationTimer.getStatus() == Timeline.Status.RUNNING) {
                return;
            }

            // Base decrease values
            int happinessDecrease = (int) (Math.random() * 3) + 1;
            int fullnessDecrease = (int) (Math.random() * 3) + 1;
            int sleepDecrease = (int) (Math.random() * 3) + 1;
            int healthDecrease = 0;

            // Apply type-specific modifications to stat decreases
            if (chao.getType() == ChaoType.DARK) {
                happinessDecrease *= 1.5; // Happiness depletes faster for DARK type
            } else if (chao.getType() == ChaoType.BLUE) {
                sleepDecrease *= 1.5; // Gets sleepy faster for BLUE type
            } else if (chao.getType() == ChaoType.RED) {
                fullnessDecrease *= 1.5; // Gets hungry faster for RED type
            } else if (chao.getType() == ChaoType.GREEN) {
                healthDecrease = 1; // Health depletes faster for GREEN type
            } else if (chao.getType() == ChaoType.HERO){
                happinessDecrease *= 0.5; //Happiness decreases slower for HERO chao
            }

            // Check for sleep at zero
            if (status.getSleep() == 0 && currentState != State.SLEEPING) {
                // Apply sleep penalty and transition to sleeping state
                status.adjustHealth(-10); // Apply health penalty
                chao.setState(State.SLEEPING);
                updateChaoImage();
            }

            // Handle the sleeping state
            if (currentState == State.SLEEPING) {
                // Increase sleep while in sleeping state
                status.adjustSleep(10); // Sleep recovers while sleeping
                sleepDecrease = 0; // No sleep decrease while sleeping

                // Check if the Chao should wake up
                if (status.getSleep() >= 100) {
                    chao.setState(State.NORMAL);
                    updateChaoImage();
                }
            }

            // Check for fullness at zero - hungry state
            if (status.getFullness() == 0) {
                if (currentState != State.HUNGRY && currentState != State.SLEEPING) {
                    chao.setState(State.HUNGRY);
                    updateChaoImage();
                }

                // In hungry state, happiness decreases faster and health decreases
                happinessDecrease *= 2; // Double happiness decrease
                healthDecrease += 5; // Health starts decreasing
            } else if (currentState == State.HUNGRY && status.getFullness() > 0) {
                // Exit hungry state if fullness is above zero
                if (status.getHappiness() > 0) {
                    chao.setState(State.NORMAL);
                    updateChaoImage();
                }
            }

            // Check for happiness at zero - angry state
            if (status.getHappiness() == 0) {
                if (currentState != State.ANGRY && currentState != State.SLEEPING &&
                        currentState != State.HUNGRY && currentState != State.SIT) {
                    chao.setState(State.ANGRY);
                    updateChaoImage();
                }
            } else if (currentState == State.ANGRY && status.getHappiness() >= 50) {
                // Exit angry state if happiness is at least 50%
                if (status.getFullness() > 0) {
                    chao.setState(State.NORMAL);
                    updateChaoImage();
                }
            }

            // Update stats based on current state and decreases
            status.updateStats(
                    -happinessDecrease,
                    -healthDecrease,
                    -fullnessDecrease,
                    -sleepDecrease
            );

            // Check if the Chao died after stat updates
            if (status.isDead()) {
                chao.setState(State.DEAD);
                updateChaoImage();
                //showGameOverScreen(); i commented this out for now i think it was overlapping dead animation
                statDecayTimeline.stop();
            } else {
                // Update the UI
                updateStatusBars();

                // Increase score for keeping Chao alive
                updateScore(score + 1);
            }
        }
    }

    // For animation
    private Timeline animationTimeline;
    private int currentFrame = 1;
    private int maxFrames = 2; // Default max frames

    /**
     * Updates the Chao image based on its current state and type.
     */
    private void updateChaoImage() {
        if (chao != null) {
            State state = chao.getState();
            ChaoType type = chao.getType();

            // Stop any existing animation
            if (animationTimeline != null) {
                animationTimeline.stop();
            }

            // Get folder based on Chao type
            String typeFolder;
            switch (type) {
                case HERO:
                    typeFolder = "HERO";
                    break;
                case DARK:
                    typeFolder = "DARK";
                    break;
                case GREEN:
                    typeFolder = "GREEN";
                    break;
                case RED:
                    typeFolder = "RED";
                    break;
                default:
                    typeFolder = "BLUE"; // Default to BLUE for NEUTRAL type
                    break;
            }

            // Set state and max frames based on current state
            String statePrefix;
            switch (state) {
                case SLEEPING:
                    statePrefix = "SLEEPING";
                    maxFrames = 2;
                    break;
                case HUNGRY:
                    statePrefix = "HUNGRY";
                    maxFrames = 4;
                    break;
                case ANGRY:
                    statePrefix = "ANGRY";
                    maxFrames = 2;
                    break;
                case DEAD:
                    statePrefix = "DEAD";
                    maxFrames = 2;
                    break;
                case HAPPY:
                    statePrefix = "HAPPY";
                    maxFrames = 2;
                    break;
                case NORMAL:
                default:
                    statePrefix = "NORMAL";
                    maxFrames = 4;
                    break;
            }

            // Reset current frame
            currentFrame = 1;

            // Load the first frame
            loadSpriteFrame(typeFolder, statePrefix, currentFrame);

            // Set up animation timeline for sprite animation
            animationTimeline = new Timeline(
                    new KeyFrame(Duration.millis(250), e -> {
                        currentFrame = (currentFrame % maxFrames) + 1;
                        loadSpriteFrame(typeFolder, statePrefix, currentFrame);
                    })
            );
            animationTimeline.setCycleCount(Timeline.INDEFINITE);
            animationTimeline.play();
        }
    }

    /**
     * Loads a specific sprite frame.
     *
     * @param typeFolder the folder for the Chao type/color
     * @param statePrefix the state prefix (NORMAL, SLEEPING, etc.)
     * @param frame the frame number
     */
    private void loadSpriteFrame(String typeFolder, String statePrefix, int frame) {
        String imageUrl = "/com/example/chaotopia/sprites/" + typeFolder + "/" + statePrefix + frame + ".png";

        try {
            chaoImageView.setImage(new Image(getClass().getResourceAsStream(imageUrl)));
        } catch (Exception e) {
            System.err.println("Failed to load image: " + imageUrl);
            // Try to load a default image if specific frame isn't found
            try {
                String defaultImageUrl = "/com/example/chaotopia/sprites/BLUE/NORMAL1.png";
                chaoImageView.setImage(new Image(getClass().getResourceAsStream(defaultImageUrl)));
            } catch (Exception ex) {
                System.err.println("Failed to load default image");
            }
        }
    }

    /**
     * Shows a temporary happy animation after positive actions.
     * Returns to the previous state after a delay.
     */
    private void showHappyAnimation() {
        if (chao != null && chao.getState() != State.DEAD && chao.getState() != State.SLEEPING) {
            // Store current state to return to it later
            previousState = chao.getState();

            // Set to happy state
            chao.setState(State.HAPPY);
            updateChaoImage();

            // Cancel any existing temporary animation timer
            if (tempAnimationTimer != null) {
                tempAnimationTimer.stop();
            }

            // Return to previous state after 1.5 seconds
            tempAnimationTimer = new Timeline(
                    new KeyFrame(Duration.seconds(1.5), e -> {
                        // Only revert if still in HAPPY state (might have changed due to other factors)
                        if (chao.getState() == State.HAPPY) {
                            chao.setState(previousState);
                            updateChaoImage();
                        }
                    })
            );
            tempAnimationTimer.setCycleCount(1);
            tempAnimationTimer.play();
        }
    }

    /**
     * Shows the game over screen when the Chao dies.
     */
    private void showGameOverScreen() {
        // Disable the action buttons
        disableAllInteractions(true);

        // Create a game over overlay
        gameOverBox = new VBox(10);
        gameOverBox.setAlignment(javafx.geometry.Pos.CENTER);
        gameOverBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-padding: 20px;");
        gameOverBox.setPrefSize(500, 300);

        Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-font-weight: bold;");

        Button newGameButton = new Button("New Game");
        newGameButton.setOnAction(e -> startNewGame());

        Button loadGameButton = new Button("Load Game");
        loadGameButton.setOnAction(e -> loadGame());

        gameOverBox.getChildren().addAll(gameOverLabel, newGameButton, loadGameButton);

        // Add the game over overlay to the center of the BorderPane
        if (mainContainer != null) {
            mainContainer.setCenter(gameOverBox);
        } else {
            // Fallback: try to find the main BorderPane
            Scene scene = statusBarsContainer.getScene();
            if (scene != null) {
                BorderPane rootPane = (BorderPane) scene.getRoot();
                if (rootPane != null) {
                    rootPane.setCenter(gameOverBox);
                }
            }
        }
    }

    /**
     * Starts a new game.
     */
    private void startNewGame() {
        // Create a new Chao
        Status initialStatus = new Status(100, 100, 100, 100);
        Chao newChao = new Chao(0, "Sonic Jr.", ChaoType.BLUE, State.NORMAL, initialStatus);

        // Set the new Chao
        setChao(newChao);

        // Reset score
        updateScore(0);

        // Remove game over screen
        if (gameOverBox != null && mainContainer != null) {
            mainContainer.getChildren().remove(gameOverBox);

            // Restore the original center content
            HBox originalContent = new HBox();
            originalContent.setAlignment(javafx.geometry.Pos.CENTER);
            originalContent.setSpacing(20);

            // Re-add Chao image container
            StackPane imageContainer = new StackPane();
            imageContainer.setStyle("-fx-background-color: #ccaa88; -fx-border-color: #886644; -fx-border-width: 3; -fx-border-radius: 5;");
            imageContainer.setPadding(new javafx.geometry.Insets(10));
            imageContainer.getChildren().add(chaoImageView);

            originalContent.getChildren().addAll(imageContainer, statusBarsContainer);
            mainContainer.setCenter(originalContent);
        }

        // Re-enable interactions
        disableAllInteractions(false);

        // Restart the decay timeline
        if (statDecayTimeline != null && !statDecayTimeline.getStatus().equals(Timeline.Status.RUNNING)) {
            statDecayTimeline.play();
        }
    }

    /**
     * Loads a saved game (placeholder).
     */
    private void loadGame() {
        // This would typically load from a file, but for now we'll just simulate it
        startNewGame();
    }

    /**
     * Enables or disables all interaction buttons.
     *
     * @param disable true to disable buttons, false to enable them
     */
    private void disableAllInteractions(boolean disable) {
        // Find all buttons in the scene
        Scene scene = statusBarsContainer.getScene();
        if (scene != null) {
            scene.getRoot().lookupAll(".button").forEach(node -> {
                if (node instanceof Button) {
                    ((Button) node).setDisable(disable);
                }
            });
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
            State currentState = chao.getState();

            // Cannot interact if sleeping
            if (currentState == State.SLEEPING) {
                return;
            }

            // If angry, can only perform actions that increase happiness
            if (currentState == State.ANGRY) {
                // Feeding increases happiness a little so it's allowed
                chao.getStatus().adjustFullness(15);
                chao.getStatus().adjustHappiness(5);

                // Check if we need to exit the angry state
                if (chao.getStatus().getHappiness() >= 50) {
                    chao.setState(State.NORMAL);
                }
            } else {
                // Normal or hungry state
                chao.getStatus().adjustFullness(15);
                chao.getStatus().adjustHappiness(5);

                // Check if we should exit the hungry state
                if (currentState == State.HUNGRY && chao.getStatus().getFullness() > 0) {
                    chao.setState(State.NORMAL);
                }
            }

            updateStatusBars();
            updateScore(score + 5);

            // Show happy animation
            showHappyAnimation();
        }
    }

    /**
     * Plays with the Chao, increasing happiness.
     */
    @FXML
    public void playWithChao() {
        if (chao != null && !chao.getStatus().isDead()) {
            State currentState = chao.getState();

            // Cannot interact if sleeping
            if (currentState == State.SLEEPING) {
                return;
            }

            // Playing is always allowed in angry state since it increases happiness
            chao.getStatus().adjustHappiness(20);
            chao.getStatus().adjustFullness(-5); // Playing makes them hungry
            chao.getStatus().adjustSleep(-5);    // And tired

            // Check if we should exit angry state
            if (currentState == State.ANGRY && chao.getStatus().getHappiness() >= 50) {
                chao.setState(State.NORMAL);
            }

            // Check if we entered hungry state from the fullness decrease
            if (chao.getStatus().getFullness() == 0 && currentState != State.HUNGRY) {
                chao.setState(State.HUNGRY);
            }

            updateStatusBars();
            updateScore(score + 10);

            // Show happy animation
            showHappyAnimation();
        }
    }

    /**
     * Lets the Chao sleep, increasing sleep and health.
     */
    @FXML
    public void letChaoSleep() {
        if (chao != null && !chao.getStatus().isDead()) {
            State currentState = chao.getState();

            // Cannot use this command if already sleeping
            if (currentState == State.SLEEPING) {
                return;
            }

            // If angry, can only perform actions that increase happiness
            if (currentState == State.ANGRY) {
                // Sleep doesn't directly increase happiness, so not allowed
                return;
            }

            chao.setState(State.SLEEPING);
            chao.getStatus().adjustSleep(25);
            chao.getStatus().adjustHealth(5);

            // If sleep was zero, the Chao might already be in sleeping state
            if (currentState != State.SLEEPING && chao.getStatus().getSleep() == 0) {
                chao.setState(State.SLEEPING);
                updateChaoImage();
            }

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
            State currentState = chao.getState();

            // Cannot interact if sleeping
            if (currentState == State.SLEEPING) {
                return;
            }

            // If angry, can only perform actions that increase happiness
            if (currentState == State.ANGRY) {
                // Healing doesn't increase happiness, so not allowed
                return;
            }

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

        if (animationTimeline != null) {
            animationTimeline.stop();
        }

        if (tempAnimationTimer != null) {
            tempAnimationTimer.stop();
        }

        // Dispose of all DrawBars to stop their animations
        if (happinessBar != null) happinessBar.dispose();
        if (healthBar != null) healthBar.dispose();
        if (fullnessBar != null) fullnessBar.dispose();
        if (sleepBar != null) sleepBar.dispose();
    }
}