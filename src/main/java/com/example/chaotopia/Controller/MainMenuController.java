package com.example.chaotopia.Controller;

import com.example.chaotopia.Application.BackgroundMusic;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class MainMenuController extends BaseController {

    public void initialize() {
        BackgroundMusic.startMenuMusic();
    }

    @FXML
    public void newGame(ActionEvent e) throws IOException {
        switchScene(e, "/com/example/chaotopia/View/LoadGame.fxml");
    }

    @FXML
    public void tutorial(ActionEvent e) throws IOException {
        switchScene(e, "/com/example/chaotopia/View/TutorialNewGame.fxml");
    }

    @FXML
    public void parentalControls(ActionEvent e) throws IOException {
        switchScene(e, "/com/example/chaotopia/View/ParentalPasswordCheck.fxml");
    }

    @FXML
    public void handleExitButton(ActionEvent actionEvent) {
        BackgroundMusic.stopMenuMusic();
        Platform.exit();
    }

}