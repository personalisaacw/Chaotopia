package com.example.chaotopia.Controller;

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

public class ParentalControlsController extends BaseController{

    @FXML
    private CheckBox playRangeCheckBox;
    //todo: go back button should go back to main menu

    @FXML
    private Label playtimeStats;

    @FXML
    private TextField startTimeField;

    @FXML
    private TextField endTimeField;

    private GameFile[] gameFiles = new GameFile[3];

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

        //todo: does this work if theres no files?
        updatePlaytimeStatsLabel();
    }

    //saves the playable time range
    public void submitPlaytimeRange() {
        String startTime = startTimeField.getText();
        String endTime = endTimeField.getText();

        try {
            // Parse the strings into LocalTime objects
            LocalTime parsedStartTime = LocalTime.parse(startTime);
            LocalTime parsedEndTime = LocalTime.parse(endTime);

            ParentalLimitations.setAllowedTime(parsedStartTime, parsedEndTime);
        } catch (DateTimeParseException e) {
            // Handle cases where the string format is incorrect
            System.err.println("Error parsing time string: " + e.getMessage());
            // You might want to show an error message to the user, log the error,
            // or use default values here.
        } catch (Exception e) {
            // Catch other potential exceptions
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        //save the limitations
        ParentalLimitations.storeParentalLimitations();
    }

    public void togglePlayRange() {
        ParentalLimitations.toggleFeature();
        ParentalLimitations.storeParentalLimitations();
    }

    public void resetPlaytimeStats() {
        ParentalStatistics.resetStatistics(gameFiles);
        updatePlaytimeStatsLabel();
    }

    public void reviveSlot1() {
        handleRevive(gameFiles[0]);
    }

    public void reviveSlot2() {
        handleRevive(gameFiles[1]);
    }

    public void reviveSlot3() {
        handleRevive(gameFiles[2]);
    }

    private void handleRevive(GameFile gameFile) {
        if (gameFile != null) {
            if (gameFile.getChao().getStatus().getHealth() == 0) {
                try {
                    ParentalControls.reviveChao(gameFile);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

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
