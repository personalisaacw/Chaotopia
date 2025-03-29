package com.example.chaotopia.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

public class ParentalControlsController extends BaseController{

    @FXML
    private CheckBox playRangeCheckBox;
    //todo: go back button should go back to main menu

    @FXML
    private Label playtimeStats;

    private String totalPlayTime;
    private String averagePlayTime;

    public void initialize() {
        //todo: load playtime data

        //todo: format in minutes? hours?
        playtimeStats.setText("Total Playtime: " + totalPlayTime + "\n" + "Average Playtime: " + averagePlayTime);
    }

    //saves the playable time range
    public void submitPlaytimeRange() {

    }

    public void enablePlayRange() {
        if (playRangeCheckBox.isSelected()) {
            System.out.println("CheckBox is checked!");
            // Perform actions when checked
        } else {
            System.out.println("CheckBox is unchecked!");
            // Perform actions when unchecked
        }
    }

    public void resetPlaytimeStats() {
        //todo: reset playtime stats
    }

    public void reviveSlot1() {

    }

    public void reviveSlot2() {

    }

    public void reviveSlot3() {

    }

    private void handleRevive() {

    }
}
