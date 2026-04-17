package com.example.messenger.common.dto;

public record LoginResponse(boolean success, String username, String displayName) {
}
