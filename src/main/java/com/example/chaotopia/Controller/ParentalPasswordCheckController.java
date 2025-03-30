package com.example.chaotopia.Controller;

import com.example.chaotopia.Model.ParentalControls;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;

import java.io.IOException;

public class ParentalPasswordCheckController extends BaseController {
    @FXML
    private PasswordField passwordField;

    public void checkPassword(ActionEvent e) throws IOException {
        String enteredPassword = passwordField.getText();

        if (ParentalControls.authenticate(enteredPassword)) {
            switchScene(e, "/com/example/chaotopia/View/ParentalControls.fxml");
        }
        //else:
            //todo: show error when password incorrect
    }
}
