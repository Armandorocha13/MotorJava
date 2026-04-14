package com.motorjava.api;

import com.motorjava.service.ferramentaria.FerramentariaService;
import com.motorjava.service.maquinas.MaquinasService;
import com.motorjava.service.common.ToolkitService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LocalServer {

    private final ObjectMapper mapper = new ObjectMapper();
    private final MaquinasService maquinasService;
    private final FerramentariaService ferramentariaService;
    private final ToolkitService toolkitService;

    public LocalServer(MaquinasService maquinasService, FerramentariaService ferramentariaService, ToolkitService toolkitService) {
        this.maquinasService = maquinasService;
        this.ferramentariaService = ferramentariaService;
        this.toolkitService = toolkitService;
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Endpoints Maquinas
        server.createContext("/api/maquinas/renomear", new ActionHandler("maquinas_renomear"));

        // Endpoints Ferramentaria
        server.createContext("/api/ferramentaria/extrair", new ActionHandler("ferramenta_extrair"));
        server.createContext("/api/ferramentaria/processo", new ActionHandler("ferramenta_processo"));

        // Endpoints Toolkit
        server.createContext("/api/toolkit/importar", new ActionHandler("toolkit_importar"));
        server.createContext("/api/toolkit/extrair", new ActionHandler("toolkit_extrair"));
        server.createContext("/api/toolkit/pbi", new ActionHandler("toolkit_pbi"));

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
                        maquinasService.atualizarBaseMaquinario();
                        msg = "Base de Maquinários atualizada com sucesso!";
                        break;
                    case "ferramenta_extrair":
                        ferramentariaService.extrairDadosDoPortal();
                        msg = "Dados de ferramentaria extraidos do portal.";
                        break;
                    case "ferramenta_processo":
                        ferramentariaService.outroProcesso();
                        msg = "Processo secundario de ferramentaria concluido.";
                        break;
                    case "toolkit_importar":
                        toolkitService.importarSaldoVolante();
                        msg = "Saldo Volante importado para o sistema.";
                        break;
                    case "toolkit_extrair":
                        toolkitService.extrairRelatorioFull();
                        msg = "Relatorio consolidado Excel gerado.";
                        break;
                    case "toolkit_pbi":
                        toolkitService.atualizarPowerBI();
                        msg = "Dados sincronizados para o Power BI.";
                        break;
                }
                Map<String, Object> resp = new HashMap<>();
                resp.put("success", true);
                resp.put("msg", msg);
                sendResponse(exchange, mapper.writeValueAsString(resp), 200);
            } catch (Exception e) {
                Map<String, Object> err = new HashMap<>();
                err.put("success", false);
                err.put("msg", e.getMessage());
                sendResponse(exchange, mapper.writeValueAsString(err), 500);
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
