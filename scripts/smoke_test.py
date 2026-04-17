#!/usr/bin/env python3
import json
import socket
import time

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


def login(username, password):
    sock = socket.create_connection((HOST, PORT), timeout=5)
    send_line(sock, {
        "type": "LOGIN_REQUEST",
        "requestId": f"login-{username}",
        "payload": {"username": username, "password": password}
    })
    response = read_line(sock)
    return sock, response


def main():
    alice_sock, alice_login = login("alice", "alice123")
    bob_sock, bob_login = login("bob", "bob123")

    assert alice_login["type"] == "LOGIN_RESPONSE", alice_login
    assert bob_login["type"] == "LOGIN_RESPONSE", bob_login

    send_line(alice_sock, {
        "type": "GET_USERS",
        "requestId": "users-alice",
        "payload": {}
    })
    users_response = read_line(alice_sock)
    assert users_response["type"] == "USERS_LIST", users_response

    send_line(alice_sock, {
        "type": "SEND_MESSAGE",
        "requestId": "msg-1",
        "payload": {"to": "bob", "text": "Hello Bob"}
    })

    bob_message = read_line(bob_sock)
    alice_ack = read_line(alice_sock)

    assert bob_message["type"] == "MESSAGE_RECEIVED", bob_message
    assert alice_ack["type"] == "MESSAGE_ACK", alice_ack
    assert bob_message["payload"]["text"] == "Hello Bob", bob_message

    send_line(bob_sock, {
        "type": "SEND_MESSAGE",
        "requestId": "msg-2",
        "payload": {"to": "alice", "text": "Hi Alice"}
    })

    alice_message = read_line(alice_sock)
    bob_ack = read_line(bob_sock)

    assert alice_message["type"] == "MESSAGE_RECEIVED", alice_message
    assert bob_ack["type"] == "MESSAGE_ACK", bob_ack
    assert alice_message["payload"]["text"] == "Hi Alice", alice_message

    alice_sock.close()
    bob_sock.close()
    print("SMOKE_TEST_OK")


if __name__ == "__main__":
    main()
