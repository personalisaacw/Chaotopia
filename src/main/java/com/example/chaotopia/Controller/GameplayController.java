package com.example.chaotopia.Controller;
import com.example.chaotopia.Model.Chao;
import com.example.chaotopia.Model.Commands;
import com.example.chaotopia.Model.Inventory;
import com.example.chaotopia.Model.Item;
import com.example.chaotopia.Model.Score;

import com.example.chaotopia.Model.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import com.example.chaotopia.Model.AnimationState;
import javafx.scene.layout.BorderPane;


import java.net.URL;
import java.util.ResourceBundle;

/**
 * GameplayController extends BaseController and integrates with ChaoStatusController
 * to manage game interaction and UI updates.
 * @author Issac Wang, Rosaline Scully
 */
public class GameplayController extends BaseController implements Initializable {

    //JavaFX
    @FXML private Label scoreLabel;
    @FXML private Label messageLabel;
    @FXML private BorderPane mainContainer;

    //Timelines
    private Timeline messageTimeline;

    //Instances
    private Chao chao;
    private Inventory inventory;
    private GameplayAnimationController statusController;
    private Score score;

    //todo: loadGame function (calls load game class)

    /**
     * Initializes the controller with required resources.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // change everything in here based on load game
        inventory = new Inventory();
        statusController = new GameplayAnimationController();
        score = new Score(0);

        // Initialize message label if it's found
        if (messageLabel != null) {
            messageLabel.setVisible(false);
        }

        // Add some default items for testing
        inventory.addItem("Red Fruit", 20);
        inventory.addItem("Blue Fruit", 20);
        inventory.addItem("Green Fruit", 20);
        inventory.addItem("Hero Fruit", 20);
        inventory.addItem("Dark Fruit", 20);
        inventory.addItem("Trumpet", 20);
        inventory.addItem("Duck", 20);
        inventory.addItem("T.V.", 20);
    }

    /**
     * Sets the ChaoStatusController reference to enable UI updates.
     *
     * @param statusController The ChaoStatusController instance
     */
    public void setStatusController(GameplayAnimationController statusController) {
        this.statusController = statusController;
    }

    /**
     * Sets the Chao reference for both this controller and the status controller.
     *
     * @param chao The Chao object
     */
    public void setChao(Chao chao) {
        this.chao = chao;

        // Update the status controller if available
        if (statusController != null) {
            statusController.setChao(chao);
        }
    }



    //play function
    @FXML
    public void playChao() {
        if (isInteractionAllowed("PLAY")) {
            Commands.play(chao);

            // Update UI and show animation
            if (statusController != null) {
                statusController.updateStatusBars();
                score.updateScore(10);
                updateScoreUI(score.getScore());
                statusController.showHappyAnimation();
            }
        }
    }

    //sleep function
    @FXML
    public void sleepChao() {
        if (isInteractionAllowed("SLEEP")) {
            Commands.sleep(chao);

            // Update UI and manage sleep state
            if (statusController != null && chao.getState() == State.SLEEPING) {
                statusController.updateStatusBars();
                score.updateScore(7);
                updateScoreUI(score.getScore());

                // Force animation update for immediate feedback
                statusController.updateChaoAnimation(State.SLEEPING);

                statusController.startSleepIncrease();
                statusController.enableAllInteractions(false); // Disable buttons during sleep
            }
        }
        //todo: add keyboard shortcuts
        //todo: play the proper sound
    }

    //exercise function
    @FXML
    public void exerciseChao() {
        if (isInteractionAllowed("EXERCISE")) {
            Commands.exercise(chao);

            // Update UI and show animation
            if (statusController != null) {
                statusController.updateStatusBars();
                score.updateScore(10);
                updateScoreUI(score.getScore());
                statusController.showHappyAnimation();
            }
        }
        //todo: add keyboard shortcuts
        //todo: play the proper sound
    }

    //vet function
    @FXML
    public void vetChao() {
        if (isInteractionAllowed("VET")) {
            Commands.vet(chao);

            // Update UI and possibly deduct points
            if (statusController != null) {
                statusController.updateStatusBars();
                score.updateScore(-20);
                updateScoreUI(score.getScore());
                statusController.showHappyAnimation();
            }
        }
        //todo: add keyboard shortcuts
        //todo: play the proper sound
    }

