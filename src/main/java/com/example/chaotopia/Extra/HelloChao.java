<<<<<<<< HEAD:src/main/java/com/example/chaotopia/Extra/HelloChao.java
package com.example.chaotopia.Extra;
========
package com.example.chaotopia.Model;
>>>>>>>> 196c8d0 (added FruitAnimation.java, FruitType.java, FruitAnimation.fxml, and some testing classes FruitAnimationController.java, FruitAnimationTest.java. I also changed the file structure.):src/main/java/com/example/chaotopia/Model/HelloApplication.java

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloChao extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloChao.class.getResource("/com/example/chaotopia/View/ChaoAnimation.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 600);
        stage.setTitle("Chaotopia");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}