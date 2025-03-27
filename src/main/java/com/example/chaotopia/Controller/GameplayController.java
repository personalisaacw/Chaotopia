package com.example.chaotopia.Controller;
import com.example.chaotopia.Model.Chao;
import com.example.chaotopia.Model.Commands;
import com.example.chaotopia.Model.Inventory;
import com.example.chaotopia.Model.Item;
import com.example.chaotopia.Model.Score;

import com.example.chaotopia.Model.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

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
        if (isInteractionAllowed()) {
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
        if (isInteractionAllowed()) {
            Commands.sleep(chao);

            // Update UI and manage sleep state
            if (statusController != null && chao.getState() == State.SLEEPING) {
                statusController.updateStatusBars();
                score.updateScore(7);
                updateScoreUI(score.getScore());
                statusController.startSleepIncrease();
                statusController.enableAllInteractions(false); // Disable buttons during sleep
            }
        }
        //todo: add keyboard shortcuts
        //todo: play the chao animation
        //todo: play the proper sound
    }

    //exercise function
    @FXML
    public void exerciseChao() {
        if (isInteractionAllowed()) {
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
        //todo: play the chao animation
        //todo: play the proper sound
    }

    //vet function
    @FXML
    public void vetChao() {
        if (isInteractionAllowed()) {
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
        //todo: play the chao animation
        //todo: play the proper sound
    }

    //pet function
    @FXML
    public void petChao() {
        if (isInteractionAllowed()) {
            Commands.pet(chao);

            // Update UI and show animation
            if (statusController != null) {
                statusController.updateStatusBars();
                score.updateScore(3);
                updateScoreUI(score.getScore());
                statusController.showHappyAnimation();
            }
        }
        //todo: add keyboard shortcuts
        //todo: play the chao animation
        //todo: play the proper sound
    }

    //bonk function
    @FXML
    public void bonkChao() {
        if (isInteractionAllowed()) {
            Commands.bonk(chao);

            // Update UI without showing happy animation (it's a bonk!)
            if (statusController != null) {
                statusController.updateStatusBars();
                score.updateScore(-3);
                updateScoreUI(score.getScore());

                // Change to angry state temporarily if not already angry
                if (chao.getState() != State.ANGRY) {
                    State previousState = chao.getState();
                    chao.setState(State.ANGRY);

                    // Force animation update
                    statusController.updateChaoAnimation(State.ANGRY);

                    // Schedule return to previous state after a delay
                    statusController.scheduleStateChange(previousState, 2.0); // 2 second delay
                }
            }
        }
        //todo: add keyboard shortcuts
        //todo: play the chao animation
        //todo: play the proper sound
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
        if (isInteractionAllowed() && inventory.getItemCount(itemName) > 0) {
            Commands.give(chao, new Item(itemName));
            inventory.removeItem(itemName);

            // Update UI and show animation
            if (statusController != null) {
                statusController.updateStatusBars();
                score.updateScore(10);
                updateScoreUI(score.getScore());
                statusController.showHappyAnimation();
            }
        } else {
            // TODO: Display "No items available" message and or sound effect
            System.out.println("No " + itemName + " available!");
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
        if (isInteractionAllowed() && inventory.getItemCount(fruitName) > 0) {
            Item fruit = new Item(fruitName);

            if (isSpecial) {
                Commands.feedSpecialFruit(chao, fruit);

                // Check if the type changed due to alignment change
                ChaoType previousType = chao.getType();
                // Let's assume the Chao updates its type internally based on alignment
                if (previousType != chao.getType() && statusController != null) {
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
        } else {
            // TODO: Display "No items available" message
            System.out.println("No " + fruitName + " available!");
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
     * Checks if interaction with the Chao is allowed based on its current state
     *
     * @return true if interaction is allowed, false otherwise
     */
    private boolean isInteractionAllowed() {
        if (chao == null) {
            return false;
        }

        // Cannot interact if dead or sleeping (except for specific actions)
        return !chao.getStatus().isDead() && chao.getState() != State.SLEEPING;
    }

    /**
     * Updates the score display.
     *
     * @param newScore the new score value
     */
    private void updateScoreUI(int newScore) {
        scoreLabel.setText("SCORE: " + score);
    }

}