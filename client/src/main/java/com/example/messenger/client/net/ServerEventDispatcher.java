package com.example.messenger.client.net;

import com.example.messenger.common.dto.ErrorResponse;
import com.example.messenger.common.dto.LoginResponse;
import com.example.messenger.common.dto.MessageAck;
import com.example.messenger.common.dto.MessageReceivedEvent;
import com.example.messenger.common.dto.UsersListResponse;
import com.example.messenger.common.protocol.Envelope;
import com.example.messenger.common.protocol.JsonCodec;
import com.example.messenger.common.protocol.MessageType;

import javax.swing.SwingUtilities;

public class ServerEventDispatcher {
    private final ServerEventListener listener;

    public ServerEventDispatcher(ServerEventListener listener) {
        this.listener = listener;
    }

    public void dispatch(Envelope<Object> envelope) {
        MessageType type = envelope.getType();
        if (type == null) {
            return;
        }

        switch (type) {
            case LOGIN_RESPONSE -> invoke(() -> listener.onLoginSuccess(convert(envelope, LoginResponse.class)));
            case USERS_LIST -> invoke(() -> listener.onUsersList(convert(envelope, UsersListResponse.class)));
            case MESSAGE_RECEIVED -> invoke(() -> listener.onMessageReceived(convert(envelope, MessageReceivedEvent.class)));
            case MESSAGE_ACK -> invoke(() -> listener.onMessageAck(convert(envelope, MessageAck.class)));
            case ERROR_RESPONSE -> invoke(() -> listener.onError(envelope.getRequestId(), convert(envelope, ErrorResponse.class)));
            default -> {
            }
        }
    }

    public void onDisconnected(String message) {
        invoke(() -> listener.onDisconnected(message));
    }

    private <T> T convert(Envelope<Object> envelope, Class<T> payloadType) {
        return JsonCodec.fromJson(JsonCodec.toJson(envelope.getPayload()), payloadType);
    }

    private void invoke(Runnable runnable) {
        SwingUtilities.invokeLater(runnable);
    }
}
