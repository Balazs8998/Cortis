package com.cortis.auth.entity;


import com.cortis.core.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        schema = "personal",
        name = "roles"
)
@Getter
@Setter
@NoArgsConstructor
public class RoleEntity extends BaseEntity {

    @Column(
            name = "name",
            nullable = false,
            unique = true
    )
    private String name;

    @Column(
            name = "is_admin",
            nullable = false
    )
    private boolean admin = false;

    @OneToMany(
            mappedBy = "role",
            fetch = FetchType.LAZY
    )
    private Set<UserRoleEntity> userRoles = new HashSet<>();

    @OneToMany(
            mappedBy = "role",
            fetch = FetchType.LAZY
    )
    private Set<RolePermissionEntity> rolePermissions =
            new HashSet<>();
}
