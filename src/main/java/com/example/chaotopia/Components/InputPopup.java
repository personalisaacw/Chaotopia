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

public class InputPopup {
    private final Alert alert;
    private final Map<ButtonType, Runnable> buttonActions = new HashMap<>();
    private final Map<ButtonType, String[]> buttonStyles = new HashMap<>();
    private TextField textField; // The text field for user input
    private String userInput; // Variable to store user input

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

    public void addButton(String text, Runnable action, String... styleClasses) {
        ButtonType buttonType = new ButtonType(text);
        alert.getButtonTypes().add(buttonType);
        buttonActions.put(buttonType, action);
        buttonStyles.put(buttonType, styleClasses);
    }

    public String getUserInput() {
        return userInput;
    }

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