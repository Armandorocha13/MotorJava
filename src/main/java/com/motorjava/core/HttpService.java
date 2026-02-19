package com.motorjava.core;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class HttpService {

    private final HttpClient httpClient;

    public HttpService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * Dispara um POST para o Webhook do Power Automate.
     */
    public CompletableFuture<HttpResponse<String>> postToPowerAutomate(String url, String jsonPayload) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Template para chamadas GET (usado no Aniel, por exemplo).
     */
    public CompletableFuture<HttpResponse<String>> getRequest(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}
