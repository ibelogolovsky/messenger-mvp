#!/usr/bin/env python3
import json
import socket
import subprocess
import sys
import time

HOST = "127.0.0.1"
PORT = 5050


def send_line(sock, payload):
    sock.sendall((json.dumps(payload) + "\n").encode("utf-8"))


def read_line(sock):
    data = b""
    while not data.endswith(b"\n"):
        chunk = sock.recv(4096)
        if not chunk:
            return None
        data += chunk
    return json.loads(data.decode("utf-8").strip())


def wait_for_port(timeout=10):
    deadline = time.time() + timeout
    while time.time() < deadline:
        try:
            with socket.create_connection((HOST, PORT), timeout=1):
                return True
        except OSError:
            time.sleep(0.2)
    return False


def main():
    server = subprocess.Popen([
        "./gradlew", ":server:run", "--args=5050"
    ], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)

    try:
        if not wait_for_port():
            raise RuntimeError("Server did not start")

        alice = socket.create_connection((HOST, PORT), timeout=5)
        send_line(alice, {
            "type": "LOGIN_REQUEST",
            "requestId": "login-alice",
            "payload": {"username": "alice", "password": "alice123"}
        })
        response = read_line(alice)
        assert response and response["type"] == "LOGIN_RESPONSE", response

        server.kill()
        server.wait(timeout=5)
        time.sleep(1)

        try:
            send_line(alice, {
                "type": "GET_USERS",
                "requestId": "after-kill",
                "payload": {}
            })
        except OSError:
            print("DISCONNECT_SMOKE_TEST_OK")
            return

        followup = read_line(alice)
        assert followup is None, followup
        print("DISCONNECT_SMOKE_TEST_OK")
    finally:
        try:
            alice.close()
        except Exception:
            pass
        if server.poll() is None:
            server.kill()


if __name__ == "__main__":
    main()
