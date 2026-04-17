package com.quizapp.shared;

import java.io.Serializable;

public class QuizSession implements Serializable {
    private int id;
    private int userId;
    private String username;
    private int score;
    private int timeRemaining;
    private boolean isActive;

    public QuizSession(int id, int userId, String username, int score, int timeRemaining, boolean isActive) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.score = score;
        this.timeRemaining = timeRemaining;
        this.isActive = isActive;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public int getScore() { return score; }
    public int getTimeRemaining() { return timeRemaining; }
    public boolean isActive() { return isActive; }

    public void setScore(int score) { this.score = score; }
    public void setTimeRemaining(int timeRemaining) { this.timeRemaining = timeRemaining; }
    public void setActive(boolean active) { isActive = active; }
}
