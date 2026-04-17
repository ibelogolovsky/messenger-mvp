package com.example.messenger.client.ui.main;

import com.example.messenger.client.state.ChatMessageVm;
import com.example.messenger.client.ui.component.MessageBubble;
import com.example.messenger.client.ui.component.UserCellRenderer;
import com.example.messenger.common.dto.UserInfo;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.List;

public class MainPanel extends JPanel {
    private final DefaultListModel<UserInfo> usersModel = new DefaultListModel<>();
    private final JList<UserInfo> usersList = new JList<>(usersModel);
    private final JPanel messagesPanel = new JPanel();
    private final JScrollPane messagesScrollPane;
    private final JLabel headerLabel = new JLabel("Select a chat");
    private final JLabel statusLabel = new JLabel("Select a chat to start messaging");
    private final JTextArea inputArea = new JTextArea(3, 20);
    private final JButton sendButton = new JButton("Send");

    public MainPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(14, 22, 33));
        messagesScrollPane = new JScrollPane(messagesPanel);

        add(createSidebar(), BorderLayout.WEST);
        add(createChatArea(), BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(300, 0));
        sidebar.setBackground(new Color(31, 41, 54));

        JLabel title = new JLabel("Chats");
        title.setForeground(new Color(230, 235, 240));
        title.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        sidebar.add(title, BorderLayout.NORTH);

        usersList.setCellRenderer(new UserCellRenderer());
        usersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersList.setBackground(new Color(31, 41, 54));
        usersList.setBorder(BorderFactory.createEmptyBorder());
        sidebar.add(new JScrollPane(usersList), BorderLayout.CENTER);

        return sidebar;
    }

    private JPanel createChatArea() {
        JPanel chatArea = new JPanel(new BorderLayout());
        chatArea.setBackground(new Color(14, 22, 33));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(true);
        header.setBackground(new Color(23, 33, 43));
        header.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        headerLabel.setForeground(new Color(230, 235, 240));
        statusLabel.setForeground(new Color(138, 151, 165));
        header.add(headerLabel, BorderLayout.NORTH);
        header.add(statusLabel, BorderLayout.SOUTH);

        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBackground(new Color(14, 22, 33));
        messagesScrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel composer = new JPanel(new BorderLayout(8, 8));
        composer.setBackground(new Color(23, 33, 43));
        composer.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        composer.add(new JScrollPane(inputArea), BorderLayout.CENTER);
        composer.add(sendButton, BorderLayout.EAST);

        inputArea.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "sendMessage");
        inputArea.getActionMap().put("sendMessage", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (sendButton.isEnabled()) {
                    sendButton.doClick();
                }
            }
        });
        inputArea.getInputMap().put(KeyStroke.getKeyStroke("shift ENTER"), "insert-break");

        chatArea.add(header, BorderLayout.NORTH);
        chatArea.add(messagesScrollPane, BorderLayout.CENTER);
        chatArea.add(composer, BorderLayout.SOUTH);
        return chatArea;
    }

    public void setUsers(List<UserInfo> users) {
        usersModel.clear();
        for (UserInfo user : users) {
            usersModel.addElement(user);
        }
    }

    public void setSelectedUser(UserInfo userInfo) {
        headerLabel.setText(userInfo == null ? "Select a chat" : userInfo.displayName());
        statusLabel.setText(userInfo == null ? "Choose a user to start messaging" : (userInfo.online() ? "online" : "offline"));
    }

    public UserInfo getSelectedUser() {
        return usersList.getSelectedValue();
    }

    public void addUserSelectionListener(javax.swing.event.ListSelectionListener listener) {
        usersList.addListSelectionListener(listener);
    }

    public void addSendAction(ActionListener actionListener) {
        sendButton.addActionListener(actionListener);
    }

    public String consumeInputText() {
        String text = inputArea.getText();
        inputArea.setText("");
        return text;
    }

    public void setComposerEnabled(boolean enabled) {
        inputArea.setEnabled(enabled);
        sendButton.setEnabled(enabled);
    }

    public void renderMessages(List<ChatMessageVm> messages) {
        messagesPanel.removeAll();
        for (ChatMessageVm message : messages) {
            messagesPanel.add(new MessageBubble(message));
            messagesPanel.add(Box.createVerticalStrut(4));
        }
        messagesPanel.revalidate();
        messagesPanel.repaint();
        scrollToBottom();
    }

    private void scrollToBottom() {
        JScrollBar verticalScrollBar = messagesScrollPane.getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
    }

    public void setStatusText(String text) {
        statusLabel.setText(text == null || text.isBlank() ? " " : text);
    }
}
