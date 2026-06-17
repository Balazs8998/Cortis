package com.cortis.desktop.auth.api;

import com.cortis.desktop.auth.dto.LoginRequest;
import com.cortis.desktop.auth.dto.LoginResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
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

            LoginRequest loginRequest = createLoginRequest(username,password);

            String requestBody =
                    objectMapper.writeValueAsString(loginRequest);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(LOGIN_URL))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(
                            HttpRequest.BodyPublishers.ofString(
                                    requestBody
                            )
                    )
                    .build();


            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            int statusCode = response.statusCode();
            String responseBody = response.body();

            System.out.println(
                    "[AuthApiClient] HTTP válasz megérkezett."
            );

            System.out.println(
                    "[AuthApiClient] HTTP státusz: "
                            + statusCode
            );

            // Csak fejlesztés közben hagyd bent,
            // mert sikeres válasz esetén tokent tartalmazhat.
            System.out.println(
                    "[AuthApiClient] HTTP response body: "
                            + responseBody
            );

            // 6. Sikeres válasz
            if (statusCode == 200) {

                System.out.println(
                        "[AuthApiClient] Sikeres státusz, "
                                + "LoginResponse feldolgozása."
                );

                LoginResponse loginResponse =
                        objectMapper.readValue(
                                responseBody,
                                LoginResponse.class
                        );



                return loginResponse;
            }

            // 7. Ismert hibakódok
            if (statusCode == 400) {
                throw new RuntimeException(
                        "Hibás bejelentkezési kérés. "
                                + responseBody
                );
            }

            if (statusCode == 401) {
                throw new RuntimeException(
                        "Hibás felhasználónév vagy jelszó."
                );
            }

            if (statusCode == 403) {
                throw new RuntimeException(
                        "A hozzáférés megtagadva."
                );
            }

            if (statusCode >= 500) {
                throw new RuntimeException(
                        "Backend szerverhiba. HTTP státusz: "
                                + statusCode
                                + ". Válasz: "
                                + responseBody
                );
            }

            throw new RuntimeException(
                    "Váratlan HTTP-válasz. Státusz: "
                            + statusCode
                            + ". Válasz: "
                            + responseBody
            );

        } catch (ConnectException exception) {

            System.err.println(
                    "[AuthApiClient] Nem érhető el a backend."
            );

            exception.printStackTrace();

            throw new RuntimeException(
                    "Nem sikerült csatlakozni a backendhez. "
                            + "Ellenőrizd, hogy fut-e a Spring Boot "
                            + "alkalmazás a 8080-as porton.",
                    exception
            );

        } catch (HttpTimeoutException exception) {

            System.err.println(
                    "[AuthApiClient] A HTTP kérés időtúllépés miatt megszakadt."
            );

            exception.printStackTrace();

            throw new RuntimeException(
                    "A szerver nem válaszolt időben.",
                    exception
            );

        } catch (JsonProcessingException exception) {

            System.err.println(
                    "[AuthApiClient] Jackson JSON-feldolgozási hiba."
            );

            exception.printStackTrace();

            throw new RuntimeException(
                    "A szerver válasza nem alakítható "
                            + "LoginResponse objektummá.",
                    exception
            );

        } catch (IOException exception) {

            System.err.println(
                    "[AuthApiClient] HTTP kommunikációs hiba."
            );

            exception.printStackTrace();

            throw new RuntimeException(
                    "Kommunikációs hiba történt a backenddel.",
                    exception
            );

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();

            System.err.println(
                    "[AuthApiClient] A HTTP kérés megszakadt."
            );

            throw new RuntimeException(
                    "A bejelentkezési kérés megszakadt.",
                    exception
            );
        }
    }

    private LoginRequest createLoginRequest(String username, String password) {
        return new LoginRequest(username, password);
    }
}