package com.example.messenger.client.ui.login;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel {
    private final JTextField hostField = new JTextField("127.0.0.1");
    private final JTextField portField = new JTextField("5050");
    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JButton loginButton = new JButton("Login");
    private final JLabel statusLabel = new JLabel(" ");

    public LoginPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        setBackground(new Color(23, 33, 43));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(31, 41, 54));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(34, 48, 64)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel title = new JLabel("Messenger Login");
        title.setForeground(new Color(230, 235, 240));
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(16));
        card.add(label("Host"));
        card.add(hostField);
        card.add(Box.createVerticalStrut(8));
        card.add(label("Port"));
        card.add(portField);
        card.add(Box.createVerticalStrut(8));
        card.add(label("Username"));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(8));
        card.add(label("Password"));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(14));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(10));
        card.add(statusLabel);

        loginButton.setAlignmentX(LEFT_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        hostField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        portField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        statusLabel.setForeground(new Color(255, 120, 120));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(card, BorderLayout.CENTER);
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(230, 235, 240));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    public void addLoginAction(ActionListener actionListener) {
        loginButton.addActionListener(actionListener);
        passwordField.addActionListener(actionListener);
    }

    public String getHost() {
        return hostField.getText().trim();
    }

    public int getPort() {
        return Integer.parseInt(portField.getText().trim());
    }

    public String getUsername() {
        return usernameField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void setStatus(String message) {
        statusLabel.setText(message == null || message.isBlank() ? " " : message);
    }

    public void setLoginEnabled(boolean enabled) {
        loginButton.setEnabled(enabled);
    }
}
