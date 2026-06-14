package com.cortis.entity.user;

import com.cortis.entity.tool.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        schema = "personal",
        name = "roles",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "roles_name_key",
                        columnNames = "name"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class RoleEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "is_admin", nullable = false)
    private Boolean admin = false;
}