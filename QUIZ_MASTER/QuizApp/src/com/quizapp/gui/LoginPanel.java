package com.quizapp.gui;

import com.quizapp.controller.QuizController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Login panel for user authentication
 */
public class LoginPanel extends JPanel implements ActionListener {
    private QuizController controller;
    private MainWindow mainWindow;
    private JTextField usernameField;
    private JButton loginButton;
    private JLabel titleLabel;

    public LoginPanel(QuizController controller, MainWindow mainWindow) {
        this.controller = controller;
        this.mainWindow = mainWindow;
        setLayout(null);
        setBackground(new Color(240, 240, 240));
        initComponents();
    }

    private void initComponents() {
        // Title
        titleLabel = new JLabel("Champion's Quiz Master");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(51, 102, 153));
        titleLabel.setBounds(100, 50, 400, 50);
        add(titleLabel);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Test Your Knowledge");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setBounds(150, 100, 300, 30);
        add(subtitleLabel);

        // Username label
        JLabel usernameLabel = new JLabel("Enter your name:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameLabel.setBounds(100, 170, 150, 30);
        add(usernameLabel);

        // Username field
        usernameField = new JTextField();
        usernameField.setBounds(100, 200, 300, 40);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        add(usernameField);

        // Login button
        loginButton = new JButton("Start Quiz");
        loginButton.setBounds(100, 280, 300, 50);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(new Color(51, 153, 102));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorder(BorderFactory.createEmptyBorder());
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(this);
        add(loginButton);

        // Instructions
        JLabel instructionsLabel = new JLabel(
                "<html>Instructions:<br>• Enter your name to start<br>• 10 questions to answer<br>• Select one option per question</html>");
        instructionsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        instructionsLabel.setForeground(new Color(80, 80, 80));
        instructionsLabel.setBounds(100, 350, 300, 100);
        add(instructionsLabel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String username = usernameField.getText().trim();

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your name!",
                        "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Login user
            controller.loginUser(username);

            // Start quiz
            controller.startQuiz();

            // Switch to quiz panel
            mainWindow.showPanel("QUIZ");
        }
    }

    public void resetPanel() {
        usernameField.setText("");
        usernameField.requestFocus();
    }
}
