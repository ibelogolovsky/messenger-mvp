package com.example.messenger.client.state;

public record ChatMessageVm(String sender, String text, long timestamp, boolean outgoing) {
}
