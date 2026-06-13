package com.cortis.entity.tool.specification;

import com.cortis.entity.tool.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        schema = "specification",
        name = "tool_type",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "tt_name_key",
                        columnNames = "name"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class ToolType extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column
    private String description;



}
