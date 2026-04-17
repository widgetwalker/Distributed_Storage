package com.quizapp.shared;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * Model class representing a quiz result
 */
public class QuizResult implements Serializable {
    private int id;
    private int userId;
    private String username;
    private int score;
    private int totalQuestions;
    private long timeTaken; // in seconds
    private LocalDateTime attemptedAt;
    private double percentage;

    // Constructor
    public QuizResult(int userId, String username, int score, int totalQuestions, long timeTaken) {
        this.userId = userId;
        this.username = username;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.timeTaken = timeTaken;
        this.attemptedAt = LocalDateTime.now();
        this.percentage = (double) score / totalQuestions * 100;
    }

    // Full constructor
    public QuizResult(int id, int userId, String username, int score, int totalQuestions,
            long timeTaken, LocalDateTime attemptedAt) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.timeTaken = timeTaken;
        this.attemptedAt = attemptedAt;
        this.percentage = (double) score / totalQuestions * 100;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    public LocalDateTime getAttemptedAt() {
        return attemptedAt;
    }

    public double getPercentage() {
        return percentage;
    }

    @Override
    public String toString() {
        return "QuizResult{" +
                "username='" + username + '\'' +
                ", score=" + score + "/" + totalQuestions +
                ", percentage=" + String.format("%.2f", percentage) + "%" +
                ", timeTaken=" + timeTaken + "s" +
                '}';
    }
}
