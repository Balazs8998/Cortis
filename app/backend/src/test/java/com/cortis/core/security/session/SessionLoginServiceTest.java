package com.cortis.core.security.session;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SessionLoginServiceTest {
    @AfterEach void clearContext() { SecurityContextHolder.clearContext(); }

    @Test void manualLoginPersistsAuthenticationAndRunsFixationStrategy() {
        SecurityContextRepository repository = mock(SecurityContextRepository.class);
        SessionAuthenticationStrategy strategy = mock(SessionAuthenticationStrategy.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication auth = UsernamePasswordAuthenticationToken.authenticated("test", null, List.of());

        new SessionLoginService(repository, strategy).signIn(auth, request, response);

        verify(strategy).onAuthentication(auth, request, response);
        org.mockito.ArgumentCaptor<SecurityContext> captor = org.mockito.ArgumentCaptor.forClass(SecurityContext.class);
        verify(repository).saveContext(captor.capture(), eq(request), eq(response));
        assertSame(auth, captor.getValue().getAuthentication());
        assertSame(auth, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test void unauthenticatedTokenMustNotBeSaved() {
        SecurityContextRepository repository = mock(SecurityContextRepository.class);
        SessionAuthenticationStrategy strategy = mock(SessionAuthenticationStrategy.class);
        Authentication auth = UsernamePasswordAuthenticationToken.unauthenticated("test", "secret");
        assertThrows(IllegalArgumentException.class, () ->
                new SessionLoginService(repository, strategy).signIn(
                        auth, new MockHttpServletRequest(), new MockHttpServletResponse()));
        verifyNoInteractions(repository, strategy);
    }
}
