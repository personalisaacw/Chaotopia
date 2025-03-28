package com.example.chaotopia.Controller;
import com.example.chaotopia.Model.Chao;
import com.example.chaotopia.Model.Status;
import com.example.chaotopia.Model.State;
import com.example.chaotopia.Model.ChaoType;

import javafx.fxml.FXML;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ChooseChaosController extends BaseController {

    private static final boolean EXISTING_SLOT = true;
    private int lastSlotClickedIndex;
    private static String SAVE_SLOT_FILE = "src/main/resources/com/example/chaotopia/Saves/save_slot_data.json";
    private static String SAVE_SLOT_INDEX_FILE = "src/main/resources/com/example/chaotopia/Saves/save_slot_index.txt";

    public void initialize() {
        lastSlotClickedIndex = loadSlotIndex();
        System.out.println("last slot clicked index: " + lastSlotClickedIndex);
    }

    public static int loadSlotIndex() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(SAVE_SLOT_INDEX_FILE)));
            return Integer.parseInt(content.trim());
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return -1; // Default value if file doesn't exist or error occurs
        }
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

    //handle chao selection
    private void handleChaoSelection(Chao chao) {
        //todo: add confirm selection button when player clicks on chao

        //save the save slot information
        updateSaveSlot(lastSlotClickedIndex);

        //todo: add the save game functionality Faye will implement
        //save the game state
            //create inventory
            //use the chao and inventory to instantiate a json file

        //todo: create the new save file here using the Chao param
        goToGameScreen();
    }

    private static void updateSaveSlot(int index) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(SAVE_SLOT_FILE))); // Read file
            JSONObject jsonData = new JSONObject(content); // Convert to JSONObject

            JSONArray saveSlots = jsonData.getJSONArray("saveSlots");

            // Update the specific index
            saveSlots.put(index, EXISTING_SLOT);

            // Write the updated JSON back to the file
            try (FileWriter writer = new FileWriter(SAVE_SLOT_FILE)) {
                writer.write(jsonData.toString(4)); // Pretty print JSON
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void goToGameScreen() {
        System.out.println("Go to game screen");
    }
}
