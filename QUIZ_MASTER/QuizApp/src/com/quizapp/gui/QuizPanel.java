package com.quizapp.gui;

import com.quizapp.controller.QuizController;
import com.quizapp.model.Question;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Quiz panel for displaying questions and options
 */
public class QuizPanel extends JPanel implements ActionListener {
    private QuizController controller;
    private MainWindow mainWindow;
    private JLabel questionLabel;
    private JRadioButton[] optionButtons;
    private ButtonGroup buttonGroup;
    private JButton nextButton, previousButton, submitButton;
    private JLabel questionNumberLabel;
    private JLabel timerLabel;
    private JProgressBar progressBar;
    private Timer timer;
    private long startTime;

    public QuizPanel(QuizController controller, MainWindow mainWindow) {
        this.controller = controller;
        this.mainWindow = mainWindow;
        setLayout(null);
        setBackground(new Color(245, 245, 245));
        initComponents();
        startTimer();
    }

    private void initComponents() {
        // Question number label
        questionNumberLabel = new JLabel();
        questionNumberLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        questionNumberLabel.setForeground(new Color(100, 100, 100));
        questionNumberLabel.setBounds(20, 20, 200, 20);
        add(questionNumberLabel);

        // Timer label
        timerLabel = new JLabel("Time: 00:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 12));
        timerLabel.setForeground(new Color(200, 50, 50));
        timerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        timerLabel.setBounds(520, 20, 100, 20);
        add(timerLabel);

        // Progress bar
        progressBar = new JProgressBar();
        progressBar.setBounds(20, 45, 600, 10);
        progressBar.setBackground(new Color(200, 200, 200));
        progressBar.setForeground(new Color(51, 153, 102));
        add(progressBar);

        // Question text
        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        questionLabel.setForeground(new Color(51, 51, 51));
        questionLabel.setBounds(20, 70, 600, 80);
        questionLabel.setVerticalAlignment(SwingConstants.TOP);
        add(questionLabel);

        // Radio buttons for options
        optionButtons = new JRadioButton[4];
        buttonGroup = new ButtonGroup();

        String[] optionLabels = { "A", "B", "C", "D" };
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton();
            optionButtons[i].setFont(new Font("Arial", Font.PLAIN, 14));
            optionButtons[i].setBounds(40, 160 + (i * 40), 550, 30);
            optionButtons[i].setBackground(new Color(245, 245, 245));
            optionButtons[i].setFocusable(false);
            buttonGroup.add(optionButtons[i]);
            add(optionButtons[i]);
        }

        // Previous button
        previousButton = new JButton("Previous");
        previousButton.setBounds(20, 360, 100, 40);
        previousButton.setFont(new Font("Arial", Font.BOLD, 12));
        previousButton.setBackground(new Color(153, 153, 153));
        previousButton.setForeground(Color.WHITE);
        previousButton.setBorder(BorderFactory.createEmptyBorder());
        previousButton.setFocusPainted(false);
        previousButton.addActionListener(this);
        add(previousButton);

        // Next button
        nextButton = new JButton("Next");
        nextButton.setBounds(140, 360, 100, 40);
        nextButton.setFont(new Font("Arial", Font.BOLD, 12));
        nextButton.setBackground(new Color(51, 153, 102));
        nextButton.setForeground(Color.WHITE);
        nextButton.setBorder(BorderFactory.createEmptyBorder());
        nextButton.setFocusPainted(false);
        nextButton.addActionListener(this);
        add(nextButton);

        // Submit button
        submitButton = new JButton("Submit Quiz");
        submitButton.setBounds(260, 360, 120, 40);
        submitButton.setFont(new Font("Arial", Font.BOLD, 12));
        submitButton.setBackground(new Color(204, 102, 0));
        submitButton.setForeground(Color.WHITE);
        submitButton.setBorder(BorderFactory.createEmptyBorder());
        submitButton.setFocusPainted(false);
        submitButton.addActionListener(this);
        add(submitButton);

        loadQuestion();
    }

    /**
     * Load and display current question
     */
    private void loadQuestion() {
        Question q = controller.getCurrentQuestion();
        if (q == null) {
            return;
        }

        // Update question number
        questionNumberLabel.setText("Question " + controller.getCurrentQuestionNumber() +
                " of " + controller.getTotalQuestions());

        // Update progress bar
        progressBar.setMaximum(controller.getTotalQuestions());
        progressBar.setValue(controller.getCurrentQuestionNumber());

        // Clear previous selection
        buttonGroup.clearSelection();

        // Set question text
        questionLabel.setText(q.getQuestionText());

        // Set options
        optionButtons[0].setText("A) " + q.getOptionA());
        optionButtons[1].setText("B) " + q.getOptionB());
        optionButtons[2].setText("C) " + q.getOptionC());
        optionButtons[3].setText("D) " + q.getOptionD());

        // Restore previous answer if available
        String previousAnswer = controller.getUserAnswer(controller.getCurrentQuestionNumber() - 1);
        if (previousAnswer != null) {
            int optionIndex = previousAnswer.charAt(0) - 'A';
            if (optionIndex >= 0 && optionIndex < 4) {
                optionButtons[optionIndex].setSelected(true);
            }
        }

        // Update button states
        previousButton.setEnabled(controller.getCurrentQuestionNumber() > 1);
        nextButton.setEnabled(controller.getCurrentQuestionNumber() < controller.getTotalQuestions());
        submitButton.setEnabled(controller.getCurrentQuestionNumber() == controller.getTotalQuestions());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Get selected option
        String selectedOption = null;
        for (int i = 0; i < 4; i++) {
            if (optionButtons[i].isSelected()) {
                selectedOption = String.valueOf((char) ('A' + i));
                break;
            }
        }

        if (e.getSource() == nextButton) {
            if (selectedOption == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select an option before proceeding!",
                        "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            controller.submitAnswer(selectedOption);
            if (controller.nextQuestion()) {
                loadQuestion();
            }

        } else if (e.getSource() == previousButton) {
            if (selectedOption != null) {
                controller.submitAnswer(selectedOption);
            }
            if (controller.previousQuestion()) {
                loadQuestion();
            }

        } else if (e.getSource() == submitButton) {
            if (selectedOption == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select an option before submitting!",
                        "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            controller.submitAnswer(selectedOption);

            // Stop timer
            timer.stop();

            // End quiz and show results
            mainWindow.showResults(controller.endQuiz());
        }
    }

    /**
     * Start timer for quiz
     */
    private void startTimer() {
        startTime = System.currentTimeMillis();
        timer = new Timer(1000, e -> updateTimer());
        timer.start();
    }

    /**
     * Update timer display
     */
    private void updateTimer() {
        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        long minutes = elapsed / 60;
        long seconds = elapsed % 60;
        timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
    }

    public void resetPanel() {
        startTime = System.currentTimeMillis();
        loadQuestion();
        startTimer();
    }
}
