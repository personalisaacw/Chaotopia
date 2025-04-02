package com.example.chaotopia.Controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.io.IOException;

/**
 * Controller for the Tutorial New Game screen, guiding the player through the initial setup.
 * This class manages the tutorial flow for starting a new game, displaying instructional steps and enabling interactions.
 */
public class TutorialNewGameController extends BaseController{

    @FXML
    private Button newGame;
    @FXML
    private StackPane step1;
    @FXML
    private StackPane step2;
    @FXML
    private StackPane step3;
    @FXML
    private StackPane step4;
    @FXML
    private StackPane step5;
    @FXML
    private ImageView step3Arrow;
    @FXML
    private ImageView step4Arrow;
    @FXML
    private ImageView step5Arrow;
    @FXML
    private int tutorialStep = 0;

    /**
     * Initializes the controller, disabling the new game button at the start.
     */
    @FXML
    public void initialize() {
        newGame.setDisable(true);
    }

    /**
     * Manages the tutorial steps, updating the UI based on the current step.
     *
     * @param tutorialStep The current step of the tutorial.
     */
    @FXML
    private void handlingTutorialSteps(int tutorialStep) {
        switch(tutorialStep){
            case 0:
                step1.setOpacity(0);
                step2.setOpacity(1);
                break;
            case 1:
                step2.setOpacity(0);
                step3.setOpacity(1);
                step3Arrow.setOpacity(1);
                break;
            case 2:
                step3.setOpacity(0);
                step3Arrow.setOpacity(0);
                step4.setOpacity(1);
                step4Arrow.setOpacity(1);
                break;
            case 3:
                step4.setOpacity(0);
                step4Arrow.setOpacity(0);
                step5.setOpacity(1);
                step5Arrow.setOpacity(1);
                newGame.setDisable(false);
                break;

        }
    }

    /**
     * Advances the tutorial to the next step and updates the UI.
     */
    @FXML
    public void step(){

        handlingTutorialSteps(tutorialStep);
        tutorialStep++;

    }

    /**
     * Navigates to the choose Chao tutorial page.
     *
     * @param e The ActionEvent triggering the navigation.
     * @throws IOException If an I/O error occurs during scene switching.
     */
    public void chooseChaoTutorialPage(ActionEvent e) throws IOException {
        switchScene(e, "/com/example/chaotopia/View/TutorialChooseChaos.fxml");
    }
}
