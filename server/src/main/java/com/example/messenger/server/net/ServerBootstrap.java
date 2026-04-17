package com.example.messenger.server.net;

import com.example.messenger.server.service.AuthService;
import com.example.messenger.server.service.MessageRouter;
import com.example.messenger.server.store.SessionRegistry;
import com.example.messenger.server.store.UserStore;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerBootstrap {
    private final int port;
    private final AuthService authService;
    private final UserStore userStore;
    private final SessionRegistry sessionRegistry;
    private final MessageRouter messageRouter;

    public ServerBootstrap(
            int port,
            AuthService authService,
            UserStore userStore,
            SessionRegistry sessionRegistry,
            MessageRouter messageRouter
    ) {
        this.port = port;
        this.authService = authService;
        this.userStore = userStore;
        this.sessionRegistry = sessionRegistry;
        this.messageRouter = messageRouter;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Messenger server started on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                Thread thread = new Thread(
                        new ClientConnectionHandler(socket, authService, userStore, sessionRegistry, messageRouter),
                        "client-handler-" + socket.getPort()
                );
                thread.start();
            }
        }
    }
}
