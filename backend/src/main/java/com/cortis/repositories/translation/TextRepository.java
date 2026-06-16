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
            categoryEntity.categoryName AS categoryName,
            keywordEntity.keyword AS keyword,
            text.translationText AS translationText
        FROM TranslationTextEntity text
        JOIN text.category categoryEntity
        JOIN text.keyword keywordEntity
        JOIN text.language languageEntity
        WHERE languageEntity.code = :languageCode
        """)
    List<TranslationProjection> findTranslationsByLanguageCode(
            @Param("languageCode") String languageCode
    );
}