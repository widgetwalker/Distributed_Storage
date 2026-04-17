package com.quizapp.gui;

import com.quizapp.controller.QuizController;
import com.quizapp.model.QuizResult;
import com.quizapp.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Result panel for displaying quiz results
 */
public class ResultPanel extends JPanel implements ActionListener {
    private QuizController controller;
    private MainWindow mainWindow;
    private JLabel scoreLabel;
    private JLabel percentageLabel;
    private JLabel timeLabel;
    private JLabel rankLabel;
    private JButton retakeButton;
    private JButton leaderboardButton;
    private JButton exitButton;
    private JPanel leaderboardPanel;

    public ResultPanel(QuizController controller, MainWindow mainWindow) {
        this.controller = controller;
        this.mainWindow = mainWindow;
        setLayout(null);
        setBackground(new Color(245, 245, 245));
        initComponents();
    }

    private void initComponents() {
        // Title
        JLabel titleLabel = new JLabel("Quiz Complete!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(51, 102, 153));
        titleLabel.setBounds(100, 30, 400, 50);
        add(titleLabel);

        // Score label
        scoreLabel = new JLabel("Score: -- / --");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setForeground(new Color(51, 153, 102));
        scoreLabel.setBounds(100, 100, 400, 40);
        add(scoreLabel);

        // Percentage label
        percentageLabel = new JLabel("Percentage: --%");
        percentageLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        percentageLabel.setForeground(new Color(100, 100, 100));
        percentageLabel.setBounds(100, 150, 400, 30);
        add(percentageLabel);

        // Time label
        timeLabel = new JLabel("Time Taken: 0s");
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        timeLabel.setForeground(new Color(100, 100, 100));
        timeLabel.setBounds(100, 190, 400, 30);
        add(timeLabel);

        // Rank/Performance label
        rankLabel = new JLabel("Performance: --");
        rankLabel.setFont(new Font("Arial", Font.BOLD, 16));
        rankLabel.setForeground(new Color(204, 102, 0));
        rankLabel.setBounds(100, 230, 400, 30);
        add(rankLabel);

        // Retake button
        retakeButton = new JButton("Retake Quiz");
        retakeButton.setBounds(80, 290, 150, 45);
        retakeButton.setFont(new Font("Arial", Font.BOLD, 14));
        retakeButton.setBackground(new Color(51, 153, 102));
        retakeButton.setForeground(Color.WHITE);
        retakeButton.setBorder(BorderFactory.createEmptyBorder());
        retakeButton.setFocusPainted(false);
        retakeButton.addActionListener(this);
        add(retakeButton);

        // Leaderboard button
        leaderboardButton = new JButton("Leaderboard");
        leaderboardButton.setBounds(250, 290, 150, 45);
        leaderboardButton.setFont(new Font("Arial", Font.BOLD, 14));
        leaderboardButton.setBackground(new Color(51, 102, 153));
        leaderboardButton.setForeground(Color.WHITE);
        leaderboardButton.setBorder(BorderFactory.createEmptyBorder());
        leaderboardButton.setFocusPainted(false);
        leaderboardButton.addActionListener(this);
        add(leaderboardButton);

        // Exit button
        exitButton = new JButton("Exit");
        exitButton.setBounds(420, 290, 100, 45);
        exitButton.setFont(new Font("Arial", Font.BOLD, 14));
        exitButton.setBackground(new Color(204, 102, 0));
        exitButton.setForeground(Color.WHITE);
        exitButton.setBorder(BorderFactory.createEmptyBorder());
        exitButton.setFocusPainted(false);
        exitButton.addActionListener(this);
        add(exitButton);
    }

    /**
     * Display quiz results
     */
    public void displayResults(QuizResult result) {
        if (result == null) {
            return;
        }

        scoreLabel.setText(String.format("Score: %d / %d", result.getScore(), result.getTotalQuestions()));

        double percentage = result.getPercentage();
        percentageLabel.setText(String.format("Percentage: %.2f%%", percentage));

        long minutes = result.getTimeTaken() / 60;
        long seconds = result.getTimeTaken() % 60;
        timeLabel.setText(String.format("Time Taken: %d:%02d", minutes, seconds));

        // Determine performance
        String performance;
        if (percentage >= 80) {
            performance = "Excellent! 🎉";
            rankLabel.setForeground(new Color(0, 153, 0));
        } else if (percentage >= 60) {
            performance = "Good! 👍";
            rankLabel.setForeground(new Color(51, 153, 102));
        } else if (percentage >= 40) {
            performance = "Average";
            rankLabel.setForeground(new Color(204, 153, 0));
        } else {
            performance = "Needs Improvement";
            rankLabel.setForeground(new Color(204, 0, 0));
        }

        rankLabel.setText("Performance: " + performance);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == retakeButton) {
            controller.startQuiz();
            mainWindow.showPanel("QUIZ");

        } else if (e.getSource() == leaderboardButton) {
            showLeaderboard();

        } else if (e.getSource() == exitButton) {
            System.exit(0);
        }
    }

    /**
     * Display leaderboard
     */
    private void showLeaderboard() {
        List<User> leaderboard = controller.getLeaderboard(10);

        StringBuilder content = new StringBuilder();
        content.append("TOP 10 PERFORMERS\n\n");
        content.append(String.format("%-5s %-20s %-10s %-10s\n", "Rank", "User", "Score", "Attempts"));
        content.append("=".repeat(50)).append("\n");

        int rank = 1;
        for (User user : leaderboard) {
            content.append(String.format("%-5d %-20s %-10d %-10d\n",
                    rank++,
                    user.getUsername(),
                    user.getTotalScore(),
                    user.getQuizzesAttempted()));
        }

        JTextArea textArea = new JTextArea(content.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Leaderboard",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
