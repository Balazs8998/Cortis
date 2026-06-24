package com.cortis.auth.security;


import com.cortis.auth.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


@Slf4j
public class CortisUserPrincipal implements UserDetails {

    private final UserEntity user;
    private final Set<GrantedAuthority> authorities;

    public CortisUserPrincipal(UserEntity user) {
        this.user = user;
        this.authorities = buildAuthorities(user);
    }

    private Set<GrantedAuthority> buildAuthorities(UserEntity user) {

        Set<GrantedAuthority> authorities = new HashSet<>();

        for (UserRoleEntity userRole : user.getUserRoles()) {

            if (userRole.getDeletedAt() != null) {
                continue;
            }

            RoleEntity role = userRole.getRole();

            if (role == null || role.getDeletedAt() != null) {
                continue;
            }

            authorities.add(
                    new SimpleGrantedAuthority(
                            "ROLE_" + role.getName().toUpperCase()
                    )
            );

            for (RolePermissionEntity rolePermission
                    : role.getRolePermissions()) {

                if (rolePermission.getDeletedAt() != null) {
                    continue;
                }

                PermissionEntity permission =
                        rolePermission.getPermission();

                if (permission == null
                        || permission.getDeletedAt() != null) {
                    continue;
                }

                authorities.add(
                        new SimpleGrantedAuthority(
                                permission.getCode()
                        )
                );
            }
        }

        log.debug("Authorities built for user={}, authorityCount={}", user.getUsername(), authorities.size());

        return authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public boolean isEnabled() {
        return user.isActive();
    }
}