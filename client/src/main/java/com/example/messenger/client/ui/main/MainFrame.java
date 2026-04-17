package com.example.messenger.client.ui.main;

import javax.swing.JFrame;

public class MainFrame extends JFrame {
    public MainFrame(MainPanel mainPanel) {
        super("Messenger MVP");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        setSize(1100, 700);
        setLocationRelativeTo(null);
    }
}
