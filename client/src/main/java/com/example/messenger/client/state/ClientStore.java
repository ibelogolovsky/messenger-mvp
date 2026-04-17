package com.example.messenger.client.state;

import com.example.messenger.common.dto.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientStore {
    private ConnectionState connectionState = ConnectionState.DISCONNECTED;
    private String currentUsername;
    private String currentDisplayName;
    private List<UserInfo> users = new ArrayList<>();
    private String selectedUsername;
    private final Map<String, List<ChatMessageVm>> messagesByUser = new HashMap<>();

    public ConnectionState getConnectionState() {
        return connectionState;
    }

    public void setConnectionState(ConnectionState connectionState) {
        this.connectionState = connectionState;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public void setCurrentUser(String currentUsername, String currentDisplayName) {
        this.currentUsername = currentUsername;
        this.currentDisplayName = currentDisplayName;
    }

    public String getCurrentDisplayName() {
        return currentDisplayName;
    }

    public List<UserInfo> getUsers() {
        return users;
    }

    public void setUsers(List<UserInfo> users) {
        this.users = new ArrayList<>(users);
    }

    public String getSelectedUsername() {
        return selectedUsername;
    }

    public void setSelectedUsername(String selectedUsername) {
        this.selectedUsername = selectedUsername;
    }

    public List<ChatMessageVm> getMessages(String username) {
        return messagesByUser.computeIfAbsent(username, ignored -> new ArrayList<>());
    }

    public void addMessage(String username, ChatMessageVm message) {
        getMessages(username).add(message);
    }
}
