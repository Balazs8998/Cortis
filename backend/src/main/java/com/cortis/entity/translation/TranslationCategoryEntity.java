package com.cortis.entity.translation;


import com.cortis.entity.tool.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        schema = "translation",
        name = "category"
)
@Getter
@Setter
@NoArgsConstructor
public class TranslationCategoryEntity extends BaseEntity {

    @Column(name = "category_name", nullable = false)
    private String categoryName;

}