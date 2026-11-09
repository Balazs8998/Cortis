package com.cortis.agent.security;

import com.cortis.auth.entity.UserEntity;
import com.cortis.auth.security.CortisUserPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AgentConversationAuthorizationServiceTest {
    @Test void foreignConversationIsIndistinguishableFromMissingConversation() {
        JdbcTemplate jdbc = mock(JdbcTemplate.class);
        UUID userId = UUID.randomUUID();
        UUID conversationId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUsername("demo");
        CortisUserPrincipal principal = new CortisUserPrincipal(user);
        when(jdbc.queryForObject(anyString(), eq(Boolean.class), eq(conversationId), eq(userId)))
                .thenReturn(false);
        AgentConversationAuthorizationService service = new AgentConversationAuthorizationService(jdbc);
        ResponseStatusException error = assertThrows(ResponseStatusException.class, () ->
                service.requireOwner(conversationId, UsernamePasswordAuthenticationToken.authenticated(
                        principal, null, principal.getAuthorities())));
        assertEquals(HttpStatus.NOT_FOUND.value(), error.getStatusCode().value());
    }
    @Test void permissionIsRequiredForAgentOperations() {
        AgentConversationAuthorizationService service = new AgentConversationAuthorizationService(mock(JdbcTemplate.class));
        assertThrows(InsufficientAuthenticationException.class, () ->
                service.requirePermission(null, "AGENT_WRITE"));
        assertThrows(AccessDeniedException.class, () ->
                service.requirePermission(UsernamePasswordAuthenticationToken.authenticated(
                        "user", null, java.util.List.of()), "AGENT_WRITE"));
    }

}
