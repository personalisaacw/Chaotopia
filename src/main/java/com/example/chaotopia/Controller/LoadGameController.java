package com.example.chaotopia.Controller;

import com.example.chaotopia.Model.Chao;
import com.example.chaotopia.Model.GameFile;
import com.example.chaotopia.Components.Popup;
import com.example.chaotopia.Model.ParentalLimitations;
import javafx.event.ActionEvent;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import static com.example.chaotopia.Model.GameFile.deleteFile;

/**
 * Controller for the Load Game screen, handling save slot selection and deletion.
 * This class manages the UI for loading existing game saves or starting a new game,
 * including displaying save slot statuses and handling user interactions.
 */
public class LoadGameController extends BaseController {
    @FXML private ImageView slot1Image, slot2Image, slot3Image;
    @FXML private Button slot1Button, slot2Button, slot3Button;

    /** An array tracking the status of each save slot (index 0-2). `true` means a save exists, `false` means it's empty. */
    private boolean[] saveSlots = new boolean[3];
    /** Constant representing the boolean state of an empty save slot. */
    private static final boolean EMPTY_SLOT = false;
    /** Constant representing the boolean state of a save slot with existing data. */
    private static final boolean EXISTING_SLOT = true;

    /** The Image object loaded from resources, representing an empty save slot button graphic. */
    private final Image emptySaveImage = new Image(getClass().getResource("/com/example/chaotopia/Assets/PlayGameScreen/empty-save-button.png").toExternalForm());
    /** The Image object loaded from resources, representing an active/existing save slot button graphic. */
    private final Image activeSaveImage = new Image(getClass().getResource("/com/example/chaotopia/Assets/PlayGameScreen/existing-save-button.png").toExternalForm());

    /**
     * Initializes the controller, checking save slot statuses and updating the UI accordingly.
     */
    @FXML
    public void initialize() {
        checkSaveSlots(); // Check save slot status using GameFile
        updateUI(); // Set correct button images based on save slot data
    }

    /**
     * Handles the click event for slot 1, navigating to either NewGame or Gameplay based on slot status.
     *
     * @param e The ActionEvent triggered by the button click.
     * @throws IOException If an I/O error occurs during scene switching.
     */
    @FXML
    private void slot1Click(ActionEvent e) throws IOException {
        handleSlotClick(e, 0);
    }

    /**
     * Handles the click event for slot 2, navigating to either NewGame or Gameplay based on slot status.
     *
     * @param e The ActionEvent triggered by the button click.
     * @throws IOException If an I/O error occurs during scene switching.
     */
    @FXML
    private void slot2Click(ActionEvent e) throws IOException {
        handleSlotClick(e, 1);
    }

    /**
     * Handles the click event for slot 3, navigating to either NewGame or Gameplay based on slot status.
     *
     * @param e The ActionEvent triggered by the button click.
     * @throws IOException If an I/O error occurs during scene switching.
     */
    @FXML
    private void slot3Click(ActionEvent e) throws IOException {
        handleSlotClick(e, 2);
    }

    /**
     * Deletes the save data for slot 1, prompting the user for confirmation.
     */
    @FXML
    private void deleteSlot1() {
        deleteSlot(0, slot1Image);
    }

    /**
     * Deletes the save data for slot 2, prompting the user for confirmation.
     */
    @FXML
    private void deleteSlot2() {
        deleteSlot(1, slot2Image);
    }

    /**
     * Deletes the save data for slot 3, prompting the user for confirmation.
     */
    @FXML
    private void deleteSlot3() {
        deleteSlot(2, slot3Image);
    }

    /**
     * Handles the navigation based on the slot's empty/existing status.
     *
     * @param e         The ActionEvent triggering the click.
     * @param slotIndex The index of the clicked slot.
     * @throws IOException If an I/O error occurs during scene switching.
     */
    private void handleSlotClick(ActionEvent e, int slotIndex) throws IOException {
        if (saveSlots[slotIndex] == EMPTY_SLOT) {
            switchScene(e, "/com/example/chaotopia/View/NewGame.fxml", controller -> {
                if (controller instanceof NewGameController) {
                    System.out.println("This is the slot index new game" + slotIndex);
                    ((NewGameController) controller).setSlotIndex(slotIndex);
                }
            });
        } else {
            switchScene(e, "/com/example/chaotopia/View/Gameplay.fxml", controller -> {
                if (controller instanceof GameplayController) {
                    System.out.println("This is the slot index load game" + slotIndex);
                    ((GameplayController) controller).setSlotIndex(slotIndex);
                }
            });
        }
    }

    /**
     * Displays a confirmation dialog for deleting a save slot.
     *
     * @param slotNumber The slot number to delete.
     * @param slotImage  The ImageView corresponding to the slot.
     */
    @FXML
    private void deleteSlot(int slotNumber, ImageView slotImage) {
        String title = "Delete Game";
        String content = "Are you sure you want to delete this game? This cannot be undone!";
        Popup dialog = new Popup(title, content);

        dialog.addButton("No", () -> {}, "btn-submit");
        dialog.addButton("Yes", () -> {updateDeletion(slotNumber, slotImage);}, "btn-cancel");

        dialog.showAndWait();
    }

    /**
     * Updates the UI and file system after deleting a save slot.
     *
     * @param slotIndex The index of the deleted slot.
     * @param slotImage The ImageView to update.
     */
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

    /**
     * Checks the status of each save slot, determining if they are empty or contain save data.
     */
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

    /**
     * Updates the UI to reflect the current save slot statuses and parental limitations.
     */
    private void updateUI() {
        ImageView[] images = {slot1Image, slot2Image, slot3Image};

        for (int i = 0; i < saveSlots.length; i++) {
            if (saveSlots[i]) {
                images[i].setImage(activeSaveImage);
            } else {
                images[i].setImage(emptySaveImage);
            }
        }

        ParentalLimitations.loadParentalLimitations();
        java.time.LocalTime currentTime = java.time.LocalTime.now();
        if (!ParentalLimitations.isPlayAllowed(currentTime) && ParentalLimitations.isEnabled()) {
            slot1Button.setDisable(true);
            slot2Button.setDisable(true);
            slot3Button.setDisable(true);

            String title = "Play Not Allowed";
            String content = "You are not allowed to play during this time!\n" +
                    "You can only play from " + ParentalLimitations
                    .getAllowedStartTime() + " to " + ParentalLimitations
                    .getAllowedEndTime() + ".";
            Popup dialog = new Popup(title, content);

            dialog.addButton("Okay", () -> {
                popStack();
            }, "btn-submit");

            dialog.showAndWait();
        }
    }
}