    //pet function
    @FXML
    public void petChao() {
        if (isInteractionAllowed("PET")) {
            // Store current alignment before adjustment
            int previousAlignment = chao.getAlignment();

            // Apply the pet command
            Commands.pet(chao);

            // Check if evolution threshold has been crossed
            if (previousAlignment < 7 && chao.getAlignment() >= 7) {
                // Schedule evolution
                triggerHeroEvolution();
            } else {
                // Regular stat updates and animation
                if (statusController != null) {
                    statusController.updateStatusBars();
                    score.updateScore(3);
                    updateScoreUI(score.getScore());
                    statusController.showHappyAnimation();
                }
            }
        }
        //todo: add keyboard shortcuts
        //todo: play the proper sound
    }

    /**
     * Handles the Hero evolution sequence
     */
    private void triggerHeroEvolution() {
        if (statusController != null) {
            // Update score
            score.updateScore(50); // Bonus points for evolution
            updateScoreUI(score.getScore());

            // Disable interactions during evolution sequence
            statusController.enableAllInteractions(false);

            // First play sit animation for 4 seconds
            AnimationState sitState = AnimationState.SIT;
            statusController.getChaoAnimation().changeAnimation(sitState);

            // Schedule the type change after 4 seconds
            Timeline evolutionTimeline = new Timeline(
                    new KeyFrame(Duration.seconds(4), e -> {
                        // Evolve the Chao
                        chao.evolve();

                        // Update animation to the new type
                        statusController.updateChaoType(chao.getType());

                        // Return to normal state
                        statusController.updateChaoState(true);

                        // Re-enable interactions
                        statusController.enableAllInteractions(true);

                        // Show happy animation to celebrate
                        statusController.showHappyAnimation();
                    })
            );
            evolutionTimeline.play();
        }
    }

    //bonk function
    @FXML
    public void bonkChao() {
        if (isInteractionAllowed("BONK")) {
            // Store current alignment before adjustment
            int previousAlignment = chao.getAlignment();

            // Apply the bonk command
            Commands.bonk(chao);

            // Update UI
            if (statusController != null) {
                statusController.updateStatusBars();
                score.updateScore(-3);
                updateScoreUI(score.getScore());

                // Check if evolution threshold has been crossed
                if (previousAlignment > -7 && chao.getAlignment() <= -7) {
                    // Schedule dark evolution
                    triggerDarkEvolution();
                } else {
                    // Just play hungry animation once
                    AnimationState hungryState = AnimationState.HUNGRY;
                    statusController.getChaoAnimation().changeAnimation(hungryState);

                    // Schedule return to previous state after animation cycle
                    Timeline returnTimeline = new Timeline(
                            new KeyFrame(Duration.seconds(1), e -> {
                                // Return to appropriate state based on current conditions
                                statusController.updateChaoState(true);
                            })
                    );
                    returnTimeline.play();
                }
            }
        }
        //todo: add keyboard shortcuts
        //todo: play the proper sound
    }

    /**
     * Handles the Dark evolution sequence
     */
    private void triggerDarkEvolution() {
        if (statusController != null) {
            // Update score
            score.updateScore(25); // Smaller bonus for dark evolution
            updateScoreUI(score.getScore());

            // Disable interactions during evolution sequence
            statusController.enableAllInteractions(false);

            // First play sit animation for 4 seconds
            AnimationState sitState = AnimationState.SIT;
            statusController.getChaoAnimation().changeAnimation(sitState);

            // Schedule the type change after 4 seconds
            Timeline evolutionTimeline = new Timeline(
                    new KeyFrame(Duration.seconds(4), e -> {
                        // Evolve the Chao
                        chao.evolve();

                        // Update animation to the new type
                        statusController.updateChaoType(chao.getType());

                        // Return to normal state
                        statusController.updateChaoState(true);

                        // Re-enable interactions
                        statusController.enableAllInteractions(true);

                        // Show angry animation to fit dark evolution
                        AnimationState angryState = AnimationState.ANGRY;
                        statusController.getChaoAnimation().changeAnimation(angryState);

                        // Return to normal after brief angry display
                        Timeline returnTimeline = new Timeline(
                                new KeyFrame(Duration.seconds(2), e2 -> {
                                    statusController.updateChaoState(true);
                                })
                        );
                        returnTimeline.play();
                    })
            );
            evolutionTimeline.play();
        }
    }

    //gifts a trumpet
    @FXML
    public void giftTrumpet() {
        giftItem("Trumpet");
    }

    //gifts a duck
    @FXML
    public void giftDuck() {
        giftItem("Duck");
    }

