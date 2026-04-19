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

    public void sincronizarConfiguracoes() throws Exception {
        String caminhoConfig = "C:\\Users\\user\\Desktop\\ARQUVOS\\RELATORIOS\\POWERBI\\BASE DE DADOS\\config\\configMaquinarios.xlsx";
        String caminhoBase = "C:\\Users\\user\\Desktop\\ARQUVOS\\RELATORIOS\\POWERBI\\BASE DE DADOS\\Contagem de dias maquinario.xlsm";
        
        ActiveXComponent excel = null;
        Dispatch wbConfig = null;
        Dispatch wbBase = null;

        try {
            logger.accept("Iniciando sincronização de configurações...");
            excel = new ActiveXComponent("Excel.Application");
            excel.setProperty("Visible", new Variant(true));
            excel.setProperty("DisplayAlerts", new Variant(false));

            Dispatch workbooks = excel.getProperty("Workbooks").toDispatch();

            // 1. Abrir base principal e preparar aba CONFIGURAÇÃO
            logger.accept("Abrindo base principal...");
            wbBase = Dispatch.call(workbooks, "Open", caminhoBase).toDispatch();
            Thread.sleep(1000);
            
            Dispatch.call(wbBase, "Activate");
            Dispatch sheets = Dispatch.get(wbBase, "Sheets").toDispatch();
            int count = Dispatch.get(sheets, "Count").toInt();
            Dispatch targetSheet = null;
            
            for (int i = 1; i <= count; i++) {
                Dispatch s = Dispatch.call(sheets, "Item", i).toDispatch();
                String name = Dispatch.get(s, "Name").toString();
                if (name.toUpperCase().contains("CONFIGURA") && name.toUpperCase().contains("O")) {
                    targetSheet = s;
                    break;
                }
            }

            if (targetSheet == null) {
                throw new Exception("Aba 'CONFIGURAÇÃO' não encontrada na planilha principal.");
            }
            
            Dispatch.call(targetSheet, "Select");
            
            // Limpar apenas o conteúdo usado para ser menos agressivo
            logger.accept("Limpando dados antigos da configuração...");
            try {
                Dispatch usedRangeTarget = Dispatch.get(targetSheet, "UsedRange").toDispatch();
                Dispatch.call(usedRangeTarget, "ClearContents");
            } catch (Exception e) {}

            // 2. Abrir arquivo de configuração e copiar conteúdo
            logger.accept("Abrindo arquivo de configuração...");
            wbConfig = Dispatch.call(workbooks, "Open", caminhoConfig).toDispatch();
            Dispatch sheetsConfig = Dispatch.get(wbConfig, "Sheets").toDispatch();
            Dispatch sheetConfig = Dispatch.call(sheetsConfig, "Item", 1).toDispatch();
            Dispatch usedRange = Dispatch.get(sheetConfig, "UsedRange").toDispatch();
            
            int rows = Dispatch.get(Dispatch.get(usedRange, "Rows").toDispatch(), "Count").toInt();
            logger.accept("Novos dados: " + rows + " linhas encontradas.");
            
            // Cópia direta para o destino (mais robusto que clipboard)
            Dispatch rangeA1 = Dispatch.call(targetSheet, "Range", "A1").toDispatch();
            Dispatch.call(usedRange, "Copy", rangeA1);
            
            logger.accept("Configurações sincronizadas com sucesso.");

            // 4. Salvar e Fechar
            Dispatch.call(wbBase, "Save");
            Thread.sleep(1000);
            logger.accept("✓ Configurações sincronizadas com sucesso!");

        } catch (Exception e) {
            logger.accept("x Erro na sincronização: " + e.getMessage());
            throw e;
        } finally {
            try {
                if (wbConfig != null) Dispatch.call(wbConfig, "Close", new Variant(false));
                if (wbBase != null) Dispatch.call(wbBase, "Close", new Variant(true));
                if (excel != null) excel.invoke("Quit", new Variant[] {});
            } catch (Exception ex) {}
        }
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
            
            logger.accept("Arquivo aberto! Verificando estrutura...");

            // Limpeza de nomes desativada pois pode estar quebrando a macro 'maquinarios'
            /*
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
            */

            // 2.2 GARANTIR QUE ESTÁ NA ABA CORRETA
            Dispatch sheets = Dispatch.get(wb, "Sheets").toDispatch();
            Dispatch targetSheet = Dispatch.call(sheets, "Item", "BASE DE DADOS").toDispatch();
            Dispatch.call(targetSheet, "Select");
            Dispatch rangeA1 = Dispatch.call(targetSheet, "Range", "A1").toDispatch();
            Dispatch.call(rangeA1, "Select");

            // 3. EXECUTAR A MACRO
            logger.accept("Executando Macro: " + nomeMacro);
            Dispatch.call(excel, "Run", nomeMacro);
            Thread.sleep(5000);

            // 4. SALVAR A BASE ORIGINAL (LIMPA E ATUALIZADA)
            logger.accept("Salvando alterações na base original...");
            Dispatch.call(wb, "Save");
            Thread.sleep(2000);

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
