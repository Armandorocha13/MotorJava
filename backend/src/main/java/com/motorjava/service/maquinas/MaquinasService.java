package com.motorjava.service.maquinas;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

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
        File dir = new File(downloadsDirStr);
        if (!dir.exists() || !dir.isDirectory()) return;

        File targetFolder = new File(targetDirStr);
        if (!targetFolder.exists()) Files.createDirectories(targetFolder.toPath());

        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            String nome = file.getName().toLowerCase();
            if (nome.contains("ffa") && nome.contains("controle") && (nome.contains("locacao") || nome.contains("locação"))) {
                moverERenomear(file, "MAQUINAS LOCADAS E PROPRIAS");
            }
        }
    }

    private void moverERenomear(File sourceFile, String novoNomeBase) throws IOException {
        String originalName = sourceFile.getName();
        String extensao = originalName.substring(originalName.lastIndexOf('.'));
        Path targetPath = Paths.get(targetDirStr, novoNomeBase + extensao);
        Files.move(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
    }

    public void atualizarBaseMaquinario() throws Exception {
        String caminhoOriginal = "C:\\Users\\user\\Desktop\\ARQUVOS\\RELATORIOS\\POWERBI\\BASE DE DADOS\\Contagem de dias maquinario.xlsm";
        String pastaBackup = "C:\\Users\\user\\Desktop\\ARQUVOS\\RELATORIOS\\POWERBI\\Backup\\";
        String nomeMacro = "maquinarios";

        ActiveXComponent excel = null;
        Dispatch wb = null;
        
        try {
            // 1. GERAR BACKUP COM DATA E HORA ANTES DE TUDO
            File backupDir = new File(pastaBackup);
            if (!backupDir.exists()) backupDir.mkdirs();

            String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String nomeBackup = "Backup_Maquinario_" + timeStamp + ".xlsm";
            Path destinoBackup = Paths.get(pastaBackup, nomeBackup);
            
            logger.accept("Gerando backup em: " + destinoBackup.toString());
            Files.copy(Paths.get(caminhoOriginal), destinoBackup, StandardCopyOption.REPLACE_EXISTING);

            // 2. INICIAR EXCEL
            logger.accept("Abrindo Excel (instância Jacob)...");
            excel = new ActiveXComponent("Excel.Application");
            
            // Força visível para interagir caso ocorra o erro estrutural de nomes
            excel.setProperty("Visible", new Variant(true)); 
            excel.setProperty("DisplayAlerts", new Variant(false)); 
            
            logger.accept("Tentando abrir arquivo original...");
            Dispatch workbooks = excel.getProperty("Workbooks").toDispatch();
            
            // SE TRAVAR AQUI, VOCÊ CLICA NO 'OK' DO NOME NO EXCEL
            wb = Dispatch.call(workbooks, "Open", caminhoOriginal).toDispatch();
            
            logger.accept("Arquivo aberto! Iniciando limpeza automatica de conflitos...");

            // Limpeza agressiva de nomes internos para que na próxima vez não peça nome
            try {
                Dispatch namesObj = Dispatch.get(wb, "Names").toDispatch();
                int count = Dispatch.get(namesObj, "Count").toInt();
                for (int i = count; i >= 1; i--) {
                    try {
                        Dispatch name = Dispatch.call(namesObj, "Item", i).toDispatch();
                        Dispatch.call(name, "Delete");
                    } catch (Exception e1) {}
                }
                logger.accept("Limpeza de " + count + " nomes concluída.");
            } catch (Exception e) {}

            // 2.2 GARANTIR QUE ESTÁ NA ABA CORRETA
            Dispatch sheets = Dispatch.get(wb, "Sheets").toDispatch();
            Dispatch targetSheet = Dispatch.call(sheets, "Item", "BASE DE DADOS").toDispatch();
            Dispatch.call(targetSheet, "Select");

            // 3. EXECUTAR A MACRO
            logger.accept("Executando Macro: " + nomeMacro);
            Dispatch.call(excel, "Run", nomeMacro);
            Thread.sleep(3000);

            // 4. SALVAR A BASE ORIGINAL (LIMPA E ATUALIZADA)
            logger.accept("Salvando alterações na base original...");
            Dispatch.call(wb, "Save");
            Thread.sleep(1000);

            logger.accept("✓ Sucesso: Backup gerado e Base original atualizada!");
            
        } catch (Exception e) {
            logger.accept("x Erro no processo: " + e.getMessage());
            throw e;
        } finally {
            try {
                if (wb != null) Dispatch.call(wb, "Close", new Variant(false));
                if (excel != null) excel.invoke("Quit", new Variant[] {});
                logger.accept("Processo finalizado.");
            } catch (Exception ex) {}
        }
    }
}
