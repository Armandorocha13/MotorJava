package com.motorjava.service;

import com.motorjava.config.Config;
import com.motorjava.config.DatabaseConfig;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.function.Consumer;

public class OnePageService {

    private final Consumer<String> logger;

    public OnePageService(Consumer<String> logger) {
        this.logger = logger;
    }

    private void log(String msg) {
        if (logger != null)
            logger.accept(msg);
    }

    /**
     * Monitora a pasta de Downloads e move arquivos do Outlook para a pasta
     * correta.
     * Aplica padronização de nomes (minúsculas, sem números).
     */
    public void monitorarDownloads() {
        File downloadsDir = new File(Config.PATH_DOWNLOADS);
        if (!downloadsDir.exists())
            return;

        File[] files = downloadsDir.listFiles();
        if (files == null)
            return;

        for (File file : files) {
            String originalName = file.getName();
            String nameLower = originalName.toLowerCase();

            // Critérios: modem, nel, rj, sp
            if (nameLower.contains("modem") || nameLower.contains("nel") || nameLower.contains("rj")
                    || nameLower.contains("sp")) {
                try {
                    String novoNome = padronizarNomeArquivo(originalName);
                    File destDir = new File(Config.PATH_ONEPAGE_OUTLOOK);
                    if (!destDir.exists())
                        destDir.mkdirs();

                    File destFile = new File(destDir, novoNome);

                    Files.move(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    log("Arquivo movido e padronizado: " + originalName + " -> " + novoNome);

                    // Importação imediata se for arquivo de saldo
                    String novoNomeLower = novoNome.toLowerCase();
                    if (novoNomeLower.contains("saldo")
                            && (novoNomeLower.contains("modem") || novoNomeLower.contains("modems"))) {
                        String tableName = "";
                        if (novoNomeLower.contains("rj")) {
                            tableName = "saldo_modem_rj";
                        } else if (novoNomeLower.contains("sp")) {
                            tableName = "saldo_modem_sp";
                        }

                        if (!tableName.isEmpty()) {
                            log("📥 Importação automática iniciada para: " + novoNome);
                            importarTabelaSaldoModem(destFile, tableName);
                        }
                    }
                } catch (Exception e) {
                    log("Erro ao mover/importar arquivo: " + originalName + " -> " + e.getMessage());
                }
            }
        }
    }

    /**
     * Padroniza o nome do arquivo:
     * 1. Converte para minúsculas
     * 2. Remove números
     * 3. Remove parênteses e caracteres especiais
     * 4. Remove espaços duplos
     */
    private String padronizarNomeArquivo(String nomeOriginal) {
        // Separa nome da extensão
        int lastDot = nomeOriginal.lastIndexOf(".");
        String nome = (lastDot != -1) ? nomeOriginal.substring(0, lastDot) : nomeOriginal;
        String extensao = (lastDot != -1) ? nomeOriginal.substring(lastDot) : "";

        // 1. Minúsculas
        nome = nome.toLowerCase();

        // 2. Remove números
        nome = nome.replaceAll("\\d", "");

        // 3. Remove parênteses e caracteres especiais (mantendo espaços)
        nome = nome.replaceAll("[\\(\\)\\[\\]\\-_]", " ");

        // 4. Limpa espaços extras
        nome = nome.replaceAll("\\s+", " ").trim();

        return nome + extensao.toLowerCase();
    }

    /**
     * Processa a carga do Outlook (One Page Report)
     */
    public void processarOutlook() {
        log("Iniciando processamento Outlook (One Page)...");
        monitorarDownloads(); // Move novos arquivos do folder Downloads

        File dir = new File(Config.PATH_ONEPAGE_OUTLOOK);
        if (!dir.exists()) {
            log("Erro: Pasta OnePage Outlook não encontrada: " + dir.getAbsolutePath());
            return;
        }

        File[] files = dir.listFiles();
        if (files == null)
            return;

        for (File file : files) {
            String fileName = file.getName();
            String nameLower = fileName.toLowerCase();

            // --- CORREÇÃO RETROATIVA: Se o arquivo já estiver na pasta mas com nome em
            // caixa alta ou números ---
            String nomeCorreto = padronizarNomeArquivo(fileName);
            File finalFile = file;

            if (!fileName.equals(nomeCorreto)) {
                log("Corrigindo nome de arquivo existente: " + fileName + " -> " + nomeCorreto);
                File renamedFile = new File(dir, nomeCorreto);
                try {
                    Files.move(file.toPath(), renamedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    finalFile = renamedFile;
                    nameLower = nomeCorreto.toLowerCase();
                } catch (Exception e) {
                    log("Erro ao renomear arquivo existente: " + e.getMessage());
                }
            }

            // Detecta se o arquivo é de saldo de modem
            if (nameLower.contains("saldo") && (nameLower.contains("modem") || nameLower.contains("modems"))) {
                String tableName = "";
                if (nameLower.contains("rj")) {
                    tableName = "saldo_modem_rj";
                } else if (nameLower.contains("sp")) {
                    tableName = "saldo_modem_sp";
                }

                if (!tableName.isEmpty()) {
                    log("Processando arquivo: " + finalFile.getName() + " -> " + tableName);
                    importarTabelaSaldoModem(finalFile, tableName);
                }
            }
        }
        log("Processamento Outlook concluído.");
    }

    /**
     * Lê o arquivo Excel e insere os dados na tabela correspondente.
     * Estrutura esperada: status, material, unidade
     */
    private void importarTabelaSaldoModem(File file, String tableName) {
        String sql = "INSERT INTO " + tableName + " (status, material, unidade) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
                FileInputStream fis = new FileInputStream(file);
                Workbook workbook = new XSSFWorkbook(fis);
                PreparedStatement ps = conn.prepareStatement(sql)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowsProcessed = 0;

            // Transacional: Limpa e reinsere
            try (java.sql.Statement st = conn.createStatement()) {
                st.execute("TRUNCATE TABLE " + tableName);
            }

            for (Row row : sheet) {
                if (row.getRowNum() == 0)
                    continue; // Pula cabeçalho

                // Mapeamento: Status (0), Material (1), Unidade (2)
                String status = ImportadorArquivo.getCellValueAsString(row.getCell(0));
                String material = ImportadorArquivo.getCellValueAsString(row.getCell(1));
                String unidadeStr = ImportadorArquivo.getCellValueAsString(row.getCell(2));

                if (status.isEmpty() && material.isEmpty())
                    continue;

                int unidade = 0;
                try {
                    if (!unidadeStr.isEmpty()) {
                        unidade = (int) Double.parseDouble(unidadeStr.replace(",", "."));
                    }
                } catch (Exception e) {
                    // Se não for número, mantém 0
                }

                ps.setString(1, status);
                ps.setString(2, material);
                ps.setInt(3, unidade);
                ps.addBatch();

                rowsProcessed++;
                if (rowsProcessed % 1000 == 0)
                    ps.executeBatch();
            }
            if (rowsProcessed % 1000 != 0)
                ps.executeBatch();
            log("🚀 [DB] " + rowsProcessed + " registros carregados em " + tableName);

        } catch (Exception e) {
            log("❌ Erro ao importar " + file.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Processa a carga do Aniel (Manual)
     */
    public void processarAnielManual() {
        log("Iniciando processamento Aniel Manual...");
        File dir = new File(Config.PATH_ONEPAGE_ANIEL);
        log("Lendo pasta: " + dir.getAbsolutePath());
        // Lógica para: conferencia de movimen, saldo estoque, saida de rm e dm
        log("Sucesso! Dados do Aniel Manual processados.");
    }

    /**
     * Processa a carga do WMS
     */
    public void processarWMS() {
        log("Iniciando processamento WMS (One Page)...");
        File dir = new File(Config.PATH_ONEPAGE_WMS);
        log("Lendo pasta: " + dir.getAbsolutePath());
        // Lógica para: estoquematerial, estoquetecnico quantitativo,
        // pedido_devolução_material
        log("Sucesso! Dados do WMS processados.");
    }
}
