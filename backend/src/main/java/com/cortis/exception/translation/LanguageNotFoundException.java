package com.cortis.exception.translation;

public class LanguageNotFoundException extends RuntimeException {

    public LanguageNotFoundException(String languageCode) {
        super("Language not found: " + languageCode);
    }
}