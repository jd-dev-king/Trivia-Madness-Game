package com.jeremiah.triviagame.ui;

import com.jeremiah.triviagame.model.GameSettings;
import com.jeremiah.triviagame.model.Question;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class GamePanel extends JPanel {

    private static final Color DEFAULT_BUTTON_COLOR =
            new Color(238, 238, 238);

    private static final Color CORRECT_COLOR =
            new Color(76, 175, 80);

    private static final Color INCORRECT_COLOR =
            new Color(244, 67, 54);

    private static final Color TIMER_SAFE_COLOR =
            new Color(76, 175, 80);

    private static final Color TIMER_WARNING_COLOR =
            new Color(255, 193, 7);

    private static final Color TIMER_DANGER_COLOR =
            new Color(244, 67, 54);

    private final GameSettings settings;
    private final List<Question> questions;
    private final BiConsumer<Integer, Integer> gameFinishedAction;

    private final JLabel progressLabel;
    private final JLabel categoryLabel;
    private final JLabel scoreLabel;
    private final JLabel timerLabel;
    private final JLabel questionLabel;
    private final JLabel feedbackLabel;

    private final JPanel answersPanel;
    private final JProgressBar timerProgressBar;
    private final List<JButton> answerButtons;

    private Timer countdownTimer;

    private int currentQuestionIndex;
    private int score;
    private int secondsRemaining;
    private boolean answerSubmitted;

    public GamePanel(
            GameSettings settings,
            List<Question> questions,
            BiConsumer<Integer, Integer> gameFinishedAction
    ) {
        this.settings = settings;
        this.questions = questions;
        this.gameFinishedAction = gameFinishedAction;

        currentQuestionIndex = 0;
        score = 0;
        answerButtons = new ArrayList<>();

        setLayout(new BorderLayout(20, 20));

        setBorder(
                BorderFactory.createEmptyBorder(
                        25,
                        35,
                        25,
                        35
                )
        );

        progressLabel = new JLabel();
        categoryLabel = new JLabel("", SwingConstants.CENTER);
        scoreLabel = new JLabel();
        timerLabel = new JLabel("", SwingConstants.RIGHT);

        progressLabel.setFont(
                new Font("SansSerif", Font.BOLD, 16)
        );

        categoryLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 16)
        );

        scoreLabel.setFont(
                new Font("SansSerif", Font.BOLD, 16)
        );

        timerLabel.setFont(
                new Font("SansSerif", Font.BOLD, 18)
        );

        JPanel informationPanel =
                new JPanel(new GridLayout(1, 4, 15, 0));

        informationPanel.add(progressLabel);
        informationPanel.add(categoryLabel);
        informationPanel.add(scoreLabel);
        informationPanel.add(timerLabel);

        timerProgressBar = new JProgressBar(
                0,
                settings.getSecondsPerQuestion()
        );

        timerProgressBar.setValue(
                settings.getSecondsPerQuestion()
        );

        timerProgressBar.setStringPainted(true);
        timerProgressBar.setForeground(TIMER_SAFE_COLOR);

        JPanel topPanel =
                new JPanel(new BorderLayout(0, 12));

        topPanel.add(
                informationPanel,
                BorderLayout.NORTH
        );

        topPanel.add(
                timerProgressBar,
                BorderLayout.SOUTH
        );

        add(topPanel, BorderLayout.NORTH);

        questionLabel = new JLabel(
                "",
                SwingConstants.CENTER
        );

        questionLabel.setFont(
                new Font("SansSerif", Font.BOLD, 23)
        );

        questionLabel.setBorder(
                BorderFactory.createEmptyBorder(
                        25,
                        20,
                        15,
                        20
                )
        );

        feedbackLabel = new JLabel(
                " ",
                SwingConstants.CENTER
        );

        feedbackLabel.setFont(
                new Font("SansSerif", Font.BOLD, 18)
        );

        JPanel centerPanel =
                new JPanel(new BorderLayout());

        centerPanel.add(
                questionLabel,
                BorderLayout.CENTER
        );

        centerPanel.add(
                feedbackLabel,
                BorderLayout.SOUTH
        );

        add(centerPanel, BorderLayout.CENTER);

        answersPanel =
                new JPanel(new GridLayout(2, 2, 15, 15));

        for (int index = 0; index < 4; index++) {
            JButton answerButton = new JButton();

            answerButton.setFont(
                    new Font("SansSerif", Font.PLAIN, 16)
            );

            answerButton.setFocusPainted(false);
            answerButton.setOpaque(true);

            answerButton.addActionListener(event ->
                    submitAnswer(answerButton)
            );

            answerButtons.add(answerButton);
            answersPanel.add(answerButton);
        }

        add(answersPanel, BorderLayout.SOUTH);

        displayQuestion();
    }

    private void displayQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            finishGame();
            return;
        }

        answerSubmitted = false;
        feedbackLabel.setText(" ");

        Question currentQuestion =
                questions.get(currentQuestionIndex);

        progressLabel.setText(
                "Question "
                        + (currentQuestionIndex + 1)
                        + " of "
                        + questions.size()
        );

        categoryLabel.setText(
                currentQuestion.getCategory()
        );

        scoreLabel.setText(
                "Score: " + score
        );

        questionLabel.setText(
                createWrappedText(
                        currentQuestion.getQuestionText(),
                        650
                )
        );

        List<String> answers =
                currentQuestion.getAnswers();

        for (int index = 0;
             index < answerButtons.size();
             index++) {

            JButton button = answerButtons.get(index);

            resetButtonAppearance(button);

            if (index < answers.size()) {
                button.setText(answers.get(index));
                button.setVisible(true);
                button.setEnabled(true);
            } else {
                button.setVisible(false);
            }
        }

        startTimer();
    }

    private void submitAnswer(JButton selectedButton) {
        if (answerSubmitted) {
            return;
        }

        answerSubmitted = true;
        stopTimer();
        disableAnswerButtons();

        Question currentQuestion =
                questions.get(currentQuestionIndex);

        String selectedAnswer =
                selectedButton.getText();

        boolean correct =
                currentQuestion.isCorrect(selectedAnswer);

        if (correct) {
            score++;
            scoreLabel.setText("Score: " + score);

            selectedButton.setBackground(CORRECT_COLOR);
            selectedButton.setForeground(Color.WHITE);

            feedbackLabel.setText("Correct!");
            feedbackLabel.setForeground(CORRECT_COLOR);

        } else {
            selectedButton.setBackground(INCORRECT_COLOR);
            selectedButton.setForeground(Color.WHITE);

            highlightCorrectAnswer(
                    currentQuestion.getCorrectAnswer()
            );

            feedbackLabel.setText(
                    "Incorrect. Correct answer: "
                            + currentQuestion.getCorrectAnswer()
            );

            feedbackLabel.setForeground(INCORRECT_COLOR);
        }

        moveAfterDelay();
    }

    private void handleTimeExpired() {
        if (answerSubmitted) {
            return;
        }

        answerSubmitted = true;
        disableAnswerButtons();

        Question currentQuestion =
                questions.get(currentQuestionIndex);

        highlightCorrectAnswer(
                currentQuestion.getCorrectAnswer()
        );

        timerLabel.setText("Time expired");

        feedbackLabel.setText(
                "Time expired. Correct answer: "
                        + currentQuestion.getCorrectAnswer()
        );

        feedbackLabel.setForeground(INCORRECT_COLOR);

        moveAfterDelay();
    }

    private void highlightCorrectAnswer(
            String correctAnswer
    ) {
        for (JButton button : answerButtons) {
            if (button.getText().equals(correctAnswer)) {
                button.setBackground(CORRECT_COLOR);
                button.setForeground(Color.WHITE);
            }
        }
    }

    private void moveAfterDelay() {
        Timer nextQuestionTimer = new Timer(
                1800,
                event -> {
                    ((Timer) event.getSource()).stop();
                    currentQuestionIndex++;
                    displayQuestion();
                }
        );

        nextQuestionTimer.setRepeats(false);
        nextQuestionTimer.start();
    }

    private void startTimer() {
        stopTimer();

        secondsRemaining =
                settings.getSecondsPerQuestion();

        updateTimerDisplay();

        countdownTimer = new Timer(
                1000,
                event -> {
                    secondsRemaining--;
                    updateTimerDisplay();

                    if (secondsRemaining <= 0) {
                        stopTimer();
                        handleTimeExpired();
                    }
                }
        );

        countdownTimer.start();
    }

    private void updateTimerDisplay() {
        timerLabel.setText(
                "Time: " + secondsRemaining + "s"
        );

        timerProgressBar.setValue(secondsRemaining);

        timerProgressBar.setString(
                secondsRemaining + " seconds"
        );

        int totalSeconds =
                settings.getSecondsPerQuestion();

        double percentageRemaining =
                totalSeconds == 0
                        ? 0
                        : secondsRemaining
                        / (double) totalSeconds;

        if (percentageRemaining > 0.50) {
            timerProgressBar.setForeground(
                    TIMER_SAFE_COLOR
            );

        } else if (percentageRemaining > 0.25) {
            timerProgressBar.setForeground(
                    TIMER_WARNING_COLOR
            );

        } else {
            timerProgressBar.setForeground(
                    TIMER_DANGER_COLOR
            );
        }
    }

    private void resetButtonAppearance(
            JButton button
    ) {
        button.setBackground(DEFAULT_BUTTON_COLOR);
        button.setForeground(Color.BLACK);
        button.setEnabled(true);
    }

    private void disableAnswerButtons() {
        for (JButton button : answerButtons) {
            button.setEnabled(false);
        }
    }

    private void stopTimer() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
    }

    private void finishGame() {
        stopTimer();

        gameFinishedAction.accept(
                score,
                questions.size()
        );
    }

    private String createWrappedText(
            String text,
            int width
    ) {
        String escapedText = text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");

        return """
                <html>
                    <div style='width:%dpx; text-align:center;'>
                        %s
                    </div>
                </html>
                """.formatted(width, escapedText);
    }
}