package com.quizapp.client;

import com.quizapp.shared.Question;
import com.quizapp.shared.QuizResult;
import com.quizapp.shared.RequestData;
import com.quizapp.shared.ResponseData;
import com.quizapp.shared.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserClient extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    private User currentUser;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private List<String> userAnswers = new ArrayList<>();
    
    private int timeRemaining = 600;
    private Timer syncTimer;
    private Timer countdownTimer;
    
    private JLabel lblTimer;
    private JLabel lblQuestion;
    private JRadioButton rdoA, rdoB, rdoC, rdoD;
    private ButtonGroup btnGroup;
    
    private JComboBox<String> comboCategory;
    private JPanel resultDetailsPanel;
    private JLabel lblResultPercentage;
    private JLabel lblResultPoints;

    public UserClient() {
        setTitle("Quiz Master - Challenge Your Mind");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIUtils.BACKGROUND_COLOR);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(UIUtils.BACKGROUND_COLOR);

        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createQuizPanel(), "QUIZ");
        mainPanel.add(createResultPanel(), "RESULT");

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

        JLabel lblTitle = new JLabel("Quiz Master");
        lblTitle.setFont(UIUtils.TITLE_FONT);
        lblTitle.setForeground(UIUtils.PRIMARY_COLOR);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(10));

        JLabel lblSub = new JLabel("Select a category and enter your name");
        lblSub.setFont(UIUtils.BODY_FONT);
        lblSub.setForeground(new Color(107, 114, 128));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblSub);
        panel.add(Box.createVerticalStrut(25));

        // Category Selection
        JLabel lblCat = new JLabel("Category:");
        lblCat.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblCat);
        
        ResponseData catRes = ClientNetwork.sendRequest(new RequestData("GET_CATEGORIES", null));
        String[] cats = {"All", "Movies", "Anime", "Maths", "CS", "Science", "Fun"};
        if (catRes.isSuccess() && catRes.getPayload() != null) {
            List<String> dynamicCats = (List<String>) catRes.getPayload();
            if (!dynamicCats.isEmpty()) {
                cats = dynamicCats.toArray(new String[0]);
            }
        }
        
        comboCategory = new JComboBox<>(cats);
        comboCategory.setMaximumSize(new Dimension(350, 45));
        comboCategory.setFont(UIUtils.BODY_FONT);
        panel.add(comboCategory);
        panel.add(Box.createVerticalStrut(20));

        JTextField txtUser = UIUtils.createStyledTextField();
        txtUser.setMaximumSize(new Dimension(350, 45));
        txtUser.setToolTipText("Enter Username");
        panel.add(txtUser);
        panel.add(Box.createVerticalStrut(20));

        JButton btnLogin = UIUtils.createStyledButton("Start Quiz", UIUtils.PRIMARY_COLOR);
        btnLogin.setMaximumSize(new Dimension(350, 50));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> {
            String username = txtUser.getText().trim();
            if (username.isEmpty()) return;
            
            Map<String, String> creds = new HashMap<>();
            creds.put("username", username);
            creds.put("role", "user");
            
            ResponseData res = ClientNetwork.sendRequest(new RequestData("LOGIN", creds));
            if (res.isSuccess()) {
                currentUser = (User) res.getPayload();
                initializeQuiz((String) comboCategory.getSelectedItem());
            } else {
                JOptionPane.showMessageDialog(this, res.getMessage());
            }
        });
        panel.add(btnLogin);

        centerWrapper.add(panel);
        return centerWrapper;
    }

    private void initializeQuiz(String category) {
        ResponseData qcRes = ClientNetwork.sendRequest(new RequestData("GET_QUESTIONS_BY_CATEGORY", category));
        if (qcRes.isSuccess() && qcRes.getPayload() != null) {
            questions = (List<Question>) qcRes.getPayload();
            if (questions.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No questions found for " + category);
                return;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Failed to load questions.");
            return;
        }

        Map<String, Object> req = new HashMap<>();
        req.put("userId", currentUser.getId());
        req.put("username", currentUser.getUsername());
        req.put("timeRemaining", timeRemaining);
        ClientNetwork.sendRequest(new RequestData("START_SESSION", req));

        currentQuestionIndex = 0;
        score = 0;
        userAnswers.clear();
        
        loadQuestionUI();
        cardLayout.show(mainPanel, "QUIZ");
        startTimers();
    }

    private void startTimers() {
        if (countdownTimer != null) countdownTimer.stop();
        if (syncTimer != null) syncTimer.stop();

        countdownTimer = new Timer(1000, e -> {
            timeRemaining--;
            int mins = timeRemaining / 60;
            int secs = timeRemaining % 60;
            lblTimer.setText(String.format("%02d:%02d", mins, secs));
            if (timeRemaining <= 30) lblTimer.setForeground(UIUtils.DANGER_COLOR);
            else lblTimer.setForeground(UIUtils.TEXT_COLOR);

            if (timeRemaining <= 0) {
                countdownTimer.stop();
                endQuiz();
            }
        });
        countdownTimer.start();

        syncTimer = new Timer(1000, e -> {
            Map<String, Object> syncMap = new HashMap<>();
            syncMap.put("userId", currentUser.getId());
            syncMap.put("score", score);
            syncMap.put("timeRemaining", timeRemaining);
            ClientNetwork.sendRequest(new RequestData("SYNC_SESSION", syncMap));
        });
        syncTimer.start();
    }

    private JPanel createQuizPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 30));
        panel.setBackground(UIUtils.BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(40, 60, 40, 60));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        
        JLabel lblTask = new JLabel("Quiz Session");
        lblTask.setFont(UIUtils.SUBTITLE_FONT);
        topPanel.add(lblTask, BorderLayout.WEST);

        lblTimer = new JLabel("10:00");
        lblTimer.setFont(new Font("Segoe UI", Font.BOLD, 22));
        topPanel.add(lblTimer, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        
        lblQuestion = new JLabel("Question text here...");
        lblQuestion.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblQuestion.setForeground(UIUtils.TEXT_COLOR);
        centerPanel.add(lblQuestion);
        centerPanel.add(Box.createVerticalStrut(40));

        rdoA = createStyledRadio("Option A");
        rdoB = createStyledRadio("Option B");
        rdoC = createStyledRadio("Option C");
        rdoD = createStyledRadio("Option D");

        btnGroup = new ButtonGroup();
        btnGroup.add(rdoA); btnGroup.add(rdoB); btnGroup.add(rdoC); btnGroup.add(rdoD);

        centerPanel.add(rdoA);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(rdoB);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(rdoC);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(rdoD);
        
        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(UIUtils.BACKGROUND_COLOR);

        JButton btnGiveUp = UIUtils.createStyledButton("Terminate", UIUtils.DANGER_COLOR);
        btnGiveUp.setPreferredSize(new Dimension(150, 45));
        btnGiveUp.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Give up and save progress?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) endQuiz();
        });

        JButton btnNext = UIUtils.createStyledButton("Submit & Next", UIUtils.ACCENT_COLOR);
        btnNext.setPreferredSize(new Dimension(180, 45));
        btnNext.addActionListener(e -> nextQuestion());

        bottomPanel.add(btnGiveUp, BorderLayout.WEST);
        bottomPanel.add(btnNext, BorderLayout.EAST);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JRadioButton createStyledRadio(String text) {
        JRadioButton rdo = new JRadioButton(text);
        rdo.setFont(UIUtils.BODY_FONT);
        rdo.setBackground(UIUtils.BACKGROUND_COLOR);
        rdo.setFocusPainted(false);
        return rdo;
    }

    private void loadQuestionUI() {
        Question q = questions.get(currentQuestionIndex);
        lblQuestion.setText("<html><div style='width:550px;'>" + (currentQuestionIndex + 1) + ". " + q.getQuestionText() + "</div></html>");
        rdoA.setText(q.getOptionA());
        rdoB.setText(q.getOptionB());
        rdoC.setText(q.getOptionC());
        rdoD.setText(q.getOptionD());
        btnGroup.clearSelection();
    }

    private void nextQuestion() {
        Question q = questions.get(currentQuestionIndex);
        String selected = null;
        if (rdoA.isSelected()) selected = "A";
        else if (rdoB.isSelected()) selected = "B";
        else if (rdoC.isSelected()) selected = "C";
        else if (rdoD.isSelected()) selected = "D";

        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an option!");
            return;
        }

        userAnswers.add(selected);
        if (selected.equalsIgnoreCase(q.getCorrectOption())) {
            score += 10;
        }

        currentQuestionIndex++;
        if (currentQuestionIndex < questions.size()) {
            loadQuestionUI();
        } else {
            endQuiz();
        }
    }

    private void endQuiz() {
        if (countdownTimer != null) countdownTimer.stop();
        if (syncTimer != null) syncTimer.stop();

        // Fill remaining userAnswers if gave up early
        while(userAnswers.size() < questions.size()) {
            userAnswers.add("NONE");
        }

        int timeTaken = 600 - timeRemaining;
        QuizResult res = new QuizResult(0, currentUser.getId(), currentUser.getUsername(),
                score, questions.size(), timeTaken, LocalDateTime.now());

        ClientNetwork.sendRequest(new RequestData("SAVE_RESULT", res));

        double percentage = ((double)score / (questions.size() * 10)) * 100;
        lblResultPercentage.setText(String.format("%.1f%%", percentage));
        lblResultPoints.setText("Points: " + score + " / " + (questions.size() * 10));
        
        buildResultDetails();
        cardLayout.show(mainPanel, "RESULT");
    }

    private void buildResultDetails() {
        resultDetailsPanel.removeAll();
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            String userAnswer = i < userAnswers.size() ? userAnswers.get(i) : "NONE";
            boolean correct = userAnswer.equalsIgnoreCase(q.getCorrectOption());
            
            JPanel item = new JPanel(new BorderLayout(10, 5));
            item.setBackground(Color.WHITE);
            item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(243, 244, 246)),
                new EmptyBorder(10, 10, 10, 10)
            ));
            
            JLabel lblQ = new JLabel("<html><div style='width:500px;'>" + (i+1) + ". " + q.getQuestionText() + "</div></html>");
            lblQ.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            item.add(lblQ, BorderLayout.CENTER);
            
            String statusText = correct ? "✓" : "✗ (You: " + userAnswer + ")";
            JLabel lblStat = new JLabel(statusText);
            lblStat.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblStat.setForeground(correct ? new Color(16, 185, 129) : UIUtils.DANGER_COLOR);
            item.add(lblStat, BorderLayout.EAST);
            
            if (!correct) {
                JLabel lblCorrect = new JLabel("Correct: " + q.getCorrectOption());
                lblCorrect.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                lblCorrect.setForeground(new Color(107, 114, 128));
                item.add(lblCorrect, BorderLayout.SOUTH);
            }
            
            resultDetailsPanel.add(item);
        }
        resultDetailsPanel.revalidate();
    }

    private JPanel createResultPanel() {
        JPanel mainResult = new JPanel(new BorderLayout());
        mainResult.setBackground(UIUtils.BACKGROUND_COLOR);
        
        // Header
        JPanel header = new JPanel(new GridLayout(1, 2));
        header.setBackground(UIUtils.PRIMARY_COLOR);
        header.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftHeader.setOpaque(false);
        JLabel lblTitle = new JLabel("Quiz Results");
        lblTitle.setFont(UIUtils.TITLE_FONT);
        lblTitle.setForeground(Color.WHITE);
        leftHeader.add(lblTitle);
        
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightHeader.setOpaque(false);
        lblResultPercentage = new JLabel("0%");
        lblResultPercentage.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblResultPercentage.setForeground(Color.WHITE);
        rightHeader.add(lblResultPercentage);
        
        header.add(leftHeader);
        header.add(rightHeader);
        mainResult.add(header, BorderLayout.NORTH);

        // Details Scroll
        resultDetailsPanel = new JPanel();
        resultDetailsPanel.setLayout(new BoxLayout(resultDetailsPanel, BoxLayout.Y_AXIS));
        resultDetailsPanel.setBackground(Color.WHITE);
        
        JScrollPane scroll = new JScrollPane(resultDetailsPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        mainResult.add(scroll, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Color.WHITE);
        footer.setBorder(new EmptyBorder(20, 40, 20, 40));
        
        lblResultPoints = new JLabel("Points: 0/0");
        lblResultPoints.setFont(UIUtils.SUBTITLE_FONT);
        footer.add(lblResultPoints, BorderLayout.WEST);
        
        JButton btnExit = UIUtils.createStyledButton("Try Another Category", UIUtils.ACCENT_COLOR);
        btnExit.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));
        footer.add(btnExit, BorderLayout.EAST);
        
        mainResult.add(footer, BorderLayout.SOUTH);

        return mainResult;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new UserClient().setVisible(true);
        });
    }
}
