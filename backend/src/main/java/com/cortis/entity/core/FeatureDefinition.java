package com.cortis.entity.core;
import com.cortis.entity.tool.BaseEntity;
import com.cortis.enums.FeatureDataType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        schema = "core",
        name = "feature_definition",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "fd_name_key",
                        columnNames = "name"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class FeatureDefinition extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_type", nullable = false)
    private FeatureDataType dataType;

}