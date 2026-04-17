package com.example.messenger.common.dto;

public record MessageAck(boolean success, String to, String text, long timestamp) {
}
