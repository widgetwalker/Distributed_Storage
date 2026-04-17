package com.quizapp.controller;

import com.quizapp.db.DatabaseManager;
import com.quizapp.model.Question;
import com.quizapp.model.User;
import com.quizapp.model.QuizResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controller for quiz application
 * Handles business logic and communication between UI and database
 */
public class QuizController {
    private DatabaseManager dbManager;
    private User currentUser;
    private List<Question> currentQuiz;
    private List<String> userAnswers;
    private long quizStartTime;
    private int currentQuestionIndex;
    private int score;

    public QuizController() {
        this.dbManager = DatabaseManager.getInstance();
        this.userAnswers = new ArrayList<>();
        this.score = 0;
        this.currentQuestionIndex = 0;
    }

    // ==================== USER MANAGEMENT ====================

    /**
     * Login or register user
     */
    public User loginUser(String username) {
        User user = dbManager.getUserByUsername(username);

        // If user doesn't exist, create new user
        if (user == null) {
            user = dbManager.createUser(username, "");
        }

        if (user != null) {
            this.currentUser = user;
            System.out.println("User logged in: " + username);
        }

        return user;
    }

    /**
     * Get current logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    // ==================== QUIZ MANAGEMENT ====================

    /**
     * Initialize and start a new quiz
     */
    public void startQuiz() {
        if (currentUser == null) {
            throw new IllegalStateException("No user logged in");
        }

        // Load all questions from database
        List<Question> allQuestions = dbManager.getAllQuestions();

        if (allQuestions.isEmpty()) {
            System.out.println("No questions found in database!");
            loadSampleQuestions();
        } else {
            this.currentQuiz = new ArrayList<>(allQuestions);
        }

        // Shuffle questions (optional)
        Collections.shuffle(currentQuiz);

        // Initialize answer tracking
        this.userAnswers = new ArrayList<>();
        for (int i = 0; i < currentQuiz.size(); i++) {
            userAnswers.add(null);
        }

        this.score = 0;
        this.currentQuestionIndex = 0;
        this.quizStartTime = System.currentTimeMillis();

        System.out.println("Quiz started with " + currentQuiz.size() + " questions");
    }

    /**
     * Get current question
     */
    public Question getCurrentQuestion() {
        if (currentQuiz == null || currentQuestionIndex >= currentQuiz.size()) {
            return null;
        }
        return currentQuiz.get(currentQuestionIndex);
    }

    /**
     * Submit answer for current question
     */
    public void submitAnswer(String answer) {
        if (currentQuiz == null || currentQuestionIndex >= currentQuiz.size()) {
            return;
        }

        Question question = currentQuiz.get(currentQuestionIndex);
        userAnswers.set(currentQuestionIndex, answer);

        // Check if answer is correct
        if (question.isAnswerCorrect(answer)) {
            score++;
        }

        System.out.println("Answer submitted: " + answer + " (Correct: " + question.isAnswerCorrect(answer) + ")");
    }

    /**
     * Move to next question
     */
    public boolean nextQuestion() {
        if (currentQuestionIndex < currentQuiz.size() - 1) {
            currentQuestionIndex++;
            return true;
        }
        return false;
    }

    /**
     * Move to previous question
     */
    public boolean previousQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            return true;
        }
        return false;
    }

    /**
     * Jump to specific question
     */
    public void goToQuestion(int index) {
        if (index >= 0 && index < currentQuiz.size()) {
            currentQuestionIndex = index;
        }
    }

    /**
     * Check if quiz is completed
     */
    public boolean isQuizComplete() {
        return currentQuestionIndex >= currentQuiz.size() - 1;
    }

    /**
     * Get current question number
     */
    public int getCurrentQuestionNumber() {
        return currentQuestionIndex + 1;
    }

    /**
     * Get total questions
     */
    public int getTotalQuestions() {
        return currentQuiz != null ? currentQuiz.size() : 0;
    }

    /**
     * Get current score
     */
    public int getCurrentScore() {
        return score;
    }

    /**
     * Get user's answer for a specific question
     */
    public String getUserAnswer(int questionIndex) {
        if (questionIndex >= 0 && questionIndex < userAnswers.size()) {
            return userAnswers.get(questionIndex);
        }
        return null;
    }

    // ==================== QUIZ RESULT ====================

    /**
     * End quiz and save result
     */
    public QuizResult endQuiz() {
        if (currentUser == null || currentQuiz == null) {
            return null;
        }

        long timeTaken = (System.currentTimeMillis() - quizStartTime) / 1000;
        QuizResult result = new QuizResult(
                currentUser.getId(),
                currentUser.getUsername(),
                score,
                currentQuiz.size(),
                timeTaken);

        // Save to database
        dbManager.saveQuizResult(result);

        System.out.println("Quiz ended. Result: " + result);

        // Reset quiz
        currentQuiz = null;
        userAnswers = new ArrayList<>();
        score = 0;
        currentQuestionIndex = 0;

        return result;
    }

    /**
     * Get previous attempts for current user
     */
    public List<QuizResult> getPreviousAttempts() {
        if (currentUser == null) {
            return new ArrayList<>();
        }
        return dbManager.getUserResults(currentUser.getId());
    }

    // ==================== LEADERBOARD ====================

    /**
     * Get leaderboard
     */
    public List<User> getLeaderboard(int limit) {
        return dbManager.getLeaderboard(limit);
    }

    // ==================== HELPER METHODS ====================

    /**
     * Load sample questions if database is empty
     */
    private void loadSampleQuestions() {
        List<Question> sampleQuestions = new ArrayList<>();

        sampleQuestions.add(new Question(1, "Which of these is the Capital of UAE?",
                "Dubai", "Abu Dhabi", "Fujera", "Ras-al-Khaimah", "B"));

        sampleQuestions.add(new Question(2, "Who created this Application?",
                "Bill Gates", "Maroor Chethan Pai", "Elon Musk", "Anupam Mittal", "B"));

        sampleQuestions.add(new Question(3, "What would be the heart rate if cardiac output is 5L?",
                "100 beats per minute", "70 beats per minute", "92 beats per minute",
                "The person has died :(", "A"));

        sampleQuestions.add(new Question(4, "Which is The host country of G20 summit this year?",
                "Brazil", "Indonesia", "India", "China", "C"));

        sampleQuestions.add(new Question(5, "What is the Parent company of ChatGPT?",
                "OpenAI", "Microsoft", "Meta", "NeuroLink", "A"));

        this.currentQuiz = sampleQuestions;
        this.userAnswers = new ArrayList<>();
        for (int i = 0; i < currentQuiz.size(); i++) {
            userAnswers.add(null);
        }
    }

    /**
     * Get statistics for current quiz
     */
    public String getQuizStatistics() {
        if (currentQuiz == null) {
            return "No active quiz";
        }

        int answered = 0;
        for (String answer : userAnswers) {
            if (answer != null) {
                answered++;
            }
        }

        double percentage = (double) score / currentQuiz.size() * 100;
        long timeTaken = (System.currentTimeMillis() - quizStartTime) / 1000;

        return String.format("Score: %d/%d (%.2f%%) | Answered: %d | Time: %ds",
                score, currentQuiz.size(), percentage, answered, timeTaken);
    }
}
