package com.motorjava;

import com.motorjava.config.DatabaseConfig;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.text.SimpleDateFormat;

public class ImportadorArquivo {

    // A conexão é obtida via DatabaseConfig

    public static void main(String[] args) {
        // Ajustado para o arquivo .xlsx correto
        String caminhoArquivo = "C:\\Users\\user\\Desktop\\ARMANDO POWER BI\\VivoAging\\Equipamentos serializados\\001.xlsx";
        executarCarga(caminhoArquivo);
    }

    private static final java.nio.charset.Charset CHARSET_LEITURA = java.nio.charset.StandardCharsets.ISO_8859_1;

    public static void executarCarga(String filePath) {
        File arquivo = new File(filePath);
        if (!arquivo.exists()) {
            System.err.println("ERRO CRÍTICO: Arquivo não encontrado no caminho: " + filePath);
            return;
        }

        // SQL: 33 placeholders (origem_arquivo + 25 CSV + 7 extras) + CURRENT_DATE para data_snapshot
        // Total: 34 colunas na tabela
        String sqlInsert = "INSERT INTO estoque_vivo_historico VALUES (" + "?,".repeat(32) + "?,CURRENT_DATE)";
        
        String sqlDeleteHoje = "DELETE FROM estoque_vivo_historico WHERE data_snapshot = CURRENT_DATE";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false); // Inicia transação

            // 1. Limpa dados de hoje se já existirem
            try (Statement st = conn.createStatement()) {
                st.execute(sqlDeleteHoje);
            }

            System.out.println("Iniciando leitura do Excel/CSV: " + filePath);
            boolean sucesso = processarArquivo(arquivo, conn, sqlInsert);

