package com.example.chaotopia.Controller;

import com.example.chaotopia.Model.ParentalControls;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;

import java.io.IOException;

/**
 * Controller for the Parental Password Check screen, handling password authentication.
 * This class allows parents to enter a password to access the parental controls settings.
 */
public class ParentalPasswordCheckController extends BaseController {
    @FXML
    private PasswordField passwordField;

    /**
     * Checks the entered password against the stored parental control password.
     * If the password is correct, navigates to the Parental Controls screen.
     *
     * @param e The ActionEvent triggered by the password check.
     * @throws IOException If an I/O error occurs during scene switching.
     */
    public void checkPassword(ActionEvent e) throws IOException {
        String enteredPassword = passwordField.getText();

        if (ParentalControls.authenticate(enteredPassword)) {
            switchScene(e, "/com/example/chaotopia/View/ParentalControls.fxml");
        }
    }
}
