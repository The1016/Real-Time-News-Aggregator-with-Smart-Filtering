package service;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class APIClient {

    private final HttpClient client;

    public APIClient() {
        client = HttpClient.newHttpClient();
    }

    public String getJson(String fullUrl) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .header("X-Api-Key", APIConfig.API_KEY)
                .GET()
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (UnknownHostException e) {
            throw new RuntimeException("No internet connection. Please check your network and try again.");
        } catch (IOException e) {
            throw new RuntimeException("Network error: Unable to reach the news server. Please try again later.");
        }

        switch (response.statusCode()) {
            case 200:
                return response.body();
            case 401:
                throw new RuntimeException("Invalid API key. Please check your API key configuration.");
            case 429:
                throw new RuntimeException("Too many requests. Please wait a moment before trying again.");
            default:
                throw new RuntimeException("Server error (HTTP " + response.statusCode() + "). Please try again later.");
        }
    }
}
