package com.example.chaotopia.Components;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A custom popup dialog for displaying messages and handling button interactions.
 * This class creates an alert dialog with configurable buttons and associated actions.
 */
public class Popup {
    /** The underlying JavaFX Alert instance used to construct and display the dialog window. */
    private final Alert alert;
    /**
     * A map storing the Runnable actions associated with each custom ButtonType.
     * When a button corresponding to a ButtonType key is clicked, the associated Runnable value is executed.
     */
    private final Map<ButtonType, Runnable> buttonActions = new HashMap<>();
    /**
     * A map storing arrays of CSS style classes associated with each custom ButtonType.
     * These classes are applied to the corresponding button node in the dialog for custom styling.
     */
    private final Map<ButtonType, String[]> buttonStyles = new HashMap<>();

    /**
     * Constructs a new Popup with the specified title and content.
     *
     * @param title   The title of the popup dialog.
     * @param content The content message displayed in the popup.
     */
    public Popup(String title, String content) {
        alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);

        Label contentLabel = new Label(content);
        contentLabel.setWrapText(true);
        contentLabel.setMaxWidth(400);
        contentLabel.setMaxHeight(Double.MAX_VALUE);

        alert.getDialogPane().setContent(contentLabel);
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
     * Displays the popup dialog and waits for user interaction.
     * Applies button styles and executes the associated action when a button is pressed.
     */
    public void showAndWait() {
        // Apply styles right before showing
        Platform.runLater(this::applyButtonStyles);

        alert.showAndWait().ifPresent(buttonType -> {
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