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
                    "No JWT token was returned by the backend."            );
        }

        SessionManager.login(
                response.userName(),
                response.token()
        );

        return response;
    }

    public LoginResponse loginWithChipCard(String chipCardNumber) {

        LoginResponse response =
                authApiClient.loginWithChipCard(chipCardNumber);

        if (response.token() == null || response.token().isBlank()){
            throw new IllegalStateException(
                    "No JWT token was returned by the backend."            );
        }

        SessionManager.login(
                response.userName(),
                response.token()
        );

        return response;
    }
}