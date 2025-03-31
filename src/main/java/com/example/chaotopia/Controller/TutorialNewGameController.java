package com.example.chaotopia.Controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.io.IOException;

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

    @FXML
    public void initialize() {
        newGame.setDisable(true);
    }

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

    @FXML
    public void step(){

        handlingTutorialSteps(tutorialStep);
        tutorialStep++;

    }

    public void chooseChaoTutorialPage(ActionEvent e) throws IOException {
        switchScene(e, "/com/example/chaotopia/View/TutorialChooseChaos.fxml");
    }



}
