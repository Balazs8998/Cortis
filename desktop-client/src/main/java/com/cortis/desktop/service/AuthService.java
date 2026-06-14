package com.cortis.desktop.service;

import com.cortis.desktop.api.AuthApiClient;
import com.cortis.desktop.dto.LoginResponse;

public class AuthService {

    private final AuthApiClient authApiClient;

    public AuthService() {
        this.authApiClient = new AuthApiClient();
    }

    public LoginResponse login(String username, String password) {
        return authApiClient.login(username, password);
    }
}