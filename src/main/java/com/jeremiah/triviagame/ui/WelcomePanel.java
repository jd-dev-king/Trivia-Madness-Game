package com.jeremiah.triviagame.ui;


import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Consumer;

public class WelcomePanel extends JPanel {

    private final JTextField playerNameField;

    public WelcomePanel(
            Consumer<String> continueAction,
            Runnable leaderboardAction
    ) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JLabel titleLabel = new JLabel(
                "Trivia Challenge",
                SwingConstants.CENTER
        );

        titleLabel.setFont(
                new Font("SansSerif", Font.BOLD, 38)
        );

        JLabel subtitleLabel = new JLabel(
                "Test your knowledge across multiple categories",
                SwingConstants.CENTER
        );

        subtitleLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 17)
        );

        JPanel headingPanel = new JPanel(new BorderLayout(0, 10));
        headingPanel.add(titleLabel, BorderLayout.NORTH);
        headingPanel.add(subtitleLabel, BorderLayout.CENTER);

        add(headingPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Player name:");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 17));

        playerNameField = new JTextField();
        playerNameField.setFont(
                new Font("SansSerif", Font.PLAIN, 17)
        );
        playerNameField.setPreferredSize(
                new Dimension(300, 42)
        );

        JButton continueButton = new JButton("Continue");
        continueButton.setFont(
                new Font("SansSerif", Font.BOLD, 17)
        );
        continueButton.setPreferredSize(
                new Dimension(180, 45)
        );

        JButton leaderboardButton =
                new JButton("View Saved Scores");

        leaderboardButton.setFont(
                new Font("SansSerif", Font.BOLD, 17)
        );

        leaderboardButton.setPreferredSize(
                new Dimension(180, 45)
        );

        constraints.gridx = 0;
        constraints.gridy = 0;
        formPanel.add(nameLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        formPanel.add(playerNameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.insets = new Insets(25, 10, 10, 10);
        formPanel.add(continueButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.insets = new Insets(5, 10, 10, 10);
        formPanel.add(leaderboardButton, constraints);

        add(formPanel, BorderLayout.CENTER);

        continueButton.addActionListener(event ->
                submitPlayerName(continueAction)
        );

        playerNameField.addActionListener(event ->
                submitPlayerName(continueAction)
        );

        leaderboardButton.addActionListener(event ->
                leaderboardAction.run()
        );
    }

    private void submitPlayerName(Consumer<String> continueAction) {
        String playerName = playerNameField.getText().trim();

        if (playerName.isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter your name before continuing.",
                    "Player Name Required",
                    JOptionPane.WARNING_MESSAGE
            );

            playerNameField.requestFocus();
            return;
        }

        if (playerName.length() > 30) {
            JOptionPane.showMessageDialog(
                    this,
                    "Player names must be 30 characters or fewer.",
                    "Player Name Too Long",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        continueAction.accept(playerName);
    }
}
