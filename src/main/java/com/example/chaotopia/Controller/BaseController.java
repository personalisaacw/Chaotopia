package com.example.chaotopia.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.Objects;
import java.util.Stack;
import java.util.function.Consumer;

import javafx.scene.control.Alert;

import javax.swing.*;


/**
 * The `BaseController` class serves as a base class for all controllers in the application.
 * It provides common functionality for scene navigation, including a back button implementation
 * and a method to switch between scenes. This class uses a stack to keep track of the navigation
 * history, allowing users to easily go back to previous scenes.
 */
public class BaseController {
    /**Static stack to track scenes*/
    private static Stack<Scene> sceneStack = new Stack<>();
    /**String path to main menu*/
    private static final String MAIN_MENU_FXML_PATH = "/com/example/chaotopia/View/MainMenu.fxml";

    /**
     * Navigates directly to the main menu scene, clearing the navigation history.
     * Requires the calling controller to perform any necessary cleanup first.
     *
     * @param e The ActionEvent from the button press (used to get the Stage).
     * @throws IOException If the Main Menu FXML file cannot be loaded.
     */
    public void goToMainMenu(ActionEvent e) throws IOException {
        sceneStack.clear(); // Clear the entire navigation history
        // Get the current stage
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        // Load the Main Menu FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_MENU_FXML_PATH));
        if (loader.getLocation() == null) {
            throw new IOException("Cannot find Main Menu FXML at: " + MAIN_MENU_FXML_PATH);
        }
        Parent root = loader.load();

        // Create a new scene for the main menu
        Scene mainMenuScene = new Scene(root);
        addCSS(mainMenuScene); // Apply CSS

        // Set the stage to the main menu scene
        stage.setScene(mainMenuScene);
        stage.show();
        System.out.println("Main Menu loaded.");
    }

    /** Pops whichever scene is at the top of the stack */
    public void popStack(){
        sceneStack.pop();
    }

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
        switchScene(e, fxmlPath, null);
    }

    protected void switchScene(ActionEvent e, String fxmlPath, Consumer<Object> controllerConfigurator) throws IOException {
        // Push the current scene to the stack
        sceneStack.push(((Node)e.getSource()).getScene());

        // Load the new scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        // Configure the controller if provided
        if (controllerConfigurator != null) {
            Object controller = loader.getController();
            controllerConfigurator.accept(controller);
        }

        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Adds CSS to the file
     * @param scene The scene to add CSS
     */
    public static void addCSS(Scene scene) {
        String css = Objects.requireNonNull(BaseController.class.getResource("/com/example/chaotopia/CSS/styles.css")).toExternalForm();
        scene.getStylesheets().add(css);
    }

    /**
     * Displays an error message to the user
     * @param message The error message to display
     */
    protected void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}