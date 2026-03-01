package com.motorjava.service.emis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.function.Consumer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.motorjava.config.DatabaseConfig;

public class EmisService {
    private final Consumer<String> logger;
    private final String downloadsDirStr;
    private final String targetDirStr;

    public EmisService(Consumer<String> logger) {
        this.logger = logger;
        this.downloadsDirStr = System.getProperty("user.home") + "\\Downloads";
        this.targetDirStr = "C:\\Users\\user\\Desktop\\ARMANDO POWER BI\\Emis e terminais";
    }

    public void renomearArquivosDaPasta() throws IOException {
        logger.accept("Varrer pasta de Downloads: " + downloadsDirStr);
        File dir = new File(downloadsDirStr);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IOException("Pasta de Downloads não encontrada: " + downloadsDirStr);
        }

        File targetFolder = new File(targetDirStr);
        if (!targetFolder.exists()) {
            logger.accept("Criando pasta destino: " + targetDirStr);
            Files.createDirectories(targetFolder.toPath());
        }

        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        int count = 0;
        for (File file : files) {
            if (file.isFile()) {
                String nome = file.getName().toLowerCase();
                if (nome.contains("terminais")) {
                    moverERenomear(file, "TERMINAIS");
                    count++;
                } else if (nome.contains("emis")) {
                    moverERenomear(file, "EMIS");
                    count++;
                }
            }
        }

        if (count == 0) {
            logger.accept("Nenhum arquivo relacionado a EMIS ou TERMINAIS foi encontrado em Downloads.");
        } else {
            logger.accept(count + " arquivo(s) processado(s) com sucesso.");
        }
    }

    private void moverERenomear(File sourceFile, String novoNomeBase) throws IOException {
        String originalName = sourceFile.getName();
        String extensao = "";
        int i = originalName.lastIndexOf('.');
        if (i > 0) {
            extensao = originalName.substring(i);
        }

        String novoNome = novoNomeBase + extensao;
        Path targetPath = Paths.get(targetDirStr, novoNome);

        logger.accept("Movendo arquivo " + originalName + " para " + targetPath.toString());
        Files.move(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
    }

    public void importarBancoDados() {
        logger.accept("Iniciando importação EMIS e TERMINAIS para o Banco de Dados (MySQL)...");

        importarArquivo("EMIS.xlsx", "emis", 9);
        importarArquivo("TERMINAIS.xlsx", "terminais", 26);

        logger.accept("Importação de todos os relatórios concluída!");
    }

    private void importarArquivo(String nomeArquivo, String tabela, int qtdColunas) {
        File file = new File(targetDirStr, nomeArquivo);
        if (!file.exists()) {
            logger.accept("Arquivo " + nomeArquivo + " não encontrado na pasta destino. Pulando...");
            return;
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
            // 1. Limpa a tabela antes de importar
            logger.accept("Limpando a tabela " + tabela + " antes da importação...");
            try (PreparedStatement stmtClean = conn.prepareStatement("TRUNCATE TABLE " + tabela)) {
                stmtClean.executeUpdate();
            }

            // 2. Realiza a Leitura e a Inserção do Excel
            logger.accept("Lendo arquivo " + nomeArquivo + "...");
            try (FileInputStream fis = new FileInputStream(file);
                    Workbook workbook = new XSSFWorkbook(fis)) {

                Sheet sheet = workbook.getSheetAt(0);

                // Montando instrução de INSERT (parametrizada para proteção)
                StringBuilder sql = new StringBuilder("INSERT INTO " + tabela + " VALUES (");
                for (int i = 0; i < qtdColunas; i++) {
                    sql.append("?");
                    if (i < qtdColunas - 1)
                        sql.append(",");
                }
                sql.append(")");

                try (PreparedStatement stmtInsert = conn.prepareStatement(sql.toString())) {
                    int batchSize = 0;
                    int rowsInserted = 0;

                    for (Row row : sheet) {
                        if (row.getRowNum() == 0)
                            continue; // Pula a primeira linha (cabeçalho)

                        for (int i = 0; i < qtdColunas; i++) {
                            Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            stmtInsert.setString(i + 1, getCellValueAsString(cell));
                        }
                        stmtInsert.addBatch();
                        batchSize++;

                        // Executa lote de inserções a cada 500 para ter melhor performance
                        if (batchSize % 500 == 0) {
                            stmtInsert.executeBatch();
                            rowsInserted += batchSize;
                            batchSize = 0;
                        }
                    }

                    // Inserir os registros restantes
                    if (batchSize > 0) {
                        stmtInsert.executeBatch();
                        rowsInserted += batchSize;
                    }

                    logger.accept("Sucesso: " + rowsInserted + " linha(s) inserida(s) na tabela " + tabela + ".");
                }
            }
        } catch (Exception e) {
            logger.accept("Erro ao importar o arquivo " + nomeArquivo + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null)
            return "";
        CellType type = cell.getCellType();
        if (type == CellType.STRING)
            return cell.getStringCellValue().trim();
        if (type == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue().toString();
            } else {
                double val = cell.getNumericCellValue();
                if (val == Math.floor(val)) {
                    return String.valueOf((long) val); // Remove casas decimais soltas
                }
                return String.valueOf(val);
            }
        }
        if (type == CellType.BOOLEAN)
            return String.valueOf(cell.getBooleanCellValue());
        return "";
    }
}
