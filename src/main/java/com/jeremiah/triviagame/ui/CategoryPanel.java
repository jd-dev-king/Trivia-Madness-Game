package com.jeremiah.triviagame.ui;

import com.jeremiah.triviagame.model.Category;
import com.jeremiah.triviagame.model.GameSettings;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CategoryPanel extends JPanel {

    private final String playerName;
    private final List<CategoryOption> categoryOptions;

    private final JComboBox<String> difficultyComboBox;
    private final JSpinner questionCountSpinner;
    private final JSpinner timerSpinner;

    public CategoryPanel(
            String playerName,
            Consumer<GameSettings> startGameAction,
            Runnable backAction
    ) {
        this.playerName = playerName;
        this.categoryOptions = new ArrayList<>();

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        JLabel titleLabel = new JLabel(
                "Choose Your Categories",
                SwingConstants.CENTER
        );

        titleLabel.setFont(
                new Font("SansSerif", Font.BOLD, 30)
        );

        JLabel playerLabel = new JLabel(
                "Player: " + playerName,
                SwingConstants.CENTER
        );

        playerLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 17)
        );

        JPanel headingPanel = new JPanel(
                new BorderLayout(0, 8)
        );

        headingPanel.add(titleLabel, BorderLayout.NORTH);
        headingPanel.add(playerLabel, BorderLayout.CENTER);

        add(headingPanel, BorderLayout.NORTH);

        JPanel categoriesPanel = new JPanel(
                new GridLayout(0, 2, 12, 12)
        );

        categoriesPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Select one or more categories"
                )
        );

        addCategory(categoriesPanel, 9, "General Knowledge");
        addCategory(categoriesPanel, 10, "Entertainment: Books");
        addCategory(categoriesPanel, 11, "Entertainment: Film");
        addCategory(categoriesPanel, 12, "Entertainment: Music");
        addCategory(categoriesPanel, 14, "Entertainment: Television");
        addCategory(categoriesPanel, 15, "Entertainment: Video Games");
        addCategory(categoriesPanel, 17, "Science and Nature");
        addCategory(categoriesPanel, 18, "Science: Computers");
        addCategory(categoriesPanel, 19, "Science: Mathematics");
        addCategory(categoriesPanel, 20, "Mythology");
        addCategory(categoriesPanel, 21, "Sports");
        addCategory(categoriesPanel, 22, "Geography");
        addCategory(categoriesPanel, 23, "History");
        addCategory(categoriesPanel, 24, "Politics");
        addCategory(categoriesPanel, 25, "Art");
        addCategory(categoriesPanel, 27, "Animals");
        addCategory(categoriesPanel, 28, "Vehicles");

        JScrollPane categoryScrollPane =
                new JScrollPane(categoriesPanel);

        categoryScrollPane.setBorder(null);
        categoryScrollPane.getVerticalScrollBar()
                .setUnitIncrement(12);

        add(categoryScrollPane, BorderLayout.CENTER);

        difficultyComboBox = new JComboBox<>(
                new String[]{
                        "Any Difficulty",
                        "Easy",
                        "Medium",
                        "Hard"
                }
        );

        questionCountSpinner = new JSpinner(
                new SpinnerNumberModel(
                        10,
                        5,
                        30,
                        5
                )
        );

        timerSpinner = new JSpinner(
                new SpinnerNumberModel(
                        20,
                        10,
                        60,
                        5
                )
        );

        JPanel optionsPanel = new JPanel(
                new GridLayout(2, 3, 12, 8)
        );

        optionsPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Game Options"
                )
        );

        optionsPanel.add(new JLabel("Difficulty"));
        optionsPanel.add(new JLabel("Questions"));
        optionsPanel.add(new JLabel("Seconds per question"));

        optionsPanel.add(difficultyComboBox);
        optionsPanel.add(questionCountSpinner);
        optionsPanel.add(timerSpinner);

        JButton backButton = new JButton("Back");
        JButton startButton = new JButton("Start Game");

        backButton.setFont(
                new Font("SansSerif", Font.BOLD, 16)
        );

        startButton.setFont(
                new Font("SansSerif", Font.BOLD, 16)
        );

        backButton.addActionListener(event ->
                backAction.run()
        );

        startButton.addActionListener(event ->
                createGameSettings(startGameAction)
        );

        JPanel buttonPanel = new JPanel(
                new GridLayout(1, 2, 15, 0)
        );

        buttonPanel.add(backButton);
        buttonPanel.add(startButton);

        JPanel bottomPanel = new JPanel(
                new BorderLayout(10, 15)
        );

        bottomPanel.add(optionsPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addCategory(
            JPanel panel,
            int categoryId,
            String categoryName
    ) {
        Category category = new Category(
                categoryId,
                categoryName
        );

        JCheckBox checkBox = new JCheckBox(categoryName);

        checkBox.setFont(
                new Font("SansSerif", Font.PLAIN, 15)
        );

        categoryOptions.add(
                new CategoryOption(category, checkBox)
        );

        panel.add(checkBox);
    }

    private void createGameSettings(
            Consumer<GameSettings> startGameAction
    ) {
        List<Category> selectedCategories =
                new ArrayList<>();

        for (CategoryOption option : categoryOptions) {
            if (option.checkBox().isSelected()) {
                selectedCategories.add(option.category());
            }
        }

        if (selectedCategories.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Select at least one trivia category.",
                    "Category Required",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String selectedDifficulty =
                difficultyComboBox.getSelectedItem().toString();

        String difficulty = switch (selectedDifficulty) {
            case "Easy" -> "easy";
            case "Medium" -> "medium";
            case "Hard" -> "hard";
            default -> "";
        };

        int questionCount =
                (Integer) questionCountSpinner.getValue();

        int secondsPerQuestion =
                (Integer) timerSpinner.getValue();

        GameSettings settings = new GameSettings(
                playerName,
                selectedCategories,
                difficulty,
                questionCount,
                secondsPerQuestion
        );

        startGameAction.accept(settings);
    }

    private record CategoryOption(
            Category category,
            JCheckBox checkBox
    ) {
    }
}
