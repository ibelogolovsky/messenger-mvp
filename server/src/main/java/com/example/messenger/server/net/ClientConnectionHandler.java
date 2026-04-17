package com.example.messenger.server.net;

import com.example.messenger.common.dto.ErrorResponse;
import com.example.messenger.common.dto.GetUsersRequest;
import com.example.messenger.common.dto.LoginRequest;
import com.example.messenger.common.dto.LoginResponse;
import com.example.messenger.common.dto.LogoutResponse;
import com.example.messenger.common.dto.SendMessageRequest;
import com.example.messenger.common.dto.UserInfo;
import com.example.messenger.common.dto.UsersListResponse;
import com.example.messenger.common.protocol.Envelope;
import com.example.messenger.common.protocol.JsonCodec;
import com.example.messenger.common.protocol.MessageType;
import com.example.messenger.server.model.UserRecord;
import com.example.messenger.server.service.AuthResult;
import com.example.messenger.server.service.AuthService;
import com.example.messenger.server.service.MessageRouter;
import com.example.messenger.server.service.RouteResult;
import com.example.messenger.server.store.SessionRegistry;
import com.example.messenger.server.store.UserStore;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ClientConnectionHandler implements Runnable {
    private final Socket socket;
    private final AuthService authService;
    private final UserStore userStore;
    private final SessionRegistry sessionRegistry;
    private final MessageRouter messageRouter;

    public ClientConnectionHandler(
            Socket socket,
            AuthService authService,
            UserStore userStore,
            SessionRegistry sessionRegistry,
            MessageRouter messageRouter
    ) {
        this.socket = socket;
        this.authService = authService;
        this.userStore = userStore;
        this.sessionRegistry = sessionRegistry;
        this.messageRouter = messageRouter;
    }

    @Override
    public void run() {
        ClientSession session = null;
        try (socket;
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {

            session = new ClientSession(socket, writer);

            String line;
            while ((line = reader.readLine()) != null) {
                Envelope<?> rawEnvelope = JsonCodec.fromJson(line, new TypeReference<Envelope<Object>>() {
                });
                handleEnvelope(rawEnvelope, line, session);
            }
        } catch (Exception ignored) {
        } finally {
            if (session != null) {
                sessionRegistry.remove(session.getUsername());
            }
        }
    }

    private void handleEnvelope(Envelope<?> rawEnvelope, String line, ClientSession session) throws IOException {
        MessageType type = rawEnvelope.getType();
        if (type == null) {
            sendError(session, rawEnvelope.getRequestId(), "BAD_REQUEST", "Message type is required");
            return;
        }

        if (session.getUsername() == null && type != MessageType.LOGIN_REQUEST) {
            sendError(session, rawEnvelope.getRequestId(), "UNAUTHORIZED", "You must login before sending commands");
            return;
        }

        switch (type) {
            case LOGIN_REQUEST -> handleLogin(line, session);
            case GET_USERS -> handleGetUsers(rawEnvelope, session);
            case SEND_MESSAGE -> handleSendMessage(line, session);
            case LOGOUT -> handleLogout(rawEnvelope, session);
            default -> sendError(session, rawEnvelope.getRequestId(), "BAD_REQUEST", "Unsupported message type");
        }
    }

    private void handleLogin(String line, ClientSession session) throws IOException {
        Envelope<LoginRequest> envelope = JsonCodec.fromJson(line, new TypeReference<Envelope<LoginRequest>>() {
        });
        LoginRequest payload = envelope.getPayload();
        if (payload == null || blank(payload.username()) || blank(payload.password())) {
            sendError(session, envelope.getRequestId(), "BAD_REQUEST", "Username and password are required");
            return;
        }

        AuthResult authResult = authService.authenticate(payload.username().trim(), payload.password(), session);
        if (!authResult.success()) {
            sendError(session, envelope.getRequestId(), authResult.code(), authResult.message());
            return;
        }

        session.send(new Envelope<>(
                MessageType.LOGIN_RESPONSE,
                envelope.getRequestId(),
                new LoginResponse(true, authResult.username(), authResult.displayName())
        ));
    }

    private void handleGetUsers(Envelope<?> rawEnvelope, ClientSession session) throws IOException {
        List<UserInfo> users = userStore.findAll().stream()
                .filter(user -> !user.username().equals(session.getUsername()))
                .map(this::toUserInfo)
                .toList();

        session.send(new Envelope<>(
                MessageType.USERS_LIST,
                rawEnvelope.getRequestId(),
                new UsersListResponse(users)
        ));
    }

    private void handleSendMessage(String line, ClientSession session) throws IOException {
        Envelope<SendMessageRequest> envelope = JsonCodec.fromJson(line, new TypeReference<Envelope<SendMessageRequest>>() {
        });
        SendMessageRequest payload = envelope.getPayload();
        if (payload == null || blank(payload.to()) || blank(payload.text())) {
            sendError(session, envelope.getRequestId(), "BAD_REQUEST", "Recipient and text are required");
            return;
        }

        RouteResult result = messageRouter.routeMessage(session, envelope.getRequestId(), payload.to().trim(), payload.text().trim());
        if (!result.success()) {
            sendError(session, envelope.getRequestId(), result.code(), result.message());
        }
    }

    private void handleLogout(Envelope<?> rawEnvelope, ClientSession session) throws IOException {
        sessionRegistry.remove(session.getUsername());
        session.send(new Envelope<>(
                MessageType.LOGOUT_RESPONSE,
                rawEnvelope.getRequestId(),
                new LogoutResponse(true)
        ));
        socket.close();
    }

    private UserInfo toUserInfo(UserRecord userRecord) {
        return new UserInfo(userRecord.username(), userRecord.displayName(), sessionRegistry.isOnline(userRecord.username()));
    }

    private void sendError(ClientSession session, String requestId, String code, String message) throws IOException {
        session.send(new Envelope<>(
                MessageType.ERROR_RESPONSE,
                requestId,
                new ErrorResponse(code, message)
        ));
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
