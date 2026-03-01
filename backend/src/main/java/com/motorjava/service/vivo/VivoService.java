package com.motorjava.service.vivo;

import com.motorjava.config.Config;
import java.io.File;
import java.util.function.Consumer;

import com.motorjava.service.common.ImportadorArquivo;
import com.motorjava.service.ihs.OnePageService;

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
            String pathData = Config.PATH_VIVO_DATA + "\\EQUIPAMENTO_SERIALIZADOS_VOLANTE_SP.xlsx";
            ImportadorArquivo.executarCarga(pathData);
            log("Sucesso: Carga Vivo processada e salva no Banco.");
        } catch (Exception e) {
            log("Erro na importação Vivo: " + e.getMessage());
        }
    }

    /**
     * Importa a planilha de Força SP para o Banco de Dados.
     */
    public void importarForcaSP() {
        log("Iniciando importação Força SP...");
        try {
            File file = new File(Config.PATH_FORCA_SP);
            if (!file.exists()) {
                log("❌ ERRO: Arquivo não encontrado: " + Config.PATH_FORCA_SP);
                return;
            }
            OnePageService svc = new OnePageService(logger);
            svc.importarArquivo(file, "forca_sp");
            log("✅ Força SP importada com sucesso.");
        } catch (Exception e) {
            log("❌ Erro na importação Força SP: " + e.getMessage());
        }
    }
}
