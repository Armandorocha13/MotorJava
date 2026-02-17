package com.motorjava;

import com.motorjava.config.DatabaseConfig;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ImportadorArquivo {

    public static void main(String[] args) {
        // Exemplo de uso
        String caminhoArquivo = "C:\\Users\\user\\Desktop\\MotorJava\\EQUIPAMENTO_SERIALIZADOS_VOLANTE_SP.xlsx";
        executarCarga(caminhoArquivo);
    }

    private static final java.nio.charset.Charset CHARSET_LEITURA = java.nio.charset.StandardCharsets.ISO_8859_1;

    public static void executarCarga(String filePath) {
        File arquivo = new File(filePath);
        if (!arquivo.exists()) {
            System.err.println("❌ ERRO CRÍTICO: Arquivo não encontrado: " + filePath);
            return;
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false); // Transação

            System.out.println("🔄 Preparando ambiente de Staging...");

            // 1. Garante que a tabela STAGE existe (estrutura genérica e flexível)
            criarTabelaStage(conn);

            // 2. Limpa a Stage
            try (Statement st = conn.createStatement()) {
                st.execute("TRUNCATE TABLE stage_stock_tecnico");
            }

            // 3. Importa o Arquivo para a Stage (sem validação, texto puro)
            System.out.println("📥 Importando arquivo para Stage: " + arquivo.getName());
            boolean sucessoStage = processarParaStage(arquivo, conn);

            if (!sucessoStage) {
                conn.rollback();
                System.out.println("❌ Falha na importação para Stage. Operação cancelada.");
                return;
            }

            System.out.println("✅ Dados carregados na Stage. Iniciando processamento e transferência...");

            // 4. Executa a Lógica de Negócio (ETL): Stage -> Tabela Final
            processarEtl(conn);

            conn.commit();
            System.out.println("✅ SUCESSO: Importação e Processamento concluídos!");

        } catch (Exception e) {
            System.err.println("❌ ERRO GERAL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void criarTabelaStage(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS stage_stock_tecnico (id INT AUTO_INCREMENT PRIMARY KEY");
            // Cria 32 colunas de texto genéricas (col01 a col32)
            for (int i = 1; i <= 32; i++) {
                sql.append(", col").append(String.format("%02d", i)).append(" TEXT");
            }
            sql.append(", data_importacao DATETIME DEFAULT CURRENT_TIMESTAMP)");
            stmt.execute(sql.toString());
        }
    }

    private static void processarEtl(Connection conn) throws SQLException {
        // Limpa tabela final
        try (Statement st = conn.createStatement()) {
            st.execute("TRUNCATE TABLE estock_tecnico");
        }

        // Monta a query de transferência com os mapeamentos e conversões
        // NOTA: O mapeamento das colunas da Tabela Final (estock_tecnico) x Colunas da Stage (baseadas no Excel)
        
        // Mapeamento baseado no que validamos anteriormente:
        // Tabela 1 (Fonte) <-- Stage col01
        // Tabela 2 (ID) <-- Stage col02 (converter para INT)
        // ...
        // Tabela 16 (tecnologia) <-- Stage col17
        // Tabela 28 (status) <-- Stage col29
        
        String sqlTransfer = 
            "INSERT INTO estock_tecnico (" +
            "Fonte_de_dados, id_tecnico, nome_do_tecnico, centro_fisico_tecnico, origem_centro_materiais, " + // 1-5
            "origem_pool_centro_materiais, sku, descricao_sku, descricao_estado, numero_de_serie, " + // 6-10
            "quantidade, id_grupo, descricao_grupo, ultima_modificacao, companhia, " + // 11-15
            "tecnologia_no_validados, origem_centro_fisico, motivo_de_indisponibilidade, wo, uf_do_tecnico, " + // 16-20
            "trasferencia, devolucoes, id_regra, retirada_danificada, estado_blockchain, " + // 21-25
            "coodernador, supervisor, status, dias, gerente, " + // 26-30
            "status_aging, expurgO" + // 31-32
            ") " +
            "SELECT " +
            "'Importação Automática', " + // 1. Fonte
            "s.col01, " + // 2. ID Técnico (Original com letras, agora que é VARCHAR)
            "s.col02, s.col03, s.col04, " + // 3-5
            "s.col05, s.col06, s.col07, s.col08, s.col09, " + // 6-10
            "CAST(NULLIF(REGEXP_REPLACE(s.col10, '[^0-9]', ''), '') AS UNSIGNED), " + // 11. Qtd
            "CAST(NULLIF(REGEXP_REPLACE(s.col11, '[^0-9]', ''), '') AS UNSIGNED), " + // 12. ID Grupo
            "s.col12, s.col13, s.col14, " + // 13-15
            
            "s.col16, " + // 16. Tecnologia
            "s.col17, " + // 17. Origem Físico
            "s.col18, " + // 18. Motivo
            "s.col19, " + // 19. WO
            "s.col20, " + // 20. UF
            "s.col21, s.col22, " + // 21-22
            "CAST(NULLIF(REGEXP_REPLACE(s.col23, '[^0-9]', ''), '') AS UNSIGNED), " + // 23. ID Regra
            "s.col24, s.col25, " + // 24-25 (Retirada, Blockchain)
            
            "f.coordenador, " + // 26. Coordenador (Vem da Força SP)
            "f.supervisor, " + // 27. Supervisor (Vem da Força SP)
            "f.status, " + // 28. Status (Vem da Força SP)
            
            "CAST(NULLIF(REGEXP_REPLACE(s.col29, '[^0-9]', ''), '') AS UNSIGNED), " + // 29. Dias (origem arquivo, se tiver)
            "NULL, " + // 30. Gerente (Não tem na Força SP)
            "NULL, " + // 31. Aging
            "NULL " + // 32. Expurgo
            "FROM stage_stock_tecnico s " +
            "LEFT JOIN forca_sp f ON f.sap = REGEXP_REPLACE(s.col01, '[^0-9]', '')"; // Join pelo ID numérico

        try (Statement st = conn.createStatement()) {
            int linhas = st.executeUpdate(sqlTransfer);
            System.out.println("📊 Dados processados e transferidos: " + linhas + " registros.");
        }
    }

    private static boolean processarParaStage(File arquivo, Connection conn) {
        // Query generica para inserir as 32 colunas de texto
        StringBuilder sql = new StringBuilder("INSERT INTO stage_stock_tecnico (");
        for (int i=1; i<=32; i++) sql.append("col").append(String.format("%02d", i)).append(",");
        sql.setLength(sql.length()-1); // remove ultima virgula
        sql.append(") VALUES (");
        for (int i=1; i<=32; i++) sql.append("?,");
        sql.setLength(sql.length()-1);
        sql.append(")");
        
        String sqlInsert = sql.toString();

        if (arquivo.getName().toLowerCase().endsWith(".csv")) {
            return importarCSV(arquivo, conn, sqlInsert);
        } else {
            try {
                return importarExcel(arquivo, conn, sqlInsert);
            } catch (Exception e) {
                if (e.toString().contains("ZipException") || e.toString().contains("OfficeXmlFileException")) {
                    return importarCSV(arquivo, conn, sqlInsert);
                }
                e.printStackTrace();
                return false;
            }
        }
    }

    // Métodos de leitura simplificados (apenas Strings)
    private static boolean importarExcel(File arquivo, Connection conn, String sqlInsert) throws Exception {
        try (FileInputStream fis = new FileInputStream(arquivo);
             Workbook workbook = new XSSFWorkbook(fis);
             PreparedStatement ps = conn.prepareStatement(sqlInsert)) {

            Sheet sheet = workbook.getSheet("DADOS");
            if (sheet == null) sheet = workbook.getSheetAt(0);

            int contador = 0;
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Pula cabeçalho

                // Verifica linha vazia
                if (row.getCell(0) == null && row.getCell(1) == null) continue;

                for (int i = 0; i < 32; i++) {
                    // Mapeamento direto: Coluna 0 do Excel -> col01 da Stage
                    ps.setString(i + 1, getCellValueAsString(row.getCell(i)));
                }
                ps.addBatch();
                contador++;
                if (contador % 1000 == 0) ps.executeBatch();
            }
            if (contador > 0) ps.executeBatch();
            
            System.out.println("📥 Excel carregado na Stage: " + contador + " linhas.");
            return true;
        }
    }

    private static boolean importarCSV(File arquivo, Connection conn, String sqlInsert) {
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(new java.io.FileInputStream(arquivo), CHARSET_LEITURA));
             PreparedStatement ps = conn.prepareStatement(sqlInsert)) {

            String linha;
            int contador = 0;
            boolean primeiraLinha = true;

            while ((linha = br.readLine()) != null) {
                if (primeiraLinha) { primeiraLinha = false; continue; }
                if (linha.trim().isEmpty()) continue;

                String[] colunas = linha.split(";", -1);
                if (colunas.length < 5) {
                    String[] colunasVirgula = linha.split(",", -1);
                    if (colunasVirgula.length > colunas.length) colunas = colunasVirgula;
                }

                for (int i = 0; i < 32; i++) {
                    String valor = (i < colunas.length) ? colunas[i] : null;
                    if (valor != null) {
                        valor = valor.trim();
                        if (valor.startsWith("\"") && valor.endsWith("\"") && valor.length() > 1) {
                            valor = valor.substring(1, valor.length() - 1);
                        }
                    }
                    ps.setString(i + 1, valor);
                }
                ps.addBatch();
                contador++;
                if (contador % 1000 == 0) ps.executeBatch();
            }
            ps.executeBatch();
            System.out.println("📥 CSV carregado na Stage: " + contador + " linhas.");
            return true;
        } catch (Exception e) {
            System.err.println("❌ Erro CSV: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        try {
            switch (cell.getCellType()) {
                case STRING: return cell.getStringCellValue().trim();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) return new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
                    double val = cell.getNumericCellValue();
                    return (val == (long) val) ? String.format("%d", (long) val) : String.valueOf(val);
                case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
                case FORMULA: return cell.toString();
                default: return "";
            }
        } catch (Throwable t) { return ""; }
    }
}