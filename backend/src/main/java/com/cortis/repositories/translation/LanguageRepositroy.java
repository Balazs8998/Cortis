package com.cortis.repositories.translation;


import com.cortis.entity.translation.TranslationLanguageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LanguageRepositroy extends JpaRepository<TranslationLanguageEntity, UUID> {
    TranslationLanguageEntity findByCode(String languageCode);

}
