package com.jeremiah.triviagame.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Question {

    private final String category;
    private final String difficulty;
    private final String questionText;
    private final String correctAnswer;
    private final List<String> answers;

    public Question(
            String category,
            String difficulty,
            String questionText,
            String correctAnswer,
            List<String> incorrectAnswers
    ) {
        this.category = category;
        this.difficulty = difficulty;
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;

        this.answers = new ArrayList<>(incorrectAnswers);
        this.answers.add(correctAnswer);
        Collections.shuffle(this.answers);
    }

    public String getCategory() {
        return category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public List<String> getAnswers() {
        return new ArrayList<>(answers);
    }

    public boolean isCorrect(String selectedAnswer) {
        return correctAnswer.equals(selectedAnswer);
    }
}
