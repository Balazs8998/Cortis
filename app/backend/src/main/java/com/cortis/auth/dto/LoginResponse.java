package com.cortis.auth.dto;

import com.cortis.auth.security.CortisUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/** No JWT or session ID is ever sent in the response body. */
public record LoginResponse(UUID userId, String userName, boolean authenticated, Set<String> authorities) {
    public static LoginResponse from(Authentication authentication) {
        return from((CortisUserPrincipal) authentication.getPrincipal());
    }

    public static LoginResponse from(CortisUserPrincipal principal) {
        Set<String> granted = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toUnmodifiableSet());
        return new LoginResponse(principal.getUserId(), principal.getUsername(), true, granted);
    }
}
