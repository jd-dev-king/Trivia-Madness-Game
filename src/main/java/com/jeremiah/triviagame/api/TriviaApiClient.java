package com.jeremiah.triviagame.api;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.jeremiah.triviagame.model.Category;
import com.jeremiah.triviagame.model.GameSettings;
import com.jeremiah.triviagame.model.Question;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class TriviaApiClient {

    private static final String API_URL =
            "https://opentdb.com/api.php";

    private final HttpClient httpClient;
    private final Gson gson;

    public TriviaApiClient() {
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();

        gson = new Gson();
    }

    public List<Question> fetchQuestions(
            GameSettings settings
    ) throws IOException, InterruptedException {

        List<Category> categories =
                new ArrayList<>(settings.getCategories());

        Collections.shuffle(categories);

        int totalQuestions = settings.getQuestionCount();

        /*
         * A category cannot receive fewer than one question.
         * If more categories are selected than questions requested,
         * only enough randomly selected categories are used.
         */
        if (categories.size() > totalQuestions) {
            categories = new ArrayList<>(
                    categories.subList(0, totalQuestions)
            );
        }

        List<Question> allQuestions = new ArrayList<>();

        int baseQuestions =
                totalQuestions / categories.size();

        int extraQuestions =
                totalQuestions % categories.size();

        for (int index = 0; index < categories.size(); index++) {
            Category category = categories.get(index);

            int categoryQuestionCount = baseQuestions;

            if (index < extraQuestions) {
                categoryQuestionCount++;
            }

            List<Question> categoryQuestions =
                    fetchCategoryQuestions(
                            category,
                            settings.getDifficulty(),
                            categoryQuestionCount
                    );

            allQuestions.addAll(categoryQuestions);
        }

        Collections.shuffle(allQuestions);

        if (allQuestions.size() > totalQuestions) {
            return new ArrayList<>(
                    allQuestions.subList(0, totalQuestions)
            );
        }

        return allQuestions;
    }

    private List<Question> fetchCategoryQuestions(
            Category category,
            String difficulty,
            int amount
    ) throws IOException, InterruptedException {

        StringBuilder url = new StringBuilder(API_URL);

        url.append("?amount=").append(amount);
        url.append("&category=").append(category.getId());
        url.append("&type=multiple");
        url.append("&encode=base64");

        if (difficulty != null && !difficulty.isBlank()) {
            url.append("&difficulty=").append(difficulty);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url.toString()))
                .timeout(Duration.ofSeconds(20))
                .header(
                        "User-Agent",
                        "TriviaGame-Java-Desktop/1.0"
                )
                .GET()
                .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() != 200) {
            throw new IOException(
                    "Open Trivia DB returned HTTP status "
                            + response.statusCode()
            );
        }

        TriviaResponse triviaResponse =
                gson.fromJson(
                        response.body(),
                        TriviaResponse.class
                );

        if (triviaResponse == null) {
            throw new IOException(
                    "Open Trivia DB returned an empty response."
            );
        }

        if (triviaResponse.responseCode != 0) {
            throw new IOException(
                    responseCodeMessage(
                            triviaResponse.responseCode
                    )
            );
        }

        List<Question> questions = new ArrayList<>();

        if (triviaResponse.results == null) {
            return questions;
        }

        for (ApiQuestion apiQuestion :
                triviaResponse.results) {

            List<String> incorrectAnswers =
                    new ArrayList<>();

            if (apiQuestion.incorrectAnswers != null) {
                for (String answer :
                        apiQuestion.incorrectAnswers) {

                    incorrectAnswers.add(
                            decodeBase64(answer)
                    );
                }
            }

            Question question = new Question(
                    decodeBase64(apiQuestion.category),
                    decodeBase64(apiQuestion.difficulty),
                    decodeBase64(apiQuestion.question),
                    decodeBase64(apiQuestion.correctAnswer),
                    incorrectAnswers
            );

            questions.add(question);
        }

        return questions;
    }

    private String decodeBase64(String encodedText) {
        if (encodedText == null || encodedText.isBlank()) {
            return "";
        }

        try {
            byte[] decodedBytes =
                    Base64.getDecoder().decode(encodedText);

            return new String(
                    decodedBytes,
                    StandardCharsets.UTF_8
            );
        } catch (IllegalArgumentException exception) {
            return encodedText;
        }
    }

    private String responseCodeMessage(int responseCode) {
        return switch (responseCode) {
            case 1 ->
                    "Open Trivia DB does not have enough questions "
                            + "for the selected options.";

            case 2 ->
                    "Open Trivia DB rejected one or more parameters.";

            case 3 ->
                    "The Open Trivia DB session token was not found.";

            case 4 ->
                    "The Open Trivia DB session token is empty.";

            case 5 ->
                    "Too many requests were sent to Open Trivia DB. "
                            + "Please wait briefly and try again.";

            default ->
                    "Open Trivia DB returned response code "
                            + responseCode + ".";
        };
    }

    private static class TriviaResponse {

        @SerializedName("response_code")
        private int responseCode;

        private List<ApiQuestion> results;
    }

    private static class ApiQuestion {

        private String category;
        private String difficulty;
        private String question;

        @SerializedName("correct_answer")
        private String correctAnswer;

        @SerializedName("incorrect_answers")
        private List<String> incorrectAnswers;
    }
}