    //gifts a tv
    @FXML
    public void giftTV() {
        giftItem("T.V.");
    }

    /**
     * Helper method to handle gift items and update UI
     *
     * @param itemName The name of the item to gift
     */
    private void giftItem(String itemName) {
        if (isInteractionAllowed("GIFT") && inventory.getItemCount(itemName) > 0) {
            Commands.give(chao, new Item(itemName));
            inventory.removeItem(itemName);

            // Update UI and show animation
            if (statusController != null) {
                statusController.updateStatusBars();
                score.updateScore(10);
                updateScoreUI(score.getScore());
                statusController.showHappyAnimation();
                displayMessage(chao.getName() + " enjoyed the " + itemName + "!", 1.5);
            }
        } else if (inventory.getItemCount(itemName) <= 0){
            // TODO: add a sounds effect
            displayMessage("No " + itemName + " available!", 2.0);
        }
    }

    public void feedRedFruit() {
        feedFruit("Red Fruit", false);
    }

    public void feedBlueFruit() {
        feedFruit("Blue Fruit", false);
    }

    public void feedGreenFruit() {
        feedFruit("Green Fruit", false);
    }

    public void feedHeroFruit() {
        feedFruit("Hero Fruit", true);
    }

    public void feedDarkFruit() {
        feedFruit("Dark Fruit", true);
    }

    /**
     * Helper method to handle feeding fruits and update UI
     *
     * @param fruitName The name of the fruit to feed
     * @param isSpecial Whether this is a special alignment-changing fruit
     */
    private void feedFruit(String fruitName, boolean isSpecial) {
        // First check if the command is allowed based on Chao state
        if (!isInteractionAllowed("FEED")) {
            // Display appropriate message based on state
            if (chao.getState() == State.ANGRY) {
                displayMessage(chao.getName() + " refuses to eat while angry!", 2.0);
            } else if (chao.getState() == State.SLEEPING) {
                displayMessage(chao.getName() + " is sleeping!", 2.0);
            } else if (chao.getStatus().isDead()) {
                displayMessage(chao.getName() + " is no longer with us...", 2.0);
            }
            return;
        }

        // Then check if we have the item in inventory
        if (inventory.getItemCount(fruitName) <= 0) {
            displayMessage("No " + fruitName + " available!", 2.0);
            return;
        }

        // If we reach here, both Chao state and inventory allow feeding
        Item fruit = new Item(fruitName);

        // Store current alignment and type
        int previousAlignment = chao.getAlignment();
        ChaoType previousType = chao.getType();

        if (isSpecial) {
            Commands.feedSpecialFruit(chao, fruit);

            // Check if Hero fruit pushed alignment over the Hero threshold
            if (fruitName.equals("Hero Fruit") && previousAlignment < 7 && chao.getAlignment() >= 7) {
                triggerHeroEvolution();
                inventory.removeItem(fruitName);
                displayMessage(chao.getName() + " is evolving into a Hero Chao!", 3.0);
                return; // Skip normal processing since evolution is triggered
            }

            // Check if Dark fruit pushed alignment over the Dark threshold
            if (fruitName.equals("Dark Fruit") && previousAlignment > -7 && chao.getAlignment() <= -7) {
                triggerDarkEvolution();
                inventory.removeItem(fruitName);
                displayMessage(chao.getName() + " is evolving into a Dark Chao!", 3.0);
                return; // Skip normal processing since evolution is triggered
            }

            // If no evolution was triggered but the alignment changed significantly,
            // we need to manually check if the type should change
            if (previousType != chao.getType() && statusController != null) {
                chao.evolve(); // Make sure type updates based on alignment
                statusController.updateChaoType(chao.getType());
            }

        } else {
            Commands.feed(chao, fruit);
        }

        inventory.removeItem(fruitName);

        // Update UI and show animation
        if (statusController != null) {
            statusController.updateStatusBars();
            score.updateScore(5);
            updateScoreUI(score.getScore());
            statusController.showHappyAnimation();
        }
    }

    /**
     * Saves the current game state.
     */
    @FXML
    public void saveGame() {
        // TODO: Implement save game functionality to JSON
        System.out.println("Game saved (not actually implemented yet)");
    }

    /**
     * Returns to the main menu.
     */
    @FXML
    public void goToMainMenu() {
        // TODO: Navigate back to main menu
        System.out.println("Returning to main menu (not actually implemented yet)");
    }

