package com.cortis.core.entity_legacy.user;

import com.cortis.core.entity_legacy.tool.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        schema = "personal",
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "users_username_key",
                        columnNames = "username"
                ),
                @UniqueConstraint(
                        name = "users_chip_code_key",
                        columnNames = "chip_code"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class UserEntity extends BaseEntity {

    @Column(nullable = false)
    private String username;


    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "chip_code")
    private String chipCode;

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

}