package com.example.messenger.client.ui.login;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class LoginFrame extends JFrame {
    public LoginFrame(LoginPanel loginPanel) {
        super("Messenger MVP Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(new JScrollPane(loginPanel));
        setSize(460, 420);
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }
}
