package com.example.chaotopia.Controller;
import com.example.chaotopia.Model.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.io.IOException;


public class NewGameController extends BaseController {
    private static final boolean EXISTING_SLOT = true;
    private int slotIndex;
    Inventory inventory = new Inventory();

    public void setSlotIndex(int slotIndex) {
        this.slotIndex = slotIndex;
    }

    @FXML
    private void selectRedChao(ActionEvent e) {
        Chao chao = new Chao(0, "Chao", ChaoType.RED, State.NORMAL, new Status());
        handleChaoSelection(e,chao);
    }

    @FXML
    private void selectBlueChao(ActionEvent e) {
        Chao chao = new Chao(0, "Chao", ChaoType.BLUE, State.NORMAL, new Status());
        handleChaoSelection(e,chao);
    }

    @FXML
    private void selectGreenChao(ActionEvent e) {
        Chao chao = new Chao(0, "Chao", ChaoType.GREEN, State.NORMAL, new Status());
        handleChaoSelection(e,chao);
    }

    // Selects the new Chao that the player picks and initializes a new game file
    private void handleChaoSelection(ActionEvent e, Chao chao) {
        //todo: add confirm selection button when player clicks on chao
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

    private void goToGameScreen(ActionEvent e) throws IOException {
        switchScene(e, "/com/example/chaotopia/View/Gameplay.fxml", controller -> {
            if (controller instanceof GameplayController) {
                ((GameplayController) controller).setSlotIndex(slotIndex);
            }
        });
    }
}
