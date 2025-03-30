package com.example.chaotopia.Controller;
import com.example.chaotopia.Model.*;

import javafx.fxml.FXML;
import java.io.IOException;


public class ChooseChaosController extends BaseController {
    private static final boolean EXISTING_SLOT = true;
    private int slotIndex;
    private int lastSlotClickedIndex;

    public void initialize() {
//        lastSlotClickedIndex = loadSlotIndex();
        System.out.println("last slot clicked index: " + lastSlotClickedIndex);
    }

    public void setSlotIndex(int slotIndex) {
        this.slotIndex = slotIndex;
    }

    // TODO: Implement load game (Faye - Why are we loading the game?)
//    public static int loadSlotIndex() {
//        try {
//            String content = new String(Files.readAllBytes(Paths.get(SAVE_SLOT_INDEX_FILE)));
//            return Integer.parseInt(content.trim());
//        } catch (IOException | NumberFormatException e) {
//            e.printStackTrace();
//            return -1; // Default value if file doesn't exist or error occurs
//        }
//    }

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


        //todo: add the save game functionality Faye will implement

        // Creates a new save file
        GameFile game = new GameFile(slotIndex, chao, new Inventory(), new Score(0), 0L, 1, 0L);
        try {
            game.save();
            System.out.println("Game saved successfully!");
        } catch (IOException e) {
            System.err.println("Failed to save game: " + e.getMessage());
        }

        //todo: create the new save file here using the Chao param
        goToGameScreen();
    }

//    @FXML
//    private void handleSlotSelection(int slotId) {
//        this.selectedSlot = slotId;
//        try {
//            if (GameFile.isEmptySlot(slotId)) {
//                // Load existing game
//                goToGameScreen();
//            } else {
//                // Show character selection UI
////                showChaoSelection();
//            }
//        } catch (IOException e) {
//            showError("Could not access save files");
//        }
//    }


    // What is this for?
//    private static void updateSaveSlot(int index) {
//        try {
//            String content = new String(Files.readAllBytes(Paths.get(SAVE_SLOT_FILE))); // Read file
//            JSONObject jsonData = new JSONObject(content); // Convert to JSONObject
//            System.out.println(jsonData.toString());
//
//            JSONArray saveSlots = jsonData.getJSONArray("saveSlots");
//
//            // Update the specific index
//            saveSlots.put(index, EXISTING_SLOT);
//
//            // Write the updated JSON back to the file
//            try (FileWriter writer = new FileWriter(SAVE_SLOT_FILE)) {
//                writer.write(jsonData.toString(4)); // Pretty print JSON
//                writer.flush();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    // TODO: Make it global since it is used in multiple places.
    private void goToGameScreen() {
        System.out.println("Go to game screen");
    }
}
