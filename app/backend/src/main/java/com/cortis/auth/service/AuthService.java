package com.cortis.auth.service;

import com.cortis.auth.dto.LoginRequest;
import com.cortis.auth.dto.LoginWithChipRequest;
import com.cortis.auth.security.ChipCardAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final ChipCodeHmacService chipCodeHmacService;

    public AuthService(AuthenticationManager authenticationManager, ChipCodeHmacService chipCodeHmacService) {
        this.authenticationManager = authenticationManager;
        this.chipCodeHmacService = chipCodeHmacService;
    }

    public Authentication login(LoginRequest request) {
        return authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.userName(), request.password())
        );
    }

    public Authentication loginWithChip(LoginWithChipRequest request) {
        String hashed = chipCodeHmacService.createHmac(request.chipCode());
        return authenticationManager.authenticate(ChipCardAuthenticationToken.unauthenticated(hashed));
    }
}
