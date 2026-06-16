package com.cortis.entity.translation;
import com.cortis.entity.tool.BaseEntity;
import jakarta.persistence.*;
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