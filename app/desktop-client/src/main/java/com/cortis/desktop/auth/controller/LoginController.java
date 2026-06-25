package com.cortis.desktop.auth.controller;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import com.cortis.desktop.translation.service.TranslationService;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuButton;
import com.cortis.desktop.auth.dto.LoginResponse;
import com.cortis.desktop.core.navigation.ViewManager;
import com.cortis.desktop.auth.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;


public class LoginController {
    @FXML
    private Label loginTitleLabel;

    @FXML
    private Label loginSubtitleLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label passwordLabel;

    @FXML
    private Button loginButton;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField visiblePasswordField;

    @FXML
    private Label errorLabel;

    @FXML
    private VBox passwordLoginBox;

    @FXML
    private VBox chipLoginBox;


    @FXML
    private MenuButton languageMenuButton;

    private final AuthService authService = new AuthService();
    private final TranslationService translationService = new TranslationService();

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
    private TextField chipCodeField;
    public void handleShowChipLogin(ActionEvent actionEvent) {


        passwordLoginBox.setVisible(false);
        passwordLoginBox.setManaged(false);

        chipLoginBox.setVisible(true);
        chipLoginBox.setManaged(true);

        errorLabel.setText("");
        chipCodeField.clear();

        Platform.runLater(chipCodeField::requestFocus);
    }

    @FXML
    public void handleShowPasswordLogin(ActionEvent actionEvent) {

        chipLoginBox.setVisible(false);
        chipLoginBox.setManaged(false);

        passwordLoginBox.setVisible(true);
        passwordLoginBox.setManaged(true);

        errorLabel.setText("");
        chipCodeField.clear();

        Platform.runLater(usernameField::requestFocus);
    }

    @FXML
    private void handleChipCardLogin() {

        String chipCardNumber = chipCodeField.getText().trim();

        if (chipCardNumber.isBlank()) {
            errorLabel.setText(
                    "The chip card number cannot be empty."
            );
            return;
        }

        try {
                    authService.loginWithChipCard(chipCardNumber);

            errorLabel.setText("You are logged in!");

            ViewManager.switchScene(
                    chipCodeField,
                    "/com/cortis/desktop/view/main-menu.view.fxml",
                    "CORTIS - Főmenü"
            );

        }  catch (Exception exception) {
            errorLabel.setText(exception.getMessage());
        }

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
            authService.login(username, password);


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

    @FXML
    public void handleLanguageChange(ActionEvent actionEvent) {

        MenuItem selectedLanguage = (MenuItem) actionEvent.getSource();
        String languageCode = selectedLanguage.getId();

        translationService.loadTranslations(languageCode);

        refreshLoginTexts();

        languageMenuButton.setText("🌐 " + languageCode.toUpperCase());
    }

    private void refreshLoginTexts() {

        loginTitleLabel.setText(
                translationService.translate(
                        "login.title",
                        "Bejelentkezés"
                )
        );

        loginSubtitleLabel.setText(
                translationService.translate(
                        "login.subtitle",
                        "Lépj be a CORTIS rendszerbe"
                )
        );

        usernameLabel.setText(
                translationService.translate(
                        "login.username.label",
                        "Felhasználónév"
                )
        );

        usernameField.setPromptText(
                translationService.translate(
                        "login.username.prompt",
                        "Add meg a felhasználóneved"
                )
        );

        passwordLabel.setText(
                translationService.translate(
                        "login.password.label",
                        "Jelszó"
                )
        );

        String passwordPrompt = translationService.translate(
                "login.password.prompt",
                "Add meg a jelszavad"
        );

        passwordField.setPromptText(passwordPrompt);
        visiblePasswordField.setPromptText(passwordPrompt);

        loginButton.setText(
                translationService.translate(
                        "login.button.submit",
                        "Bejelentkezés"
                )
        );

        // TODO: Add translations for the new chip-card login controls after fixing the exception dialog message handling.

    }

}