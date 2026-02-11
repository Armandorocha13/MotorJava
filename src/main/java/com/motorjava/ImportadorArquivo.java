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
        String caminhoArquivo = "C:\\Users\\user\\Desktop\\EQUIPAMENTO_SERIALIZADOS_VOLANTE_SP.xlsx";
        executarCarga(caminhoArquivo);
    }

    /**
     * MÉTODO DE CARGA
     * Executa todo o processo de leitura e gravação.
     * @param filePath Caminho completo do arquivo .xlsx
     */
    public static void executarCarga(String filePath) {
        File arquivo = new File(filePath);
        if (!arquivo.exists()) {
            System.err.println("ERRO CRÍTICO: Arquivo não encontrado no caminho: " + filePath);
            return;
        }

        // --- PREPARAÇÃO DAS QUERIES ---
        // Query de Insert com 33 interrogações (uma para cada coluna) + 1 data automática
        String sqlInsert = "INSERT INTO estoque_vivo_historico VALUES (" + "?,".repeat(33) + "CURRENT_DATE)";
        
        // Query para não duplicar dados do mesmo dia
        String sqlDeleteHoje = "DELETE FROM estoque_vivo_historico WHERE data_snapshot = CURRENT_DATE";

        // --- INÍCIO DA CONEXÃO COM BANCO ---
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false); // IMPORTANTE: Desliga o salvamento automático para fazer tudo de uma vez (Transaction)

            // 1. LIMPEZA PRÉVIA
            // Remove dados se já rodou hoje, para evitar duplicidade
            try (Statement st = conn.createStatement()) {
                st.execute(sqlDeleteHoje);
                System.out.println("🧹 Limpeza realizada: Dados de hoje removidos para nova carga.");
            }

            System.out.println("Iniciando leitura do Excel: " + filePath);

            // 2. LEITURA DO ARQUIVO EXCEL
            try (FileInputStream fis = new FileInputStream(new File(filePath));
                 Workbook workbook = new XSSFWorkbook(fis)) { // XSSFWorkbook é para arquivos .xlsx novos

                // Pega a aba chamada "DADOS"
                Sheet sheet = workbook.getSheet("DADOS");
                if (sheet == null) {
                    // Fallback se não achar DADOS, tenta pegar índice 0 mas avisa
                    System.err.println("AVISO: Aba 'DADOS' não encontrada. Tentando usar a primeira aba...");
                    sheet = workbook.getSheetAt(0);
                }

                PreparedStatement ps = conn.prepareStatement(sqlInsert);
                int contador = 0;
                int linhasIgnoradas = 0;

                // 3. ITERAÇÃO SOBRE AS LINHAS
                for (Row row : sheet) {
                    // Pula o cabeçalho (linha 0)
                    if (row.getRowNum() == 0)
                        continue;

                    // Verificação básica se a linha está vazia
                    if (row.getCell(0) == null && row.getCell(1) == null) {
                        linhasIgnoradas++;
                        continue;
                    }

                    // Preenche as 33 colunas vindas do Excel
                    for (int i = 0; i < 33; i++) {
                        Cell cell = row.getCell(i);
                        String valor = getCellValueAsString(cell);

                        if (valor == null || valor.isEmpty()) {
                            ps.setNull(i + 1, Types.VARCHAR); // Se vazio, manda NULL pro banco
                        } else {
                            ps.setString(i + 1, valor);
                        }
                    }

                    ps.addBatch(); // Adiciona no pacote de envio

                    // Executa a cada 1000 linhas para não "entupir" a memória
                    if (++contador % 1000 == 0) {
                        ps.executeBatch();
                        System.out.println("Processadas " + contador + " linhas...");
                    }
                }

                ps.executeBatch(); // Insere o restante
                
                // 4. FINALIZAÇÃO
                conn.commit(); // CONFIRMA A GRAVAÇÃO NO BANCO (Se der erro antes, nada é salvo)
                System.out.println("✅ Carga finalizada! Total: " + contador + " registros inseridos.");

            } catch (Exception e) {
                conn.rollback(); // Se der erro no Excel, cancela tudo no banco
                System.err.println("❌ Falha na leitura do Excel. Transação desfeita.");
                throw e;
            }

        } catch (Exception e) {
            System.err.println("ERRO NA CARGA: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Converte qualquer tipo de célula (Texto, Número, Booleano) para String
     * Isso evita erros de "Cannot get a STRING value from a NUMERIC cell"
     */
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