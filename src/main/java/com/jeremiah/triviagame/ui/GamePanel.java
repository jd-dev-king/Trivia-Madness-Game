package com.jeremiah.triviagame.ui;

import com.jeremiah.triviagame.model.GameSettings;
import com.jeremiah.triviagame.model.Question;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class GamePanel extends JPanel {

    private final GameSettings settings;
    private final List<Question> questions;
    private final BiConsumer<Integer, Integer> gameFinishedAction;

    private final JLabel progressLabel;
    private final JLabel categoryLabel;
    private final JLabel scoreLabel;
    private final JLabel timerLabel;
    private final JLabel questionLabel;
    private final JLabel feedbackLabel;

    private JProgressBar timerProgressBar;
    private final List<RoundedButton> answerButtons;

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
        setBackground(Theme.BACKGROUND);

        setBorder(
                BorderFactory.createEmptyBorder(
                        25,
                        40,
                        30,
                        40
                )
        );

        progressLabel = createInfoLabel(
                "",
                SwingConstants.LEFT
        );

        categoryLabel = createInfoLabel(
                "",
                SwingConstants.CENTER
        );

        scoreLabel = createInfoLabel(
                "",
                SwingConstants.CENTER
        );

        timerLabel = createInfoLabel(
                "",
                SwingConstants.RIGHT
        );

        add(createTopPanel(), BorderLayout.NORTH);

        questionLabel = new JLabel(
                "",
                SwingConstants.CENTER
        );

        questionLabel.setFont(
                new Font("SansSerif", Font.BOLD, 24)
        );

        questionLabel.setForeground(Theme.TEXT);

        feedbackLabel = new JLabel(
                " ",
                SwingConstants.CENTER
        );

        feedbackLabel.setFont(Theme.BUTTON_FONT);
        feedbackLabel.setForeground(Theme.MUTED_TEXT);

        add(createQuestionArea(), BorderLayout.CENTER);
        add(createAnswerArea(), BorderLayout.SOUTH);

        displayQuestion();
    }

    private JLabel createInfoLabel(
            String text,
            int alignment
    ) {
        JLabel label = new JLabel(
                text,
                alignment
        );

        label.setFont(Theme.BUTTON_FONT);
        label.setForeground(Theme.TEXT);

        return label;
    }

    private JPanel createTopPanel() {
        RoundedPanel infoCard = new RoundedPanel(
                new BorderLayout(0, 14),
                24
        );

        infoCard.setBackground(Theme.PANEL);

        infoCard.setBorder(
                BorderFactory.createEmptyBorder(
                        18,
                        20,
                        18,
                        20
                )
        );

        JPanel informationPanel = new JPanel(
                new GridLayout(1, 4, 15, 0)
        );

        informationPanel.setOpaque(false);

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
        timerProgressBar.setForeground(Theme.SUCCESS);
        timerProgressBar.setBackground(Theme.PANEL_LIGHT);
        timerProgressBar.setBorderPainted(false);

        timerProgressBar.setPreferredSize(
                new Dimension(0, 24)
        );

        infoCard.add(
                informationPanel,
                BorderLayout.NORTH
        );

        infoCard.add(
                timerProgressBar,
                BorderLayout.SOUTH
        );

        return infoCard;
    }

    private JPanel createQuestionArea() {
        RoundedPanel questionCard = new RoundedPanel(
                new BorderLayout(0, 20),
                30
        );

        questionCard.setBackground(Theme.PANEL);

        questionCard.setBorder(
                BorderFactory.createEmptyBorder(
                        35,
                        40,
                        30,
                        40
                )
        );

        JLabel questionHeader = new JLabel(
                "QUESTION",
                SwingConstants.CENTER
        );

        questionHeader.setFont(Theme.HEADING_FONT);
        questionHeader.setForeground(Theme.ACCENT);

        questionCard.add(
                questionHeader,
                BorderLayout.NORTH
        );

        questionCard.add(
                questionLabel,
                BorderLayout.CENTER
        );

        questionCard.add(
                feedbackLabel,
                BorderLayout.SOUTH
        );

        JPanel wrapper = new JPanel(
                new BorderLayout()
        );

        wrapper.setOpaque(false);

        wrapper.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        0,
                        10,
                        0
                )
        );

        wrapper.add(
                questionCard,
                BorderLayout.CENTER
        );

        return wrapper;
    }

    private JPanel createAnswerArea() {
        JPanel answerGrid = new JPanel(
                new GridLayout(2, 2, 15, 15)
        );

        answerGrid.setOpaque(false);

        for (int index = 0; index < 4; index++) {
            RoundedButton answerButton =
                    new RoundedButton(
                            "",
                            Theme.PANEL_LIGHT,
                            Theme.PRIMARY,
                            Theme.ACCENT
                    );

            answerButton.setFont(
                    new Font(
                            "SansSerif",
                            Font.BOLD,
                            16
                    )
            );

            answerButton.setPreferredSize(
                    new Dimension(0, 72)
            );

            answerButton.addActionListener(event ->
                    submitAnswer(answerButton)
            );

            answerButtons.add(answerButton);
            answerGrid.add(answerButton);
        }

        return answerGrid;
    }

    private void displayQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            finishGame();
            return;
        }

        answerSubmitted = false;

        feedbackLabel.setText(" ");
        feedbackLabel.setForeground(
                Theme.MUTED_TEXT
        );

        Question currentQuestion =
                questions.get(currentQuestionIndex);

        progressLabel.setText(
                "QUESTION "
                        + (currentQuestionIndex + 1)
                        + " / "
                        + questions.size()
        );

        categoryLabel.setText(
                currentQuestion.getCategory()
        );

        scoreLabel.setText(
                "SCORE: " + score
        );

        questionLabel.setText(
                createWrappedText(
                        currentQuestion.getQuestionText(),
                        650
                )
        );

        List<String> answers =
                currentQuestion.getAnswers();

        for (
                int index = 0;
                index < answerButtons.size();
                index++
        ) {
            RoundedButton button =
                    answerButtons.get(index);

            resetAnswerButton(button);

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

    private void submitAnswer(
            RoundedButton selectedButton
    ) {
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
                currentQuestion.isCorrect(
                        selectedAnswer
                );

        if (correct) {
            score++;

            scoreLabel.setText(
                    "SCORE: " + score
            );

            selectedButton.setButtonColors(
                    Theme.SUCCESS,
                    Theme.SUCCESS,
                    Theme.SUCCESS
            );

            selectedButton.setForeground(Color.WHITE);

            feedbackLabel.setText("CORRECT!");
            feedbackLabel.setForeground(
                    Theme.SUCCESS
            );

        } else {
            selectedButton.setButtonColors(
                    Theme.ERROR,
                    Theme.ERROR,
                    Theme.ERROR
            );

            selectedButton.setForeground(Color.WHITE);

            highlightCorrectAnswer(
                    currentQuestion.getCorrectAnswer()
            );

            feedbackLabel.setText(
                    "Incorrect — correct answer: "
                            + currentQuestion.getCorrectAnswer()
            );

            feedbackLabel.setForeground(
                    Theme.ERROR
            );
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

        timerLabel.setText("TIME EXPIRED");

        feedbackLabel.setText(
                "Time expired — correct answer: "
                        + currentQuestion.getCorrectAnswer()
        );

        feedbackLabel.setForeground(
                Theme.ERROR
        );

        moveAfterDelay();
    }

    private void highlightCorrectAnswer(
            String correctAnswer
    ) {
        for (RoundedButton button : answerButtons) {
            if (button.getText().equals(correctAnswer)) {
                button.setButtonColors(
                        Theme.SUCCESS,
                        Theme.SUCCESS,
                        Theme.SUCCESS
                );

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
                "TIME: "
                        + secondsRemaining
                        + "s"
        );

        timerProgressBar.setValue(
                secondsRemaining
        );

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
                    Theme.SUCCESS
            );

        } else if (percentageRemaining > 0.25) {
            timerProgressBar.setForeground(
                    Theme.WARNING
            );

        } else {
            timerProgressBar.setForeground(
                    Theme.ERROR
            );
        }
    }

    private void resetAnswerButton(
            RoundedButton button
    ) {
        button.setButtonColors(
                Theme.PANEL_LIGHT,
                Theme.PRIMARY,
                Theme.ACCENT
        );

        button.setForeground(Theme.TEXT);
        button.setEnabled(true);
    }

    private void disableAnswerButtons() {
        for (RoundedButton button : answerButtons) {
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
                """.formatted(
                width,
                escapedText
        );
    }
}