    /**
     * Cleans up resources when the controller is no longer needed.
     */
    public void shutdown() {
        if (statusController != null) {
            statusController.shutdown();
        }
    }

    /**
     * Displays a message on screen or falls back to console if UI elements aren't ready.
     *
     * @param message The message to display
     * @param durationSeconds How long to show the message (in seconds)
     */
    public void displayMessage(String message, double durationSeconds) {
        // First print to console as a fallback
        System.out.println(message);

        try {
            // Only attempt UI updates if we're in a properly initialized state
            if (mainContainer != null && mainContainer.getScene() != null) {
                // Check if we need to create the label
                if (messageLabel == null) {
                    messageLabel = new Label();
                    messageLabel.setStyle("-fx-background-color: rgba(0,0,0,0.8); -fx-text-fill: white; " +
                            "-fx-padding: 10 15; -fx-background-radius: 5; " +
                            "-fx-font-size: 14px; -fx-font-weight: bold;");
                    messageLabel.setManaged(false); // So it doesn't affect layout
                    messageLabel.setVisible(false);
                }

                // Set the message text
                messageLabel.setText(message);

                // Add to scene if not already there
                if (!mainContainer.getChildren().contains(messageLabel)) {
                    mainContainer.getChildren().add(messageLabel);
                    messageLabel.setLayoutX(mainContainer.getWidth() - 250); // Position in bottom right
                    messageLabel.setLayoutY(mainContainer.getHeight() - 100);
                }

                // Make visible
                messageLabel.setVisible(true);

                // Create timeline to hide it after duration
                if (messageTimeline != null) {
                    messageTimeline.stop();
                }

                messageTimeline = new Timeline(
                        new KeyFrame(Duration.seconds(durationSeconds), e -> {
                            messageLabel.setVisible(false);
                        })
                );
                messageTimeline.play();
            }
        } catch (Exception e) {
            // If anything goes wrong, we still have the console output
            System.err.println("Error displaying message in UI: " + e.getMessage());
        }
    }

    /**
     * Creates the message label if it doesn't exist in the FXML
     */
    private void createMessageLabel() {
        messageLabel = new Label();
        messageLabel.setVisible(false);
        messageLabel.setManaged(false);
        messageLabel.setMouseTransparent(true);

        // Set it to appear above other elements
        StackPane.setAlignment(messageLabel, javafx.geometry.Pos.BOTTOM_RIGHT);
        StackPane.setMargin(messageLabel, new javafx.geometry.Insets(0, 20, 20, 0));
    }

    public static void showGameMessage(String message) {
        // This is a static method that will be called from Commands
        // We need to get the active controller instance
        System.out.println(message);
//        try {
//            // Try to get the current scene
//            Scene scene = javafx.stage.Stage.getWindows().stream()
//                    .filter(window -> window instanceof javafx.stage.Stage)
//                    .map(window -> ((javafx.stage.Stage) window).getScene())
//                    .findFirst()
//                    .orElse(null);
//
//            if (scene != null) {
//                GameplayController controller = (GameplayController) scene.getUserData();
//                if (controller != null) {
//                    // Use Platform.runLater to ensure UI updates happen on JavaFX thread
//                    javafx.application.Platform.runLater(() -> {
//                        controller.displayMessage(message, 2.5); // Show for 2.5 seconds
//                    });
//                }
//            }
//        } catch (Exception e) {
//            // Fallback to console if something went wrong
//            System.out.println(message);
//        }
    }

    /**
     * Checks if interaction with the Chao is allowed based on its current state
     *
     * @param commandType The type of command being attempted
     * @return true if interaction is allowed, false otherwise
     */
    private boolean isInteractionAllowed(String commandType) {
        if (chao == null) {
            return false;
        }

        State currentState = chao.getState();

        // Dead state: No commands allowed
        if (chao.getStatus().isDead()) {
            return false;
        }

        // Sleeping state: No commands allowed
        if (currentState == State.SLEEPING) {
            return false;
        }

        // Angry state: Only Gift and Play allowed
        if (currentState == State.ANGRY) {
            return commandType.equals("GIFT") || commandType.equals("PLAY");
        }

        // Hungry or Normal state: All commands allowed
        return true;
    }

    /**
     * Updates the score display.
     *
     * @param newScore the new score value
     */
    private void updateScoreUI(int newScore) {
        scoreLabel.setText("SCORE: " + newScore);
    }

}