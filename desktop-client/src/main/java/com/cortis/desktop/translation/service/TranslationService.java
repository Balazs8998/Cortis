package com.cortis.desktop.translation.service;


import com.cortis.desktop.translation.api.TranslationApiClient;
import com.cortis.desktop.translation.cache.TranslationCache;
import com.cortis.desktop.translation.dto.TranslationResponse;

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
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String translate(String key, String text) {

        String translateText = translationCache.getCache().getOrDefault(key, text);

        return translateText;

    }

}
