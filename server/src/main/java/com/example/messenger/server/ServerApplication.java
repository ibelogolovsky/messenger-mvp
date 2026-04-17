package com.example.messenger.server;

import com.example.messenger.server.net.ServerBootstrap;
import com.example.messenger.server.service.AuthService;
import com.example.messenger.server.service.MessageRouter;
import com.example.messenger.server.store.SessionRegistry;
import com.example.messenger.server.store.UserStore;

public class ServerApplication {
    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 5050;

        UserStore userStore = new UserStore();
        SessionRegistry sessionRegistry = new SessionRegistry();
        AuthService authService = new AuthService(userStore, sessionRegistry);
        MessageRouter messageRouter = new MessageRouter(sessionRegistry, userStore);

        new ServerBootstrap(port, authService, userStore, sessionRegistry, messageRouter).start();
    }
}
