package com.example.chaotopia.Controller;

import com.example.chaotopia.Model.*;
import com.example.chaotopia.Model.FruitAnimation;
import com.example.chaotopia.Model.FruitType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.ImageView; // Import ImageView
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import javafx.fxml.Initializable;
import com.example.chaotopia.Model.AnimationState;


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
    @FXML private Label nameLabel;
    @FXML private ImageView fruitImageView;

    //Timelines
    private Timeline messageTimeline;

    //Instances
    private Chao chao;
    private Inventory inventory;
    private GameplayAnimationController statusController;
    private Score score;
    private FruitAnimation fruitAnimation;

    //todo: loadGame function (calls load game class)

    /**
     * Initializes the controller with required resources.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // change everything in here based on load game
        inventory = new Inventory();
        //statusController = new GameplayAnimationController();
        score = new Score(0);

        if (messageLabel != null) {
            messageLabel.setVisible(false);
        }

        if (fruitImageView != null) {
            fruitAnimation = new FruitAnimation(fruitImageView, FruitType.RED);
            fruitImageView.setVisible(false); // Ensure it's hidden initially
        } else {
            System.err.println("Warning: fruitImageView is null in GameplayController.initialize. Check FXML.");
        }

        if(scoreLabel != null) scoreLabel.setText("SCORE: " + score.getScore());
        if(nameLabel != null) nameLabel.setText("Chao Name");

        // Add some default items for testing
        inventory.addItem("Red Fruit", 20);
        inventory.addItem("Blue Fruit", 20);
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

        // If chao is already set, pass it to statusController
        if(this.chao != null && this.statusController != null){
            this.statusController.setChao(this.chao);
        }

    }

    /**
     * Sets the Chao reference for both this controller and the status controller.
     *
     * @param chao The Chao object
     */
    public void setChao(Chao chao) {
        this.chao = chao;
        if(nameLabel != null) {
            nameLabel.setText(chao.getName());
        }
        // Pass chao to status controller if it's ready
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
            String commandResult = Commands.vet(chao);
            if (commandResult != null) {
                displayMessage(commandResult, 2.0);
                return;
            }
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
        giftItem("Tv");
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
            //TODO: play sound
            return;
        }

        if (statusController == null || fruitAnimation == null || fruitImageView == null) {
            System.err.println("Cannot feed fruit: Controller or animation components not initialized.");
            return;
        }

        FruitType currentFruitType = getFruitTypeFromName(fruitName);
        if (currentFruitType != null) {
            fruitAnimation.changeFruitType(currentFruitType); // Makes view visible and starts animation
        }
        statusController.showHappyAnimation();

        // If we reach here, both Chao state and inventory allow feeding
        Item fruit = new Item(fruitName);
        int previousAlignment = chao.getAlignment();
        ChaoType previousType = chao.getType();
        boolean evolutionTriggered = false;

        if (isSpecial) {
            Commands.feedSpecialFruit(chao, fruit); // This applies stats/alignment

            // Check for evolution AFTER applying the command
            if (fruitName.equals("Hero Fruit") && previousAlignment < 7 && chao.getAlignment() >= 7) {
                triggerHeroEvolution();
                evolutionTriggered = true;
            } else if (fruitName.equals("Dark Fruit") && previousAlignment > -7 && chao.getAlignment() <= -7) {
                triggerDarkEvolution();
                evolutionTriggered = true;
            }
            // If no immediate evolution, check if type changed due to alignment shift
            else if (previousType != chao.getType() && !evolutionTriggered) {
                chao.evolve(); // Ensure Chao model type is updated
                statusController.updateChaoType(chao.getType());
            }

        } else { // Regular fruit
            Commands.feed(chao, fruit);
        }

        // Update Inventory and Score
        inventory.removeItem(fruitName);
        if (!evolutionTriggered) {
            score.updateScore(5);
            updateScoreUI(score.getScore());
        }
        // Update status bars
        statusController.updateStatusBars();
    }

    /**
     * Helper method to convert fruit name string to FruitType enum.
     *
     * @param fruitName Name like "Red Fruit", "Hero Fruit"
     * @return Corresponding FruitType or null if not found
     */
    private FruitType getFruitTypeFromName(String fruitName) {
        switch (fruitName) {
            case "Red Fruit": return FruitType.RED;
            case "Blue Fruit": return FruitType.BLUE;
            case "Green Fruit": return FruitType.GREEN;
            case "Hero Fruit": return FruitType.HERO;
            case "Dark Fruit": return FruitType.DARK;
            default: return null;
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
        System.out.println(message); // Keep console fallback

        // messageLabel should be injected by FXML
        if (messageLabel != null) {
            // Ensure UI updates happen on the JavaFX Application Thread
            javafx.application.Platform.runLater(() -> {
                messageLabel.setText(message);
                messageLabel.setVisible(true);

                // Stop existing timeline if it's running
                if (messageTimeline != null) {
                    messageTimeline.stop();
                }

                // Create timeline to hide the label after duration
                messageTimeline = new Timeline(
                        new KeyFrame(Duration.seconds(durationSeconds), e -> {
                            messageLabel.setVisible(false);
                        })
                );
                messageTimeline.play();
            });
        } else {
            System.err.println("Error: messageLabel is null. Check FXML definition.");
        }
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