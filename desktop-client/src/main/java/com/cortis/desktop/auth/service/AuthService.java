package com.cortis.desktop.auth.service;

import com.cortis.desktop.auth.api.AuthApiClient;
import com.cortis.desktop.auth.dto.LoginResponse;

public class AuthService {

    private final AuthApiClient authApiClient;

    public AuthService() {
        this.authApiClient = new AuthApiClient();
    }

    public LoginResponse login(String username, String password) {
        return authApiClient.login(username, password);
    }
}