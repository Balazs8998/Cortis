package com.cortis.entity.user;

import com.cortis.entity.tool.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        schema = "personal",
        name = "permissions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "permissions_code_key",
                        columnNames = "code"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class PermissionEntity extends BaseEntity {

    @Column(nullable = false)
    private String code;

    @Column
    private String description;
}
