package com.example.messenger.client.net;

import com.example.messenger.common.protocol.Envelope;
import com.example.messenger.common.protocol.JsonCodec;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class NetworkClient {
    private Socket socket;
    private BufferedWriter writer;
    private Thread readerThread;

    public void connect(String host, int port, ServerEventDispatcher dispatcher) throws IOException {
        socket = new Socket(host, port);
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        readerThread = new Thread(() -> {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    Envelope<Object> envelope = JsonCodec.fromJson(line, new TypeReference<Envelope<Object>>() {
                    });
                    dispatcher.dispatch(envelope);
                }
                dispatcher.onDisconnected("Connection closed");
            } catch (Exception e) {
                dispatcher.onDisconnected("Connection lost");
            }
        }, "server-reader");
        readerThread.start();
    }

    public synchronized void send(Envelope<?> envelope) throws IOException {
        writer.write(JsonCodec.toJson(envelope));
        writer.write("\n");
        writer.flush();
    }

    public synchronized void close() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
    }
}
