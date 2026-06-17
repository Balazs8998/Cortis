package com.cortis.desktop.translation.api;

import com.cortis.desktop.translation.dto.TranslationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class TranslationApiClient {

    private static final String TRANSLATION_API_URL = "http://localhost:8080/api/translation/";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;


    public TranslationApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5)).build();
        this.objectMapper = new ObjectMapper();
    }


    public TranslationResponse fetchTranslations(String languageCode) throws Exception {

        String TRANSLATION_API_URL_WITH_LANGUAGE_CODE = TRANSLATION_API_URL + languageCode;


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TRANSLATION_API_URL_WITH_LANGUAGE_CODE))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .GET()
                .build();


        HttpResponse<String> response =
                httpClient.send(request,HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();
        String responseBody = response.body();

        if (statusCode != 200) {
            throw new RuntimeException("Translation API hiba: " + responseBody);
        }

        TranslationResponse translationResponse = objectMapper.readValue(responseBody,TranslationResponse.class);

        return translationResponse;

    }

}
