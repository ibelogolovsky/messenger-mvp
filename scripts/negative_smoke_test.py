#!/usr/bin/env python3
import json
import socket

HOST = "127.0.0.1"
PORT = 5050


def send_line(sock, payload):
    sock.sendall((json.dumps(payload, ensure_ascii=False) + "\n").encode("utf-8"))


def read_line(sock):
    data = b""
    while not data.endswith(b"\n"):
        chunk = sock.recv(4096)
        if not chunk:
            raise RuntimeError("Connection closed")
        data += chunk
    return json.loads(data.decode("utf-8").strip())


def login(username, password, request_id):
    sock = socket.create_connection((HOST, PORT), timeout=5)
    send_line(sock, {
        "type": "LOGIN_REQUEST",
        "requestId": request_id,
        "payload": {"username": username, "password": password}
    })
    return sock, read_line(sock)


def main():
    invalid_sock, invalid_response = login("alice", "wrong", "bad-login")
    assert invalid_response["type"] == "ERROR_RESPONSE", invalid_response
    assert invalid_response["payload"]["code"] == "INVALID_CREDENTIALS", invalid_response
    invalid_sock.close()

    alice_sock, alice_login = login("alice", "alice123", "alice-ok")
    assert alice_login["type"] == "LOGIN_RESPONSE", alice_login

    duplicate_sock, duplicate_response = login("alice", "alice123", "alice-dup")
    assert duplicate_response["type"] == "ERROR_RESPONSE", duplicate_response
    assert duplicate_response["payload"]["code"] == "DUPLICATE_LOGIN", duplicate_response
    duplicate_sock.close()

    send_line(alice_sock, {
        "type": "SEND_MESSAGE",
        "requestId": "offline-msg",
        "payload": {"to": "charlie", "text": "Hello offline Charlie"}
    })
    offline_response = read_line(alice_sock)
    assert offline_response["type"] == "ERROR_RESPONSE", offline_response
    assert offline_response["payload"]["code"] == "RECIPIENT_OFFLINE", offline_response

    send_line(alice_sock, {
        "type": "SEND_MESSAGE",
        "requestId": "self-msg",
        "payload": {"to": "alice", "text": "Hello self"}
    })
    self_response = read_line(alice_sock)
    assert self_response["type"] == "ERROR_RESPONSE", self_response
    assert self_response["payload"]["code"] == "SELF_SEND_FORBIDDEN", self_response

    alice_sock.close()
    print("NEGATIVE_SMOKE_TEST_OK")


if __name__ == "__main__":
    main()
