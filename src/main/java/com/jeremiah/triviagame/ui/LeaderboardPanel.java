package com.jeremiah.triviagame.ui;

import com.jeremiah.triviagame.database.ScoreRepository;
import com.jeremiah.triviagame.model.Score;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

public class LeaderboardPanel extends JPanel {

    private final ScoreRepository scoreRepository;
    private final DefaultTableModel tableModel;
    private final JTable scoreTable;

    public LeaderboardPanel(
            ScoreRepository scoreRepository,
            Runnable homeAction
    ) {
        this.scoreRepository = scoreRepository;

        setLayout(new BorderLayout(20, 20));
        setBackground(Theme.BACKGROUND);

        setBorder(
                BorderFactory.createEmptyBorder(
                        30,
                        40,
                        30,
                        40
                )
        );

        add(createHeaderPanel(), BorderLayout.NORTH);

        String[] columns = {
                "Rank",
                "Player",
                "Score",
                "Questions",
                "Percentage",
                "Category",
                "Difficulty",
                "Played"
        };

        tableModel = new DefaultTableModel(
                columns,
                0
        ) {
            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {
                return false;
            }
        };

        scoreTable = createScoreTable();

        JScrollPane scrollPane =
                createTableScrollPane();

        add(scrollPane, BorderLayout.CENTER);
        add(
                createButtonPanel(homeAction),
                BorderLayout.SOUTH
        );

        loadScores();
    }

    private JPanel createHeaderPanel() {
        JLabel titleLabel = new JLabel(
                "LEADERBOARD",
                SwingConstants.CENTER
        );

        titleLabel.setFont(Theme.TITLE_FONT);
        titleLabel.setForeground(Theme.ACCENT);

        JLabel subtitleLabel = new JLabel(
                "Top saved scores from Trivia Madness",
                SwingConstants.CENTER
        );

        subtitleLabel.setFont(Theme.BODY_FONT);
        subtitleLabel.setForeground(Theme.MUTED_TEXT);

        JPanel headerPanel = new JPanel(
                new GridLayout(2, 1, 0, 8)
        );

        headerPanel.setOpaque(false);
        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);

