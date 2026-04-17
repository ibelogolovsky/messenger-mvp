package com.example.messenger.server.store;

import com.example.messenger.server.model.UserRecord;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class UserStore {
    private final Map<String, UserRecord> users = new LinkedHashMap<>();

    public UserStore() {
        add(new UserRecord("alice", "alice123", "Alice"));
        add(new UserRecord("bob", "bob123", "Bob"));
        add(new UserRecord("charlie", "charlie123", "Charlie"));
    }

    private void add(UserRecord userRecord) {
        users.put(userRecord.username(), userRecord);
    }

    public Optional<UserRecord> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }

    public Collection<UserRecord> findAll() {
        return users.values();
    }
}
