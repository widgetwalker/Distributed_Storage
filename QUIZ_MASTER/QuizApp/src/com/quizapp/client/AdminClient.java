package com.quizapp.client;

import com.quizapp.shared.QuizSession;
import com.quizapp.shared.RequestData;
import com.quizapp.shared.ResponseData;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminClient extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DefaultTableModel tableModel;

    public AdminClient() {
        setTitle("Quiz Master - Admin Dashboard");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIUtils.BACKGROUND_COLOR);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(UIUtils.BACKGROUND_COLOR);

        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createDashboardPanel(), "DASHBOARD");

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }

    private JPanel createLoginPanel() {
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(UIUtils.BACKGROUND_COLOR);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(40, 40, 40, 40)
        ));

        JLabel lblTitle = new JLabel("Admin Login");
        lblTitle.setFont(UIUtils.TITLE_FONT);
        lblTitle.setForeground(UIUtils.PRIMARY_COLOR);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(30));

        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(UIUtils.BODY_FONT);
        lblUser.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblUser);
        panel.add(Box.createVerticalStrut(5));

        JTextField txtUser = UIUtils.createStyledTextField();
        txtUser.setText("admin");
        txtUser.setMaximumSize(new Dimension(300, 45));
        panel.add(txtUser);
        panel.add(Box.createVerticalStrut(20));

        JButton btnLogin = UIUtils.createStyledButton("Access Dashboard", UIUtils.PRIMARY_COLOR);
        btnLogin.setMaximumSize(new Dimension(300, 50));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> {
            Map<String, String> creds = new HashMap<>();
            creds.put("username", txtUser.getText());
            creds.put("role", "admin");
            
            ResponseData res = ClientNetwork.sendRequest(new RequestData("LOGIN", creds));
            if (res.isSuccess()) {
                cardLayout.show(mainPanel, "DASHBOARD");
                startAutoRefresh();
            } else {
                JOptionPane.showMessageDialog(this, res.getMessage(), "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(btnLogin);

        centerWrapper.add(panel);
        return centerWrapper;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(UIUtils.BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        
        JLabel lblHeader = new JLabel("Live Participants Tracking");
        lblHeader.setFont(UIUtils.SUBTITLE_FONT);
        lblHeader.setForeground(UIUtils.TEXT_COLOR);
        headerPanel.add(lblHeader, BorderLayout.WEST);

        JLabel lblStatus = new JLabel("Status: Monitoring Real-time Data");
        lblStatus.setFont(UIUtils.BODY_FONT);
        lblStatus.setForeground(UIUtils.ACCENT_COLOR);
        headerPanel.add(lblStatus, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "User", "Current Score", "Timer (Seconds)"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        JTable table = new JTable(tableModel);
        table.setRowHeight(45);
        table.setFont(UIUtils.BODY_FONT);
        table.setSelectionBackground(new Color(238, 242, 255));
        table.setGridColor(new Color(243, 244, 246));
        table.setShowVerticalLines(false);
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(249, 250, 251));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setForeground(new Color(75, 85, 99));
        header.setPreferredSize(new Dimension(100, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 231, 235)));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<cols.length; i++) table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private void startAutoRefresh() {
        Timer timer = new Timer(1000, e -> {
            ResponseData res = ClientNetwork.sendRequest(new RequestData("ADMIN_DASHBOARD", null));
            if (res.isSuccess() && res.getPayload() != null) {
                List<QuizSession> sessions = (List<QuizSession>) res.getPayload();
                tableModel.setRowCount(0);
                for (QuizSession s : sessions) {
                    tableModel.addRow(new Object[]{
                            s.getId(), s.getUsername(), s.getScore(), s.getTimeRemaining()
                    });
                }
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AdminClient().setVisible(true);
        });
    }
}
