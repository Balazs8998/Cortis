package com.cortis.auth.service;

import com.cortis.auth.dto.LoginRequest;
import com.cortis.auth.dto.LoginResponse;
import com.cortis.auth.security.CortisUserPrincipal;
import com.cortis.core.security.jwt.JwtService;
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

        UsernamePasswordAuthenticationToken authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(
                        loginRequest.userName(),
                        loginRequest.password()
                );

        Authentication authentication =
                authenticationManager.authenticate(authenticationRequest);

        CortisUserPrincipal principal =
                (CortisUserPrincipal) authentication.getPrincipal();

        String token = jwtService.generateToken(principal);

        return new LoginResponse(
                principal.getUsername(),
                authentication.isAuthenticated(),
                token
        );
    }
}