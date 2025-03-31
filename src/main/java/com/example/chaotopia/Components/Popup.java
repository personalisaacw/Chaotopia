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

public class Popup {
    private final Alert alert;
    private final Map<ButtonType, Runnable> buttonActions = new HashMap<>();
    private final Map<ButtonType, String[]> buttonStyles = new HashMap<>();

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

    public void addButton(String text, Runnable action, String... styleClasses) {
        ButtonType buttonType = new ButtonType(text);
        alert.getButtonTypes().add(buttonType);
        buttonActions.put(buttonType, action);
        buttonStyles.put(buttonType, styleClasses);
    }

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