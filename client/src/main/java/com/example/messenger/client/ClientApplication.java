package com.example.messenger.client;

import com.example.messenger.client.net.NetworkClient;
import com.example.messenger.client.net.ServerEventDispatcher;
import com.example.messenger.client.net.ServerEventListener;
import com.example.messenger.client.state.ChatMessageVm;
import com.example.messenger.client.state.ClientStore;
import com.example.messenger.client.state.ConnectionState;
import com.example.messenger.client.ui.login.LoginFrame;
import com.example.messenger.client.ui.login.LoginPanel;
import com.example.messenger.client.ui.main.MainFrame;
import com.example.messenger.client.ui.main.MainPanel;
import com.example.messenger.common.dto.ErrorResponse;
import com.example.messenger.common.dto.GetUsersRequest;
import com.example.messenger.common.dto.LoginRequest;
import com.example.messenger.common.dto.LoginResponse;
import com.example.messenger.common.dto.MessageAck;
import com.example.messenger.common.dto.MessageReceivedEvent;
import com.example.messenger.common.dto.SendMessageRequest;
import com.example.messenger.common.dto.UserInfo;
import com.example.messenger.common.dto.UsersListResponse;
import com.example.messenger.common.protocol.Envelope;
import com.example.messenger.common.protocol.MessageType;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.IOException;
import java.util.UUID;

public class ClientApplication implements ServerEventListener {
    private final ClientStore store = new ClientStore();
    private final NetworkClient networkClient = new NetworkClient();
    private LoginPanel loginPanel;
    private LoginFrame loginFrame;
    private MainPanel mainPanel;
    private MainFrame mainFrame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatDarkLaf.setup();
            new ClientApplication().start();
        });
    }

    private void start() {
        loginPanel = new LoginPanel();
        loginFrame = new LoginFrame(loginPanel);
        loginPanel.addLoginAction(event -> attemptLogin());
        loginFrame.setVisible(true);
    }

    private void attemptLogin() {
        if (loginPanel.getUsername().isBlank() || loginPanel.getPassword().isBlank()) {
            loginPanel.setStatus("Username and password are required");
            return;
        }

        loginPanel.setLoginEnabled(false);
        loginPanel.setStatus("Connecting...");
        store.setConnectionState(ConnectionState.CONNECTING);

        try {
            networkClient.connect(loginPanel.getHost(), loginPanel.getPort(), new ServerEventDispatcher(this));
            store.setConnectionState(ConnectionState.AUTHENTICATING);
            networkClient.send(new Envelope<>(
                    MessageType.LOGIN_REQUEST,
                    requestId(),
                    new LoginRequest(loginPanel.getUsername(), loginPanel.getPassword())
            ));
        } catch (Exception e) {
            store.setConnectionState(ConnectionState.ERROR);
            loginPanel.setLoginEnabled(true);
            loginPanel.setStatus("Failed to connect to server");
        }
    }

    @Override
    public void onLoginSuccess(LoginResponse response) {
        store.setConnectionState(ConnectionState.CONNECTED);
        store.setCurrentUser(response.username(), response.displayName());
        loginPanel.setStatus(" ");
        openMainWindow();
        requestUsers();
        startUsersRefreshLoop();
    }

    @Override
    public void onUsersList(UsersListResponse response) {
        store.setUsers(response.users());
        mainPanel.setUsers(response.users());
        mainPanel.setStatusText("Connected");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        store.addMessage(event.from(), new ChatMessageVm(event.from(), event.text(), event.timestamp(), false));
        UserInfo selectedUser = mainPanel.getSelectedUser();
        if (selectedUser != null && selectedUser.username().equals(event.from())) {
            mainPanel.renderMessages(store.getMessages(event.from()));
        }
        mainPanel.setStatusText("New message from " + event.from());
    }

    @Override
    public void onMessageAck(MessageAck ack) {
        store.addMessage(ack.to(), new ChatMessageVm(store.getCurrentUsername(), ack.text(), ack.timestamp(), true));
        UserInfo selectedUser = mainPanel.getSelectedUser();
        if (selectedUser != null && selectedUser.username().equals(ack.to())) {
            mainPanel.renderMessages(store.getMessages(ack.to()));
        }
        mainPanel.setStatusText("Message delivered");
    }

    @Override
    public void onError(String requestId, ErrorResponse errorResponse) {
        if (mainFrame == null) {
            loginPanel.setLoginEnabled(true);
            loginPanel.setStatus(errorResponse.message());
            return;
        }
        mainPanel.setStatusText(errorResponse.message());
    }

    @Override
    public void onDisconnected(String message) {
        store.setConnectionState(ConnectionState.DISCONNECTED);
        if (mainPanel != null) {
            mainPanel.setComposerEnabled(false);
            mainPanel.setStatusText(message);
        }
        if (loginPanel != null && mainFrame == null) {
            loginPanel.setLoginEnabled(true);
            loginPanel.setStatus(message);
        }
    }

    private void openMainWindow() {
        mainPanel = new MainPanel();
        mainFrame = new MainFrame(mainPanel);
        mainPanel.addUserSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                UserInfo userInfo = mainPanel.getSelectedUser();
                mainPanel.setSelectedUser(userInfo);
                if (userInfo != null) {
                    store.setSelectedUsername(userInfo.username());
                    mainPanel.renderMessages(store.getMessages(userInfo.username()));
                    mainPanel.setComposerEnabled(store.getConnectionState() == ConnectionState.CONNECTED);
                } else {
                    mainPanel.setComposerEnabled(false);
                }
            }
        });
        mainPanel.addSendAction(event -> sendMessage());
        mainPanel.setComposerEnabled(false);
        loginFrame.dispose();
        mainFrame.setVisible(true);
    }

    private void requestUsers() {
        try {
            networkClient.send(new Envelope<>(
                    MessageType.GET_USERS,
                    requestId(),
                    new GetUsersRequest()
            ));
        } catch (IOException e) {
            onDisconnected("Failed to load users");
        }
    }

    private void sendMessage() {
        UserInfo selectedUser = mainPanel.getSelectedUser();
        if (selectedUser == null) {
            return;
        }
        String text = mainPanel.consumeInputText().trim();
        if (text.isBlank()) {
            mainPanel.setStatusText("Message cannot be empty");
            return;
        }

        try {
            networkClient.send(new Envelope<>(
                    MessageType.SEND_MESSAGE,
                    requestId(),
                    new SendMessageRequest(selectedUser.username(), text)
            ));
            mainPanel.setStatusText("Sending...");
        } catch (IOException e) {
            onDisconnected("Failed to send message");
        }
    }

    private void startUsersRefreshLoop() {
        Thread refreshThread = new Thread(() -> {
            while (store.getConnectionState() == ConnectionState.CONNECTED) {
                try {
                    Thread.sleep(2000);
                    requestUsers();
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }, "users-refresh");
        refreshThread.setDaemon(true);
        refreshThread.start();
    }

    private String requestId() {
        return UUID.randomUUID().toString();
    }
}
