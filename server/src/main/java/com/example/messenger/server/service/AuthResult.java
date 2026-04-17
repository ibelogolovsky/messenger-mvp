package com.example.messenger.server.service;

public record AuthResult(boolean success, String code, String message, String username, String displayName) {
    public static AuthResult success(String username, String displayName) {
        return new AuthResult(true, null, null, username, displayName);
    }

    public static AuthResult error(String code, String message) {
        return new AuthResult(false, code, message, null, null);
    }
}
