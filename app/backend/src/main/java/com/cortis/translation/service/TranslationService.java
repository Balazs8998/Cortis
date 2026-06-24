package com.cortis.translation.service;

import com.cortis.core.exception.ex.LanguageNotFoundException;
import com.cortis.translation.repository.TextRepository;
import com.cortis.translation.dto.TranslationProjection;
import com.cortis.translation.dto.TranslationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
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

        TranslationResponse response = makeTranslationResponse(translations);

        log.info("Translations loaded successfully: language={}, count={}", languageCode, translations.size());
        return response;
    }


    private TranslationResponse makeTranslationResponse(
            List<TranslationProjection> translations
    ) {
        TranslationResponse response = new TranslationResponse();

        log.info("Building translation response");
        translations.forEach(translation -> {

            String translationKey =
                    translation.getCategoryName()
                            + "."
                            + translation.getKeyword();

            log.info("Adding translation to response: key={}", translationKey,translation.getTranslationText());
            response.getTranslations().put(
                    translationKey,
                    translation.getTranslationText()
            );
        });

        return response;
    }


}
