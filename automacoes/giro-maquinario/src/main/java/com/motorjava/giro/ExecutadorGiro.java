package com.motorjava.giro;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Classe responsável pela execução da automação de Giro de Maquinário.
 * Utiliza o Jacob para automação VBA no Excel.
 */
public class ExecutadorGiro {

    /**
     * Sincroniza as configurações entre a planilha de configuração e a base.
     */
    public void sincronizarConfiguracoes(String caminhoConfig, String caminhoBase) throws Exception {
        ActiveXComponent excel = null;
        try {
            excel = new ActiveXComponent("Excel.Application");
            excel.setProperty("Visible", new Variant(false));
            Dispatch workbooks = excel.getProperty("Workbooks").toDispatch();

            // Abrir Planilha de Configuração
            Dispatch wbConfig = Dispatch.call(workbooks, "Open", caminhoConfig).toDispatch();
            
            // Rodar Macro de Sincronização (Ajustar nome se necessário no VBA)
            try {
                Dispatch.call(excel, "Run", "SincronizarConfiguracoes");
            } catch (Exception e) {
                // Se a macro tiver outro nome ou falhar, tenta salvar e fechar
            }

            Dispatch.call(wbConfig, "Save");
            Dispatch.call(wbConfig, "Close", new Variant(false));

        } finally {
            if (excel != null) {
                excel.invoke("Quit", new Variant[] {});
            }
        }
    }

    /**
     * Atualiza a base de maquinário processando os arquivos da pasta de Downloads.
     */
    public void atualizarBase(String caminhoBase, String pastaBackup, String diretorioDownloads, String diretorioDestino) throws Exception {
        // 1. Criar Backup
        fazerBackup(caminhoBase, pastaBackup);

        // 2. Processar via Excel/VBA
        ActiveXComponent excel = null;
        try {
            excel = new ActiveXComponent("Excel.Application");
            excel.setProperty("Visible", new Variant(false));
            Dispatch workbooks = excel.getProperty("Workbooks").toDispatch();

            Dispatch wbBase = Dispatch.call(workbooks, "Open", caminhoBase).toDispatch();
            
            // Executa a macro principal de processamento
            Dispatch.call(excel, "Run", "ExecutarGiroMaquinario");

            Dispatch.call(wbBase, "Save");
            Dispatch.call(wbBase, "Close", new Variant(false));

            // 3. Mover resultado para o destino final se necessário
            // (Lógica customizada conforme a necessidade do usuário)

        } finally {
            if (excel != null) {
                excel.invoke("Quit", new Variant[] {});
            }
        }
    }

    private void fazerBackup(String arquivoOriginal, String pastaBackup) throws Exception {
        File original = new File(arquivoOriginal);
        if (!original.exists()) return;

        File pasta = new File(pastaBackup);
        if (!pasta.exists()) pasta.mkdirs();

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nomeBackup = "BACKUP_" + timestamp + "_" + original.getName();
        File destino = new File(pasta, nomeBackup);

        Files.copy(original.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}
