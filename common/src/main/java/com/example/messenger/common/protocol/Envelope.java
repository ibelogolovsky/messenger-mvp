package com.example.messenger.common.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Envelope<T> {
    private final MessageType type;
    private final String requestId;
    private final T payload;

    @JsonCreator
    public Envelope(
            @JsonProperty("type") MessageType type,
            @JsonProperty("requestId") String requestId,
            @JsonProperty("payload") T payload
    ) {
        this.type = type;
        this.requestId = requestId;
        this.payload = payload;
    }

    public MessageType getType() {
        return type;
    }

    public String getRequestId() {
        return requestId;
    }

    public T getPayload() {
        return payload;
    }
}
