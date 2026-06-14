package com.cortis.desktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class CortisDesktopApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                CortisDesktopApplication.class.getResource(
                        "/com/cortis/desktop/view/login-view.fxml"
                )
        );

        Parent root = loader.load();

        Scene scene = new Scene(root, 1100, 700);

        scene.getStylesheets().add(
                Objects.requireNonNull(
                        CortisDesktopApplication.class.getResource(
                                "/com/cortis/desktop/css/login.css"
                        )
                ).toExternalForm()
        );

        stage.setTitle("CORTIS – Login");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}