package com.cortis.desktop.auth.service;

import com.cortis.desktop.auth.api.AuthApiClient;
import com.cortis.desktop.auth.dto.LoginResponse;
import com.cortis.desktop.auth.session.SessionManager;

public class AuthService {

    private final AuthApiClient authApiClient;

    public AuthService() {
        this.authApiClient = new AuthApiClient();
    }

    public LoginResponse login(String userName, String password) {

        LoginResponse response =
                authApiClient.login(userName, password);

        if (response.token() == null || response.token().isBlank()) {
            throw new IllegalStateException(
                    "A backend nem adott vissza JWT tokent."
            );
        }

        SessionManager.login(
                response.userName(),
                response.token()
        );

        return response;
    }
}