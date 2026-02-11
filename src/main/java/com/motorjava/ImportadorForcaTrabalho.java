package com.motorjava;

import com.motorjava.config.DatabaseConfig;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.sql.*;

/**
 * Importador de dados da Força de Trabalho (técnicos)
 * Lê arquivo Excel "Força VIVO SP.xlsx" e importa para a tabela forca_trabalho
 */
public class ImportadorForcaTrabalho {
    
    public static void executarCarga(String filePath) {
        File arquivo = new File(filePath);
        if (!arquivo.exists()) {
            System.err.println("ERRO: Arquivo não encontrado: " + filePath);
            return;
        }

        // SQL para inserir ou atualizar (UPSERT)
        String sqlUpsert = "INSERT INTO forca_trabalho " +
                "(sap, colaborador, sexo, funcao, coordenador, supervisor, contato, observacao, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "colaborador = VALUES(colaborador), " +
                "sexo = VALUES(sexo), " +
                "funcao = VALUES(funcao), " +
                "coordenador = VALUES(coordenador), " +
                "supervisor = VALUES(supervisor), " +
                "contato = VALUES(contato), " +
                "observacao = VALUES(observacao), " +
                "status = VALUES(status)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlUpsert);
             FileInputStream fis = new FileInputStream(arquivo);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            int contador = 0;
            int atualizado = 0;

            System.out.println("📖 Lendo arquivo: " + arquivo.getName());
            System.out.println("📊 Total de linhas: " + sheet.getLastRowNum());

            // Pula o cabeçalho (linha 0)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // Lê as colunas do Excel
                String sap = getCellValueAsString(row.getCell(0));
                String colaborador = getCellValueAsString(row.getCell(1));
                String sexo = getCellValueAsString(row.getCell(2));
                String funcao = getCellValueAsString(row.getCell(3));
                String coordenador = getCellValueAsString(row.getCell(4));
                String supervisor = getCellValueAsString(row.getCell(5));
                String contato = getCellValueAsString(row.getCell(6));
                String observacao = getCellValueAsString(row.getCell(7));
                String status = getCellValueAsString(row.getCell(8));

                // Validação básica
                if (sap == null || sap.trim().isEmpty()) {
                    System.out.println("⚠️ Linha " + (i + 1) + ": SAP vazio, pulando...");
                    continue;
                }

                // Define valores padrão
                if (status == null || status.trim().isEmpty()) {
                    status = "ATIVO";
                }

                // Prepara o statement
                ps.setString(1, sap.trim());
                ps.setString(2, colaborador != null ? colaborador.trim() : "");
                ps.setString(3, sexo != null ? sexo.trim().toUpperCase() : null);
                ps.setString(4, funcao != null ? funcao.trim() : null);
                ps.setString(5, coordenador != null ? coordenador.trim() : null);
                ps.setString(6, supervisor != null ? supervisor.trim() : null);
                ps.setString(7, contato != null ? contato.trim() : null);
                ps.setString(8, observacao != null ? observacao.trim() : null);
                ps.setString(9, status.trim().toUpperCase());

                int resultado = ps.executeUpdate();
                if (resultado > 0) {
                    contador++;
                    if (resultado == 2) atualizado++; // ON DUPLICATE KEY UPDATE retorna 2
                }

                if (contador % 50 == 0) {
                    System.out.println("... Processados " + contador + " registros");
                }
            }

            System.out.println("✅ Importação concluída!");
            System.out.println("📊 Total processado: " + contador);
            System.out.println("🔄 Atualizados: " + atualizado);
            System.out.println("➕ Novos: " + (contador - atualizado));

        } catch (Exception e) {
            System.err.println("❌ Erro ao importar força de trabalho: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                // Para números (como SAP), retorna sem decimais
                double val = cell.getNumericCellValue();
                if (val == (long) val) {
                    return String.format("%d", (long) val);
                }
                return String.valueOf(val);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return null;
        }
    }

    public static void main(String[] args) {
        // Teste standalone
        if (args.length > 0) {
            executarCarga(args[0]);
        } else {
            System.out.println("Uso: java ImportadorForcaTrabalho <caminho_arquivo.xlsx>");
        }
    }
}
