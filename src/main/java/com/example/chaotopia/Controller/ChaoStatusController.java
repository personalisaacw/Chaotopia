package com.example.chaotopia.Controller;

import com.example.chaotopia.Model.*;
        import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    @FXML private BorderPane mainContainer;

    private VBox gameOverBox;
    private Chao chao;
    private DrawBar happinessBar;
    private DrawBar healthBar;
    private DrawBar fullnessBar;
    private DrawBar sleepBar;
    private Timeline tempAnimationTimer;
    private State previousState = State.NORMAL;
    private ChaoAnimation chaoAnimation;


    private int score = 0;
    private Timeline statDecayTimeline;
    private boolean isSleeping = false;

    private Timeline stateMonitorTimeline;
    private Timeline sleepIncreaseTimeline;

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

        // Set up more frequent state monitor (runs every 250ms)
        stateMonitorTimeline = new Timeline(
                new KeyFrame(Duration.millis(250), e -> monitorChaoState())
        );
        stateMonitorTimeline.setCycleCount(Timeline.INDEFINITE);
        stateMonitorTimeline.play();

        // Initialize ChaoAnimation with default values
        chaoAnimation = new ChaoAnimation(chaoImageView, ChaoType.DARK, AnimationState.NORMAL);
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

        // Initialize animation
        chaoAnimation.changeChaoType(chao.getType());
        chaoAnimation.changeState(chao.getState());
        chaoAnimation.startAnimation();

        // Immediately check if we need to awaken sleep monitor
        if (stateMonitorTimeline != null &&
                stateMonitorTimeline.getStatus() != Timeline.Status.RUNNING) {
            stateMonitorTimeline.play();
        }
    }

    /**
     * Updates all status bars to reflect the current Chao status.
     */
    public void updateStatusBars() {
        if (chao != null) {
            Status status = chao.getStatus();

            // If the Chao is dead, force health to display as 0
            if (status.isDead()) {
                healthBar.updateValue(0);
            } else {
                healthBar.updateValue(status.getHealth());
            }

            happinessBar.updateValue(status.getHappiness());
            fullnessBar.updateValue(status.getFullness());
            sleepBar.updateValue(status.getSleep());

            // Check if Chao is dead and handle accordingly
            if (status.isDead() && chao.getState() != State.DEAD) {
                chao.setState(State.DEAD);
                chaoAnimation.changeState(State.DEAD);
                statDecayTimeline.stop();

            }
        }
    }

    /**
     * Starts a gradual sleep increase at a steady rate
     */
    private void startSleepIncrease() {
        // Stop any existing sleep timeline
        if (sleepIncreaseTimeline != null) {
            sleepIncreaseTimeline.stop();
        }

        // Create a new timeline that increases sleep by 2 points every 500ms
        sleepIncreaseTimeline = new Timeline(
                new KeyFrame(Duration.millis(500), e -> {
                    if (chao != null && isSleeping && chao.getStatus().getSleep() < 100) {
                        chao.getStatus().adjustSleep(2);
                        updateStatusBars();
                    } else {
                        // Stop the timeline when sleep is full or chao isn't sleeping
                        sleepIncreaseTimeline.stop();
                    }
                })
        );

        sleepIncreaseTimeline.setCycleCount(Timeline.INDEFINITE);
        sleepIncreaseTimeline.play();
    }
    /**
     * Continuously monitors Chao state to ensure immediate visual updates
     * Runs independently of the stat decay system
     */
    private void monitorChaoState() {
        if (chao == null || chao.getStatus().isDead()) return;

        Status status = chao.getStatus();
        State currentState = chao.getState();
        State newState;

        // Determine the highest priority state based on current stats
        if (status.isDead()) {
            newState = State.DEAD;
        } else if (isSleeping) {
            // Sleep gets highest priority when sleep flag is set
            newState = State.SLEEPING;

            // Check if sleep is at 100% to wake up
            if (status.getSleep() >= 100) {
                isSleeping = false;

                // Stop the sleep increase timeline if it's running
                if (sleepIncreaseTimeline != null && sleepIncreaseTimeline.getStatus() == Timeline.Status.RUNNING) {
                    sleepIncreaseTimeline.stop();
                }

                enableAllInteractions(true);

                // After waking up, determine next highest priority state
                if (status.getFullness() <= 0) {
                    newState = State.HUNGRY;
                } else if (status.getHappiness() <= 0) {
                    newState = State.ANGRY;
                } else {
                    newState = State.NORMAL;
                }
            }
        } else if (status.getSleep() <= 0) {
            // Force sleep when sleep drops to 0
            newState = State.SLEEPING;
            isSleeping = true;
            enableAllInteractions(false);

            // Start gradual sleep increase
            startSleepIncrease();
        } else if (currentState == State.HAPPY && tempAnimationTimer != null &&
                tempAnimationTimer.getStatus() == Timeline.Status.RUNNING) {
            // Keep the HAPPY state if animation timer is active
            return;
        } else if (status.getFullness() <= 0) {
            newState = State.HUNGRY;
        } else if (status.getHappiness() <= 0) {
            newState = State.ANGRY;
        } else {
            newState = State.NORMAL;
        }

        // If the state needs to change, update it immediately
        if (newState != currentState) {
            chao.setState(newState);
            chaoAnimation.changeState(newState);

            // Always restart animation to ensure it shows immediately
            chaoAnimation.stopAnimation();
            chaoAnimation.startAnimation();
        }
    }

    /**
     * Handles state prioritization to determine which animation state should be displayed.
     * Priority order: DEAD > SLEEPING > HUNGRY > ANGRY > NORMAL
     *
     * @param forceUpdate If true, forces the animation to update immediately
     */
    private void updateChaoState(boolean forceUpdate) {
        if (chao == null) return;

        Status status = chao.getStatus();
        State currentState = chao.getState();
        State newState = currentState;

        // Check conditions in priority order
        if (status.isDead()) {
            newState = State.DEAD;
        } else if (isSleeping || currentState == State.SLEEPING) {
            newState = State.SLEEPING;

            // If sleep is full, wake up
            if (status.getSleep() >= 100) {
                newState = State.NORMAL;
                isSleeping = false;
                enableAllInteractions(true);
            }
        } else if (status.getFullness() <= 0) {
            newState = State.HUNGRY;
        } else if (status.getHappiness() <= 0) {
            newState = State.ANGRY;
        } else if (currentState == State.HAPPY) {
            // Keep HAPPY state if it's currently showing (will be reset by timer)
        } else {
            newState = State.NORMAL;
        }

        // Update if state changed or force update is requested
        if (newState != currentState || forceUpdate) {
            chao.setState(newState);
            chaoAnimation.changeState(newState);

            // If force update, stop and restart animation for immediate feedback
            if (forceUpdate) {
                chaoAnimation.stopAnimation();
                chaoAnimation.startAnimation();
            }
        }
    }

    /**
     * Overloaded method for convenience - updates state without forcing
     */
    private void updateChaoState() {
        updateChaoState(false);
    }

    /**
     * Decreases the stats over time to simulate natural decay.
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

            // Store current values to detect changes
            int prevHappiness = status.getHappiness();
            int prevFullness = status.getFullness();
            int prevSleep = status.getSleep();
            int prevHealth = status.getHealth();

            // Base decrease values
            int happinessDecrease = (int) (Math.random() * 3) + 1;
            int fullnessDecrease = (int) (Math.random() * 3) + 1;
            int sleepDecrease = (int) (Math.random() * 3) + 1;
            int healthDecrease = 0;

            // Apply type-specific modifications to stat decreases
            if (chao.getType() == ChaoType.DARK) {
                happinessDecrease = (int)(happinessDecrease * 1.5); // Happiness depletes faster for DARK type
            } else if (chao.getType() == ChaoType.BLUE) {
                sleepDecrease = (int)(sleepDecrease * 1.5); // Gets sleepy faster for BLUE type
            } else if (chao.getType() == ChaoType.RED) {
                fullnessDecrease = (int)(fullnessDecrease * 1.5); // Gets hungry faster for RED type
            } else if (chao.getType() == ChaoType.GREEN) {
                healthDecrease = 1; // Health depletes faster for GREEN type
            } else if (chao.getType() == ChaoType.HERO){
                happinessDecrease = (int)(happinessDecrease * 0.5); //Happiness decreases slower for HERO chao
            }

            // Check for sleep at zero - force sleep
            if (status.getSleep() == 0 && !isSleeping) {
                // Apply sleep penalty and transition to sleeping state
                status.adjustHealth(-10); // Apply health penalty
                isSleeping = true;
                enableAllInteractions(false);
                chao.setState(State.SLEEPING);
                chaoAnimation.changeState(State.SLEEPING);
            }

            // Handle the sleeping state
            if (isSleeping || currentState == State.SLEEPING) {
                // Increase sleep while in sleeping state
                status.adjustSleep(10); // Sleep recovers while sleeping
                sleepDecrease = 0; // No sleep decrease while sleeping

                // Don't wake up here - will be handled in updateChaoState()
            }

            // Check for fullness at zero - hungry state effects
            if (status.getFullness() == 0) {
                // In hungry state, happiness decreases faster and health decreases
                happinessDecrease *= 2; // Double happiness decrease
                healthDecrease += 5; // Health starts decreasing
            }

            // Update stats based on current state and decreases
            status.updateStats(
                    -happinessDecrease,
                    -healthDecrease,
                    -fullnessDecrease,
                    -sleepDecrease
            );

            // Check if any stats changed significantly
            boolean significantChange =
                    (prevHappiness > 0 && status.getHappiness() <= 0) ||  // Happiness hit zero
                            (prevFullness > 0 && status.getFullness() <= 0) ||    // Fullness hit zero
                            (prevSleep > 0 && status.getSleep() <= 0) ||          // Sleep hit zero
                            (prevSleep < 100 && status.getSleep() >= 100) ||      // Sleep reached full
                            (prevHealth > 0 && status.getHealth() <= 0);          // Health hit zero

            // Update the UI
            updateStatusBars();

            // Update state priority - force immediate update if significant change
            updateChaoState(significantChange);

            // Only increase score if not dead
            if (!status.isDead()) {
                updateScore(score + 1);
            }
        }
    }

    /**
     * Shows a temporary happy animation after positive actions.
     * Returns to the previous state after a delay.
     */
    private void showHappyAnimation() {
        if (chao != null && chao.getState() != State.DEAD && !isSleeping) {
            // Store current state to return to it later
            previousState = chao.getState();

            // Set to happy state
            chao.setState(State.HAPPY);

            // Force animation update for immediate feedback
            chaoAnimation.changeState(State.HAPPY);
            chaoAnimation.stopAnimation();
            chaoAnimation.startAnimation();

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

                            // Force animation update when returning to previous state
                            chaoAnimation.changeState(previousState);
                            chaoAnimation.stopAnimation();
                            chaoAnimation.startAnimation();
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
        enableAllInteractions(false);

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

        // Reset sleep status
        isSleeping = false;

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
        enableAllInteractions(true);

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
     * Enables or disables all interaction buttons except New Game and Load Game.
     *
     * @param enable true to enable buttons, false to disable them
     */
    private void enableAllInteractions(boolean enable) {
        // Find all buttons in the scene
        Scene scene = statusBarsContainer.getScene();
        if (scene != null) {
            scene.getRoot().lookupAll(".button").forEach(node -> {
                if (node instanceof Button) {
                    Button button = (Button) node;
                    String text = button.getText();

                    // Don't disable New Game or Load Game buttons
                    if (!text.equals("New Game") && !text.equals("Load Game")) {
                        button.setDisable(!enable);
                    }
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
        if (chao != null && !chao.getStatus().isDead() && !isSleeping) {
            State currentState = chao.getState();

            // If angry, can only perform actions that increase happiness
            if (currentState == State.ANGRY) {
                return;
            } else {
                // Normal or hungry state
                chao.getStatus().adjustFullness(15);
            }

            updateStatusBars();
            updateScore(score + 10);

            // Show happy animation
            showHappyAnimation();
        }
    }

    /**
     * Plays with the Chao, increasing happiness.
     */
    @FXML
    public void playWithChao() {
        if (chao != null && !chao.getStatus().isDead() && !isSleeping) {
            // Playing is always allowed in angry state since it increases happiness
            chao.getStatus().adjustHappiness(20);

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
        if (chao != null && !chao.getStatus().isDead() && !isSleeping) {
            State currentState = chao.getState();

            // If angry, can only perform actions that increase happiness
            if (currentState == State.ANGRY) {
                // Sleep doesn't directly increase happiness, so not allowed
                return;
            }

            // Put Chao to sleep
            isSleeping = true;
            chao.setState(State.SLEEPING);

            // Update the UI immediately
            updateStatusBars();
            updateScore(score + 7);

            // Force animation update for immediate feedback
            chaoAnimation.changeState(State.SLEEPING);
            chaoAnimation.stopAnimation();
            chaoAnimation.startAnimation();

            // Disable interactions during sleep
            enableAllInteractions(false);
        }
    }

    /**
     * Heals the Chao, increasing health but costing game points.
     */
    @FXML
    public void healChao() {
        if (chao != null && !chao.getStatus().isDead() && !isSleeping) {
            State currentState = chao.getState();

            // If angry, can only perform actions that increase happiness
            if (currentState == State.ANGRY) {
                // Healing doesn't increase happiness, so not allowed
                return;
            }

            chao.getStatus().adjustHealth(15);
            updateStatusBars();
            updateScore(score - 20); // Healing costs points

            // Show happy animation
            showHappyAnimation();
        }
    }

    /**
     * Stops the game timers when the application is closing.
     */
    public void shutdown() {
        if (statDecayTimeline != null) {
            statDecayTimeline.stop();
        }

        if (tempAnimationTimer != null) {
            tempAnimationTimer.stop();
        }

        if (stateMonitorTimeline != null) {
            stateMonitorTimeline.stop();
        }

        // Dispose of all DrawBars to stop their animations
        if (happinessBar != null) happinessBar.dispose();
        if (healthBar != null) healthBar.dispose();
        if (fullnessBar != null) fullnessBar.dispose();
        if (sleepBar != null) sleepBar.dispose();

        if (chaoAnimation != null) {
            chaoAnimation.stopAnimation();
        }
    }
}