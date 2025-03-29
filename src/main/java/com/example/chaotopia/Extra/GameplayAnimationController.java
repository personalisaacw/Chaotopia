package com.example.chaotopia.Extra;

import com.example.chaotopia.Controller.GameplayController;
import com.example.chaotopia.Model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import javafx.scene.Node;

/**
 * Controller class for managing the Chao status UI and game logic.
 * @author Rosaline Scully
 */
public class GameplayAnimationController implements Initializable {

    @FXML private VBox statusBarsContainer;
    @FXML private Label nameLabel;
    @FXML private ImageView chaoImageView;
    @FXML private BorderPane mainContainer;

    private VBox gameOverBox;
    private Chao chao;
    private DrawBar happinessBar;
    private DrawBar healthBar;
    private DrawBar fullnessBar;
    private DrawBar sleepBar;
    private ChaoAnimation chaoAnimation;

    private Timeline statDecayTimeline;
    private boolean isSleeping = false;
    private State previousState = State.NORMAL;

    private Timeline stateMonitorTimeline;
    private Timeline sleepIncreaseTimeline;
    private Timeline tempAnimationTimer;
    public Label scoreLabel;

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
     * Manually initializes the controller with required UI elements.
     * This is used for testing or when not loading from FXML.
     *
     * @param statusBarsContainer VBox to hold status bars
     * @param nameLabel Label for Chao name
     * @param scoreLabel Label for score display
     * @param chaoImageView ImageView for Chao animation
     * @param mainContainer BorderPane main container
     */
    public void initializeManually(VBox statusBarsContainer, Label nameLabel,
                                  Label scoreLabel, ImageView chaoImageView,
                                   BorderPane mainContainer) {
        this.statusBarsContainer = statusBarsContainer;
        this.nameLabel = nameLabel;
        this.scoreLabel = scoreLabel;
        this.chaoImageView = chaoImageView;
        this.mainContainer = mainContainer;

        // Now call the regular initialize method
        initialize(null, null);
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
     * Gets the current Chao.
     *
     * @return the current Chao
     */
    public Chao getChao() {
        return this.chao;
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
                showGameOverScreen();
                statDecayTimeline.stop();
            }
        }
    }

    /**
     * Starts the sleep increase process when the Chao is sleeping.
     */
    public void startSleepIncrease() {
        isSleeping = true;

        // Stop any existing sleep timeline
        if (sleepIncreaseTimeline != null) {
            sleepIncreaseTimeline.stop();
        }

        // Force animation update for immediate feedback
        chaoAnimation.changeState(State.SLEEPING);
        chaoAnimation.stopAnimation();
        chaoAnimation.startAnimation();

        // Create a new timeline that increases sleep by 2 points every 500ms
        sleepIncreaseTimeline = new Timeline(
                new KeyFrame(Duration.millis(500), e -> {
                    if (chao != null && isSleeping && chao.getStatus().getSleep() < 100) {
                        chao.getStatus().adjustSleep(2);
                        updateStatusBars();
                    } else {
                        // Stop the timeline when sleep is full or chao isn't sleeping
                        sleepIncreaseTimeline.stop();

                        // If sleep is full, wake up
                        if (chao != null && chao.getStatus().getSleep() >= 100) {
                            isSleeping = false;
                            enableAllInteractions(true);

                            // Update state based on current conditions
                            updateChaoState(true);
                        }
                    }
                })
        );

        sleepIncreaseTimeline.setCycleCount(Timeline.INDEFINITE);
        sleepIncreaseTimeline.play();
    }

    /**
     * Updates the Chao animation to the specified state.
     *
     * @param state the state to animate
     */
    public void updateChaoAnimation(State state) {
        if (chaoAnimation != null) {
            chaoAnimation.changeState(state);
            chaoAnimation.stopAnimation();
            chaoAnimation.startAnimation();
        }
    }

    /**
     * Gets the current ChaoAnimation object.
     *
     * @return the ChaoAnimation object
     */
    public ChaoAnimation getChaoAnimation() {
        return this.chaoAnimation;
    }

    /**
     * Updates the Chao type animation.
     *
     * @param type the new Chao type
     */
    public void updateChaoType(ChaoType type) {
        if (chaoAnimation != null) {
            chaoAnimation.changeChaoType(type);
            chaoAnimation.stopAnimation();
            chaoAnimation.startAnimation();
        }
    }

    /**
     * Schedules a state change after a delay.
     *
     * @param newState the state to change to
     * @param delayInSeconds the delay in seconds
     */
    public void scheduleStateChange(State newState, double delayInSeconds) {
        // Cancel any existing temporary animation timer
        if (tempAnimationTimer != null) {
            tempAnimationTimer.stop();
        }

        // Schedule state change after delay
        tempAnimationTimer = new Timeline(
                new KeyFrame(Duration.seconds(delayInSeconds), e -> {
                    chao.setState(newState);

                    // Force animation update
                    chaoAnimation.changeState(newState);
                    chaoAnimation.stopAnimation();
                    chaoAnimation.startAnimation();
                })
        );
        tempAnimationTimer.setCycleCount(1);
        tempAnimationTimer.play();
    }

    /**
     * Shows a temporary happy animation after positive actions.
     * Returns to the previous state after a delay.
     *
     * @return true if animation was shown, false otherwise
     */
    public boolean showHappyAnimation() {
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
            return true;
        }
        return false;
    }

    /**
     * Enables or disables all interaction buttons except New Game and Load Game.
     *
     * @param enable true to enable buttons, false to disable them
     */
    public void enableAllInteractions(boolean enable) {
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
     * DON'T NEED THIS
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

            // Show happy animation
            showHappyAnimation();
        }
    }

    /**
     * DON'T NEED THIS
     */
    @FXML
    public void playWithChao() {
        if (chao != null && !chao.getStatus().isDead() && !isSleeping) {
            // Playing is always allowed in angry state since it increases happiness
            chao.getStatus().adjustHappiness(20);

            updateStatusBars();

            // Show happy animation
            showHappyAnimation();
        }
    }

    /**
     * DON'T NEED THIS
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

            // Force animation update for immediate feedback
            chaoAnimation.changeState(State.SLEEPING);
            chaoAnimation.stopAnimation();
            chaoAnimation.startAnimation();

            // Disable interactions during sleep
            enableAllInteractions(false);
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

    /**
     * Continuously monitors Chao state to ensure immediate visual updates
     * Runs independently of the stat decay system
     */
    private void monitorChaoState() {
        if (chao == null || chao.getStatus().isDead()) return;

        Status status = chao.getStatus();
        State currentState = chao.getState();
        State newState = currentState; // Start assuming no change

        // Determine the highest priority state based on current stats
        if (status.isDead()) {
            newState = State.DEAD;
        } else if (isSleeping) {
            // ... (existing logic for when already sleeping) ...
            newState = State.SLEEPING; // Keep sleeping state
            if (status.getSleep() >= 100) {
                // Wake up logic...
                isSleeping = false;
                enableAllInteractions(true);
                // Determine next state after waking up...
                if (status.getHappiness() <= 0) newState = State.ANGRY;
                else if (status.getFullness() <= 0) newState = State.HUNGRY;
                else newState = State.NORMAL;
                // Stop sleep increase timeline if running
                if (sleepIncreaseTimeline != null && sleepIncreaseTimeline.getStatus() == Timeline.Status.RUNNING) {
                    sleepIncreaseTimeline.stop();
                }
            }

        } else if (status.getSleep() <= 0) { // Check if sleep just dropped
            // Force sleep when sleep drops to 0 or below
            newState = State.SLEEPING;

            System.out.println("Sleep hit <= 0. Applying health penalty.");
            status.adjustHealth(-15);

            // Update health bar immediately after penalty application
            // Ensure update happens on the JavaFX Application Thread
            Platform.runLater(() -> {
                if (healthBar != null) {
                    healthBar.updateValue(status.getHealth());
                }
            });

            isSleeping = true;
            enableAllInteractions(false);
            startSleepIncrease();

        } else if (currentState == State.HAPPY && tempAnimationTimer != null &&
                tempAnimationTimer.getStatus() == Timeline.Status.RUNNING) {
            // Return here to prevent overwriting HAPPY state prematurely
            return;
        } else if (status.getHappiness() <= 0 || (currentState == State.ANGRY && status.getHappiness() < 50)) {
            newState = State.ANGRY;
        } else if (status.getFullness() <= 0) {
            newState = State.HUNGRY;
        } else {
            newState = State.NORMAL;
        }

        // If the state needs to change, update it immediately
        if (newState != currentState) {
            chao.setState(newState);
            // Check chaoAnimation is not null before using
            if(chaoAnimation != null){
                chaoAnimation.changeState(newState);
                // Always restart animation to ensure it shows immediately
                chaoAnimation.stopAnimation();
                chaoAnimation.startAnimation();
            }
        }
    }

    /**
     * Handles state prioritization to determine which animation state should be displayed.
     * Priority order: DEAD > SLEEPING > HUNGRY > ANGRY > NORMAL
     *
     * @param forceUpdate If true, forces the animation to update immediately
     */
    public void updateChaoState(boolean forceUpdate) {
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
        } else if (status.getHappiness() <= 0 || (currentState == State.ANGRY && status.getHappiness() < 50)) {
            newState = State.ANGRY;
        } else if(status.getFullness() <= 0){
            newState = State.HUNGRY;
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
    public void updateChaoState() {
        updateChaoState(false);
    }

    private void decreaseStats() {
        if (chao != null && !chao.getStatus().isDead()) {
            // Store current values to detect changes
            Status status = chao.getStatus();
            State currentState = chao.getState();
            int prevHappiness = status.getHappiness();
            int prevFullness = status.getFullness();
            int prevSleep = status.getSleep();
            int prevHealth = status.getHealth();

            // Instead of directly manipulating stats, call a new method in Commands
            Commands.applyNaturalDecay(chao);

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
        }
    }

    /**
     * Shows the game over screen when the Chao dies.
     */
    private void showGameOverScreen() {
        // First ensure we're on the JavaFX thread
        if (!javafx.application.Platform.isFxApplicationThread()) {
            javafx.application.Platform.runLater(this::showGameOverScreen);
            return;
        }

        try {
            // Disable the action buttons
            enableAllInteractions(false);

            // Create game over popup components
            VBox gameOverBox = new VBox(10);
            gameOverBox.setAlignment(javafx.geometry.Pos.CENTER);
            gameOverBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 20px; " +
                    "-fx-border-color: #FF4136; -fx-border-width: 3px; -fx-border-radius: 10px; " +
                    "-fx-background-radius: 10px;");
            gameOverBox.setMaxSize(300, 150);
            gameOverBox.setMinSize(250, 120);

            Label gameOverLabel = new Label("GAME OVER");
            gameOverLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-font-weight: bold;");

            HBox buttonBox = new HBox(15);
            buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
            buttonBox.setPadding(new javafx.geometry.Insets(10, 0, 0, 0));

            Button newGameButton = new Button("Play Again?");
            newGameButton.setStyle("-fx-background-color: #2ECC40; -fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-padding: 8 15; -fx-background-radius: 5;");
            newGameButton.setOnAction(e -> startNewGame());

            Button mainMenuButton = new Button("Main Menu");
            mainMenuButton.setStyle("-fx-background-color: #0074D9; -fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-padding: 8 15; -fx-background-radius: 5;");
            mainMenuButton.setOnAction(e -> {
                // Attempt to navigate to main menu
                Scene scene = mainContainer.getScene();
                if (scene != null && scene.getUserData() instanceof GameplayController) {
                    ((GameplayController) scene.getUserData()).goToMainMenu();
                }
            });

            buttonBox.getChildren().addAll(newGameButton, mainMenuButton);
            gameOverBox.getChildren().addAll(gameOverLabel, buttonBox);

            // Create a full-screen transparent overlay for the popup
            StackPane overlay = new StackPane();
            overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);"); // Semi-transparent background
            overlay.getChildren().add(gameOverBox);
            overlay.setMouseTransparent(false); // Allow clicking on buttons

            // Position in center bottom area
            StackPane.setAlignment(gameOverBox, javafx.geometry.Pos.BOTTOM_CENTER);
            StackPane.setMargin(gameOverBox, new javafx.geometry.Insets(0, 0, 70, 0));

            // Store reference for later removal
            this.gameOverBox = gameOverBox;

            // Add to scene - use BorderPane directly if possible
            Scene scene = chaoImageView.getScene();
            if (scene != null) {
                BorderPane root = (BorderPane) scene.getRoot();
                if (root != null) {
                    // Use BorderPane's center as a StackPane
                    StackPane centerStack = (StackPane) root.lookup("#centerStackPane");

                    if (centerStack == null) {
                        // If no StackPane exists, create one and add current content to it
                        centerStack = new StackPane();
                        centerStack.setId("centerStackPane");

                        // Move current center content to StackPane
                        Node currentCenter = root.getCenter();
                        root.setCenter(null);
                        centerStack.getChildren().add(currentCenter);
                        root.setCenter(centerStack);
                    }

                    // Add overlay to the StackPane
                    centerStack.getChildren().add(overlay);
                }
            }
        } catch (Exception e) {
            System.err.println("Error showing game over screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Starts a new game.
     */
    private void startNewGame() {
        try {
            // Create a new Chao with fresh stats
            Status initialStatus = new Status(100, 100, 100, 100);
            Chao newChao = new Chao(0, "Sonic Jr.", ChaoType.BLUE, State.NORMAL, initialStatus);

            // Reset sleep status
            isSleeping = false;

            // Set the new Chao
            setChao(newChao);

            // Remove game over overlay
            Scene scene = chaoImageView.getScene();
            if (scene != null) {
                BorderPane root = (BorderPane) scene.getRoot();
                if (root != null) {
                    StackPane centerStack = (StackPane) root.lookup("#centerStackPane");
                    if (centerStack != null && centerStack.getChildren().size() > 1) {
                        // Remove the last child which should be our overlay
                        centerStack.getChildren().remove(centerStack.getChildren().size() - 1);
                    }
                }
            }

            // Re-enable interactions
            enableAllInteractions(true);

            // Restart the decay timeline
            if (statDecayTimeline != null && statDecayTimeline.getStatus() != Timeline.Status.RUNNING) {
                statDecayTimeline.play();
            }
        } catch (Exception e) {
            System.err.println("Error starting new game: " + e.getMessage());
            e.printStackTrace();
        }
    }
}