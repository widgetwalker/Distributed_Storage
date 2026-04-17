package com.quizapp.shared;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * Model class representing a user
 */
public class User implements Serializable {
    private int id;
    private String username;
    private String email;
    private int totalScore;
    private int quizzesAttempted;
    private LocalDateTime lastAttempt;

    // Constructor
    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.totalScore = 0;
        this.quizzesAttempted = 0;
    }

    // Full constructor
    public User(int id, String username, String email, int totalScore, int quizzesAttempted) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.totalScore = totalScore;
        this.quizzesAttempted = quizzesAttempted;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getQuizzesAttempted() {
        return quizzesAttempted;
    }

    public double getAverageScore() {
        return quizzesAttempted == 0 ? 0 : (double) totalScore / quizzesAttempted;
    }

    public LocalDateTime getLastAttempt() {
        return lastAttempt;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public void setQuizzesAttempted(int quizzesAttempted) {
        this.quizzesAttempted = quizzesAttempted;
    }

    public void setLastAttempt(LocalDateTime lastAttempt) {
        this.lastAttempt = lastAttempt;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", totalScore=" + totalScore +
                ", averageScore=" + getAverageScore() +
                '}';
    }
}
