package com.example.chaotopia.Controller;

import com.example.chaotopia.Model.Chao;
import com.example.chaotopia.Model.GameFile;
import javafx.event.ActionEvent;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Map;

import static com.example.chaotopia.Model.GameFile.deleteFile;

public class PlayGameController extends BaseController {
    @FXML
    private ImageView slot1Image, slot2Image, slot3Image;

    private boolean[] saveSlots = new boolean[3];
    private static final boolean EMPTY_SLOT = false;
    private static final boolean EXISTING_SLOT = true;

    private final Image emptySaveImage = new Image(getClass().getResource("/com/example/chaotopia/Assets/PlayGameScreen/empty-save-button.png").toExternalForm());
    private final Image activeSaveImage = new Image(getClass().getResource("/com/example/chaotopia/Assets/PlayGameScreen/existing-save-button.png").toExternalForm());

    @FXML
    public void initialize() {
        checkSaveSlots(); // Check save slot status using GameFile
        updateUI(); // Set correct button images based on save slot data
    }

    @FXML
    private void slot1Click(ActionEvent e) throws IOException {
        handleSlotClick(e, 0);
    }

    @FXML
    private void slot2Click(ActionEvent e) throws IOException {
        handleSlotClick(e, 1);
    }

    @FXML
    private void slot3Click(ActionEvent e) throws IOException {
        handleSlotClick(e, 2);
    }

    @FXML
    private void deleteSlot1() {
        updateDeletion(0, slot1Image);
    }

    @FXML
    private void deleteSlot2() {
        updateDeletion(1, slot2Image);
    }

    @FXML
    private void deleteSlot3() {
        updateDeletion(2, slot3Image);
    }

    // Takes to ChooseChaos or Gameplay, depending if it is empty
    private void handleSlotClick(ActionEvent e, int slotIndex) throws IOException {
        if (saveSlots[slotIndex] == EMPTY_SLOT) {
            switchScene(e, "/com/example/chaotopia/View/ChooseChaos.fxml", controller -> {
                if (controller instanceof ChooseChaosController) {
                    ((ChooseChaosController) controller).setSlotIndex(slotIndex);
                }
            });
        } else {
            // TODO: Filler for now because for implementation into gameplay, you will need the getters. I would pass the slotIndex parameter into the gameplay for initialization to occur
            GameFile loadedGame = new GameFile(slotIndex);

            if (loadedGame.getChao() != null) {
                Chao chao = loadedGame.getChao();
                System.out.println("\nChao: " + chao.getName());
                System.out.println("- Type: " + chao.getType());
                System.out.println("- State: " + chao.getState());
                System.out.println("- Alignment: " + chao.getAlignment());
                System.out.println("- Status: " + chao.getStatus().getCurrStats());
            }

            if (loadedGame.getInventory() != null) {
                System.out.println("\nInventory:");
                for (Map.Entry<String, Integer> entry : loadedGame.getInventory().getItems().entrySet()) {
                    System.out.println("- " + entry.getKey() + ": " + entry.getValue());
                }
            }

            if (loadedGame.getScore() != null) {
                System.out.println("\nScore: " + loadedGame.getScore().getScore());
            }

            goToGameplay();
        }
    }

    private void goToGameplay() {
        System.out.println("Go to gameplay screen...");
    }

    private void updateDeletion(int slotIndex, ImageView slotImage) {
        if (saveSlots[slotIndex] == EXISTING_SLOT) {
            try {
                // Delete the save file
                deleteFile(slotIndex);

                // Update UI and state
                saveSlots[slotIndex] = EMPTY_SLOT;
                slotImage.setImage(emptySaveImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkSaveSlots() {
        for (int i = 0; i < saveSlots.length; i++) {
            try {
                // Use GameFile's method to check if slot is empty
                saveSlots[i] = !GameFile.isEmptySlot(i);
            } catch (IOException e) {
                System.err.println("Error checking save slot " + i + ": " + e.getMessage());
                saveSlots[i] = false;
            }
        }
    }

    private void updateUI() {
        ImageView[] images = {slot1Image, slot2Image, slot3Image};

        for (int i = 0; i < saveSlots.length; i++) {
            if (saveSlots[i]) {
                images[i].setImage(activeSaveImage);
            } else {
                images[i].setImage(emptySaveImage);
            }
        }
    }
}