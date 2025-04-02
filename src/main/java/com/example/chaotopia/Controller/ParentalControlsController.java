package com.example.chaotopia.Controller;

import com.example.chaotopia.Components.Popup;
import com.example.chaotopia.Model.GameFile;
import com.example.chaotopia.Model.ParentalControls;
import com.example.chaotopia.Model.ParentalLimitations;
import com.example.chaotopia.Model.ParentalStatistics;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * Controller for the Parental Controls screen, handling parental settings and game statistics.
 * This class allows parents to set play time limitations, view playtime statistics, and revive pets.
 */
public class ParentalControlsController extends BaseController{

    @FXML
    private CheckBox playRangeCheckBox;

    @FXML
    private Label playtimeStats;

    @FXML
    private TextField startTimeField;

    @FXML
    private TextField endTimeField;

    /**Initialize Game files*/
    private GameFile[] gameFiles = new GameFile[3];

    /**
     * Initializes the controller, loading parental limitations and setting up the UI.
     */
    public void initialize() {
        //load limitations file
        ParentalLimitations.loadParentalLimitations();

        //set the checkbox to be enabled/disabled
        playRangeCheckBox.setSelected(ParentalLimitations.isEnabled());

        //initialize array of GameFiles
        for (int slotID = 0; slotID < 3; slotID++) {
            try{
                gameFiles[slotID] = new GameFile(slotID);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        updatePlaytimeStatsLabel();
    }

    /**
     * Saves the playable time range entered by the user.
     */
    public void submitPlaytimeRange() {
        String startTime = startTimeField.getText();
        String endTime = endTimeField.getText();

        try {
            String title = "Set Playtime";
            String content = "Are you sure you want to set the playtime?";
            Popup dialog = new Popup(title, content);

            dialog.addButton("Yes", () -> {
                LocalTime parsedStartTime = LocalTime.parse(startTime);
                LocalTime parsedEndTime = LocalTime.parse(endTime);
                ParentalLimitations.setAllowedTime(parsedStartTime, parsedEndTime);
            }, "btn-submit");

            dialog.addButton("No", () -> {
                // Do nothing
            }, "btn-cancel");

            dialog.showAndWait();

        } catch (DateTimeParseException e) {
            String title = "Set Playtime";
            String content = "You entered an invalid time. Please enter your time in 24-hour format (e.g. 13:30).";
            Popup dialog = new Popup(title, content);

            dialog.addButton("Okay", () -> {

            }, "btn-submit");

            dialog.showAndWait();
            System.err.println("Error parsing time string: " + e.getMessage());

        } catch (Exception e) {
            System.err.println("Error parsing time string: " + e.getMessage());
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();

            String title = "Set Playtime";
            String content = "An unknown error occurred. Please try again.";
            Popup dialog = new Popup(title, content);

            dialog.addButton("Okay", () -> {

            }, "btn-submit");

            dialog.showAndWait();
        }

        //save the limitations
        ParentalLimitations.storeParentalLimitations();
    }

    /**
     * Toggles the play time range feature on or off.
     */
    public void togglePlayRange() {
        ParentalLimitations.toggleFeature();
        ParentalLimitations.storeParentalLimitations();
    }

    /**
     * Resets the playtime statistics for all game slots.
     */
    public void resetPlaytimeStats() {
        String title = "Reset Statistics";
        String content = "Are you sure you want to reset the statistics?";
        Popup dialog = new Popup(title, content);

        dialog.addButton("Yes", () -> {
            ParentalStatistics.resetStatistics(gameFiles);
            updatePlaytimeStatsLabel();
        }, "btn-submit");

        dialog.addButton("No", () -> {
            // Do nothing
        }, "btn-cancel");

        dialog.showAndWait();
    }

    /**
     * Revives the pet in slot 1 if it is dead.
     */
    public void reviveSlot1() {
        handleRevive(gameFiles[0]);
    }

    /**
     * Revives the pet in slot 2 if it is dead.
     */
    public void reviveSlot2() {
        handleRevive(gameFiles[1]);
    }

    /**
     * Revives the pet in slot 3 if it is dead.
     */
    public void reviveSlot3() {
        handleRevive(gameFiles[2]);
    }

    /**
     * Handles the revival process for a given game file.
     *
     * @param gameFile The game file containing the pet to revive.
     */
    private void handleRevive(GameFile gameFile) {
        if (gameFile == null) {
            return;
        }

        if (gameFile.getChao().getStatus().getHealth() == 0) {
            showReviveConfirmationDialog(gameFile);
        } else {
            showCannotReviveDialog();
        }
    }

    /**
     * Shows a confirmation dialog before reviving a pet.
     *
     * @param gameFile The game file containing the pet to revive.
     */
    private void showReviveConfirmationDialog(GameFile gameFile) {
        String title = "Revive Pet";
        String content = "Are you sure you want to revive the pet?";
        Popup dialog = new Popup(title, content);

        dialog.addButton("Yes", () -> {
            try {
                ParentalControls.reviveChao(gameFile);
            } catch (IOException e) {
                showErrorDialog("Revival Failed", "An error occurred while reviving the pet: " + e.getMessage());
            }
        }, "btn-submit");

        dialog.addButton("No", () -> {}, "btn-cancel");

        dialog.showAndWait();
    }

    /**
     * Shows a dialog indicating that the pet cannot be revived because it is alive.
     */
    private void showCannotReviveDialog() {
        String title = "Cannot Revive Pet";
        String content = "You cannot revive the pet. The pet is alive.";
        Popup dialog = new Popup(title, content);

        dialog.addButton("Okay", () -> {}, "btn-submit");

        dialog.showAndWait();
    }

    /**
     * Shows an error dialog with a given title and message.
     *
     * @param title   The title of the error dialog.
     * @param message The error message to display.
     */
    private void showErrorDialog(String title, String message) {
        Popup errorDialog = new Popup(title, message);
        errorDialog.addButton("Okay", () -> {}, "btn-submit");
        errorDialog.showAndWait();
    }

    /**
     * Updates the playtime statistics label with the total and average playtime.
     */
    private void updatePlaytimeStatsLabel() {
        //initialize parental statistics
        ParentalStatistics.loadParentalStatistics(gameFiles);

        //format the total and average playtime
        long totalPlayTimeHrs = (ParentalStatistics.getTotalPlaytime() / 3600);
        long totalPlayTimeMins = (ParentalStatistics.getTotalPlaytime() / 60);
        long totalPlayTimeSecs = (ParentalStatistics.getTotalPlaytime() % 60);
        long averagePlayTimeHrs = (ParentalStatistics.getAveragePlaytime() / 3600);
        long averagePlayTimeMins = (ParentalStatistics.getAveragePlaytime() / 60);
        long averagePlayTimeSecs = (ParentalStatistics.getAveragePlaytime() % 60);

        String formattedStats = String.format(
                "Total Playtime: %02d:%02d:%02d\nAverage Playtime: %02d:%02d:%02d",
                totalPlayTimeHrs,
                totalPlayTimeMins,
                totalPlayTimeSecs,
                averagePlayTimeHrs,
                averagePlayTimeMins,
                averagePlayTimeSecs
        );

        //print the total and average playtime to the label
        playtimeStats.setText(formattedStats);
    }
}
