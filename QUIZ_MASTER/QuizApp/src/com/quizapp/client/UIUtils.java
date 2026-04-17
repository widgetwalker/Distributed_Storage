package com.quizapp.client;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class UIUtils {
    public static final Color PRIMARY_COLOR = new Color(79, 70, 229); // Modern Indigo
    public static final Color SECONDARY_COLOR = new Color(243, 244, 246);
    public static final Color ACCENT_COLOR = new Color(16, 185, 129); // Green
    public static final Color DANGER_COLOR = new Color(239, 68, 68); // Red
    public static final Color BACKGROUND_COLOR = new Color(255, 255, 255);
    public static final Color TEXT_COLOR = new Color(31, 41, 55);
    
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public static JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(getForeground());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(BODY_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1, true),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return field;
    }

    public static JPanel createCard(Component content) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.add(content);
        return card;
    }
}
