package com.cortis.agent.security;

import com.cortis.auth.security.CortisUserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

/**
 * Phase 2 security primitive, to be used by EVERY Phase 4 conversation/message/run endpoint.
 * A foreign conversation and a missing conversation both return 404.
 */
@Service
public class AgentConversationAuthorizationService {
    private final JdbcTemplate jdbc;
    public AgentConversationAuthorizationService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public UUID requireOwner(UUID conversationId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CortisUserPrincipal principal)) {
            throw new InsufficientAuthenticationException("Authentication required");
        }
        Boolean exists = jdbc.queryForObject("""
            SELECT EXISTS (
                SELECT 1 FROM agent.conversation
                WHERE id = ? AND owner_user_id = ? AND deleted_at IS NULL
            )
            """, Boolean.class, conversationId, principal.getUserId());
        if (!Boolean.TRUE.equals(exists)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found");
        }
        return conversationId;
    }

    public void requirePermission(Authentication authentication, String permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InsufficientAuthenticationException("Authentication required");
        }
        if (authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(permission))) {
            throw new AccessDeniedException("Permission denied");
        }
    }
}
