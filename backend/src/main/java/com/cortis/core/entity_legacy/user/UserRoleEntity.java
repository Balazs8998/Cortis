package com.cortis.core.entity_legacy.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        schema = "personal",
        name = "user_roles"
)
@Getter
@Setter
@NoArgsConstructor
public class UserRoleEntity {

    @EmbeddedId
    private UserRoleIdEntity id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(
            name = "user_id",
            foreignKey = @ForeignKey(name = "user_roles_user_id_fkey")
    )
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(
            name = "role_id",
            foreignKey = @ForeignKey(name = "user_roles_role_id_fkey")
    )
    private RoleEntity role;
}