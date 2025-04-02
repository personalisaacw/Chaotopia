package com.example.chaotopia.Controller;
import com.example.chaotopia.Components.InputPopup;
import com.example.chaotopia.Model.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Controller for the New Game screen, handling Chao selection and game initialization.
 * This class allows the player to select a Chao type and name, then initializes a new game file
 * with default inventory and navigates to the gameplay screen.
 */
public class NewGameController extends BaseController {
    /** Constant representing the boolean state of a save slot with existing data. */
    private static final boolean EXISTING_SLOT = true;
    /** The index (0, 1, or 2) of the save slot selected on the previous screen (LoadGameController) where this new game will be saved. */
    private int slotIndex;
    /** An instance of the {@link Inventory} class, initialized to hold the default starting items for the new game. */
    Inventory inventory = new Inventory();

    /**
     * Sets the slot index for the new game.
     *
     * @param slotIndex The index of the slot to save the new game in.
     */
    public void setSlotIndex(int slotIndex) {
        this.slotIndex = slotIndex;
    }

    /**
     * Handles the selection of a Red Chao, prompting the user for a name and initializing the game.
     *
     * @param e The ActionEvent triggered by the selection.
     */
    @FXML
    private void selectRedChao(ActionEvent e) {
        String name = getNameFromUser();
        Chao chao = new Chao(0, name, ChaoType.RED, State.NORMAL, new Status());
        handleChaoSelection(e,chao);
    }

    /**
     * Handles the selection of a Blue Chao, prompting the user for a name and initializing the game.
     *
     * @param e The ActionEvent triggered by the selection.
     */
    @FXML
    private void selectBlueChao(ActionEvent e) {
        String name = getNameFromUser();
        Chao chao = new Chao(0, name, ChaoType.BLUE, State.NORMAL, new Status());
        handleChaoSelection(e,chao);
    }

    /**
     * Handles the selection of a Green Chao, prompting the user for a name and initializing the game.
     *
     * @param e The ActionEvent triggered by the selection.
     */
    @FXML
    private void selectGreenChao(ActionEvent e) {
        String name = getNameFromUser();
        Chao chao = new Chao(0, name, ChaoType.GREEN, State.NORMAL, new Status());
        handleChaoSelection(e,chao);
    }

    /**
     * Initializes a new game with the selected Chao and navigates to the gameplay screen.
     *
     * @param e    The ActionEvent triggering the selection.
     * @param chao The selected Chao object.
     */
    private void handleChaoSelection(ActionEvent e, Chao chao) {
        addDefaultInventory();
        // Creates a new save file
        GameFile game = new GameFile(slotIndex, chao, inventory, new Score(0), 0L, 0, 0L);
        try {
            game.save();
            System.out.println("Game saved successfully!");
        } catch (IOException exp) {
            System.err.println("Failed to save game: " + exp.getMessage());
        }

        try{
            goToGameScreen(e);
        }catch (IOException exp){
            System.err.println("Failed to go to game screen: " + exp.getMessage());
        }

    }

    /**
     * Adds default items to the player's inventory at the start of a new game.
     */
    private void addDefaultInventory() {
        System.out.println("Adding default inventory items...");
        inventory.addItem("Red Fruit", 3);
        inventory.addItem("Blue Fruit", 3);
        inventory.addItem("Green Fruit", 3);
        inventory.addItem("Hero Fruit", 3);
        inventory.addItem("Dark Fruit", 3);
        inventory.addItem("Trumpet", 1);
        inventory.addItem("Duck", 1);
        inventory.addItem("T.V.", 1);
    }

    /**
     * Navigates to the gameplay screen, passing the slot index to the controller.
     *
     * @param e The ActionEvent triggering the navigation.
     * @throws IOException If an I/O error occurs during scene switching.
     */
    private void goToGameScreen(ActionEvent e) throws IOException {
        switchScene(e, "/com/example/chaotopia/View/Gameplay.fxml", controller -> {
            if (controller instanceof GameplayController) {
                ((GameplayController) controller).setSlotIndex(slotIndex);
            }
        });
    }

    /**
     * Prompts the user to enter a name for their Chao using an InputPopup dialog.
     *
     * @return The name entered by the user.
     */
    private String getNameFromUser() {
        /* Default name of Chao */
        AtomicReference<String> name = new AtomicReference<>("Chao");
        String title = "Name Your Chao";
        String content = "Please name your Chao in the text box below!";
        InputPopup dialog = new InputPopup(title, content);
        dialog.addButton("Okay", () -> {
            name.set(dialog.getUserInput());
            System.out.println("User entered: " + name);
        }, "btn-submit");
        dialog.showAndWait();
        return name.get();
    }
}
