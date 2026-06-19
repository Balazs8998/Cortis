package com.cortis.auth.dto;



public record LoginRequest(String userName, String passwordHash) {}