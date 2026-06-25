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
        name = "users"
)
@Getter
@Setter
@NoArgsConstructor
public class UserEntity extends BaseEntity {

    @Column(
            name = "username",
            nullable = false,
            unique = true
    )
    private String username;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(
            name = "chip_code_hash",
            unique = true
    )
    private String chipCode;

    @Column(
            name = "is_active",
            nullable = false
    )
    private boolean active = true;

    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.LAZY
    )
    private Set<UserRoleEntity> userRoles = new HashSet<>();
}