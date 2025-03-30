//package com.example.chaotopia.Extra;
//
//import com.example.chaotopia.Extra.GameplayAnimationController;
//import com.example.chaotopia.Controller.GameplayController;
//import com.example.chaotopia.Model.Chao;
//import com.example.chaotopia.Model.ChaoType;
//import com.example.chaotopia.Model.State;
//import com.example.chaotopia.Model.Status;
//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.Label; // Import Label explicitly
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.BorderPane;
//// import javafx.scene.layout.StackPane; // Not directly needed here anymore
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//
//public class GameplayTest extends Application {
//
//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        // Load the FXML file (this also creates GameplayController and calls its initialize)
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/chaotopia/View/gameplay_test.fxml"));
//        Parent root = loader.load(); // Root is likely the BorderPane
//
//        // Get the GameplayController instance created by the FXMLLoader
//        GameplayController gameplayController = loader.getController();
//        if (gameplayController == null) {
//            throw new RuntimeException("Failed to load GameplayController from FXML.");
//        }
//
//        // Create the non-FXML controller manually
//        GameplayAnimationController statusController = new GameplayAnimationController();
//
//        // --- Look up ALL required UI elements from the loaded FXML ---
//        // Ensure root is the correct type or use more specific lookups if needed
//        if (!(root instanceof BorderPane)) {
//            throw new RuntimeException("Root element is not a BorderPane as expected.");
//        }
//        BorderPane mainContainer = (BorderPane) root;
//
//        VBox statusBarsContainer = (VBox) mainContainer.lookup("#statusBarsContainer");
//        Label nameLabel = (Label) mainContainer.lookup("#nameLabel");
//        Label scoreLabel = (Label) mainContainer.lookup("#scoreLabel");
//        // *** Look up the chaoImageView defined in FXML ***
//        ImageView chaoImageView = (ImageView) mainContainer.lookup("#chaoImageView");
//        // *** Look up fruitImageView (needed by GameplayController, but check injection) ***
//        ImageView fruitImageView = (ImageView) mainContainer.lookup("#fruitImageView"); // Already in GameplayController via @FXML
//
//        // --- Verify lookups ---
//        if (statusBarsContainer == null || nameLabel == null || scoreLabel == null || chaoImageView == null || fruitImageView == null) {
//            // Check console for lookup errors if any are null
//            System.err.println("StatusBarsContainer: " + statusBarsContainer);
//            System.err.println("NameLabel: " + nameLabel);
//            System.err.println("ScoreLabel: " + scoreLabel);
//            System.err.println("ChaoImageView: " + chaoImageView);
//            System.err.println("FruitImageView: " + fruitImageView); // Should be found
//            throw new RuntimeException("Failed to look up one or more required UI elements from FXML. Check fx:id attributes.");
//        }
//
//        // Initialize the GameplayAnimationController manually, passing the looked-up elements
//        statusController.initializeManually(
//                statusBarsContainer,
//                nameLabel,
//                scoreLabel,
//                chaoImageView, // Pass the looked-up ImageView
//                mainContainer
//        );
//
//        // Connect the controllers
//        gameplayController.setStatusController(statusController);
//
//        // Create a Chao to test with
//        Status initialStatus = new Status(100, 100, 100, 100);
//        Chao testChao = new Chao(0, "Test Chao", ChaoType.BLUE, State.NORMAL, initialStatus);
//
//        // Set the Chao for both controllers (nameLabel update happens here too now)
//        gameplayController.setChao(testChao); // This will also call statusController.setChao
//
//        // Set up the scene
//        Scene scene = new Scene(root, 800, 600);
//        scene.setUserData(gameplayController); // For potential access elsewhere (like GameOver)
//
//        primaryStage.setTitle("Chaotopia - Gameplay Test");
//        primaryStage.setScene(scene);
//        primaryStage.show();
//
//        // Clean up when closing
//        primaryStage.setOnCloseRequest(event -> {
//            System.out.println("Shutdown requested...");
//            gameplayController.shutdown();
//            // statusController shutdown is called within gameplayController.shutdown()
//            System.out.println("Application closing.");
//        });
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}