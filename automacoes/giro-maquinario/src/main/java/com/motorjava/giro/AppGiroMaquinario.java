package com.motorjava.giro;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppGiroMaquinario {

    public static void main(String[] args) {
        System.out.println("--- Giro de Maquinário Automation ---");
        // Se houver argumentos, pode-se decidir qual método rodar
        // Por padrão, pode rodar ambos ou esperar um comando
    }

    public void sincronizarConfiguracoes(String caminhoConfig, String caminhoBase) throws Exception {
        ActiveXComponent excel = null;
        Dispatch wbConfig = null;
        Dispatch wbBase = null;

        try {
            System.out.println("Iniciando sincronização de configurações...");
            excel = new ActiveXComponent("Excel.Application");
            excel.setProperty("Visible", new Variant(true));
            excel.setProperty("DisplayAlerts", new Variant(false));
            excel.setProperty("AlertBeforeOverwriting", new Variant(false));

            Dispatch workbooks = excel.getProperty("Workbooks").toDispatch();

            System.out.println("Abrindo base principal...");
            wbBase = Dispatch.call(workbooks, "Open", caminhoBase).toDispatch();
            excel.setProperty("DisplayAlerts", new Variant(false)); 
            cleanNames(wbBase);
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
            
            System.out.println("Limpando dados antigos da configuração...");
            try {
                Dispatch usedRangeTarget = Dispatch.get(targetSheet, "UsedRange").toDispatch();
                Dispatch.call(usedRangeTarget, "ClearContents");
            } catch (Exception e) {}

            System.out.println("Abrindo arquivo de configuração...");
            wbConfig = Dispatch.call(workbooks, "Open", caminhoConfig).toDispatch();
            excel.setProperty("DisplayAlerts", new Variant(false)); 
            clearFilters(wbConfig);
            cleanNames(wbConfig);
            Dispatch sheetsConfig = Dispatch.get(wbConfig, "Sheets").toDispatch();
            Dispatch sheetConfig = Dispatch.call(sheetsConfig, "Item", 1).toDispatch();
            Dispatch usedRange = Dispatch.get(sheetConfig, "UsedRange").toDispatch();
            
            int rows = Dispatch.get(Dispatch.get(usedRange, "Rows").toDispatch(), "Count").toInt();
            System.out.println("Novos dados: " + rows + " linhas encontradas.");
            
            Dispatch rangeA1 = Dispatch.call(targetSheet, "Range", "A1").toDispatch();
            Dispatch.call(usedRange, "Copy", rangeA1);
            
            System.out.println("Configurações sincronizadas com sucesso.");
            cleanNames(wbBase);
            Dispatch.call(wbBase, "Save");
            Thread.sleep(1000);
            System.out.println("✓ Configurações sincronizadas com sucesso!");

        } finally {
            try {
                if (wbConfig != null) Dispatch.call(wbConfig, "Close", new Variant(false));
                if (wbBase != null) Dispatch.call(wbBase, "Close", new Variant(true));
                if (excel != null) excel.invoke("Quit", new Variant[] {});
            } catch (Exception ex) {}
        }
    }

    public void atualizarBase(String caminhoBase, String pastaBackup, String downloadsDir, String targetDir) throws Exception {
        String nomeMacro = "maquinarios";

        ActiveXComponent excel = null;
        Dispatch wb = null;
        
        try {
            // 1. Renomear arquivos da pasta downloads (Lógica que estava no MaquinasService)
            renomearArquivos(downloadsDir, targetDir);

            // 2. Backup
            File backupDir = new File(pastaBackup);
            if (!backupDir.exists()) backupDir.mkdirs();

            String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String nomeBackup = "Backup_Maquinario_" + timeStamp + ".xlsm";
            Path destinoBackup = Paths.get(pastaBackup, nomeBackup);
            
            System.out.println("Gerando backup em: " + destinoBackup.toString());
            Files.copy(Paths.get(caminhoBase), destinoBackup, StandardCopyOption.REPLACE_EXISTING);

            // 3. Excel
            System.out.println("Abrindo Excel...");
            excel = new ActiveXComponent("Excel.Application");
            excel.setProperty("Visible", new Variant(true)); 
            excel.setProperty("DisplayAlerts", new Variant(false)); 
            excel.setProperty("AlertBeforeOverwriting", new Variant(false)); 
            
            Dispatch workbooks = excel.getProperty("Workbooks").toDispatch();
            wb = Dispatch.call(workbooks, "Open", caminhoBase).toDispatch();
            excel.setProperty("DisplayAlerts", new Variant(false)); 
            clearFilters(wb);
            cleanNames(wb);
            
            Dispatch sheets = Dispatch.get(wb, "Sheets").toDispatch();
            Dispatch targetSheet = Dispatch.call(sheets, "Item", "BASE DE DADOS").toDispatch();
            Dispatch.call(targetSheet, "Select");
            Dispatch rangeA1 = Dispatch.call(targetSheet, "Range", "A1").toDispatch();
            Dispatch.call(rangeA1, "Select");

            System.out.println("Executando Macro: " + nomeMacro);
            Dispatch.call(excel, "Run", nomeMacro);
            Thread.sleep(5000);

            System.out.println("Salvando alterações...");
            cleanNames(wb);
            Dispatch.call(wb, "Save");
            Thread.sleep(2000);

            System.out.println("✓ Sucesso: Backup gerado e Base atualizada!");
            
        } finally {
            try {
                if (wb != null) Dispatch.call(wb, "Close", new Variant(false));
                if (excel != null) excel.invoke("Quit", new Variant[] {});
            } catch (Exception ex) {}
        }
    }

    private void renomearArquivos(String downloadsDirStr, String targetDirStr) throws IOException {
        File dir = new File(downloadsDirStr);
        if (!dir.exists() || !dir.isDirectory()) return;

        File targetFolder = new File(targetDirStr);
        if (!targetFolder.exists()) Files.createDirectories(targetFolder.toPath());

        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            String nome = file.getName().toLowerCase();
            if (nome.contains("ffa") && nome.contains("controle") && (nome.contains("locacao") || nome.contains("locação"))) {
                String originalName = file.getName();
                String extensao = originalName.substring(originalName.lastIndexOf('.'));
                Path targetPath = Paths.get(targetDirStr, "MAQUINAS LOCADAS E PROPRIAS" + extensao);
                Files.move(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Arquivo movido e renomeado: " + originalName);
            }
        }
    }

    private void cleanNames(Dispatch workbook) {
        try {
            Dispatch names = Dispatch.get(workbook, "Names").toDispatch();
            int count = Dispatch.get(names, "Count").toInt();
            System.out.println("Limpando " + count + " nomes do workbook...");
            for (int i = count; i >= 1; i--) {
                try {
                    Dispatch name = Dispatch.call(names, "Item", i).toDispatch();
                    Dispatch.call(name, "Delete");
                } catch (Exception e) {}
            }
        } catch (Exception e) {
            System.err.println("Erro ao limpar nomes: " + e.getMessage());
        }
    }

    private void clearFilters(Dispatch workbook) {
        try {
            Dispatch sheets = Dispatch.get(workbook, "Sheets").toDispatch();
            int count = Dispatch.get(sheets, "Count").toInt();
            for (int i = 1; i <= count; i++) {
                try {
                    Dispatch sheet = Dispatch.call(sheets, "Item", i).toDispatch();
                    Dispatch.put(sheet, "AutoFilterMode", new Variant(false));
                } catch (Exception e) {}
            }
        } catch (Exception e) {
            System.err.println("Erro ao limpar filtros: " + e.getMessage());
        }
    }
}