        return headerPanel;
    }

    private JTable createScoreTable() {
        JTable table = new JTable(tableModel);

        table.setFont(Theme.BODY_FONT);
        table.setForeground(Theme.TEXT);
        table.setBackground(Theme.PANEL);
        table.setSelectionBackground(Theme.PRIMARY);
        table.setSelectionForeground(Color.WHITE);

        table.setGridColor(Theme.PANEL_LIGHT);
        table.setRowHeight(34);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(
                new Dimension(0, 1)
        );

        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();

        header.setFont(Theme.BUTTON_FONT);
        header.setForeground(Theme.TEXT);
        header.setBackground(Theme.PANEL_LIGHT);
        header.setReorderingAllowed(false);
        header.setPreferredSize(
                new Dimension(0, 38)
        );

        DefaultTableCellRenderer cellRenderer =
                new DefaultTableCellRenderer() {

                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table,
                            Object value,
                            boolean isSelected,
                            boolean hasFocus,
                            int row,
                            int column
                    ) {
                        JLabel label =
                                (JLabel) super
                                        .getTableCellRendererComponent(
                                                table,
                                                value,
                                                isSelected,
                                                hasFocus,
                                                row,
                                                column
                                        );

                        label.setFont(Theme.BODY_FONT);
                        label.setBorder(
                                BorderFactory.createEmptyBorder(
                                        4,
                                        10,
                                        4,
                                        10
                                )
                        );

                        if (isSelected) {
                            label.setBackground(Theme.PRIMARY);
                            label.setForeground(Color.WHITE);
                        } else {
                            label.setBackground(
                                    row % 2 == 0
                                            ? Theme.PANEL
                                            : Theme.PANEL_LIGHT
                            );

                            label.setForeground(Theme.TEXT);
                        }

                        if (column == 0
                                || column == 2
                                || column == 3
                                || column == 4) {

                            label.setHorizontalAlignment(
                                    SwingConstants.CENTER
                            );

                        } else {
                            label.setHorizontalAlignment(
                                    SwingConstants.LEFT
                            );
                        }

                        if (column == 0 && !isSelected) {
                            label.setForeground(
                                    rankColor(row + 1)
                            );
                        }

                        return label;
                    }
                };

        for (
                int columnIndex = 0;
                columnIndex < table.getColumnCount();
                columnIndex++
        ) {
            table.getColumnModel()
                    .getColumn(columnIndex)
                    .setCellRenderer(cellRenderer);
        }

        table.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(55);

        table.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(120);

        table.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(70);

        table.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(90);

        table.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(100);

        table.getColumnModel()
                .getColumn(5)
                .setPreferredWidth(260);

        table.getColumnModel()
                .getColumn(6)
                .setPreferredWidth(100);

        table.getColumnModel()
                .getColumn(7)
                .setPreferredWidth(150);

        return table;
    }

    private JScrollPane createTableScrollPane() {
        RoundedPanel tableCard = new RoundedPanel(
                new BorderLayout(),
                28
        );

        tableCard.setBackground(Theme.PANEL);

        tableCard.setBorder(
                BorderFactory.createEmptyBorder(
                        18,
                        18,
                        18,
                        18
                )
        );

        JScrollPane scrollPane =
                new JScrollPane(scoreTable);

        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(
                Theme.PANEL
        );

        tableCard.add(
                scrollPane,
                BorderLayout.CENTER
        );

        JScrollPane outerScrollPane =
                new JScrollPane(tableCard);

        outerScrollPane.setBorder(null);
        outerScrollPane.setOpaque(false);
        outerScrollPane.getViewport().setOpaque(false);

        return outerScrollPane;
    }

    private JPanel createButtonPanel(
            Runnable homeAction
    ) {
        RoundedButton homeButton =
                new RoundedButton(
                        "BACK TO HOME",
                        Theme.PANEL_LIGHT,
                        Theme.PRIMARY,
                        Theme.ACCENT
                );

        RoundedButton resetButton =
                new RoundedButton(
                        "RESET ALL SCORES",
                        Theme.ERROR,
                        new Color(255, 110, 110),
                        Theme.WARNING
                );

        homeButton.addActionListener(event ->
                homeAction.run()
        );

        resetButton.addActionListener(event ->
                resetScores()
        );

        JPanel buttonPanel = new JPanel(
                new GridLayout(1, 2, 15, 0)
        );

        buttonPanel.setOpaque(false);
        buttonPanel.add(homeButton);
        buttonPanel.add(resetButton);

        return buttonPanel;
    }

    private void loadScores() {
        tableModel.setRowCount(0);

        List<Score> scores =
                scoreRepository.getAllScores();

        int rank = 1;

        for (Score score : scores) {
            tableModel.addRow(
                    new Object[]{
                            rank,
                            score.getPlayerName(),
                            score.getScore(),
                            score.getTotalQuestions(),
                            String.format(
                                    "%.1f%%",
                                    score.getPercentage()
                            ),
                            score.getCategory(),
                            formatDifficulty(
                                    score.getDifficulty()
                            ),
                            score.getPlayedAt()
                    }
            );

            rank++;
        }
    }

    private void resetScores() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Delete every saved score?\n"
                        + "This action cannot be undone.",
                "Reset Scores",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            scoreRepository.deleteAllScores();
            loadScores();

            JOptionPane.showMessageDialog(
                    this,
                    "All saved scores were deleted.",
                    "Scores Reset",
                    JOptionPane.INFORMATION_MESSAGE
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

    private String formatDifficulty(
            String difficulty
    ) {
        if (difficulty == null
                || difficulty.isBlank()) {
            return "Any";
        }

        return difficulty.substring(0, 1)
                .toUpperCase()
                + difficulty.substring(1);
    }

    private Color rankColor(int rank) {
    return switch (rank) {
        case 1 -> new Color(255, 215, 0);   // Gold
        case 2 -> new Color(220, 230, 240); // Bright silver
        case 3 -> new Color(205, 127, 50);  // Bronze
        default -> Theme.MUTED_TEXT;
    };
  }
}
