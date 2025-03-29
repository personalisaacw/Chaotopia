package com.example.chaotopia.Controller;

import javafx.event.ActionEvent;

import java.io.IOException;

public class ParentalPasswordCheckController extends BaseController {
    private String password;

    public void initialize() {
        //todo: call parental controls class to set password variable
    }

    public void checkPassword(ActionEvent e) throws IOException {
        //used for testing
        switchScene(e, "/com/example/chaotopia/View/ParentalControls.fxml");

        //if password correct:
            //todo: call parental controls
            //go to parental controls screen
        //else:
            //todo: show error when password incorrect
    }
}
