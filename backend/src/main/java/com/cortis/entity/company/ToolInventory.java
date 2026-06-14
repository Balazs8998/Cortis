package java.com.cortis.entity.company;

import com.cortis.entity.tool.BaseEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.com.cortis.entity.tool.specification.ToolMaster;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        schema = "company",
        name = "tool_inventory",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "ti_tool_code_key",
                        columnNames = "tool_code"
                )
        }
)
public class ToolInventory extends BaseEntity {

    @Column(name = "tool_code", nullable = false)
    private String toolCode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "tool_master_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "ti_tool_master_id_fkey"
            )
    )
    private ToolMaster toolMaster;
}