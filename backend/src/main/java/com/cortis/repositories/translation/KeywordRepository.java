package com.cortis.repositories.translation;

import com.cortis.entity.translation.TranslationKeywordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface KeywordRepository extends JpaRepository<TranslationKeywordEntity, UUID> {
}
