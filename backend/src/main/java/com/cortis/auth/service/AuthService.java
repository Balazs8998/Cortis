package com.cortis.auth.service;

import com.cortis.auth.dto.LoginRequest;
import com.cortis.auth.dto.LoginResponse;
import com.cortis.auth.security.CortisUserPrincipal;
import com.cortis.core.session.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest loginRequest) {

        log.info("1.Login request received: {}", loginRequest);
        UsernamePasswordAuthenticationToken authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(
                        loginRequest.userName(),
                        loginRequest.passwordHash()
                );

        log.info("2.Authentication request created");
        Authentication authentication =
                authenticationManager.authenticate(authenticationRequest);

        log.info("3.Authentication successful");
        CortisUserPrincipal principal =
                (CortisUserPrincipal) authentication.getPrincipal();

        log.info("4.User principal created");
        String token = jwtService.generateToken(principal);

        log.info("5.JWT token generated");
        return new LoginResponse(
                principal.getUsername(),
                authentication.isAuthenticated(),
                token
        );
    }
}