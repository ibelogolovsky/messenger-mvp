package com.example.messenger.server.net;

import com.example.messenger.common.protocol.Envelope;
import com.example.messenger.common.protocol.JsonCodec;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientSession {
    private final Socket socket;
    private final BufferedWriter writer;
    private volatile String username;

    public ClientSession(Socket socket, BufferedWriter writer) {
        this.socket = socket;
        this.writer = writer;
    }

    public synchronized void send(Envelope<?> envelope) throws IOException {
        writer.write(JsonCodec.toJson(envelope));
        writer.write("\n");
        writer.flush();
    }

    public Socket getSocket() {
        return socket;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
