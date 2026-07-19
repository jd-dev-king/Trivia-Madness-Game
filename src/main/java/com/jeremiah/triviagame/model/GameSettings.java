package com.jeremiah.triviagame.model;

import java.util.ArrayList;
import java.util.List;

public class GameSettings {

    private final String playerName;
    private final List<Category> categories;
    private final String difficulty;
    private final int questionCount;
    private final int secondsPerQuestion;

    public GameSettings(
            String playerName,
            List<Category> categories,
            String difficulty,
            int questionCount,
            int secondsPerQuestion
    ) {
        this.playerName = playerName;
        this.categories = new ArrayList<>(categories);
        this.difficulty = difficulty;
        this.questionCount = questionCount;
        this.secondsPerQuestion = secondsPerQuestion;
    }

    public String getPlayerName() {
        return playerName;
    }

    public List<Category> getCategories() {
        return new ArrayList<>(categories);
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public int getSecondsPerQuestion() {
        return secondsPerQuestion;
    }
}
