package com.example.messenger.client.ui.component;

import com.example.messenger.common.dto.UserInfo;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

public class UserCellRenderer extends DefaultListCellRenderer {
    private static final Color BG = new Color(31, 41, 54);
    private static final Color SELECTED = new Color(43, 82, 120);
    private static final Color TEXT = new Color(230, 235, 240);
    private static final Color SECONDARY = new Color(138, 151, 165);

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof UserInfo userInfo) {
            String status = userInfo.online() ? "online" : "offline";
            label.setText("<html><div style='font-weight:600'>" + userInfo.displayName() + "</div>"
                    + "<div style='color:#8A97A5;font-size:10px'>" + status + "</div></html>");
        }
        label.setOpaque(true);
        label.setBackground(isSelected ? SELECTED : BG);
        label.setForeground(TEXT);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 13f));
        label.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        return label;
    }
}
