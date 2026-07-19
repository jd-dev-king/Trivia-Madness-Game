package com.jeremiah.triviagame.ui;

import com.jeremiah.triviagame.model.Category;
import com.jeremiah.triviagame.model.GameSettings;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
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
        setBackground(Theme.BACKGROUND);

        setBorder(
                BorderFactory.createEmptyBorder(
                        25,
                        40,
                        25,
                        40
                )
        );

        add(createHeaderPanel(), BorderLayout.NORTH);

        JScrollPane categoryScrollPane =
                createCategoryScrollPane();

        add(categoryScrollPane, BorderLayout.CENTER);

        difficultyComboBox = createDifficultyComboBox();

        questionCountSpinner = createSpinner(
                10,
                5,
                30,
                5
        );

        timerSpinner = createSpinner(
                20,
                10,
                60,
                5
        );

        JPanel bottomPanel = createBottomPanel(
                startGameAction,
                backAction
        );

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JLabel titleLabel = new JLabel(
                "BUILD YOUR CHALLENGE",
                SwingConstants.CENTER
        );

        titleLabel.setFont(Theme.TITLE_FONT);
        titleLabel.setForeground(Theme.ACCENT);

        JLabel playerLabel = new JLabel(
                "PLAYER: " + playerName.toUpperCase(),
                SwingConstants.CENTER
        );

        playerLabel.setFont(Theme.BUTTON_FONT);
        playerLabel.setForeground(Theme.MUTED_TEXT);

        JLabel instructionLabel = new JLabel(
                "Choose one or more categories and configure your game.",
                SwingConstants.CENTER
        );

        instructionLabel.setFont(Theme.BODY_FONT);
        instructionLabel.setForeground(Theme.TEXT);

        JPanel headingPanel = new JPanel(
                new GridLayout(3, 1, 0, 7)
        );

        headingPanel.setOpaque(false);
        headingPanel.add(titleLabel);
        headingPanel.add(playerLabel);
        headingPanel.add(instructionLabel);

        return headingPanel;
    }

    private JScrollPane createCategoryScrollPane() {
        RoundedPanel categoryCard = new RoundedPanel(
                new BorderLayout(),
                28
        );

        categoryCard.setBackground(Theme.PANEL);

        categoryCard.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        22,
                        20,
                        22
                )
        );

        JLabel categoryTitle = new JLabel(
                "SELECT CATEGORIES",
                SwingConstants.LEFT
        );

        categoryTitle.setFont(Theme.HEADING_FONT);
        categoryTitle.setForeground(Theme.TEXT);

        JPanel categoryGrid = new JPanel(
                new GridLayout(0, 2, 12, 12)
        );

        categoryGrid.setOpaque(false);

        addCategory(categoryGrid, 9, "General Knowledge");
        addCategory(categoryGrid, 10, "Entertainment: Books");
        addCategory(categoryGrid, 11, "Entertainment: Film");
        addCategory(categoryGrid, 12, "Entertainment: Music");
        addCategory(categoryGrid, 14, "Entertainment: Television");
        addCategory(categoryGrid, 15, "Entertainment: Video Games");
        addCategory(categoryGrid, 17, "Science and Nature");
        addCategory(categoryGrid, 18, "Science: Computers");
        addCategory(categoryGrid, 19, "Science: Mathematics");
        addCategory(categoryGrid, 20, "Mythology");
        addCategory(categoryGrid, 21, "Sports");
        addCategory(categoryGrid, 22, "Geography");
        addCategory(categoryGrid, 23, "History");
        addCategory(categoryGrid, 24, "Politics");
        addCategory(categoryGrid, 25, "Art");
        addCategory(categoryGrid, 27, "Animals");
        addCategory(categoryGrid, 28, "Vehicles");

        categoryCard.add(
                categoryTitle,
                BorderLayout.NORTH
        );

        categoryCard.add(
                categoryGrid,
                BorderLayout.CENTER
        );

        JScrollPane scrollPane =
                new JScrollPane(categoryCard);

        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(14);

        scrollPane.getVerticalScrollBar()
                .setPreferredSize(new Dimension(12, 0));

        scrollPane.getVerticalScrollBar().setUI(
                new BasicScrollBarUI() {
                    @Override
                    protected void configureScrollBarColors() {
                        thumbColor = Theme.PRIMARY;
                        trackColor = Theme.BACKGROUND;
                    }

                    @Override
                    protected javax.swing.JButton createDecreaseButton(
                            int orientation
                    ) {
                        return createInvisibleButton();
                    }

                    @Override
                    protected javax.swing.JButton createIncreaseButton(
                            int orientation
                    ) {
                        return createInvisibleButton();
                    }

                    private javax.swing.JButton createInvisibleButton() {
                        javax.swing.JButton button =
                                new javax.swing.JButton();

                        button.setPreferredSize(
                                new Dimension(0, 0)
                        );

                        return button;
                    }
                }
        );

        return scrollPane;
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

        JCheckBox checkBox =
                new JCheckBox(categoryName);

        checkBox.setFont(Theme.BODY_FONT);
        checkBox.setForeground(Theme.TEXT);
        checkBox.setBackground(Theme.PANEL_LIGHT);
        checkBox.setFocusPainted(false);
        checkBox.setOpaque(true);

        checkBox.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Theme.PANEL_LIGHT,
                                2
                        ),
                        BorderFactory.createEmptyBorder(
                                10,
                                12,
                                10,
                                12
                        )
                )
        );

        checkBox.addItemListener(event -> {
            if (checkBox.isSelected()) {
                checkBox.setBackground(Theme.PRIMARY);
                checkBox.setForeground(Color.WHITE);
            } else {
                checkBox.setBackground(Theme.PANEL_LIGHT);
                checkBox.setForeground(Theme.TEXT);
            }
        });

        categoryOptions.add(
                new CategoryOption(
                        category,
                        checkBox
                )
        );

        panel.add(checkBox);
    }

    private JPanel createBottomPanel(
            Consumer<GameSettings> startGameAction,
            Runnable backAction
    ) {
        RoundedPanel optionsCard = new RoundedPanel(
                new GridBagLayout(),
                24
        );

        optionsCard.setBackground(Theme.PANEL);

        optionsCard.setBorder(
                BorderFactory.createEmptyBorder(
                        18,
                        22,
                        18,
                        22
                )
        );

        GridBagConstraints constraints =
                new GridBagConstraints();

        constraints.insets = new Insets(
                7,
                10,
                7,
                10
        );

        constraints.fill =
                GridBagConstraints.HORIZONTAL;

        constraints.weightx = 1;

        addOption(
                optionsCard,
                constraints,
                0,
                "DIFFICULTY",
                difficultyComboBox
        );

        addOption(
                optionsCard,
                constraints,
                1,
                "QUESTIONS",
                questionCountSpinner
        );

        addOption(
                optionsCard,
                constraints,
                2,
                "SECONDS PER QUESTION",
                timerSpinner
        );

        RoundedButton backButton =
                new RoundedButton(
                        "BACK",
                        Theme.PANEL_LIGHT,
                        Theme.PRIMARY,
                        Theme.ACCENT
                );

        RoundedButton startButton =
                new RoundedButton(
                        "START GAME"
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

        buttonPanel.setOpaque(false);
        buttonPanel.add(backButton);
        buttonPanel.add(startButton);

        JPanel bottomPanel = new JPanel(
                new BorderLayout(0, 15)
        );

        bottomPanel.setOpaque(false);
        bottomPanel.add(optionsCard, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        return bottomPanel;
    }

    private void addOption(
            JPanel panel,
            GridBagConstraints constraints,
            int column,
            String labelText,
            java.awt.Component component
    ) {
        JLabel label = new JLabel(
                labelText,
                SwingConstants.CENTER
        );

        label.setFont(Theme.BUTTON_FONT);
        label.setForeground(Theme.MUTED_TEXT);

        constraints.gridx = column;
        constraints.gridy = 0;

        panel.add(label, constraints);

        constraints.gridy = 1;

        panel.add(component, constraints);
    }

    private JComboBox<String> createDifficultyComboBox() {
    JComboBox<String> comboBox = new JComboBox<>(
            new String[]{
                    "Any Difficulty",
                    "Easy",
                    "Medium",
                    "Hard"
            }
    );

    comboBox.setFont(Theme.BODY_FONT);
    comboBox.setForeground(Theme.TEXT);
    comboBox.setBackground(Theme.PANEL_LIGHT);
    comboBox.setFocusable(false);
    comboBox.setOpaque(true);

    comboBox.setPreferredSize(
            new Dimension(200, 42)
    );

    /*
     * Prevents macOS from forcing the native white combo-box style.
     */
    comboBox.setUI(new BasicComboBoxUI());

    comboBox.setRenderer(
            new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                        JList<?> list,
                        Object value,
                        int index,
                        boolean isSelected,
                        boolean cellHasFocus
                ) {
                    JLabel label =
                            (JLabel) super.getListCellRendererComponent(
                                    list,
                                    value,
                                    index,
                                    isSelected,
                                    cellHasFocus
                            );

                    label.setFont(Theme.BODY_FONT);
                    label.setOpaque(true);

                    label.setBorder(
                            BorderFactory.createEmptyBorder(
                                    8,
                                    12,
                                    8,
                                    12
                            )
                    );

                    if (isSelected) {
                        label.setBackground(Theme.PRIMARY);
                        label.setForeground(Color.WHITE);
                    } else {
                        label.setBackground(Theme.PANEL_LIGHT);
                        label.setForeground(Theme.TEXT);
                    }

                    return label;
                }
            }
    );

    return comboBox;
}

    private JSpinner createSpinner(
            int initialValue,
            int minimum,
            int maximum,
            int step
    ) {
        JSpinner spinner = new JSpinner(
                new SpinnerNumberModel(
                        initialValue,
                        minimum,
                        maximum,
                        step
                )
        );

        spinner.setFont(Theme.BODY_FONT);

        spinner.setPreferredSize(
                new Dimension(160, 42)
        );

        JSpinner.DefaultEditor editor =
                (JSpinner.DefaultEditor) spinner.getEditor();

        editor.getTextField().setFont(Theme.BODY_FONT);
        editor.getTextField().setForeground(Theme.TEXT);
        editor.getTextField().setBackground(
                Theme.PANEL_LIGHT
        );

        editor.getTextField().setCaretColor(
                Theme.ACCENT
        );

        editor.getTextField().setHorizontalAlignment(
                SwingConstants.CENTER
        );

        return spinner;
    }

    private void createGameSettings(
            Consumer<GameSettings> startGameAction
    ) {
        List<Category> selectedCategories =
                new ArrayList<>();

        for (CategoryOption option : categoryOptions) {
            if (option.checkBox().isSelected()) {
                selectedCategories.add(
                        option.category()
                );
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
                difficultyComboBox
                        .getSelectedItem()
                        .toString();

        String difficulty =
                switch (selectedDifficulty) {
                    case "Easy" -> "easy";
                    case "Medium" -> "medium";
                    case "Hard" -> "hard";
                    default -> "";
                };

        int questionCount =
                (Integer) questionCountSpinner
                        .getValue();

        int secondsPerQuestion =
                (Integer) timerSpinner
                        .getValue();

        GameSettings settings =
                new GameSettings(
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
