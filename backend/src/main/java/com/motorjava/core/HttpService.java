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
     * Realiza um GET autenticado com o Token do Aniel.
     */
    public CompletableFuture<HttpResponse<String>> getAnielRequest(String url, String token) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("token", token)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Realiza um POST autenticado com os cabeçalhos em PascalCase (Token, Usuario,
     * Senha).
     */
    public CompletableFuture<HttpResponse<String>> postAnielRequest(String url, String token, String user, String pass,
            String jsonBody) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Token", token)
                .header("Usuario", user)
                .header("Senha", pass)
                .header("Content-Type", "application/json")
                .header("Accept", "*/*")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Template para chamadas GET padrão.
     */
    public CompletableFuture<HttpResponse<String>> getRequest(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}
