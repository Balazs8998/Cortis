package com.cortis.auth.dto;


import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Username cannot be blank")
        String userName,
        @NotBlank(message = "Password cannot be blank")
        String password
) {}