package com.cortis.desktop.auth.api;

import com.cortis.desktop.auth.dto.LoginRequest;
import com.cortis.desktop.auth.dto.LoginResponse;

import com.cortis.desktop.core.exception.ex.ApiException;
import com.cortis.desktop.core.exception.ErrorResponse;
import com.cortis.desktop.core.exception.ex.NetworkException;
import com.cortis.desktop.core.exception.ex.UnexpectedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


import java.io.IOException;
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

        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
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

            if (response.statusCode() >= 200
                    && response.statusCode() < 300) {

                return objectMapper.readValue(
                        response.body(),
                        LoginResponse.class
                );
            }

            if (response.statusCode() >= 400) {

                ErrorResponse errorResponse =
                        objectMapper.readValue(
                                response.body(),
                                ErrorResponse.class
                        );

                throw new ApiException(errorResponse);
            }

            throw new UnexpectedException(
                    "Unexpected login response. HTTP status: "
                            + response.statusCode()
            );

        } catch (ApiException exception) {
            throw exception;

        } catch (JsonProcessingException exception) {
            throw new UnexpectedException(
                    "Could not process authentication JSON response",
                    exception
            );

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            throw new NetworkException(
                    "Authentication request was interrupted",
                    exception
            );

        } catch (IOException exception) {
            throw new NetworkException(
                    "Could not connect to the backend",
                    exception
            );
        }
    }
}