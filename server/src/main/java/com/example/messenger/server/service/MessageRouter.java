package com.example.messenger.server.service;

import com.example.messenger.common.dto.MessageAck;
import com.example.messenger.common.dto.MessageReceivedEvent;
import com.example.messenger.common.protocol.Envelope;
import com.example.messenger.common.protocol.MessageType;
import com.example.messenger.server.net.ClientSession;
import com.example.messenger.server.store.SessionRegistry;
import com.example.messenger.server.store.UserStore;

import java.io.IOException;
import java.util.Optional;

public class MessageRouter {
    private final SessionRegistry sessionRegistry;
    private final UserStore userStore;

    public MessageRouter(SessionRegistry sessionRegistry, UserStore userStore) {
        this.sessionRegistry = sessionRegistry;
        this.userStore = userStore;
    }

    public RouteResult routeMessage(ClientSession senderSession, String requestId, String to, String text) {
        String from = senderSession.getUsername();
        if (from == null) {
            return RouteResult.error("UNAUTHORIZED", "You must login before sending commands");
        }

        if (from.equals(to)) {
            return RouteResult.error("SELF_SEND_FORBIDDEN", "Sending messages to yourself is not allowed");
        }

        if (userStore.findByUsername(to).isEmpty()) {
            return RouteResult.error("UNKNOWN_USER", "Recipient does not exist");
        }

        Optional<ClientSession> recipientOptional = sessionRegistry.findByUsername(to);
        if (recipientOptional.isEmpty()) {
            return RouteResult.error("RECIPIENT_OFFLINE", "Recipient is offline");
        }

        long timestamp = System.currentTimeMillis();
        ClientSession recipientSession = recipientOptional.get();

        try {
            recipientSession.send(new Envelope<>(
                    MessageType.MESSAGE_RECEIVED,
                    null,
                    new MessageReceivedEvent(from, text, timestamp)
            ));

            senderSession.send(new Envelope<>(
                    MessageType.MESSAGE_ACK,
                    requestId,
                    new MessageAck(true, to, text, timestamp)
            ));
            return RouteResult.ok();
        } catch (IOException e) {
            return RouteResult.error("INTERNAL_ERROR", "Failed to deliver message");
        }
    }
}
