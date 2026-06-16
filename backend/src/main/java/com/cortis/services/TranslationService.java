package com.cortis.services;

import com.cortis.dto.translation.TranslationProjection;
import com.cortis.dto.translation.TranslationResponse;
import com.cortis.exception.translation.LanguageNotFoundException;
import com.cortis.repositories.translation.CategoryRepository;
import com.cortis.repositories.translation.LanguageRepositroy;
import com.cortis.repositories.translation.TextRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TranslationService {

    private final LanguageRepositroy languageRepositroy;
    private final CategoryRepository categoryRepository;
    private final TextRepository textRepository;

    public TranslationService(LanguageRepositroy languageRepositroy, CategoryRepository categoryRepository, TextRepository textRepository) {
        this.languageRepositroy = languageRepositroy;
        this.categoryRepository = categoryRepository;
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
