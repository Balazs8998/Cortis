package com.cortis.repositories.translation;

import com.cortis.dto.translation.TranslationProjection;
import com.cortis.entity.translation.TranslationTextEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TextRepository
        extends JpaRepository<TranslationTextEntity, UUID> {

    @Query("""
            SELECT
                category.categoryName AS categoryName,
                keyword.keyword AS keyword,
                text.translationText AS translationText
            FROM TranslationTextEntity text
            JOIN text.category category
            JOIN text.keyword keyword
            JOIN text.language language
            WHERE language.code = :languageCode
            """)
    List<TranslationProjection> findTranslationsByLanguageCode(
            @Param("languageCode") String languageCode
    );
}