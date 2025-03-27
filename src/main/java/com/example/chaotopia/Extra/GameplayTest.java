package com.example.chaotopia.Extra;

import com.example.chaotopia.Controller.GameplayAnimationController;
import com.example.chaotopia.Controller.GameplayController;
import com.example.chaotopia.Model.Chao;
import com.example.chaotopia.Model.ChaoType;
import com.example.chaotopia.Model.State;
import com.example.chaotopia.Model.Status;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GameplayTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/chaotopia/View/gameplay_test.fxml"));
        Parent root = loader.load();

        // Get the GameplayController
        GameplayController gameplayController = loader.getController();

        // Create a new GameplayAnimationController
        GameplayAnimationController statusController = new GameplayAnimationController();

        // Get references to UI elements needed by the ChaoStatusController
        BorderPane mainContainer = (BorderPane) root;
        VBox statusBarsContainer = (VBox) mainContainer.lookup("#statusBarsContainer");
        StackPane chaoImageContainer = (StackPane) mainContainer.lookup("#chaoImageContainer");

        // Create and add an ImageView for the Chao
        ImageView chaoImageView = new ImageView();
        chaoImageView.setFitWidth(150);
        chaoImageView.setFitHeight(150);
        chaoImageView.setPreserveRatio(true);
        chaoImageContainer.getChildren().add(chaoImageView);

        // Initialize the ChaoStatusController with necessary references
        statusController.initializeManually(
                statusBarsContainer,
                (javafx.scene.control.Label) mainContainer.lookup("#nameLabel"),
                (javafx.scene.control.Label) mainContainer.lookup("#scoreLabel"),
                chaoImageView,
                mainContainer
        );

        // Connect the controllers
        gameplayController.setStatusController(statusController);

        // Create a Chao to test with
        Status initialStatus = new Status(100, 100, 100, 100);
        Chao testChao = new Chao(0, "Test Chao", ChaoType.BLUE, State.NORMAL, initialStatus);

        // Set the Chao for both controllers
        gameplayController.setChao(testChao);

        // Set up the scene
        Scene scene = new Scene(root, 800, 600);

        // Store the controller in the scene's userData for access from GameOverScreen
        scene.setUserData(gameplayController);

        primaryStage.setTitle("Chaotopia - Gameplay Test");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Clean up when closing
        primaryStage.setOnCloseRequest(event -> {
            gameplayController.shutdown();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}