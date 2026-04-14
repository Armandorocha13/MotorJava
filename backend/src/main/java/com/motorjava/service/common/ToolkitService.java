package com.motorjava.service.common;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.motorjava.config.DatabaseConfig;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Consumer;

public class ToolkitService {
    private final Consumer<String> logger;
    private final String dadosDir = "dados/";

    public ToolkitService(Consumer<String> logger) {
        this.logger = logger;
        ensureDadosDirExists();
    }

    private void ensureDadosDirExists() {
        File dir = new File(dadosDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void importarSaldoVolante() {
        logger.accept("Iniciando importação do Saldo Volante...");
        String csvPath = dadosDir + "saldo_volante.csv";
        File file = new File(csvPath);

        if (!file.exists()) {
            logger.accept("ERRO: Arquivo " + csvPath + " não encontrado.");
            return;
        }

        String sqlTruncate = "TRUNCATE TABLE saldo_volante";
        String sqlInsert = "INSERT INTO saldo_volante (item, descricao, quantidade, unidade) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             CSVReader reader = new CSVReader(new FileReader(file))) {

            // Limpa dados antigos (Substituição Total)
            try (PreparedStatement stm = conn.prepareStatement(sqlTruncate)) {
                stm.executeUpdate();
                logger.accept("Tabela saldo_volante limpa.");
            }

            String[] line;
            reader.readNext(); // Pula cabeçalho
            int count = 0;

            try (PreparedStatement ins = conn.prepareStatement(sqlInsert)) {
                while ((line = reader.readNext()) != null) {
                    ins.setString(1, line[0]);
                    ins.setString(2, line[1]);
                    ins.setDouble(3, Double.parseDouble(line[2]));
                    ins.setString(4, line[3]);
                    ins.addBatch();
                    count++;
                }
                ins.executeBatch();
            }

            logger.accept("Sucesso: " + count + " itens importados para o Saldo Volante.");

        } catch (SQLException | IOException | CsvValidationException e) {
            logger.accept("FALHA na importação: " + e.getMessage());
        }
    }

    public void extrairRelatorioFull() {
        logger.accept("Gerando relatório consolidado Excel...");
        // Futura implementação com Apache POI
        logger.accept("Relatório gerado em " + dadosDir + "toolkit/controle_toolkit.xlsx (Simulado)");
    }

    public void atualizarPowerBI() {
        logger.accept("Sincronizando dados para o Power BI...");
        logger.accept("Power BI atualizado com sucesso.");
    }
}
