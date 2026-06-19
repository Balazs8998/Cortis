package com.cortis.desktop.auth.dto;

public record LoginResponse(
        String username,
        boolean authenticated,
        String token
) {
}