            if (sucesso) {
                conn.commit();
                System.out.println("✅ Transação commitada com sucesso.");
            } else {
                conn.rollback();
                System.out.println("❌ Transação revertida devido a falhas.");
            }

        } catch (Exception e) {
            System.err.println("❌ ERRO GERAL NA CONEXÃO: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean processarArquivo(File arquivo, Connection conn, String sqlInsert) {
        // Tenta detectar pelo nome
        if (arquivo.getName().toLowerCase().endsWith(".csv")) {
            return importarCSV(arquivo, conn, sqlInsert);
        } else {
            // Tenta como Excel, se falhar (ex: renomeado errado), tenta CSV
            try {
                return importarExcel(arquivo, conn, sqlInsert);
            } catch (Exception e) {
                if (e.toString().contains("ZipException") || e.toString().contains("OfficeXmlFileException")) {
                    System.out.println("⚠️ Falha ao ler como Excel (possível CSV renomeado). Tentando ler como texto...");
                    return importarCSV(arquivo, conn, sqlInsert);
                }
                e.printStackTrace();
                return false;
            }
        }
    }

    private static boolean importarExcel(File arquivo, Connection conn, String sqlInsert) throws Exception {
        try (FileInputStream fis = new FileInputStream(arquivo);
             Workbook workbook = new XSSFWorkbook(fis)) {

                // Pega a aba chamada "DADOS"
                Sheet sheet = workbook.getSheet("DADOS");
                if (sheet == null) {
                    System.out.println("⚠️ AVISO: Aba 'DADOS' não encontrada. Tentando usar a primeira aba...");
                    sheet = workbook.getSheetAt(0);
                }
                
                System.out.println("📄 Aba Selecionada: " + sheet.getSheetName());
                int totalLinhas = sheet.getLastRowNum();
                System.out.println("📊 Total de linhas detectadas (estimado): " + totalLinhas);

                PreparedStatement ps = conn.prepareStatement(sqlInsert);
                int contador = 0;
                int linhasIgnoradas = 0;

                // Itera sobre as linhas
                for (Row row : sheet) {
                    int rowNum = row.getRowNum();

                    // Pula o cabeçalho (linha 0)
                    if (rowNum == 0) continue;

                    // Verificação básica se a linha está vazia
                    if (row.getCell(0) == null && row.getCell(1) == null) {
                        linhasIgnoradas++;
                        continue;
                    }
                    
                    // Debug da primeira linha de dados
                    if (contador == 0) {
                        System.out.println("🔎 [DEBUG] Primeira linha de dados encontrada (Linha " + rowNum + "):");
                        for (int k=0; k<5; k++) { // Mostra as primeiras 5 células
                            System.out.println("   Cel " + k + ": " + getCellValueAsString(row.getCell(k)));
                        }
                    }

                    // Preenche as 33 colunas vindas do Excel
                    for (int i = 0; i < 33; i++) {
                        Cell cell = row.getCell(i);
                        String valor = getCellValueAsString(cell);

                        if (valor == null || valor.isEmpty()) {
                            ps.setNull(i + 1, Types.VARCHAR);
                        } else {
                            ps.setString(i + 1, valor);
                        }
                    }

                    ps.addBatch();
                    contador++;

                    // Executa a cada 1000 linhas
                    if (contador % 1000 == 0) {
                        ps.executeBatch();
                        System.out.println("... Processados " + contador + " registros");
                    }
                }

                if (contador > 0) {
                    ps.executeBatch(); // Insere o restante
                    conn.commit(); // Grava tudo
                    System.out.println("✅ SUCESSO: Carga finalizada! Total de " + contador + " registros inseridos.");
                } else {
                    System.out.println("⚠️ ALERTA: Nenhum registro válido encontrado para importação.");
                    System.out.println("   Linhas ignoradas (vazias): " + linhasIgnoradas);
                }

                return true;
            } 
    }

    private static boolean importarCSV(File arquivo, Connection conn, String sqlInsert) {
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(new java.io.FileInputStream(arquivo), CHARSET_LEITURA));
             PreparedStatement ps = conn.prepareStatement(sqlInsert)) {

            String linha;
            int contador = 0;
            boolean primeiraLinha = true;

            System.out.println("📖 Lendo arquivo como CSV/Texto Plano...");
            
            while ((linha = br.readLine()) != null) {
                if (primeiraLinha) {
                    primeiraLinha = false;
                    // Mostra cabeçalho para debug
                    System.out.println("🔎 Cabeçalho CSV: " + linha);
                    continue; 
                }

                if (linha.trim().isEmpty()) continue;

                // Tenta separar por ponto e vírgula ou vírgula
                String[] colunas = linha.split(";", -1);
                if (colunas.length < 5) { // Se tiver muito pouca coluna, tenta vírgula
                    String[] colunasVirgula = linha.split(",", -1);
                    if (colunasVirgula.length > colunas.length) colunas = colunasVirgula;
                }

                // Total de 33 valores: 1 (origem_arquivo) + 25 (CSV) + 7 (extras como NULL)
                // data_snapshot (34ª coluna) é preenchida com CURRENT_DATE no SQL
                for (int i = 0; i < 33; i++) {
                    String valor;
                    
                    if (i == 0) {
                        // Coluna 1: origem_arquivo = nome do arquivo
                        valor = arquivo.getName();
                    } else if (i >= 1 && i <= 25) {
                        // Colunas 2-26: dados do CSV (índices 0-24)
                        int csvIndex = i - 1;
                        valor = (csvIndex < colunas.length) ? colunas[csvIndex] : null;
                        if (valor != null) {
                            valor = valor.trim();
                            // Remove aspas "valor"
                            if (valor.startsWith("\"") && valor.endsWith("\"") && valor.length() > 1) {
                                valor = valor.substring(1, valor.length() - 1);
                            }
                        }
                    } else {
                        // Colunas 27-33: extras como NULL
                        valor = null;
                    }
                    
                    // Insere o valor como está (VARCHAR aceita qualquer texto)
                    if (valor == null || valor.isEmpty()) {
                        ps.setNull(i + 1, Types.VARCHAR);
                    } else {
                        ps.setString(i + 1, valor);
                    }
                }
                
                ps.addBatch();
                contador++;
                
                if (contador % 1000 == 0) {
                    System.out.println("... Processados " + contador + " CSV rows");
                    ps.executeBatch();
                }
            }
            
            ps.executeBatch();
            System.out.println("✅ Leitura CSV OK. Total: " + contador);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Erro ao ler CSV: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
        

    // Método auxiliar para validar se uma string parece ser uma data
    private static boolean isValidDate(String valor) {
        if (valor == null || valor.isEmpty()) return false;
        
        // Aceita formatos comuns: yyyy-MM-dd, dd/MM/yyyy, dd-MM-yyyy
        // Rejeita texto puro como "POSTO AVANÇADO"
        
        // Se contém letras (exceto mês abreviado), não é data
        if (valor.matches(".*[a-zA-Z]{4,}.*")) {
            return false; // Texto com 4+ letras consecutivas
        }
        
        // Tenta parsear como data
        try {
            // Tenta formato ISO (yyyy-MM-dd)
            if (valor.matches("\\d{4}-\\d{2}-\\d{2}")) {
                java.sql.Date.valueOf(valor);
                return true;
            }
            
            // Tenta formato brasileiro (dd/MM/yyyy ou dd-MM-yyyy)
            if (valor.matches("\\d{2}[/-]\\d{2}[/-]\\d{4}")) {
                String[] partes = valor.split("[/-]");
                int dia = Integer.parseInt(partes[0]);
                int mes = Integer.parseInt(partes[1]);
                int ano = Integer.parseInt(partes[2]);
                
                // Validação básica
                if (dia >= 1 && dia <= 31 && mes >= 1 && mes <= 12 && ano >= 1900 && ano <= 2100) {
                    return true;
                }
            }
            
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    // Método auxiliar para converter qualquer célula em String
    private static String getCellValueAsString(Cell cell) {
        if (cell == null)
            return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    return sdf.format(cell.getDateCellValue());
                } else {
                    // Evita notação científica para números inteiros (ID, etc)
                    double val = cell.getNumericCellValue();
                    if (val == (long) val)
                        return String.format("%d", (long) val);
                    else
                        return String.valueOf(val);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return "";
        }
    }
}