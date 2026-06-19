package com.cortis.desktop.auth.dto;



public record LoginRequest(String userName, String passwordHash) {
}
