package com.example.chaotopia.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PlayGameController extends BaseController {
    @FXML
    private ImageView slot1Image, slot2Image, slot3Image;

    private boolean[] saveSlots = new boolean[3];

    private final Image emptySaveImage = new Image(getClass().getResource("/com/example/chaotopia/Assets/empty-save-button.png").toExternalForm());
    private final Image activeSaveImage = new Image(getClass().getResource("/com/example/chaotopia/Assets/existing-save-button.png").toExternalForm());


    private final String SAVE_FILE = "src/main/resources/com/example/chaotopia/Saves/save_data.json"; // JSON save file

    @FXML
    public void initialize() {
        loadGameState(); // Load save information when starting
        updateUI(); // Set correct button images based on save slot data
    }

    @FXML
    private void handleSlot1Click() {
        createGame(0, slot1Image);
    }

    @FXML
    private void handleSlot2Click() {
        createGame(1, slot2Image);
    }

    @FXML
    private void handleSlot3Click() {
        createGame(2, slot3Image);
    }

    //todo: confirmation message when clicking delete slot

    @FXML
    private void deleteSlot1() {
        deleteSaveFile(0, slot1Image);
    }

    @FXML
    private void deleteSlot2() {
        deleteSaveFile(1, slot2Image);
    }

    @FXML
    private void deleteSlot3() {
        deleteSaveFile(2, slot3Image);
    }

    private void createGame(int slotIndex, ImageView slotImage) {
        if (!saveSlots[slotIndex]) { //if it's an empty save slot, go to choose your chaos screen and update the save slot information
            saveSlots[slotIndex] = true;
            slotImage.setImage(activeSaveImage);
//            saveGameState(); // Save updated state to JSON
            goToChooseChaos(); //function to go to a new screen
        } else { //if its an active save slot, immediately go to the game play screen.
            goToGameplay();
        }
    }

    //todo: change function name to goToNextScreen, and make this function go to next screen
    private void goToChooseChaos() {
        System.out.println("Go to choose your Chaos screen...");
    }

    private void goToGameplay() {
        System.out.println("Go to gameplay screen...");
    }

    private void deleteSaveFile(int slotIndex, ImageView slotImage) {
        //if the slot is active, delete the save slot and save file information
        if (saveSlots[slotIndex]) {
            saveSlots[slotIndex] = false;
            //todo: delete json save file information (pending on Save Game/Load Game functionality)
            slotImage.setImage(emptySaveImage);
//            saveGameState(); // Save updated state to JSON
        }
    }

//    private void saveGameState() {
//        JSONObject saveData = new JSONObject();
//        JSONArray slotsArray = new JSONArray();
//
//        for (boolean slot : saveSlots) {
//            slotsArray.put(slot);
//        }
//
//        saveData.put("saveSlots", slotsArray);
//
//        try (FileWriter file = new FileWriter(SAVE_FILE)) {
//            file.write(saveData.toString(4)); // Pretty print JSON
//            file.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void loadGameState() {
        if (Files.exists(Paths.get(SAVE_FILE))) {
            try {
                String content = new String(Files.readAllBytes(Paths.get(SAVE_FILE)));
                JSONObject saveData = new JSONObject(content);
                JSONArray slotsArray = saveData.getJSONArray("saveSlots");

                for (int i = 0; i < saveSlots.length; i++) {
                    saveSlots[i] = slotsArray.getBoolean(i);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUI() {
        ImageView[] images = {slot1Image, slot2Image, slot3Image};

        //if the current save slot is true, there is an active save
        for (int i = 0; i < saveSlots.length; i++) {
            if (saveSlots[i]) {
                images[i].setImage(activeSaveImage);
            } else {
                images[i].setImage(emptySaveImage);
            }
        }
    }
}