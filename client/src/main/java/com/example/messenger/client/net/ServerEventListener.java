package com.example.messenger.client.net;

import com.example.messenger.common.dto.ErrorResponse;
import com.example.messenger.common.dto.LoginResponse;
import com.example.messenger.common.dto.MessageAck;
import com.example.messenger.common.dto.MessageReceivedEvent;
import com.example.messenger.common.dto.UsersListResponse;

public interface ServerEventListener {
    void onLoginSuccess(LoginResponse response);

    void onUsersList(UsersListResponse response);

    void onMessageReceived(MessageReceivedEvent event);

    void onMessageAck(MessageAck ack);

    void onError(String requestId, ErrorResponse errorResponse);

    void onDisconnected(String message);
}
