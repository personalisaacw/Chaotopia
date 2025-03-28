package com.example.chaotopia.Application;

import com.example.chaotopia.Controller.BaseController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Gameplay extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/chaotopia/View/Gameplay.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        BaseController.addCSS(scene);
        primaryStage.setScene(scene);
        primaryStage.setTitle("ChaoTopia - Play Game");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}