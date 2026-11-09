package com.cortis.core.security.session;

import com.cortis.auth.security.CortisUserPrincipal;
import com.cortis.core.exception.ErrorResponse;
import com.cortis.core.security.audit.SecurityAuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
public class CortisAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper mapper;
    private final SecurityAuditService auditService;
    public CortisAccessDeniedHandler(ObjectMapper mapper, SecurityAuditService auditService) {
        this.mapper = mapper;
        this.auditService = auditService;
    }
    @Override public void handle(HttpServletRequest request, HttpServletResponse response,
                                 AccessDeniedException exception) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID id = auth != null && auth.getPrincipal() instanceof CortisUserPrincipal principal
                ? principal.getUserId() : null;
        auditService.record("ACCESS_DENIED", "REJECTED", id, request);
        response.setStatus(403);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        mapper.writeValue(response.getOutputStream(), new ErrorResponse(
                Instant.now(), 403, "ACCESS_DENIED", "common.error.access_denied",
                null, request.getRequestURI(), null));
    }
}
