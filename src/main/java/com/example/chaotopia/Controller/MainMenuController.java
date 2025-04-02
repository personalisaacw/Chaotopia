package com.example.chaotopia.Controller;

import com.example.chaotopia.Application.BackgroundMusic;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

/**
 * Controller for the Main Menu screen, handling user interactions and navigation.
 * This class manages the main menu options, including starting a new game, accessing the tutorial,
 * configuring parental controls, and exiting the application.
 */
public class MainMenuController extends BaseController {

    /**
     * Initializes the controller, starting the background menu music.
     */
    public void initialize() {
        BackgroundMusic.startMenuMusic();
    }

    /**
     * Handles the "New Game" button click, navigating to the Load Game screen.
     *
     * @param e The ActionEvent triggered by the button click.
     * @throws IOException If an I/O error occurs during scene switching.
     */
    @FXML
    public void newGame(ActionEvent e) throws IOException {
        switchScene(e, "/com/example/chaotopia/View/LoadGame.fxml");
    }

    /**
     * Handles the "Tutorial" button click, navigating to the Tutorial New Game screen.
     *
     * @param e The ActionEvent triggered by the button click.
     * @throws IOException If an I/O error occurs during scene switching.
     */
    @FXML
    public void tutorial(ActionEvent e) throws IOException {
        switchScene(e, "/com/example/chaotopia/View/TutorialNewGame.fxml");
    }

    /**
     * Handles the "Parental Controls" button click, navigating to the Parental Password Check screen.
     *
     * @param e The ActionEvent triggered by the button click.
     * @throws IOException If an I/O error occurs during scene switching.
     */
    @FXML
    public void parentalControls(ActionEvent e) throws IOException {
        switchScene(e, "/com/example/chaotopia/View/ParentalPasswordCheck.fxml");
    }

    /**
     * Handles the "Exit" button click, stopping the menu music and exiting the application.
     *
     * @param actionEvent The ActionEvent triggered by the button click.
     */
    @FXML
    public void handleExitButton(ActionEvent actionEvent) {
        BackgroundMusic.stopMenuMusic();
        Platform.exit();
    }

}