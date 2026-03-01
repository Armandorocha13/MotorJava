package com.motorjava.service.ihs;

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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import com.motorjava.service.common.ImportadorArquivo;

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
            int colStatus = -1, colMaterial = -1, colUnidade = -1;

            // 1. Identifica as colunas pelo cabeçalho (Linha 0)
            Row header = sheet.getRow(0);
            if (header != null) {
                for (int i = 0; i < header.getLastCellNum(); i++) {
                    String h = ImportadorArquivo.getCellValueAsString(header.getCell(i)).toLowerCase();
                    if (h.contains("status"))
                        colStatus = i;
                    else if (h.contains("material") || h.contains("descricao"))
                        colMaterial = i;
                    else if (h.contains("unidade") || h.contains("quant") || h.contains("saldo"))
                        colUnidade = i;
                }
            }

            // Fallback se não encontrar cabeçalhos específicos: usa ordem 0, 1, 2
            if (colStatus == -1)
                colStatus = 0;
            if (colMaterial == -1)
                colMaterial = 1;
            if (colUnidade == -1)
                colUnidade = 2;

            log("Mapeamento " + tableName + ": Status(col " + colStatus + "), Material(col " + colMaterial
                    + "), Unidade(col " + colUnidade + ")");

            // 2. Transacional: Limpa e reinsere
            try (java.sql.Statement st = conn.createStatement()) {
                st.execute("TRUNCATE TABLE " + tableName);
            }

            for (Row row : sheet) {
                if (row.getRowNum() == 0)
                    continue; // Pula cabeçalho

                String status = ImportadorArquivo.getCellValueAsString(row.getCell(colStatus));
                String material = ImportadorArquivo.getCellValueAsString(row.getCell(colMaterial));
                String unidadeStr = ImportadorArquivo.getCellValueAsString(row.getCell(colUnidade));

                if (status.isEmpty() && material.isEmpty())
                    continue;

                int unidade = 0;
                try {
                    if (!unidadeStr.isEmpty()) {
                        // Remove pontos de milhar e troca vírgula por ponto para parser
                        String cleanUnid = unidadeStr.replace(".", "").replace(",", ".");
                        unidade = (int) Math.round(Double.parseDouble(cleanUnid));
                    }
                } catch (Exception e) {
                    // Se falhar o parse, mantém 0
                }

                ps.setString(1, status);
                ps.setString(2, material);
                ps.setInt(3, unidade);
                ps.addBatch();

                rowsProcessed++;
                if (rowsProcessed % 500 == 0)
                    ps.executeBatch();
            }

            if (rowsProcessed > 0) {
                ps.executeBatch();
                log("🚀 [DB] Sucesso: " + rowsProcessed + " registros carregados em " + tableName);
            } else {
                log("⚠️ [DB] Nenhum dado válido encontrado na planilha " + file.getName());
            }

        } catch (Exception e) {
            log("❌ Erro fatal na importação de " + file.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Processa a carga do Aniel (Manual)
     */
    public void processarAnielManual() {
        log("Iniciando processamento Aniel Manual...");
        File dir = new File(Config.PATH_ONEPAGE_ANIEL);
        if (!dir.exists()) {
            log("❌ Erro: Pasta Aniel não encontrada: " + dir.getAbsolutePath());
            return;
        }

        File[] files = dir.listFiles();
        if (files == null)
            return;

        for (File file : files) {
            String fileName = file.getName();
            if (fileName.startsWith("~$"))
                continue; // Ignora arquivos temporários do Excel

            String nameLower = fileName.toLowerCase();
            String tableName = "";

            if (nameLower.contains("saldo") && nameLower.contains("estoque")) {
                tableName = "aniel_saldo_estoque";
            } else if (nameLower.contains("saldo") && nameLower.contains("volante")) {
                tableName = "aniel_saldo_volante";
            } else if (nameLower.contains("confmaterial")) {
                tableName = "aniel_conferencia_material";
            } else if (nameLower.contains("saida") && (nameLower.contains("rm") || nameLower.contains("dm"))) {
                // Futuro: aniel_saida_rm_dm
            }

            if (!tableName.isEmpty()) {
                log("📥 [Aniel] Importando: " + file.getName() + " -> " + tableName);
                importarArquivo(file, tableName);
            }
        }
        log("✅ Processamento Aniel Manual finalizado.");
    }

    public void processarWMS() {
        log("Iniciando monitoramento e processamento WMS...");

        File downloadsDir = new File(Config.PATH_DOWNLOADS);
        File wmsDir = new File(Config.PATH_ONEPAGE_WMS);
        if (!wmsDir.exists())
            wmsDir.mkdirs();

        // 1. Processa novos arquivos da pasta Downloads (Move + Importa)
        File[] downloadFiles = downloadsDir.listFiles();
        if (downloadFiles != null) {
            for (File file : downloadFiles) {
                processarArquivoWms(file, wmsDir, true);
            }
        }

        // 2. Processa arquivos que já estão na pasta WMS (Garante importação se
        // faltaram antes)
        File[] wmsFiles = wmsDir.listFiles();
        if (wmsFiles != null) {
            for (File file : wmsFiles) {
                processarArquivoWms(file, wmsDir, false);
            }
        }

        log("Processamento WMS concluído.");
    }

    private void processarArquivoWms(File file, File wmsDir, boolean mover) {
        String originalName = file.getName();
        String nameLower = originalName.toLowerCase();
        String tableName = "";

        // Detecção ultra-resiliente
        if (nameLower.contains("estoque")
                && (nameLower.contains("material") || nameLower.equals("estoquematerial.xlsx"))) {
            tableName = "estoque_detalhado";
        } else if (nameLower.contains("tecnico")
                && (nameLower.contains("quantitativo") || nameLower.contains("quant"))) {
            tableName = "estoque_tecnico_quantitativo";
        } else if (nameLower.contains("pedido") || nameLower.contains("devolu")
                || (nameLower.contains("material") && !nameLower.contains("estoque"))) {
            if (nameLower.contains("pedido") || nameLower.contains("devolu")) {
                tableName = "pedido_devolucao_material";
            }
        }

        if (!tableName.isEmpty()) {
            try {
                File finalFile = file;
                if (mover) {
                    String novoNome = padronizarNomeArquivo(originalName);
                    finalFile = new File(wmsDir, novoNome);
                    Files.move(file.toPath(), finalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    log("WMS: Arquivo movido: " + originalName + " -> " + novoNome);
                }

                log("📥 [WMS] Importando dados para: " + tableName);
                importarArquivo(finalFile, tableName);
            } catch (Exception e) {
                log("Erro no arquivo WMS " + originalName + ": " + e.getMessage());
            }
        }
    }

    /**
     * Importador genérico que mapeia colunas dinamicamente baseado nos scripts SQL
     * criados.
     */
    public void importarArquivo(File file, String tableName) {
        try (Connection conn = DatabaseConfig.getConnection();
                FileInputStream fis = new FileInputStream(file);
                Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row header = null;

            // Busca o cabeçalho nas primeiras 10 linhas
            for (int i = 0; i < 10; i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;
                int count = 0;
                for (Cell cell : row) {
                    if (cell != null && !ImportadorArquivo.getCellValueAsString(cell).trim().isEmpty())
                        count++;
                }
                if (count > 5) { // Se tiver mais de 5 colunas preenchidas, assume ser o cabeçalho
                    header = row;
                    break;
                }
            }

            if (header == null) {
                log("⚠️ Cabeçalho não encontrado na planilha: " + file.getName());
                return;
            }

            // 1. Pega as colunas reais da tabela no Banco de Dados
            // Para PostgreSQL (Supabase), precisamos especificar o schema 'public'
            List<String> validDbColumns = new ArrayList<>();
            try (java.sql.ResultSet rs = conn.getMetaData().getColumns(null, "public", tableName, null)) {
                while (rs.next()) {
                    validDbColumns.add(rs.getString("COLUMN_NAME").toLowerCase());
                }
            }

            if (validDbColumns.isEmpty()) {
                log("❌ Tabela '" + tableName + "' não encontrada no schema 'public' do Supabase.");
                log("⚠️  Verifique se a tabela foi criada corretamente no Supabase.");
                return;
            }

            // 2. Mapeia colunas do Excel e filtra apenas as que existem no Banco
            List<String> excelColumns = new ArrayList<>();
            List<Integer> validIndices = new ArrayList<>();

            for (int i = 0; i < header.getLastCellNum(); i++) {
                String raw = ImportadorArquivo.getCellValueAsString(header.getCell(i));
                if (raw == null || raw.trim().isEmpty())
                    continue;

                String colName = raw.toLowerCase()
                        .trim()
                        .replace(" ", "_")
                        .replace("á", "a").replace("à", "a").replace("â", "a").replace("ã", "a")
                        .replace("é", "e").replace("è", "e").replace("ê", "e")
                        .replace("í", "i").replace("ì", "i").replace("î", "i")
                        .replace("ó", "o").replace("ò", "o").replace("ô", "o").replace("õ", "o")
                        .replace("ú", "u").replace("ù", "u").replace("û", "u")
                        .replace("ç", "c")
                        .replaceAll("[^a-z0-9_]", "");

                if (validDbColumns.contains(colName)) {
                    excelColumns.add(colName);
                    validIndices.add(i);
                } else {
                    log("ℹ️ Ignorando coluna do Excel (não existe no banco): " + raw + " -> " + colName);
                }
            }

            if (excelColumns.isEmpty()) {
                log("❌ Nenhuma coluna compatível encontrada entre Excel e Banco (" + tableName + ")");
                return;
            }

            log("📍 Importando colunas: " + excelColumns);

            // 3. Monta SQL com backticks (proteção para nomes como 'data')
            StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
            StringBuilder values = new StringBuilder("VALUES (");
            for (int i = 0; i < excelColumns.size(); i++) {
                sql.append("\"").append(excelColumns.get(i)).append("\"")
                        .append(i == excelColumns.size() - 1 ? "" : ", ");
                values.append("?").append(i == excelColumns.size() - 1 ? "" : ", ");
            }
            sql.append(") ").append(values).append(")");

            // 4. Limpa e Insere (no PostgreSQL, RESTART IDENTITY reseta os IDs sequenciais)
            try (java.sql.Statement st = conn.createStatement()) {
                st.execute("TRUNCATE TABLE " + tableName + " RESTART IDENTITY");
            }

            try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                int count = 0;
                int headerRowNum = header.getRowNum();
                for (Row row : sheet) {
                    if (row.getRowNum() <= headerRowNum)
                        continue;

                    for (int i = 0; i < validIndices.size(); i++) {
                        String val = ImportadorArquivo.getCellValueAsString(row.getCell(validIndices.get(i)));
                        ps.setString(i + 1, val);
                    }
                    ps.addBatch();
                    count++;
                    if (count % 500 == 0)
                        ps.executeBatch();
                }
                ps.executeBatch();
                log("✅ [DB] Sucesso: " + count + " registros em " + tableName);
            }

        } catch (Exception e) {
            log("❌ Falha crítica na importação (" + tableName + "): " + e.getMessage());
            e.printStackTrace();
        }
    }

}
