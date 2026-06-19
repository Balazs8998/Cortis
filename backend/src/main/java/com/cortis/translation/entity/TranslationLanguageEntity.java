package com.cortis.translation.entity;

import com.cortis.core.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        schema = "translation",
        name = "language",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "language_code_key",
                        columnNames = "code"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class TranslationLanguageEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String code;

}