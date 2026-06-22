package com.cortis.desktop.core.navigation;

import com.cortis.desktop.CortisDesktopApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class ViewManager {

    public static void switchScene(
            Node node,
            String fxml,
            String title
    ) throws IOException {

        URL fxmlUrl = CortisDesktopApplication.class.getResource(fxml);

        if (fxmlUrl == null) {
            throw new IllegalArgumentException(
                    "FXML file not found: " + fxml
            );
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);

        Parent root = loader.load();

        Stage stage = (Stage) node
                .getScene()
                .getWindow();

        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
    }
}