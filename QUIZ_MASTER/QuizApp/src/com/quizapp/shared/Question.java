package com.quizapp.shared;

import java.io.Serializable;

/**
 * Model class representing a quiz question
 */
public class Question implements Serializable {
    private int id;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctOption; // A, B, C, or D
    private String difficulty; // Easy, Medium, Hard
    private String category; // Category Name (Movies, Maths, etc)

    // Constructor
    public Question(int id, String questionText, String optionA, String optionB,
            String optionC, String optionD, String correctOption) {
        this.id = id;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
        this.category = "General";
    }

    // Full constructor
    public Question(int id, String questionText, String optionA, String optionB,
            String optionC, String optionD, String correctOption, String category, String difficulty) {
        this(id, questionText, optionA, optionB, optionC, optionD, correctOption);
        this.category = category;
        this.difficulty = difficulty;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getCategory() {
        return category;
    }

    // Setters
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Check if the given answer is correct
     */
    public boolean isAnswerCorrect(String answer) {
        return this.correctOption.equalsIgnoreCase(answer);
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", questionText='" + questionText + '\'' +
                ", correctOption='" + correctOption + '\'' +
                '}';
    }
}
