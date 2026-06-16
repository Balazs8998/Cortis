package com.cortis.entity.translation;

import com.cortis.entity.tool.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        schema = "translation",
        name = "text",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "text_keyword_id_language_id_category_id_key",
                        columnNames = {
                                "keyword_id",
                                "language_id",
                                "category_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class TranslationTextEntity extends BaseEntity {

    @Column(name = "translation_text", nullable = false)
    private String translationText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "keyword_id",
            nullable = false
    )
    private TranslationKeywordEntity keyword;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "language_id",
            nullable = false
    )
    private TranslationLanguageEntity language;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "category_id"
    )
    private TranslationCategoryEntity category;
}