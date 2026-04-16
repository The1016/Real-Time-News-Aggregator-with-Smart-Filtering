package service;

import java.io.IOException;
import java.net.URI;
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

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "HTTP Error: " + response.statusCode() + "\nResponse: " + response.body()
            );
        }

        return response.body();
    }
}
