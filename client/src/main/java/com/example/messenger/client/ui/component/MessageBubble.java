package com.example.messenger.client.ui.component;

import com.example.messenger.client.state.ChatMessageVm;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class MessageBubble extends JPanel {
    private static final Color OUTGOING = new Color(43, 82, 120);
    private static final Color INCOMING = new Color(24, 37, 51);
    private static final Color TEXT = new Color(230, 235, 240);
    private static final Color META = new Color(138, 151, 165);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());

    public MessageBubble(ChatMessageVm message) {
        setLayout(new FlowLayout(message.outgoing() ? FlowLayout.RIGHT : FlowLayout.LEFT));
        setOpaque(false);

        JPanel bubble = new JPanel();
        bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));
        bubble.setBackground(message.outgoing() ? OUTGOING : INCOMING);
        bubble.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        bubble.setAlignmentX(message.outgoing() ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);

        JLabel senderLabel = new JLabel(message.outgoing() ? "You" : message.sender());
        senderLabel.setForeground(META);
        JLabel textLabel = new JLabel("<html><body style='width: 220px'>" + escape(message.text()) + "</body></html>");
        textLabel.setForeground(TEXT);
        JLabel timeLabel = new JLabel(TIME_FORMATTER.format(Instant.ofEpochMilli(message.timestamp())));
        timeLabel.setForeground(META);

        bubble.add(senderLabel);
        bubble.add(Box.createVerticalStrut(2));
        bubble.add(textLabel);
        bubble.add(Box.createVerticalStrut(2));
        bubble.add(timeLabel);

        add(bubble);
    }

    private String escape(String text) {
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br/>");
    }
}
