package com.cortis.desktop.auth.session;

public class SessionManager {

    private static String token;
    private static String username;

    private SessionManager() {
    }

    public static void login(String newUsername, String newToken) {
        username = newUsername;
        token = newToken;
    }

    public static String getToken() {
        return token;
    }

    public static String getUsername() {
        return username;
    }

    public static boolean isLoggedIn() {
        return token != null && !token.isBlank();
    }

    public static void logout() {
        username = null;
        token = null;
    }
}