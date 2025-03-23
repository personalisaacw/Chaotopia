<<<<<<<< HEAD:src/main/java/com/example/chaotopia/Extra/ChaoAnimationController.java
package com.example.chaotopia.Extra;
========
package com.example.chaotopia.Model;
>>>>>>>> 196c8d0 (added FruitAnimation.java, FruitType.java, FruitAnimation.fxml, and some testing classes FruitAnimationController.java, FruitAnimationTest.java. I also changed the file structure.):src/main/java/com/example/chaotopia/Model/HelloController.java

import com.example.chaotopia.AnimationState;
import com.example.chaotopia.ChaoAnimation;
import com.example.chaotopia.ChaoType;
import com.example.chaotopia.State;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ChaoAnimationController {

    @FXML
    private ImageView characterImageView;

    @FXML
    private VBox controlPanel;

    @FXML
    private Label statusLabel;

    private ChaoAnimation chaoAnimation;
    private ChaoType currentChaoType = ChaoType.DARK;
    private State currentState = State.NORMAL;

    @FXML
    public void initialize() {
        // Set initial image
        characterImageView.setImage(new Image(getClass().getResourceAsStream("/com/example/chaotopia/sprites/DARK/NORMAL1.png")));

        // Create animation instance
        chaoAnimation = new ChaoAnimation(characterImageView, ChaoType.DARK, AnimationState.NORMAL);

        // Start the animation
        chaoAnimation.startAnimation();

        // Create UI controls for testing different states and types
        createTestingUI();

        // Update status label
        updateStatusLabel();
    }

    /**
     * Creates a testing UI with buttons for all states and Chao types
     */
    private void createTestingUI() {
        // Create a label for the Chao type section
        Label typeLabel = new Label("Chao Types:");
        typeLabel.setStyle("-fx-font-weight: bold; -fx-padding: 5 0 5 0;");
        controlPanel.getChildren().add(typeLabel);

        // Create HBox for Chao type buttons
        HBox typeButtons = new HBox(5);
        controlPanel.getChildren().add(typeButtons);

        // Add buttons for each Chao type
        for (ChaoType type : ChaoType.values()) {
            Button button = new Button(type.name());
            button.setPrefWidth(80);
            button.setOnAction(e -> {
                changeChaoType(type);
                updateStatusLabel();
            });
            // Highlight the current type
            if (type == currentChaoType) {
                button.setStyle("-fx-background-color: lightblue;");
            }
            typeButtons.getChildren().add(button);
        }

        // Add spacer
        controlPanel.getChildren().add(new Label(" "));

        // Create a label for the state section
        Label stateLabel = new Label("Animation States:");
        stateLabel.setStyle("-fx-font-weight: bold; -fx-padding: 5 0 5 0;");
        controlPanel.getChildren().add(stateLabel);

        // Create a grid for state buttons (2 columns)
        GridPane stateGrid = new GridPane();
        stateGrid.setHgap(5);
        stateGrid.setVgap(5);
        controlPanel.getChildren().add(stateGrid);

        // Add buttons for each state
        int row = 0;
        int col = 0;
        for (State state : State.values()) {
            Button button = new Button(state.name());
            button.setPrefWidth(120);
            button.setOnAction(e -> {
                changeState(state);
                updateStatusLabel();

                // Update button styles
                stateGrid.getChildren().forEach(node -> {
                    if (node instanceof Button) {
                        ((Button) node).setStyle("");
                    }
                });
                button.setStyle("-fx-background-color: lightgreen;");
            });

            // Highlight the current state
            if (state == currentState) {
                button.setStyle("-fx-background-color: lightgreen;");
            }

            stateGrid.add(button, col, row);

            // Move to next column or row
            col++;
            if (col > 1) {
                col = 0;
                row++;
            }
        }

        // Add animation speed controls
        Label speedLabel = new Label("Animation Speed:");
        speedLabel.setStyle("-fx-font-weight: bold; -fx-padding: 15 0 5 0;");
        controlPanel.getChildren().add(speedLabel);

        HBox speedButtons = new HBox(5);
        controlPanel.getChildren().add(speedButtons);

        Button slowButton = new Button("Slow");
        slowButton.setOnAction(e -> chaoAnimation.setAnimationSpeed(0.4));

        Button normalButton = new Button("Normal");
        normalButton.setOnAction(e -> chaoAnimation.setAnimationSpeed(0.2));

        Button fastButton = new Button("Fast");
        fastButton.setOnAction(e -> chaoAnimation.setAnimationSpeed(0.1));

        speedButtons.getChildren().addAll(slowButton, normalButton, fastButton);
    }

    /**
     * Updates the status label to show current Chao type and state
     */
    private void updateStatusLabel() {
        if (statusLabel != null) {
            statusLabel.setText("Current: " + currentChaoType + " Chao - " + currentState + " state");
        }
    }

    /**
     * Changes the Chao's state and updates the animation
     */
    public void changeState(State state) {
        currentState = state;
        chaoAnimation.changeState(state);

        // Refresh the UI to highlight the current selection
        controlPanel.getChildren().clear();
        createTestingUI();
    }

    /**
     * Changes the Chao type and updates the animation
     */
    public void changeChaoType(ChaoType type) {
        currentChaoType = type;
        chaoAnimation.changeChaoType(type);

        // Refresh the UI to highlight the current selection
        controlPanel.getChildren().clear();
        createTestingUI();
    }
}