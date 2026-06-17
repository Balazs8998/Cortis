package com.cortis.core.entity_legacy.translation;

import com.cortis.core.entity_legacy.tool.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(
        schema = "translation",
        name = "keyword"
)
@Getter
@Setter
@NoArgsConstructor
public class TranslationKeywordEntity extends BaseEntity {

    @Column(nullable = false)
    private String keyword;

}