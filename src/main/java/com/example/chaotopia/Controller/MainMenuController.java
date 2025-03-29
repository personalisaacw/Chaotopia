package com.example.chaotopia.Controller;

import javafx.event.ActionEvent;
import java.io.IOException;

public class MainMenuController extends BaseController {

    public void newGame(ActionEvent e) throws IOException {
        switchScene(e, "/com/example/chaotopia/View/PlayGame.fxml");
    }

    public void loadGame(ActionEvent e) throws IOException {
        switchScene(e, "/com/example/chaotopia/View/LoadGame.fxml");
    }

    public void tutorial(ActionEvent e) throws IOException {
        switchScene(e, "/com/example/chaotopia/View/Tutorial.fxml");
    }

    public void parentalControls(ActionEvent e) throws IOException {
        switchScene(e, "/com/example/chaotopia/View/ParentalControls.fxml");
    }
}