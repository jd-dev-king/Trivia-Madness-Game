package com.jeremiah.triviagame.database;

import com.jeremiah.triviagame.model.Score;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ScoreRepository {

    public void saveScore(
            String playerName,
            int score,
            int totalQuestions,
            String category,
            String difficulty
    ) {
        String sql = """
                INSERT INTO scores (
                    player_name,
                    score,
                    total_questions,
                    category,
                    difficulty,
                    percentage
                )
                VALUES (?, ?, ?, ?, ?, ?);
                """;

        double percentage = totalQuestions == 0
                ? 0
                : score * 100.0 / totalQuestions;

        try (
                Connection connection =
                        DatabaseManager.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, playerName);
            statement.setInt(2, score);
            statement.setInt(3, totalQuestions);
            statement.setString(4, category);
            statement.setString(5, difficulty);
            statement.setDouble(6, percentage);

            statement.executeUpdate();

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Unable to save the score.",
                    exception
            );
        }
    }

    public List<Score> getAllScores() {
        String sql = """
                SELECT
                    id,
                    player_name,
                    score,
                    total_questions,
                    category,
                    difficulty,
                    percentage,
                    played_at
                FROM scores
                ORDER BY
                    percentage DESC,
                    score DESC,
                    played_at DESC;
                """;

        List<Score> scores = new ArrayList<>();

        try (
                Connection connection =
                        DatabaseManager.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {
            while (resultSet.next()) {
                Score score = new Score(
                        resultSet.getInt("id"),
                        resultSet.getString("player_name"),
                        resultSet.getInt("score"),
                        resultSet.getInt("total_questions"),
                        resultSet.getString("category"),
                        resultSet.getString("difficulty"),
                        resultSet.getDouble("percentage"),
                        resultSet.getString("played_at")
                );

                scores.add(score);
            }

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Unable to load saved scores.",
                    exception
            );
        }

        return scores;
    }

    public void deleteAllScores() {
        String sql = "DELETE FROM scores;";

        try (
                Connection connection =
                        DatabaseManager.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.executeUpdate();

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Unable to reset saved scores.",
                    exception
            );
        }
    }
}
