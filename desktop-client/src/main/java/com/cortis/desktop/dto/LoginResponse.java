package com.cortis.desktop.dto;

public record LoginResponse(
        String username,
        String token
) {
}