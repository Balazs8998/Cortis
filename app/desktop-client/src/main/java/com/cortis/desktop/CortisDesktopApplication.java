package com.cortis.desktop;

import com.cortis.desktop.core.exception.DesktopExceptionHandler;
import com.cortis.desktop.translation.service.TranslationService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class CortisDesktopApplication extends Application {

    private DesktopExceptionHandler exceptionHandler;
    private TranslationService translationService;

    @Override
    public void start(Stage stage) throws IOException {

        translationService = new TranslationService();

        exceptionHandler =
                new DesktopExceptionHandler(translationService);

        Thread.setDefaultUncaughtExceptionHandler(
                (thread, throwable) ->
                        Platform.runLater(
                                () -> exceptionHandler.handle(throwable)
                        )
        );

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