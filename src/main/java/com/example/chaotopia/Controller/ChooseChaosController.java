package com.example.chaotopia.Controller;
import com.example.chaotopia.Model.*;

import javafx.fxml.FXML;
import java.io.IOException;


public class ChooseChaosController extends BaseController {
    private static final boolean EXISTING_SLOT = true;
    private int slotIndex;

    public void setSlotIndex(int slotIndex) {
        this.slotIndex = slotIndex;
    }

    @FXML
    private void selectRedChao() {
        Chao chao = new Chao(0, "Chao", ChaoType.RED, State.NORMAL, new Status());
        handleChaoSelection(chao);
    }

    @FXML
    private void selectBlueChao() {
        Chao chao = new Chao(0, "Chao", ChaoType.BLUE, State.NORMAL, new Status());
        handleChaoSelection(chao);
    }

    @FXML
    private void selectGreenChao() {
        Chao chao = new Chao(0, "Chao", ChaoType.GREEN, State.NORMAL, new Status());
        handleChaoSelection(chao);
    }

    // Selects the new Chao that the player picks and initializes a new game file
    private void handleChaoSelection(Chao chao) {
        //todo: add confirm selection button when player clicks on chao

        // Creates a new save file
        GameFile game = new GameFile(slotIndex, chao, new Inventory(), new Score(0), 0L, 0, 0L);
        try {
            game.save();
            System.out.println("Game saved successfully!");
        } catch (IOException e) {
            System.err.println("Failed to save game: " + e.getMessage());
        }

        goToGameScreen();
    }

    // TODO: Connect to actual gameplay
    private void goToGameScreen() {
        System.out.println("Go to game screen");
    }
}
