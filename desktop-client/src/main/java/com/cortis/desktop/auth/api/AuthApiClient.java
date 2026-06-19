package com.cortis.desktop.auth.api;

import com.cortis.desktop.auth.dto.LoginRequest;
import com.cortis.desktop.auth.dto.LoginResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class AuthApiClient {

    private static final String LOGIN_URL =
            "http://localhost:8080/api/auth/login";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AuthApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        this.objectMapper = new ObjectMapper();
    }


    public LoginResponse login(String username, String password) {

        try {
            LoginRequest loginRequest =
                    new LoginRequest(username, password);

            String requestBody =
                    objectMapper.writeValueAsString(loginRequest);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(LOGIN_URL))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() == 200) {
                return objectMapper.readValue(
                        response.body(),
                        LoginResponse.class
                );
            }

            if (response.statusCode() == 401) {
                throw new RuntimeException(
                        "Hibás felhasználónév vagy jelszó."
                );
            }

            throw new RuntimeException(
                    "Login hiba. HTTP státusz: "
                            + response.statusCode()
                            + ". Válasz: "
                            + response.body()
            );

        } catch (Exception exception) {
            throw new RuntimeException(
                    "Bejelentkezési hiba: "
                            + exception.getMessage(),
                    exception
            );
        }
    }
}