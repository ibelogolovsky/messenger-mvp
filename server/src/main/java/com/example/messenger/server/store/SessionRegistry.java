package com.example.messenger.server.store;

import com.example.messenger.server.net.ClientSession;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SessionRegistry {
    private final ConcurrentMap<String, ClientSession> sessions = new ConcurrentHashMap<>();

    public boolean isOnline(String username) {
        return sessions.containsKey(username);
    }

    public boolean register(String username, ClientSession session) {
        return sessions.putIfAbsent(username, session) == null;
    }

    public void remove(String username) {
        if (username != null) {
            sessions.remove(username);
        }
    }

    public Optional<ClientSession> findByUsername(String username) {
        return Optional.ofNullable(sessions.get(username));
    }
}
