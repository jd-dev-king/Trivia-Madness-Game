package com.jeremiah.triviagame.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseManager {

    private static final Path DATA_DIRECTORY = Path.of("data");
    private static final String DATABASE_URL =
            "jdbc:sqlite:" + DATA_DIRECTORY.resolve("trivia_game.db");

    private DatabaseManager() {
    }

    public static void initializeDatabase() {
        createDataDirectory();

        String createScoresTable = """
                CREATE TABLE IF NOT EXISTS scores (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    player_name TEXT NOT NULL,
                    score INTEGER NOT NULL,
                    total_questions INTEGER NOT NULL,
                    category TEXT NOT NULL,
                    difficulty TEXT NOT NULL,
                    percentage REAL NOT NULL,
                    played_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
                );
                """;

        try (
                Connection connection = getConnection();
                Statement statement = connection.createStatement()
        ) {
            statement.execute(createScoresTable);
            System.out.println("Trivia database initialized.");
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Unable to initialize the trivia database.",
                    exception
            );
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

    private static void createDataDirectory() {
        try {
            Files.createDirectories(DATA_DIRECTORY);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Unable to create the data directory.",
                    exception
            );
        }
    }
}