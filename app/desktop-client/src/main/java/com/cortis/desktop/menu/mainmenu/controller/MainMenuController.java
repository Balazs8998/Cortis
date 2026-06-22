package com.cortis.desktop.menu.mainmenu.controller;

import com.cortis.desktop.core.navigation.ViewManager;
import com.cortis.desktop.menu.mainmenu.service.MainMenuService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class MainMenuController {

    private final MainMenuService mainMenuService;

    @FXML
    private Button logoutButton;

    @FXML
    private Button warehouseButton;

    public MainMenuController() {
        this.mainMenuService = new MainMenuService();
    }

    @FXML
    private void handleLogout() throws IOException {
        ViewManager.switchScene(
                logoutButton,
                "/com/cortis/desktop/view/login-view.fxml",
                "CORTIS - Login"
        );
    }

    @FXML
    private void openWarehouse() throws IOException {
        ViewManager.switchScene(
                warehouseButton,
                "/com/cortis/desktop/view/warehouse-view.fxml",
                "CORTIS - Raktár"
        );
    }
}