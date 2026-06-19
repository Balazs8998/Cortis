package com.cortis.auth.controller;

import com.cortis.auth.dto.LoginRequest;
import com.cortis.auth.dto.LoginResponse;
import com.cortis.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        System.out.println("1.Login request received: " + request);
        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }
}