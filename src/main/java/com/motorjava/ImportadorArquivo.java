package com.motorjava;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import com.motorjava.config.DatabaseConfig;
import java.sql.*;

public class ImportadorArquivo {

    // Configurações do Banco
    // A conexão é obtida via DatabaseConfig


    public static void main(String[] args) {
        String caminhoArquivo ="C:\\Users\\user\\Desktop\\EQUIPAMENTO_SERIALIZADOS_VOLANTE_SP.xlsx - DADOS.csv";
        executarCarga(caminhoArquivo);
    }

    public static void executarCarga(String csvPath) {
        // SQL com 33 interrogações + 1 para a data de hoje (Snapshot)
        String sqlInsert = "INSERT INTO estoque_vivo_historico VALUES (" + "?,".repeat(33) + "CURRENT_DATE)";
        String sqlDeleteHoje = "DELETE FROM estoque_vivo_historico WHERE data_snapshot = CURRENT_DATE";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false); // Inicia transação

            // 1. Limpa dados de hoje se já existirem (evita duplicidade ao re-testar)
            try (Statement st = conn.createStatement()) {
                st.execute(sqlDeleteHoje);
            }

            // 2. Lê o arquivo e prepara o lote (Batch)
            try (CSVReader reader = new CSVReaderBuilder(new FileReader(csvPath, StandardCharsets.UTF_8))
                    .withCSVParser(new CSVParserBuilder().withSeparator(',').build()) // Nota: DADOS.csv usa vírgula
                    .withSkipLines(1) // Pula o cabeçalho
                    .build()) {

                PreparedStatement ps = conn.prepareStatement(sqlInsert);
                String[] linha;
                int contador = 0;

                System.out.println("Iniciando leitura do arquivo...");

                while ((linha = reader.readNext()) != null) {
                    // Preenche as 33 colunas vindas do CSV
                    for (int i = 0; i < 33; i++) {
                        if (i < linha.length) {
                            String valor = linha[i].trim();
                            // Trata campos vazios para o SQL entender como NULL
                            if (valor.isEmpty()) {
                                ps.setNull(i + 1, Types.VARCHAR);
                            } else {
                                ps.setString(i + 1, valor);
                            }
                        } else {
                            ps.setNull(i + 1, Types.VARCHAR);
                        }
                    }

                    ps.addBatch(); // Adiciona ao lote de memória

                    // Executa o lote a cada 1000 linhas para não travar a memória
                    if (++contador % 1000 == 0) {
                        ps.executeBatch();
                        System.out.println("Processadas " + contador + " linhas...");
                    }
                }

                ps.executeBatch(); // Insere o restante
                conn.commit();     // Grava tudo definitivamente
                System.out.println("Carga finalizada com sucesso! Total: " + contador + " registros.");

            } catch (Exception e) {
                conn.rollback(); // Se der erro em qualquer linha, desfaz tudo
                throw e;
            }

        } catch (Exception e) {
            System.err.println("ERRO NA CARGA: " + e.getMessage());
            e.printStackTrace();
        }
    }
}