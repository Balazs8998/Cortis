package com.cortis.entity.tool.specification;

import com.cortis.entity.tool.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        schema = "specification",
        name = "tool_type_mounting_option",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "ttmo_tool_type_id_option_name_key",
                        columnNames = {"type_id", "option_name"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class ToolTypeMountingOption extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private ToolType toolType;

    @Column(name = "option_name", nullable = false)
    private String optionName;

    @Column(name = "requirement_basis", nullable = false)
    private String requirementBasis;

    @OneToMany(mappedBy = "mountingOption", fetch = FetchType.LAZY)
    private List<ToolMountingFeatureRequirement> requirements = new ArrayList<>();
}