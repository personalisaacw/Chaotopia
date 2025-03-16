package com.example.chaotopia.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Stack;

/**
 * The `BaseController` class serves as a base class for all controllers in the application.
 * It provides common functionality for scene navigation, including a back button implementation
 * and a method to switch between scenes. This class uses a stack to keep track of the navigation
 * history, allowing users to easily go back to previous scenes.
 */
public class BaseController {
    private static Stack<Scene> sceneStack = new Stack<>(); // Static stack to track scenes

    /**
     * Handles the action for the back button. This method pops the previous scene from the
     * navigation stack and sets it as the current scene, effectively navigating the user back
     * to the previous screen.
     *
     * @param e The `ActionEvent` triggered by clicking the back button.
     */
    public void goBack(ActionEvent e) {
        if (!sceneStack.isEmpty()) {
            Scene previousScene = sceneStack.pop();
            Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            stage.setScene(previousScene);
        }
    }

    /**
     * Switches the current scene to a new scene specified by the provided FXML file path.
     * The current scene is pushed onto the navigation stack before loading the new scene,
     * allowing the user to navigate back to it later.
     *
     * @param e The `ActionEvent` triggered by the action that initiates the scene switch.
     * @param fxmlPath The path to the FXML file for the new scene.
     * @throws IOException If the FXML file cannot be loaded.
     */
    protected void switchScene(ActionEvent e, String fxmlPath) throws IOException {
        // Push the current scene to the stack
        sceneStack.push(((Node)e.getSource()).getScene());

        // Load the new scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}