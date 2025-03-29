package com.example.chaotopia.Controller;

import javafx.event.ActionEvent;

import java.io.FileWriter;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PlayGameController extends BaseController {
    @FXML
    private ImageView slot1Image, slot2Image, slot3Image;

    private boolean[] saveSlots = new boolean[3];
    private static final boolean EMPTY_SLOT = false;
    private static final boolean EXISTING_SLOT = true;

    private final Image emptySaveImage = new Image(getClass().getResource("/com/example/chaotopia/Assets/PlayGameScreen/empty-save-button.png").toExternalForm());
    private final Image activeSaveImage = new Image(getClass().getResource("/com/example/chaotopia/Assets/PlayGameScreen/existing-save-button.png").toExternalForm());

    private static String SAVE_SLOT_FILE = "src/main/resources/com/example/chaotopia/Saves/save_slot_data.json";
    private static String SAVE_SLOT_INDEX_FILE = "src/main/resources/com/example/chaotopia/Saves/save_slot_index.txt";

    @FXML
    public void initialize() {
        loadSaveSlots(); // Load save information when starting
        updateUI(); // Set correct button images based on save slot data
    }

    @FXML
    private void slot1Click(ActionEvent e) throws IOException {
        int slotIndex = 0;
        handleSlotClick(e, slotIndex);
    }

    @FXML
    private void slot2Click(ActionEvent e) throws IOException {
        int slotIndex = 1;
        handleSlotClick(e, slotIndex); }

    @FXML
    private void slot3Click(ActionEvent e) throws IOException {
        int slotIndex = 2;
        handleSlotClick(e, slotIndex);
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

    private void handleSlotClick(ActionEvent e, int slotIndex) throws IOException {
        if (saveSlots[slotIndex] == EMPTY_SLOT) { //if it's an empty save slot, go to choose your chaos screen and update the save slot information
            saveSlotIndex(slotIndex);
            switchScene(e, "/com/example/chaotopia/View/ChooseChaos.fxml"); //function to go to choose your Chaos
        } else { //if its an active save slot, immediately go to the game play screen.
            goToGameplay();
        }
    }

    public static void saveSlotIndex(int slotIndex) {
        try (FileWriter writer = new FileWriter(SAVE_SLOT_INDEX_FILE)) {
            writer.write(String.valueOf(slotIndex));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void goToGameplay() {
        System.out.println("Go to gameplay screen...");
    }

    private void deleteSaveFile(int slotIndex, ImageView slotImage) {
        //if the slot is active, delete the save slot and save file information
        if (saveSlots[slotIndex] == EXISTING_SLOT) {
            saveSlots[slotIndex] = EMPTY_SLOT;
            updateSaveSlot(slotIndex);
            //todo: delete json save file information (pending on Save Game/Load Game functionality)
            slotImage.setImage(emptySaveImage);
        }
    }

    private static void updateSaveSlot(int index) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(SAVE_SLOT_FILE))); // Read file
            JSONObject jsonData = new JSONObject(content); // Convert to JSONObject

            JSONArray saveSlots = jsonData.getJSONArray("saveSlots");

            // Update the specific index
            saveSlots.put(index, EMPTY_SLOT);

            // Write the updated JSON back to the file
            try (FileWriter writer = new FileWriter(SAVE_SLOT_FILE)) {
                writer.write(jsonData.toString(4)); // Pretty print JSON
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSaveSlots() {
        Path saveFilePath = Paths.get(SAVE_SLOT_FILE);

        try {
            // Create the file if it doesn't exist
            if (!Files.exists(saveFilePath)) {
                JSONObject initialSaveData = new JSONObject();
                JSONArray initialSlots = new JSONArray();

                // Initialize all save slots to false
                for (int i = 0; i < saveSlots.length; i++) {
                    initialSlots.put(false);
                }

                initialSaveData.put("saveSlots", initialSlots);

                Files.write(saveFilePath, initialSaveData.toString().getBytes());
            }

            // Read the save file
            String content = new String(Files.readAllBytes(saveFilePath));
            JSONObject saveData = new JSONObject(content);
            JSONArray slotsArray = saveData.getJSONArray("saveSlots");

            // Populate save slots
            for (int i = 0; i < saveSlots.length; i++) {
                saveSlots[i] = slotsArray.getBoolean(i);
            }
        } catch (IOException e) {
            // Log the error and optionally set a default state
            System.err.println("Error loading game state: " + e.getMessage());

            // Optionally initialize all slots to false as a fallback
            for (int i = 0; i < saveSlots.length; i++) {
                saveSlots[i] = false;
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