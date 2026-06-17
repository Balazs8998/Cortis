package com.cortis.core.entity_legacy.tool.specification;

import com.cortis.core.entity_legacy.tool.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        schema = "specification",
        name = "tool_master",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "tm_manufacturer_code_manufacturer_key",
                        columnNames = {
                                "manufacturer_code",
                                "manufacturer"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class ToolMaster extends BaseEntity {

    @Column(name = "manufacturer_code", nullable = false)
    private String manufacturerCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "type_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "tm_tool_type_fkey")
    )

    private ToolType toolType;

    @Column(nullable = false)
    private String manufacturer;

    @Column
    private String description;

    @Column(name = "order_code")
    private String orderCode;

    @Column
    private String link;

    @Column
    private String catalog;

}