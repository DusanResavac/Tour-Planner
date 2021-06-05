package TourProject.DataAccessLayer.API;

import TourProject.DataAccessLayer.Config;
import TourProject.Model.api.OpenAIRequest;
import TourProject.Model.api.OpenAIResponseBody;
import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class OpenAI {
    public static CompletableFuture<String> generateDescription (String input) {
        HttpClient client = HttpClient.newHttpClient();
        OpenAIRequest requestBody = new OpenAIRequest();
        requestBody.setPrompt("Write a short paragraph about my bike tour while addressing the following points: " + input.trim());
        requestBody.setMax_tokens(95);
        requestBody.setTemperature(0.18);
        requestBody.setTop_p(1.0);
        requestBody.setFrequency_penalty(0.1);
        requestBody.setPresence_penalty(0.0);
        String openAIKey = (String) Config.getInstance().getAttribute("openai_key");
        if (openAIKey == null) {
            openAIKey = "";
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/engines/curie-instruct-beta/completions"))
                .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(requestBody)))
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", "Bearer " + openAIKey)
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(response -> {
                    OpenAIResponseBody responseBody = new Gson().fromJson(response, OpenAIResponseBody.class);
                    return responseBody.getChoices().size() == 0 ? null : responseBody.getChoices().get(0).getText().replaceFirst("\\s+", "");
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }
}
