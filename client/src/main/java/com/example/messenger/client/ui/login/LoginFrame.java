package com.example.messenger.client.ui.login;

import javax.swing.JFrame;

public class LoginFrame extends JFrame {
    public LoginFrame(LoginPanel loginPanel) {
        super("Messenger MVP Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(loginPanel);
        setSize(420, 260);
        setLocationRelativeTo(null);
    }
}
