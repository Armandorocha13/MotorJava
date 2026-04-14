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
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

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

    public void atualizarBaseMaquinario() throws Exception {
        // INFORMAÇÕES CONFIGURADAS PELO USUÁRIO
        String caminhoOriginal = "C:\\Users\\user\\Desktop\\ARQUVOS\\RELATORIOS\\POWERBI\\BASE DE DADOS\\Contagem de dias maquinario.xlsm";
        String pastaBackup = "C:\\Users\\user\\Desktop\\ARQUVOS\\RELATORIOS\\POWERBI\\Backup\\";
        String nomeMacro = "maquinarios"; // NOME DA MACRO NO EXCEL

        logger.accept("Iniciando processo de Atualização de Base via VBA...");
        ActiveXComponent excel = null;
        Dispatch wb = null;
        
        try {
            // 1. BACKUP AUTOMÁTICO
            File backupDir = new File(pastaBackup);
            if (!backupDir.exists()) backupDir.mkdirs();

            Path origem = Paths.get(caminhoOriginal);
            Path destino = Paths.get(pastaBackup + "Backup_Antes_Macro.xlsm");
            
            logger.accept("Gerando backup em: " + destino.toString());
            Files.copy(origem, destino, StandardCopyOption.REPLACE_EXISTING);

            // 2. INICIAR EXCEL (Motor VBA)
            logger.accept("Abrindo Excel (instância Jacob)...");
            excel = new ActiveXComponent("Excel.Application");
            excel.setProperty("Visible", new Variant(false)); 
            excel.setProperty("DisplayAlerts", new Variant(false)); // EVITA TRAVAMENTOS POR POP-UPS
            
            Dispatch workbooks = excel.getProperty("Workbooks").toDispatch();
            wb = Dispatch.call(workbooks, "Open", caminhoOriginal).toDispatch();

            // 2.1 GARANTIR QUE ESTÁ NA ABA CORRETA
            logger.accept("Selecionando aba 'BASE DE DADOS'...");
            Dispatch sheets = excel.getProperty("Sheets").toDispatch();
            Dispatch targetSheet = Dispatch.call(sheets, "Item", "BASE DE DADOS").toDispatch();
            Dispatch.call(targetSheet, "Select");

            // 3. EXECUTAR A GRAVAÇÃO
            logger.accept("Executando Macro: " + nomeMacro);
            Dispatch.call(excel, "Run", nomeMacro);

            // 4. SALVAR E FINALIZAR
            logger.accept("Salvando alterações...");
            Dispatch.call(wb, "Save");

            logger.accept("✓ Sucesso: Backup gerado e Base atualizada!");
            
        } catch (Exception e) {
            logger.accept("x Erro no processo VBA: " + e.getMessage());
            throw e;
        } finally {
            try {
                if (wb != null) {
                    Dispatch.call(wb, "Close", new Variant(false));
                }
                if (excel != null) {
                    excel.invoke("Quit", new Variant[] {});
                }
                logger.accept("Excel encerrado com segurança.");
            } catch (Exception ex) {
                // Silencioso no encerramento
            }
        }
    }
}
