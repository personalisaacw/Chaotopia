package com.example.chaotopia.Controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.io.IOException;

/**
 * Controller for the Tutorial Choose Chaos screen, guiding the player through the initial steps.
 * This class manages the tutorial flow for choosing a Chao, displaying instructional steps and enabling interactions.
 */
public class TutorialChooseChaosController extends BaseController{
    @FXML
    private Button chooseRed;
    @FXML
    private Button chooseGreen;
    @FXML
    private Button chooseBlue;
    @FXML
    private StackPane step1;
    @FXML
    private StackPane step2;
    @FXML
    private StackPane step3;
    @FXML
    private Label redDescription;
    @FXML
    private Label greenDescription;
    @FXML
    private Label blueDescription;
    @FXML
    private ImageView step3Arrow;
    private int tutorialStep = 0;

    /**
     * Initializes the controller, disabling Chao selection buttons at the start.
     */
    @FXML
    public void initialize() {
        chooseRed.setDisable(true);
        chooseGreen.setDisable(true);
        chooseBlue.setDisable(true);
    }

    /**
     * Manages the tutorial steps, updating the UI based on the current step.
     *
     * @param tutorialStep The current step of the tutorial.
     */
    private void handlingTutorialSteps(int tutorialStep) {
        switch(tutorialStep){
            case 0:
                step1.setOpacity(0);
                step2.setOpacity(1);
                redDescription.setOpacity(1);
                greenDescription.setOpacity(1);
                blueDescription.setOpacity(1);
                break;
            case 1:
                step2.setOpacity(0);
                redDescription.setOpacity(0.52);
                greenDescription.setOpacity(0.52);
                blueDescription.setOpacity(0.52);
                step3.setOpacity(1);
                step3Arrow.setOpacity(1);
                chooseRed.setDisable(false);
                break;


        }
    }

    /**
     * Advances the tutorial to the next step and updates the UI.
     */
    public void step(){

        handlingTutorialSteps(tutorialStep);
        tutorialStep++;

    }

    /**
     * Navigates to the gameplay tutorial page.
     *
     * @param e The ActionEvent triggering the navigation.
     * @throws IOException If an I/O error occurs during scene switching.
     */
    public void gamePlayTutorialPage(ActionEvent e) throws IOException {
        switchScene(e, "/com/example/chaotopia/View/TutorialGameplay2.fxml");
    }

    /**
     * Navigates back two steps in the tutorial.
     *
     * @param e The ActionEvent triggering the navigation.
     */
    public void goBack2Steps(ActionEvent e) {
        popStack();
        goBack(e);
    }
}
