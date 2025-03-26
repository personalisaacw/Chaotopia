package com.example.chaotopia.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
//import org.json.JSONArray;
//import org.json.JSONObject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PlayGameController extends BaseController {
    @FXML
    private Button slot1Button, slot2Button, slot3Button;
    @FXML
    private ImageView slot1Image, slot2Image, slot3Image;

    //private boolean[] saveSlots = new boolean[3];
    private boolean[] saveSlots = {true, false, false}; //default value for testing

    private final Image emptySaveImage = new Image(getClass().getResource("/com/example/chaotopia/Assets/empty-save-button.png").toExternalForm());
    private final Image activeSaveImage = new Image(getClass().getResource("/com/example/chaotopia/Assets/existing-save-button.png").toExternalForm());


    private final String SAVE_FILE = "save_data.json"; // JSON save file

    @FXML
    public void initialize() {
        //loadGameState(); // Load save information when starting
        //updateUI(); // Set correct button images based on save slot data
        if (saveSlots[0]) {
            slot1Image.setImage(activeSaveImage);
        } else {
            slot1Image.setImage(emptySaveImage);
        }
    }

    @FXML
    private void handleSlot1Click() {
        //do something
    }

//    @FXML
//    private void handleSlot2Click() {
//        //do something
//    }

//    @FXML
//    private void handleSlot3Click() {
//        //do something
//    }

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

//    private void loadGameState() {
//        if (Files.exists(Paths.get(SAVE_FILE))) {
//            try {
//                String content = new String(Files.readAllBytes(Paths.get(SAVE_FILE)));
//                JSONObject saveData = new JSONObject(content);
//                JSONArray slotsArray = saveData.getJSONArray("saveSlots");
//
//                for (int i = 0; i < saveSlots.length; i++) {
//                    saveSlots[i] = slotsArray.getBoolean(i);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    private void updateUI() {
//        Button[] buttons = {slot1Button, slot2Button, slot3Button};
//        ImageView[] images = {slot1Image, slot2Image, slot3Image};
//
//        //if the current save slot is true, there is an active save
//        for (int i = 0; i < saveSlots.length; i++) {
//            if (saveSlots[i]) {
//                buttons[i].setDisable(true);
//                images[i].setImage(activeSaveImage);
//            } else {
//                buttons[i].setDisable(false);
//                images[i].setImage(emptySaveImage);
//            }
//        }
//    }
}