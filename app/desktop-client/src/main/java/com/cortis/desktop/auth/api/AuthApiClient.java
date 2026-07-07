package com.cortis.desktop.auth.api;

import com.cortis.desktop.auth.dto.LoginRequest;
import com.cortis.desktop.auth.dto.LoginResponse;

import com.cortis.desktop.auth.dto.LoginWithChipRequest;
import com.cortis.desktop.core.exception.ex.ApiException;
import com.cortis.desktop.core.exception.ErrorResponse;
import com.cortis.desktop.core.exception.ex.NetworkException;
import com.cortis.desktop.core.exception.ex.UnexpectedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Slf4j
public class AuthApiClient {

    private static final String LOGIN_URL =
            "http://localhost:8080/api/auth/login";

    private static final String LOGIN_WITH_CHIP_URL =
            "http://localhost:8080/api/auth/loginWithChip";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AuthApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    public LoginResponse loginWithChipCard(String chipCardNumber) {
        log.info("Fronted login with chip card:");


        LoginWithChipRequest loginWithChipRequest =
                new LoginWithChipRequest(chipCardNumber);

        return executeLoginRequest(loginWithChipRequest, LOGIN_WITH_CHIP_URL);

    }

    public LoginResponse login(String username, String password) {
        log.info("Fronted login with username and password:");

        LoginRequest loginRequest =
                new LoginRequest(username, password);

       return executeLoginRequest(loginRequest, LOGIN_URL);
    }

    private String makeRequestBody(Object request) throws JsonProcessingException {
        return objectMapper.writeValueAsString(request);
    }

    private HttpRequest requestBuilder(String requestBody, String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws IOException, InterruptedException {

      return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private LoginResponse executeLoginRequest(Object loginRequest, String url) {

        try {


            String requestBody =
                    makeRequestBody(loginRequest);

            HttpRequest request =
                    requestBuilder(requestBody, url);

            HttpResponse<String> response =
                    sendRequest(request);

            if (response.statusCode() >= 200
                    && response.statusCode() < 300) {

                return objectMapper.readValue(
                        response.body().toString(),
                        LoginResponse.class
                );
            }

            if (response.statusCode() >= 400) {

                ErrorResponse errorResponse =
                        objectMapper.readValue(
                                response.body().toString(),
                                ErrorResponse.class
                        );

                throw new ApiException(errorResponse);
            }

            throw new UnexpectedException(
                    "Unexpected login response. HTTP status: "
                            + response.statusCode()
            );

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