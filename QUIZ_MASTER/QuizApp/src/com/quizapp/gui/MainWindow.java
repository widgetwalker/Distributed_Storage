package com.quizapp.gui;

import com.quizapp.controller.QuizController;
import com.quizapp.model.QuizResult;

import javax.swing.*;
import java.awt.CardLayout;

/**
 * Main application window
 * Uses CardLayout to switch between different panels
 */
public class MainWindow extends JFrame {
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private LoginPanel loginPanel;
    private QuizPanel quizPanel;
    private ResultPanel resultPanel;
    private QuizController controller;

    public MainWindow() {
        setTitle("Champion's Quiz Master");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        // Initialize controller
        controller = new QuizController();

        // Create card layout
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Create panels
        loginPanel = new LoginPanel(controller, this);
        quizPanel = new QuizPanel(controller, this);
        resultPanel = new ResultPanel(controller, this);

        // Add panels to card layout
        cardPanel.add(loginPanel, "LOGIN");
        cardPanel.add(quizPanel, "QUIZ");
        cardPanel.add(resultPanel, "RESULT");

        // Add card panel to frame
        add(cardPanel);

        // Show login panel first
        cardLayout.show(cardPanel, "LOGIN");

        setVisible(true);
    }

    /**
     * Switch to specified panel
     */
    public void showPanel(String panelName) {
        if (panelName.equals("LOGIN")) {
            loginPanel.resetPanel();
        } else if (panelName.equals("QUIZ")) {
            quizPanel.resetPanel();
        }
        cardLayout.show(cardPanel, panelName);
    }

    /**
     * Display quiz results
     */
    public void showResults(QuizResult result) {
        resultPanel.displayResults(result);
        cardLayout.show(cardPanel, "RESULT");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow());
    }
}
