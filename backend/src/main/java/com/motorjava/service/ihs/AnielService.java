package com.motorjava.service.ihs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.motorjava.config.Config;
import com.motorjava.config.DatabaseConfig;
import com.motorjava.core.HttpService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.function.Consumer;

public class AnielService {

    private final HttpService httpService;
    private final ObjectMapper objectMapper;
    private final Consumer<String> logger;

    public AnielService(Consumer<String> logger) {
        this.httpService = new HttpService();
        this.objectMapper = new ObjectMapper();
        this.logger = logger;
    }

    private void log(String msg) {
        if (logger != null)
            logger.accept(msg);
    }

    /**
     * Sincroniza as movimentacoes (Aplicacoes e Remocoes) do Aniel para o Banco.
     */
    public void sincronizarMovimentacoes() {
        log("Iniciando busca de movimentações no Aniel...");

        try {
            // Token puríssimo
            String tokenValue = Config.ANIEL_TOKEN;
            String url = Config.ANIEL_API_BASE_URL + Config.ENDPOINT_MOVIMENTACOES + "?api_key=" + tokenValue;

            // Formato internacional que o terminal aceitou
            java.time.LocalDate hoje = java.time.LocalDate.now();
            java.time.LocalDate inicio = hoje.minusDays(30);

            // Payload o mais simples possível
            String payload = String.format("{\"data_Ini\": \"%s\", \"data_Fim\": \"%s\"}",
                    inicio.toString(), hoje.toString());

            log("Chamando API com Token: " + tokenValue);

            httpService.postAnielRequest(url, tokenValue, Config.ANIEL_USER, Config.ANIEL_PASS, payload)
                    .thenAccept(response -> {
                        log("Resposta Bruta: " + response.body());
                        processarESalvarMovimentacoes(response.body());
                    })
                    .exceptionally(ex -> {
                        log("Falha na chamada: " + ex.getMessage());
                        return null;
                    });
        } catch (Exception e) {
            log("Erro de configuração: " + e.getMessage());
        }
    }

    private void processarESalvarMovimentacoes(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            boolean ok = root.path("ok").asBoolean();

            if (!ok) {
                String errorMsg = root.path("error").asText();
                log("Servidor recusou os dados. Mensagem: " + errorMsg);
                log("DICA: Se for 'Inválidos', pode ser o Token na URL ou a Senha no Header.");
                return;
            }

            log("Processando dados recebidos do Aniel conforme padrão Swagger...");
            JsonNode dataNode = root.path("data");

            if (!dataNode.isArray() || dataNode.size() == 0) {
                log("Aviso: Login OK, mas não há movimentações no período selecionado.");
                return;
            }

            int count = 0;
            try (Connection conn = DatabaseConfig.getConnection()) {
                String sql = "INSERT INTO aniel_movimentacoes " +
                        "(contrato, projeto, equipe, numero_obra, rdo, contrato_assinante, numero_os, data_agendamento, data_aplicacao, cod_material, cod_cpl, cod_cpl_aux, quantidade_aplic, quantidade_removida) "
                        +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    for (JsonNode node : dataNode) {
                        ps.setString(1, node.path("contrato").asText());
                        ps.setString(2, node.path("projeto").asText());
                        ps.setString(3, node.path("equipe").asText());
                        ps.setString(4, node.path("número_da_Obra").asText());
                        ps.setString(5, node.path("rdo").asText());
                        ps.setString(6, node.path("contrato_Assinante").asText());
                        ps.setString(7, node.path("numero_OS").asText());
                        ps.setString(8, formatDateTime(node.path("dataHora_Agendamento").asText()));
                        ps.setString(9, formatDateTime(node.path("dataHora_Aplicacao").asText()));
                        ps.setString(10, node.path("cod_Material").asText());
                        ps.setString(11, node.path("codCpl").asText());
                        ps.setString(12, node.path("codCpl_Aux").asText());
                        ps.setInt(13, node.path("quantidade_Aplic").asInt());
                        ps.setInt(14, node.path("quantidade_Removida").asInt());

                        ps.addBatch();
                        count++;
                    }
                    ps.executeBatch();
                }
            }
            log("Sucesso! " + count + " registros do Aniel mapeados pelo Swagger foram salvos.");
        } catch (Exception e) {
            log("Erro ao salvar no Banco: " + e.getMessage());
        }
    }

    private String formatDateTime(String value) {
        if (value == null || value.isEmpty() || value.equalsIgnoreCase("null") || value.contains("string")) {
            return null;
        }
        return value;
    }
}
