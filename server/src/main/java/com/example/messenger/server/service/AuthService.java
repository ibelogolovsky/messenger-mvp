package com.example.messenger.server.service;

import com.example.messenger.server.model.UserRecord;
import com.example.messenger.server.net.ClientSession;
import com.example.messenger.server.store.SessionRegistry;
import com.example.messenger.server.store.UserStore;

import java.util.Optional;

public class AuthService {
    private final UserStore userStore;
    private final SessionRegistry sessionRegistry;

    public AuthService(UserStore userStore, SessionRegistry sessionRegistry) {
        this.userStore = userStore;
        this.sessionRegistry = sessionRegistry;
    }

    public AuthResult authenticate(String username, String password, ClientSession session) {
        Optional<UserRecord> userOptional = userStore.findByUsername(username);
        if (userOptional.isEmpty()) {
            return AuthResult.error("INVALID_CREDENTIALS", "Invalid username or password");
        }

        UserRecord userRecord = userOptional.get();
        if (!userRecord.password().equals(password)) {
            return AuthResult.error("INVALID_CREDENTIALS", "Invalid username or password");
        }

        if (!sessionRegistry.register(username, session)) {
            return AuthResult.error("DUPLICATE_LOGIN", "User is already logged in");
        }

        session.setUsername(username);
        return AuthResult.success(userRecord.username(), userRecord.displayName());
    }
}
