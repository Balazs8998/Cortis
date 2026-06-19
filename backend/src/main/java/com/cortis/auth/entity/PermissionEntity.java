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
        name = "permissions"
)
@Getter
@Setter
@NoArgsConstructor
public class PermissionEntity extends BaseEntity {

    @Column(
            name = "code",
            nullable = false,
            unique = true
    )
    private String code;

    @Column(name = "description")
    private String description;

    @OneToMany(
            mappedBy = "permission",
            fetch = FetchType.LAZY
    )
    private Set<RolePermissionEntity> rolePermissions =
            new HashSet<>();
}
