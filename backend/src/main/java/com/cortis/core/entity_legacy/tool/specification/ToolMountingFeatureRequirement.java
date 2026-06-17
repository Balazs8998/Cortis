package com.cortis.core.entity_legacy.tool.specification;


import com.cortis.core.entity_legacy.core.EntityFeature;
import com.cortis.core.entity_legacy.tool.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        schema = "specification",
        name = "tool_mounting_feature_requirement",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "tmfr_mounting_option_id_requirement_feature_id_key",
                        columnNames = {"mounting_option_id", "requirement_feature_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class ToolMountingFeatureRequirement extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "mounting_option_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "tmfr_mounting_option_id_fkey")
    )
    private ToolTypeMountingOption mountingOption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "requirement_feature_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "tmfr_requirement_feature_id_fkey")
    )
    private EntityFeature requirementFeature;
}