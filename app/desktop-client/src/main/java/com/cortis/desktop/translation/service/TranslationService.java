package com.cortis.desktop.translation.service;


import com.cortis.desktop.translation.api.TranslationApiClient;
import com.cortis.desktop.translation.cache.TranslationCache;
import com.cortis.desktop.translation.dto.TranslationResponse;

import java.io.IOException;

public class TranslationService {

    private final TranslationCache translationCache;
    private final TranslationApiClient translationApiClient;

    public TranslationService() {
        this.translationCache = new TranslationCache();
        this.translationApiClient = new TranslationApiClient();
    }

    public void loadTranslations(String languageCode) {
        try {
            TranslationResponse response = translationApiClient.fetchTranslations(languageCode);
            translationCache.getCache().clear();
            translationCache.getCache().putAll(response.getTranslations());
        }catch (IOException exception) {
            throw new RuntimeException(
                    "Could not connect to the server.",
                    exception
            );

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "The request was interrupted",
                    exception
            );
        }
    }

    public String translate(String key, String text) {

        String translateText = translationCache.getCache().getOrDefault(key, text);

        return translateText;

    }

}
