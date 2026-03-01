package com.motorjava.service.maquinas;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
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

public class MaquinasService {
    private final Consumer<String> logger;
    private final String downloadsDirStr;
    private final String targetDirStr;

    public MaquinasService(Consumer<String> logger) {
        this.logger = logger;
        this.downloadsDirStr = System.getProperty("user.home") + "\\Downloads";
        this.targetDirStr = "C:\\Users\\user\\Desktop\\ARMANDO POWER BI\\Marquinario locados e proprios";
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
                // palavras-chaves: ffa controle locacao
                if (nome.contains("ffa") && nome.contains("controle")
                        && (nome.contains("locacao") || nome.contains("locação"))) {
                    moverERenomear(file, "MAQUINAS LOCADAS E PROPRIAS");
                    count++;
                }
            }
        }

        if (count == 0) {
            logger.accept("Nenhum arquivo 'ffa controle locacao' encontrado em Downloads.");
        } else {
            logger.accept(count + " arquivo(s) processado(s) com sucesso para o relatório de Maquinários.");
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
        logger.accept("Iniciando importação de Maquinários para o Banco de Dados (MySQL)...");
        String nomeArquivo = "MAQUINAS LOCADAS E PROPRIAS.xlsx";
        String tabela = "maquinarios";
        int qtdColunas = 14;

        File file = new File(targetDirStr, nomeArquivo);
        if (!file.exists()) {
            logger.accept("Arquivo " + nomeArquivo + " não encontrado na pasta destino. Pulando importação...");
            return;
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
            logger.accept("Limpando a tabela " + tabela + " antes da importação...");
            try (PreparedStatement stmtClean = conn.prepareStatement("TRUNCATE TABLE " + tabela)) {
                stmtClean.executeUpdate();
            }

            logger.accept("Lendo arquivo " + nomeArquivo + "...");
            try (FileInputStream fis = new FileInputStream(file);
                    Workbook workbook = new XSSFWorkbook(fis)) {

                Sheet sheet = workbook.getSheetAt(0);

                String sql = "INSERT INTO " + tabela + " (id_locacao, local_instalacao, equipamento, modelo, serie, "
                        + "data_inicio_locacao, data_devolucao, qtdade, data_inicio_periodo, data_final_periodo, "
                        + "dias, valor_contrato_unit, valor_total, observacao) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement stmtInsert = conn.prepareStatement(sql)) {
                    int batchSize = 0;
                    int rowsInserted = 0;

                    for (Row row : sheet) {
                        if (row.getRowNum() == 0)
                            continue; // Pula o cabeçalho

                        for (int i = 0; i < qtdColunas; i++) {
                            Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            stmtInsert.setString(i + 1, getCellValueAsString(cell));
                        }
                        stmtInsert.addBatch();
                        batchSize++;

                        if (batchSize % 500 == 0) {
                            stmtInsert.executeBatch();
                            rowsInserted += batchSize;
                            batchSize = 0;
                        }
                    }

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
                    return String.valueOf((long) val);
                }
                return String.valueOf(val);
            }
        }
        if (type == CellType.BOOLEAN)
            return String.valueOf(cell.getBooleanCellValue());
        return "";
    }
}
