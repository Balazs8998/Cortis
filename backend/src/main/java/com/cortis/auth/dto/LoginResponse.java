package com.cortis.auth.dto;

public record LoginResponse(
        String username,
        boolean authenticated,
        String token
) {
}