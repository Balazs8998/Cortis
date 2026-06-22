package com.cortis.translation.service;

import com.cortis.core.exception.translation.LanguageNotFoundException;
import com.cortis.translation.repositorie.TextRepository;
import com.cortis.translation.dto.TranslationProjection;
import com.cortis.translation.dto.TranslationResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TranslationService {


    private final TextRepository textRepository;

    public TranslationService( TextRepository textRepository) {

        this.textRepository = textRepository;
    }


    public TranslationResponse loadTranslations(String languageCode) {

        List<TranslationProjection> translations =
                textRepository.findTranslationsByLanguageCode(languageCode);


        if (translations.isEmpty()) {
            throw new LanguageNotFoundException(languageCode);
        }

        return makeTranslationResponse(translations);
    }


    private TranslationResponse makeTranslationResponse(
            List<TranslationProjection> translations
    ) {
        TranslationResponse response = new TranslationResponse();

        translations.forEach(translation -> {

            String translationKey =
                    translation.getCategoryName()
                            + "."
                            + translation.getKeyword();

            response.getTranslations().put(
                    translationKey,
                    translation.getTranslationText()
            );
        });

        return response;
    }


}
