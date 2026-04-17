package com.example.messenger.common.dto;

public record MessageReceivedEvent(String from, String text, long timestamp) {
}
