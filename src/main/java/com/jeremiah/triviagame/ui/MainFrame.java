package com.jeremiah.triviagame.ui;

import com.jeremiah.triviagame.api.TriviaApiClient;
import com.jeremiah.triviagame.database.ScoreRepository;
import com.jeremiah.triviagame.model.Category;
import com.jeremiah.triviagame.model.GameSettings;
import com.jeremiah.triviagame.model.Question;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.stream.Collectors;

public class MainFrame extends JFrame {

    private static final String WELCOME_SCREEN =
            "welcome";

    private static final String CATEGORY_SCREEN =
            "categories";

    private static final String LOADING_SCREEN =
            "loading";

    private static final String GAME_SCREEN =
            "game";

    private static final String RESULTS_SCREEN =
            "results";

    private static final String LEADERBOARD_SCREEN =
            "leaderboard";

    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final TriviaApiClient triviaApiClient;
    private final ScoreRepository scoreRepository;

    private GameSettings currentSettings;

    public MainFrame() {
        setTitle("Trivia Challenge");

        setDefaultCloseOperation(
                JFrame.DO_NOTHING_ON_CLOSE
        );

        setSize(1000, 720);

        setMinimumSize(
                new Dimension(800, 620)
        );

        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        triviaApiClient = new TriviaApiClient();
        scoreRepository = new ScoreRepository();

        setContentPane(cardPanel);

        addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(
                            WindowEvent event
                    ) {
                        confirmExit();
                    }
                }
        );

        showWelcomeScreen();
    }

    private void showWelcomeScreen() {
        WelcomePanel welcomePanel =
                new WelcomePanel(
                        this::showCategoryScreen,
                        this::showLeaderboardScreen
                );

        replaceScreen(
                WELCOME_SCREEN,
                welcomePanel
        );
    }

    private void showCategoryScreen(
            String playerName
    ) {
        CategoryPanel categoryPanel =
                new CategoryPanel(
                        playerName,
                        this::loadGame,
                        this::showWelcomeScreen
                );

        replaceScreen(
                CATEGORY_SCREEN,
                categoryPanel
        );
    }

    private void loadGame(
            GameSettings settings
    ) {
        currentSettings = settings;

        showLoadingScreen();

        SwingWorker<List<Question>, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected List<Question> doInBackground()
                            throws Exception {

                        return triviaApiClient.fetchQuestions(
                                settings
                        );
                    }

                    @Override
                    protected void done() {
                        try {
                            List<Question> questions =
                                    get();

                            if (questions.isEmpty()) {
                                throw new IllegalStateException(
                                        "No questions were returned."
                                );
                            }

                            showGameScreen(
                                    settings,
                                    questions
                            );

                        } catch (Exception exception) {
                            showApiError(exception);

                            showCategoryScreen(
                                    settings.getPlayerName()
                            );
                        }
                    }
                };

        worker.execute();
    }

    private void showLoadingScreen() {
        JPanel loadingPanel = new JPanel(
                new BorderLayout(20, 20)
        );

        loadingPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        100,
                        160,
                        250,
                        160
                )
        );

        JLabel loadingLabel = new JLabel(
                "Downloading trivia questions...",
                SwingConstants.CENTER
        );

        loadingLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        23
                )
        );

        JProgressBar progressBar =
                new JProgressBar();

        progressBar.setIndeterminate(true);

        loadingPanel.add(
                loadingLabel,
                BorderLayout.CENTER
        );

        loadingPanel.add(
                progressBar,
                BorderLayout.SOUTH
        );

        replaceScreen(
                LOADING_SCREEN,
                loadingPanel
        );
    }

    private void showGameScreen(
            GameSettings settings,
            List<Question> questions
    ) {
        GamePanel gamePanel = new GamePanel(
                settings,
                questions,
                this::finishAndSaveGame
        );

        replaceScreen(
                GAME_SCREEN,
                gamePanel
        );
    }

    private void finishAndSaveGame(
            int score,
            int totalQuestions
    ) {
        saveCurrentScore(
                score,
                totalQuestions
        );

        showResultsScreen(
                score,
                totalQuestions
        );
    }

    private void saveCurrentScore(
            int score,
            int totalQuestions
    ) {
        if (currentSettings == null) {
            return;
        }

        String categories =
                currentSettings.getCategories()
                        .stream()
                        .map(Category::getName)
                        .collect(
                                Collectors.joining(", ")
                        );

        String difficulty =
                currentSettings.getDifficulty();

        if (difficulty == null
                || difficulty.isBlank()) {
            difficulty = "any";
        }

        try {
            scoreRepository.saveScore(
                    currentSettings.getPlayerName(),
                    score,
                    totalQuestions,
                    categories,
                    difficulty
            );

        } catch (IllegalStateException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "The game finished, but the score "
                            + "could not be saved.\n"
                            + exception.getMessage(),
                    "Score Save Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void showResultsScreen(
            int score,
            int totalQuestions
    ) {
        JPanel resultsPanel = new JPanel(
                new BorderLayout(20, 20)
        );

        resultsPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        50,
                        70,
                        50,
                        70
                )
        );

        double percentage =
                totalQuestions == 0
                        ? 0
                        : score * 100.0
                        / totalQuestions;

        String performanceMessage =
                getPerformanceMessage(percentage);

        String playerName =
                currentSettings == null
                        ? "Player"
                        : currentSettings.getPlayerName();

        String message = """
                <html>
                    <div style='text-align:center;'>
                        <h1>Game Complete!</h1>
                        <h2>%s</h2>
                        <p>You scored %d out of %d.</p>
                        <p>Final percentage: %.1f%%</p>
                        <h3>%s</h3>
                        <p>Your score was saved.</p>
                    </div>
                </html>
                """.formatted(
                playerName,
                score,
                totalQuestions,
                percentage,
                performanceMessage
        );

        JLabel resultsLabel = new JLabel(
                message,
                SwingConstants.CENTER
        );

        resultsLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        20
                )
        );

        JButton playAgainButton =
                new JButton("Play Again");

        JButton leaderboardButton =
                new JButton("View Scores");

        JButton homeButton =
                new JButton("Change Player");

        playAgainButton.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        16
                )
        );

        leaderboardButton.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        16
                )
        );

        homeButton.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        16
                )
        );

        playAgainButton.addActionListener(event -> {
            if (currentSettings != null) {
                showCategoryScreen(
                        currentSettings.getPlayerName()
                );
            } else {
                showWelcomeScreen();
            }
        });

        leaderboardButton.addActionListener(event ->
                showLeaderboardScreen()
        );

        homeButton.addActionListener(event ->
                showWelcomeScreen()
        );

        JPanel buttonPanel = new JPanel(
                new GridLayout(1, 3, 15, 0)
        );

        buttonPanel.add(playAgainButton);
        buttonPanel.add(leaderboardButton);
        buttonPanel.add(homeButton);

        resultsPanel.add(
                resultsLabel,
                BorderLayout.CENTER
        );

        resultsPanel.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        replaceScreen(
                RESULTS_SCREEN,
                resultsPanel
        );
    }

    private String getPerformanceMessage(
            double percentage
    ) {
        if (percentage >= 90) {
            return "Outstanding trivia performance!";
        }

        if (percentage >= 75) {
            return "Great job!";
        }

        if (percentage >= 60) {
            return "Good effort!";
        }

        if (percentage >= 40) {
            return "Keep practicing!";
        }

        return "Try again and improve your score!";
    }

    private void showLeaderboardScreen() {
        try {
            LeaderboardPanel leaderboardPanel =
                    new LeaderboardPanel(
                            scoreRepository,
                            this::showWelcomeScreen
                    );

            replaceScreen(
                    LEADERBOARD_SCREEN,
                    leaderboardPanel
            );

        } catch (IllegalStateException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void showApiError(
            Exception exception
    ) {
        Throwable cause = exception.getCause();

        String message;

        if (cause != null
                && cause.getMessage() != null) {

            message = cause.getMessage();

        } else if (exception.getMessage() != null) {

            message = exception.getMessage();

        } else {
            message =
                    "An unknown error occurred while "
                            + "downloading questions.";
        }

        JOptionPane.showMessageDialog(
                this,
                message,
                "Unable to Load Questions",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void confirmExit() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit Trivia Challenge?",
                "Exit Trivia Challenge",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    private void replaceScreen(
            String screenName,
            JPanel panel
    ) {
        removeExistingScreen(screenName);

        panel.setName(screenName);

        cardPanel.add(
                panel,
                screenName
        );

        cardLayout.show(
                cardPanel,
                screenName
        );

        cardPanel.revalidate();
        cardPanel.repaint();
    }

    private void removeExistingScreen(
            String screenName
    ) {
        for (java.awt.Component component :
                cardPanel.getComponents()) {

            if (screenName.equals(
                    component.getName()
            )) {
                cardPanel.remove(component);
                break;
            }
        }
    }
}