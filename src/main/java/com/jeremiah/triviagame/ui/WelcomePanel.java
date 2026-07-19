package com.jeremiah.triviagame.ui;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.function.Consumer;

public class WelcomePanel extends JPanel {

    private JTextField playerNameField;

    public WelcomePanel(
            Consumer<String> continueAction,
            Runnable leaderboardAction
    ) {
        setLayout(new BorderLayout(25, 25));
        setBackground(Theme.BACKGROUND);

        setBorder(
                BorderFactory.createEmptyBorder(
                        35,
                        55,
                        30,
                        55
                )
        );

        JPanel headingPanel = createHeadingPanel();
        add(headingPanel, BorderLayout.NORTH);

        JPanel heroContentPanel = new JPanel(
                new GridLayout(1, 2, 35, 0)
        );

        heroContentPanel.setOpaque(false);

        JPanel imagePanel = createHeroImagePanel();
        RoundedPanel formCard = createFormCard(
                continueAction,
                leaderboardAction
        );

        heroContentPanel.add(imagePanel);
        heroContentPanel.add(formCard);

        add(heroContentPanel, BorderLayout.CENTER);

        JLabel footerLabel = new JLabel(
                "Powered by Open Trivia DB",
                SwingConstants.CENTER
        );

        footerLabel.setFont(Theme.BODY_FONT);
        footerLabel.setForeground(Theme.MUTED_TEXT);

        add(footerLabel, BorderLayout.SOUTH);
    }

    private JPanel createHeadingPanel() {
        JLabel titleLabel = new JLabel(
                "TRIVIA MADNESS",
                SwingConstants.CENTER
        );

        titleLabel.setFont(Theme.TITLE_FONT);
        titleLabel.setForeground(Theme.ACCENT);

        JLabel subtitleLabel = new JLabel(
                "Challenge your knowledge. Beat your best score.",
                SwingConstants.CENTER
        );

        subtitleLabel.setFont(Theme.BODY_FONT);
        subtitleLabel.setForeground(Theme.MUTED_TEXT);

        JPanel headingPanel = new JPanel(
                new BorderLayout(0, 10)
        );

        headingPanel.setOpaque(false);
        headingPanel.add(titleLabel, BorderLayout.NORTH);
        headingPanel.add(subtitleLabel, BorderLayout.CENTER);

        return headingPanel;
    }

    private JPanel createHeroImagePanel() {
        RoundedPanel imageCard = new RoundedPanel(
                new BorderLayout(),
                32
        );

        imageCard.setBackground(Theme.PANEL);

        imageCard.setBorder(
                BorderFactory.createEmptyBorder(
                        18,
                        18,
                        18,
                        18
                )
        );

        ImageIcon heroIcon = ImageUtils.loadScaledIcon(
                "/images/trivia-hero.png",
                390,
                360
        );

        JLabel heroImageLabel;

        if (heroIcon != null) {
            heroImageLabel = new JLabel(
                    heroIcon,
                    SwingConstants.CENTER
            );
        } else {
            heroImageLabel = new JLabel(
                    "<html><div style='text-align:center;'>"
                            + "<h2>Trivia Madness</h2>"
                            + "<p>Add trivia-hero.png to:</p>"
                            + "<p>src/main/resources/images</p>"
                            + "</div></html>",
                    SwingConstants.CENTER
            );

            heroImageLabel.setForeground(Theme.MUTED_TEXT);
            heroImageLabel.setFont(Theme.BODY_FONT);
        }

        imageCard.add(
                heroImageLabel,
                BorderLayout.CENTER
        );

        return imageCard;
    }

    private RoundedPanel createFormCard(
            Consumer<String> continueAction,
            Runnable leaderboardAction
    ) {
        RoundedPanel formCard = new RoundedPanel(
                new GridBagLayout(),
                32
        );

        formCard.setBackground(Theme.PANEL);

        formCard.setBorder(
                BorderFactory.createEmptyBorder(
                        40,
                        45,
                        40,
                        45
                )
        );

        GridBagConstraints constraints =
                new GridBagConstraints();

        constraints.gridx = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.insets = new Insets(
                10,
                10,
                10,
                10
        );

        JLabel welcomeLabel = new JLabel(
                "READY TO PLAY?",
                SwingConstants.CENTER
        );

        welcomeLabel.setFont(Theme.HEADING_FONT);
        welcomeLabel.setForeground(Theme.TEXT);

        JLabel nameLabel = new JLabel("PLAYER NAME");

        nameLabel.setFont(Theme.BUTTON_FONT);
        nameLabel.setForeground(Theme.TEXT);

        playerNameField = new JTextField();

        playerNameField.setFont(Theme.BODY_FONT);
        playerNameField.setForeground(Theme.TEXT);
        playerNameField.setBackground(Theme.PANEL_LIGHT);
        playerNameField.setCaretColor(Theme.ACCENT);

        playerNameField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Theme.PRIMARY,
                                2
                        ),
                        BorderFactory.createEmptyBorder(
                                11,
                                14,
                                11,
                                14
                        )
                )
        );

        playerNameField.setPreferredSize(
                new Dimension(310, 48)
        );

        RoundedButton continueButton =
                new RoundedButton("START SETUP");

        RoundedButton leaderboardButton =
                new RoundedButton(
                        "VIEW LEADERBOARD",
                        Theme.PANEL_LIGHT,
                        Theme.PRIMARY,
                        Theme.ACCENT
                );

        constraints.gridy = 0;
        constraints.insets = new Insets(
                5,
                10,
                25,
                10
        );
        formCard.add(welcomeLabel, constraints);

        constraints.gridy = 1;
        constraints.insets = new Insets(
                10,
                10,
                5,
                10
        );
        formCard.add(nameLabel, constraints);

        constraints.gridy = 2;
        constraints.insets = new Insets(
                5,
                10,
                15,
                10
        );
        formCard.add(playerNameField, constraints);

        constraints.gridy = 3;
        constraints.insets = new Insets(
                20,
                10,
                8,
                10
        );
        formCard.add(continueButton, constraints);

        constraints.gridy = 4;
        constraints.insets = new Insets(
                8,
                10,
                10,
                10
        );
        formCard.add(leaderboardButton, constraints);

        continueButton.addActionListener(event ->
                submitPlayerName(continueAction)
        );

        leaderboardButton.addActionListener(event ->
                leaderboardAction.run()
        );

        playerNameField.addActionListener(event ->
                submitPlayerName(continueAction)
        );

        return formCard;
    }

    private void submitPlayerName(
            Consumer<String> continueAction
    ) {
        String playerName =
                playerNameField.getText().trim();

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
