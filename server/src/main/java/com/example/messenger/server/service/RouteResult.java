package com.example.messenger.server.service;

public record RouteResult(boolean success, String code, String message) {
    public static RouteResult ok() {
        return new RouteResult(true, null, null);
    }

    public static RouteResult error(String code, String message) {
        return new RouteResult(false, code, message);
    }
}
