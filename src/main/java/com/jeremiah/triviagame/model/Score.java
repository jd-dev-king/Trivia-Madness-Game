package com.jeremiah.triviagame.model;

public class Score {

    private final int id;
    private final String playerName;
    private final int score;
    private final int totalQuestions;
    private final String category;
    private final String difficulty;
    private final double percentage;
    private final String playedAt;

    public Score(
            int id,
            String playerName,
            int score,
            int totalQuestions,
            String category,
            String difficulty,
            double percentage,
            String playedAt
    ) {
        this.id = id;
        this.playerName = playerName;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.category = category;
        this.difficulty = difficulty;
        this.percentage = percentage;
        this.playedAt = playedAt;
    }

    public int getId() {
        return id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public String getCategory() {
        return category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public double getPercentage() {
        return percentage;
    }

    public String getPlayedAt() {
        return playedAt;
    }
}
