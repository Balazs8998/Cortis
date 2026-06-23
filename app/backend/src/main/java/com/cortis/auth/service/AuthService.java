package com.cortis.auth.service;

import com.cortis.auth.dto.LoginRequest;
import com.cortis.auth.dto.LoginResponse;
import com.cortis.auth.security.CortisUserPrincipal;
import com.cortis.core.security.jwt.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {


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

        log.debug("Received login request for username={}", loginRequest.userName());

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

        log.info("Login completed successfully for user={}", principal.getUsername());

        return new LoginResponse(
                principal.getUsername(),
                authentication.isAuthenticated(),
                token
        );
    }
}