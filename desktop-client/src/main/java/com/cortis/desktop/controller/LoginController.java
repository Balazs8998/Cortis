package com.cortis.desktop.controller;

import com.cortis.desktop.dto.LoginResponse;
import com.cortis.desktop.manager.ViewManager;
import com.cortis.desktop.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField visiblePasswordField;

    @FXML
    private Label errorLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void initialize() {

        // Mindkét mező mindig ugyanazt a jelszót tartalmazza.
        visiblePasswordField.textProperty()
                .bindBidirectional(passwordField.textProperty());

        // Induláskor a sima TextField legyen elrejtve.
        visiblePasswordField.setVisible(false);
        visiblePasswordField.setManaged(false);

        passwordField.setVisible(true);
        passwordField.setManaged(true);
    }

    @FXML
    private void showPassword() {

        visiblePasswordField.setVisible(true);
        visiblePasswordField.setManaged(true);

        passwordField.setVisible(false);
        passwordField.setManaged(false);

        visiblePasswordField.requestFocus();
        visiblePasswordField.positionCaret(
                visiblePasswordField.getText().length()
        );
    }

    @FXML
    private void hidePassword() {

        visiblePasswordField.setVisible(false);
        visiblePasswordField.setManaged(false);

        passwordField.setVisible(true);
        passwordField.setManaged(true);

        passwordField.requestFocus();
        passwordField.positionCaret(
                passwordField.getText().length()
        );
    }

    @FXML
    private void handleLogin() {

        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            errorLabel.setText(
                    "The username and password cannot be empty."
            );
            return;
        }

        try {
            LoginResponse response =
                    authService.login(username, password);

            System.out.println(
                    "Successfully logged in as:"
                            + response.username()
            );

            System.out.println(
                    "Token: "
                            + response.token()
            );

            errorLabel.setText("You are logged in!");

            ViewManager.switchScene(
                    usernameField,
                    "/com/cortis/desktop/view/main-menu.view.fxml",
                    "CORTIS - Főmenü"
            );

        } catch (Exception exception) {
            errorLabel.setText(exception.getMessage());
        }
    }
}