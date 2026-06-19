package com.cortis.desktop.auth.dto;

public record LoginResponse(
        String userName,
        boolean authenticated,
        String token
) {
}