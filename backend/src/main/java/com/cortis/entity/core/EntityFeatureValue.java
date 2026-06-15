package com.cortis.entity.core;
import com.cortis.entity.tool.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        schema = "specification",
        name = "entity_feature_value",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "efv_entity_type_entity_id_feature_id_key",
                        columnNames = {
                                "entity_type",
                                "entity_id",
                                "entity_feature_id"
                        }
                )
        }
)
public class EntityFeatureValue extends BaseEntity {

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "entity_feature_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "efv_entity_feature_id_fkey")
    )
    private EntityFeature entityFeature;

    @Column(name = "value_number")
    private BigDecimal valueNumber;

    @Column(name = "value_text")
    private String valueText;

    @Column(name = "entity_type", nullable = false)
    private String entityType;
}