package com.motorjava.api;

import com.motorjava.service.maquinas.MaquinasService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class LocalServer {

    private final MaquinasService maquinasService;

    public LocalServer(MaquinasService maquinasService) {
        this.maquinasService = maquinasService;
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Endpoints Maquinas
        server.createContext("/api/maquinas/renomear", new ActionHandler("maquinas_renomear"));
        server.createContext("/api/maquinas/importar", new ActionHandler("maquinas_importar"));

        server.createContext("/api/status", exchange -> {
            sendResponse(exchange, "{\"status\": \"online\", \"engine\": \"Motor Java v4.0\"}", 200);
        });

        server.setExecutor(null);
        server.start();
        System.out.println("Backend API rodando em http://localhost:8080");
    }

    private class ActionHandler implements HttpHandler {
        private String action;

        public ActionHandler(String action) {
            this.action = action;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            configCORS(exchange);
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            try {
                String msg = "";
                switch (action) {
                    case "maquinas_renomear":
                        maquinasService.renomearArquivosDaPasta();
                        msg = "Arquivos de Maquinários renomeados e movidos.";
                        break;
                    case "maquinas_importar":
                        maquinasService.importarBancoDados();
                        msg = "Importação do Maquinário concluída.";
                        break;
                }
                sendResponse(exchange, "{\"success\": true, \"msg\": \"" + msg + "\"}", 200);
            } catch (Exception e) {
                sendResponse(exchange, "{\"success\": false, \"msg\": \"" + e.getMessage() + "\"}", 500);
            }
        }
    }

    private void configCORS(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }

    private void sendResponse(HttpExchange exchange, String response, int code) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        byte[] bytes = response.getBytes();
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
