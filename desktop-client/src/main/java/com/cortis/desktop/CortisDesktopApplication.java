package com.cortis.desktop;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class CortisDesktopApplication extends Application {

    @Override
    public void start(Stage primaryStage) {

        Label titleLabel = new Label("CORTIS Desktop Client");

        BorderPane root = new BorderPane();
        root.setCenter(titleLabel);

        Scene scene = new Scene(root, 1000, 700);

        primaryStage.setTitle("CORTIS");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}