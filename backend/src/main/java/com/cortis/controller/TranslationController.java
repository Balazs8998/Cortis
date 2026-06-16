package com.cortis.controller;

import com.cortis.dto.translation.TranslationResponse;
import com.cortis.services.TranslationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/translation")
public class TranslationController {

    private final TranslationService translationService;

    TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }


    @GetMapping("/{languageCode}")
    public ResponseEntity<TranslationResponse> getTranslationsByLanguageCode(
            @PathVariable("languageCode") String languageCode
    ) {
        TranslationResponse translationResponse =
                translationService.loadTranslations(languageCode);

        return ResponseEntity.ok(translationResponse);
    }

}
