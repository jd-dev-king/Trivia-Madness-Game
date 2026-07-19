package com.jeremiah.triviagame.ui;

import com.jeremiah.triviagame.database.ScoreRepository;
import com.jeremiah.triviagame.model.Score;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
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
        setBorder(
                BorderFactory.createEmptyBorder(
                        25,
                        30,
                        25,
                        30
                )
        );

        JLabel titleLabel = new JLabel(
                "Saved Scores",
                SwingConstants.CENTER
        );

        titleLabel.setFont(
                new Font("SansSerif", Font.BOLD, 30)
        );

        add(titleLabel, BorderLayout.NORTH);

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

        scoreTable = new JTable(tableModel);
        scoreTable.setFont(
                new Font("SansSerif", Font.PLAIN, 14)
        );
        scoreTable.setRowHeight(28);
        scoreTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 14)
        );
        scoreTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane =
                new JScrollPane(scoreTable);

        add(scrollPane, BorderLayout.CENTER);

        JButton homeButton =
                new JButton("Back to Home");

        JButton resetButton =
                new JButton("Reset All Scores");

        homeButton.setFont(
                new Font("SansSerif", Font.BOLD, 16)
        );

        resetButton.setFont(
                new Font("SansSerif", Font.BOLD, 16)
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

        buttonPanel.add(homeButton);
        buttonPanel.add(resetButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadScores();
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
}