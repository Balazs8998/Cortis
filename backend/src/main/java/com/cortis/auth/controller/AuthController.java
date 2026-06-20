package com.cortis.auth.controller;

import com.cortis.auth.dto.LoginRequest;
import com.cortis.auth.dto.LoginResponse;
import com.cortis.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid LoginRequest request
    ) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }


    /*



    @PreAuthorize("hasAuthority('READ')")
    @GetMapping("/permission")
    public Map<String, Object> testPermission(
            Authentication authentication
    ) {
        return Map.of(
                "message", "A TOOL_READ jogosultság működik.",
                "username", authentication.getName(),
                "authorities", authentication.getAuthorities()
        );
    }
*/


}