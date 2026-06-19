package com.cortis.auth.dto;

public record LoginResponse(
        String userName,
        boolean authenticated,
        String token
) {
}