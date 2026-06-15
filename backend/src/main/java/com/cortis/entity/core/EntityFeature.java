package com.cortis.entity.core;

import com.cortis.entity.tool.BaseEntity;
import com.cortis.enums.RoleType;
import com.cortis.enums.EntityType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        schema = "core",
        name = "entity_feature",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "ef_feature_id_entity_type_role_key",
                        columnNames = {
                                "feature_id",
                                "entity_type",
                                "role"
                        }
                )
        }
)
public class EntityFeature extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false)
    private EntityType entityType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "feature_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "ef_feature_definition_id_fkey"
            )
    )
    private FeatureDefinition feature;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private RoleType role;
}