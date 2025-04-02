package com.example.chaotopia.Components;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A custom popup dialog that includes a text input field and configurable buttons.
 * This class allows for the creation of an alert dialog with a text field for user input,
 * along with the ability to add custom buttons with associated actions and styles.
 */
public class InputPopup {
    /** The underlying JavaFX Alert instance used to display the dialog. */
    private final Alert alert;
    /** A map storing the Runnable actions associated with each custom ButtonType added to the dialog. */
    private final Map<ButtonType, Runnable> buttonActions = new HashMap<>();
    /** A map storing the CSS style classes associated with each custom ButtonType added to the dialog. */
    private final Map<ButtonType, String[]> buttonStyles = new HashMap<>();
    /** The TextField component where the user enters their input. */
    private TextField textField;
    /** A string variable to store the text entered by the user in the TextField when the dialog is closed. */
    private String userInput;

    /**
     * Constructs a new InputPopup with the specified title and content.
     *
     * @param title   The title of the popup dialog.
     * @param content The content message displayed in the popup.
     */
    public InputPopup(String title, String content) {
        alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);

        // Create the content label
        Label contentLabel = new Label(content);
        contentLabel.setFont(Font.font("Querina Handwritten"));
        contentLabel.setWrapText(true);
        contentLabel.setMaxWidth(400);
        contentLabel.setMaxHeight(Double.MAX_VALUE);

        // Create the TextField for user input
        textField = new TextField();
        textField.setFont(Font.font("Querina Handwritten"));
        textField.setPromptText("Enter your input here...");

        // Add a VBox to hold both the label and the TextField
        VBox vbox = new VBox(10, contentLabel, textField);
        vbox.setMaxWidth(400);

        alert.getDialogPane().setContent(vbox);
        alert.getButtonTypes().clear();
        alert.setGraphic(null);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/com/example/chaotopia/CSS/styles.css")).toExternalForm()
        );
        dialogPane.getStyleClass().add("custom-popup");
        dialogPane.setMinHeight(Region.USE_PREF_SIZE);
    }

    /**
     * Adds a button to the popup dialog with the specified text, action, and style classes.
     *
     * @param text        The text displayed on the button.
     * @param action      The Runnable action to be executed when the button is pressed.
     * @param styleClasses An array of style classes to apply to the button.
     */
    public void addButton(String text, Runnable action, String... styleClasses) {
        ButtonType buttonType = new ButtonType(text);
        alert.getButtonTypes().add(buttonType);
        buttonActions.put(buttonType, action);
        buttonStyles.put(buttonType, styleClasses);
    }

    /**
     * Retrieves the user's input from the TextField.
     *
     * @return The text entered by the user.
     */
    public String getUserInput() {
        return userInput;
    }

    /**
     * Displays the popup dialog and waits for user interaction.
     * Applies button styles and executes the associated action when a button is pressed.
     * Stores the user input from the textfield into the userInput string.
     */
    public void showAndWait() {
        // Apply styles right before showing
        Platform.runLater(this::applyButtonStyles);

        alert.showAndWait().ifPresent(buttonType -> {
            // Store the user input when dialog is closed
            userInput = textField.getText();

            Runnable action = buttonActions.get(buttonType);
            if (action != null) {
                action.run();
            }
        });
    }

    /**
     * Applies the specified style classes to the buttons in the popup dialog.
     */
    private void applyButtonStyles() {
        for (Map.Entry<ButtonType, String[]> entry : buttonStyles.entrySet()) {
            Node buttonNode = alert.getDialogPane().lookupButton(entry.getKey());
            if (buttonNode != null) {
                buttonNode.getStyleClass().add("popup-button"); // Default class
                buttonNode.getStyleClass().addAll(entry.getValue());
            }
        }
    }
}