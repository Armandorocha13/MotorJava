package com.motorjava.api;

import com.motorjava.service.ihs.OnePageService;
import com.motorjava.service.vivo.VivoService;
import com.motorjava.service.emis.EmisService;
import com.motorjava.service.maquinas.MaquinasService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class LocalServer {

    private final OnePageService onePageService;
    private final VivoService vivoService;
    private final EmisService emisService;
    private final MaquinasService maquinasService;

    public LocalServer(OnePageService onePageService, VivoService vivoService, EmisService emisService,
            MaquinasService maquinasService) {
        this.onePageService = onePageService;
        this.vivoService = vivoService;
        this.emisService = emisService;
        this.maquinasService = maquinasService;
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Endpoints One Page
        server.createContext("/api/outlook", new ActionHandler("outlook"));
        server.createContext("/api/aniel", new ActionHandler("aniel"));
        server.createContext("/api/wms", new ActionHandler("wms"));

        // Endpoints Vivo
        server.createContext("/api/vivo/atualizar", new ActionHandler("vivo_update"));
        server.createContext("/api/vivo/importar", new ActionHandler("vivo_import"));
        server.createContext("/api/vivo/forca", new ActionHandler("vivo_forca"));

        // Endpoints Emis
        server.createContext("/api/emis/renomear", new ActionHandler("emis_renomear"));
        server.createContext("/api/emis/importar", new ActionHandler("emis_importar"));

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
                    case "outlook":
                        onePageService.processarOutlook();
                        msg = "Outlook processado.";
                        break;
                    case "aniel":
                        onePageService.processarAnielManual();
                        msg = "Aniel processado.";
                        break;
                    case "wms":
                        onePageService.processarWMS();
                        msg = "WMS processado.";
                        break;
                    case "vivo_update":
                        vivoService.atualizarExcelVivo();
                        msg = "Excel Vivo atualizado.";
                        break;
                    case "vivo_import":
                        vivoService.importarCargaVivo();
                        msg = "Carga Vivo importada.";
                        break;
                    case "vivo_forca":
                        vivoService.importarForcaSP();
                        msg = "Força SP importada.";
                        break;
                    case "emis_renomear":
                        emisService.renomearArquivosDaPasta();
                        msg = "Arquivos EMIS/TERMINAIS renomeados e movidos.";
                        break;
                    case "emis_importar":
                        emisService.importarBancoDados();
                        msg = "Importação concluída.";
                        break;
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
