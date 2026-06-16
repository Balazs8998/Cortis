package com.cortis.entity.translation;

import com.cortis.entity.tool.BaseEntity;
import jakarta.persistence.*;
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