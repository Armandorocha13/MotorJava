package com.motorjava.service;

import com.motorjava.config.Config;
import java.io.File;
import java.util.function.Consumer;

public class VivoService {
    private final Consumer<String> logger;

    public VivoService(Consumer<String> logger) {
        this.logger = logger;
    }

    private void log(String msg) {
        if (logger != null)
            logger.accept(msg);
    }

    /**
     * Executa a atualização do Excel via Script VBS.
     */
    public void atualizarExcelVivo() {
        log("Iniciando atualização de dados Vivo Aging...");
        try {
            String scriptPath = Config.SCRIPT_VIVO_VBS;
            File script = new File(scriptPath);

            if (!script.exists()) {
                log("ERRO: Script VBS não encontrado em: " + scriptPath);
                return;
            }

            Process p = Runtime.getRuntime().exec("wscript \"" + scriptPath + "\"");
            int exitCode = p.waitFor();

            if (exitCode == 0) {
                log("Sucesso: Planilha Vivo Aging atualizada.");
            } else {
                log("Falha: O script retornou código " + exitCode);
            }
        } catch (Exception e) {
            log("Erro crítico no Vivo Service: " + e.getMessage());
        }
    }

    /**
     * Inicia a carga de dados para o Banco.
     */
    public void importarCargaVivo() {
        log("Iniciando importação de carga Vivo para o Banco...");
        try {
            // Aqui chamamos o importador que já existe
            String pathData = Config.PATH_VIVO_DATA + "\\EQUIPAMENTO_SERIALIZADOS_VOLANTE_SP.xlsx";
            ImportadorArquivo.executarCarga(pathData);
            log("Sucesso: Carga Vivo processada e salva no Banco.");
        } catch (Exception e) {
            log("Erro na importação Vivo: " + e.getMessage());
        }
    }
}
