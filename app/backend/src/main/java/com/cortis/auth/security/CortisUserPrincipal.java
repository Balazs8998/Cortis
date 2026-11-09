package com.cortis.auth.security;

import com.cortis.auth.entity.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** Stores a detached, serializable snapshot instead of a live JPA graph in the HTTP session. */
public final class CortisUserPrincipal implements UserDetails, CredentialsContainer, Serializable {
    @Serial private static final long serialVersionUID = 1L;

    private final UUID userId;
    private final String username;
    private final boolean enabled;
    private final Set<GrantedAuthority> authorities;
    private String passwordHash;

    public CortisUserPrincipal(UserEntity user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.passwordHash = user.getPasswordHash();
        this.enabled = user.isActive() && user.getDeletedAt() == null;
        this.authorities = Collections.unmodifiableSet(buildAuthorities(user));
    }

    private static Set<GrantedAuthority> buildAuthorities(UserEntity user) {
        Set<GrantedAuthority> result = new HashSet<>();
        for (UserRoleEntity userRole : user.getUserRoles()) {
            if (userRole.getDeletedAt() != null) continue;
            RoleEntity role = userRole.getRole();
            if (role == null || role.getDeletedAt() != null) continue;
            result.add(new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase(java.util.Locale.ROOT)));
            for (RolePermissionEntity rolePermission : role.getRolePermissions()) {
                if (rolePermission.getDeletedAt() != null) continue;
                PermissionEntity permission = rolePermission.getPermission();
                if (permission != null && permission.getDeletedAt() == null) {
                    result.add(new SimpleGrantedAuthority(permission.getCode()));
                }
            }
        }
        return result;
    }

    public UUID getUserId() { return userId; }
    @Override public String getUsername() { return username; }
    @Override public String getPassword() { return passwordHash; }
    @Override public boolean isEnabled() { return enabled; }
    @Override public Set<GrantedAuthority> getAuthorities() { return authorities; }
    @Override public void eraseCredentials() { passwordHash = null; }
}
