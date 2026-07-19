package com.jeremiah.triviagame;

import com.jeremiah.triviagame.database.DatabaseManager;
import com.jeremiah.triviagame.ui.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();

        SwingUtilities.invokeLater(() -> {
            setSystemLookAndFeel();

            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }

    private static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception exception) {
            System.err.println(
                    "System look and feel could not be loaded: "
                            + exception.getMessage()
            );
        }
    }
}
