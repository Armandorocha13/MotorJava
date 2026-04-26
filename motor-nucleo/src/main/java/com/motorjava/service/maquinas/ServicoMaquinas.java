package com.motorjava.service.maquinas;

import java.util.function.Consumer;

import com.motorjava.config.GerenciadorConfiguracao;
import com.motorjava.giro.AppGiroMaquinario;

public class ServicoMaquinas {
    private final Consumer<String> logger;
    private final String downloadsDirStr;
    private final String targetDirStr;
    private final String caminhoConfig;
    private final String caminhoBase;
    private final String pastaBackup;
    
    private final AppGiroMaquinario giroApp;

    public ServicoMaquinas(Consumer<String> logger) {
        this.logger = logger;
        this.downloadsDirStr = GerenciadorConfiguracao.get("path.downloads", System.getProperty("user.home") + "\\Downloads");
        this.targetDirStr = GerenciadorConfiguracao.get("path.target.maquinaria", "C:\\Users\\user\\Desktop\\ARMANDO POWER BI\\Marquinario locados e proprios");
        this.caminhoConfig = GerenciadorConfiguracao.get("path.excel.config");
        this.caminhoBase = GerenciadorConfiguracao.get("path.excel.base");
        this.pastaBackup = GerenciadorConfiguracao.get("path.backup.maquinaria");
        
        this.giroApp = new AppGiroMaquinario();
    }

    public void sincronizarConfiguracoes() throws Exception {
        try {
            logger.accept("Iniciando sincronização de configurações via módulo de automação...");
            giroApp.sincronizarConfiguracoes(caminhoConfig, caminhoBase);
            logger.accept("✓ Sucesso: Configurações sincronizadas.");
        } catch (Exception e) {
            logger.accept("x Erro na sincronização: " + e.getMessage());
            throw e;
        }
    }

    public void atualizarBaseMaquinario() throws Exception {
        try {
            logger.accept("Iniciando processamento de base via módulo de automação...");
            giroApp.atualizarBase(caminhoBase, pastaBackup, downloadsDirStr, targetDirStr);
            logger.accept("✓ Sucesso: Base e backup atualizados.");
        } catch (Exception e) {
            logger.accept("x Erro no processo: " + e.getMessage());
            throw e;
        }
    }

}
