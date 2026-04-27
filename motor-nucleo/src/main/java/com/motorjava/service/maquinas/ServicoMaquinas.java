package com.motorjava.service.maquinas;

import java.util.function.Consumer;
import com.motorjava.config.GerenciadorConfiguracao;
import com.motorjava.giro.ExecutadorGiro;

/**
 * Serviço responsável pela gestão de maquinários.
 * Segue o princípio de Encapsulamento.
 */
public class ServicoMaquinas {
    private final Consumer<String> logger;
    private final String diretorioDownloads;
    private final String diretorioDestino;
    private final String caminhoConfigExcel;
    private final String caminhoBaseExcel;
    private final String pastaBackup;
    
    private final ExecutadorGiro executadorGiro;

    public ServicoMaquinas(Consumer<String> logger) {
        this.logger = logger;
        this.diretorioDownloads = GerenciadorConfiguracao.get("path.downloads", System.getProperty("user.home") + "\\Downloads");
        this.diretorioDestino = GerenciadorConfiguracao.get("path.target.maquinaria", "C:\\Users\\user\\Desktop\\ARMANDO POWER BI\\Marquinario locados e proprios");
        this.caminhoConfigExcel = GerenciadorConfiguracao.get("path.excel.config");
        this.caminhoBaseExcel = GerenciadorConfiguracao.get("path.excel.base");
        this.pastaBackup = GerenciadorConfiguracao.get("path.backup.maquinaria");
        
        this.executadorGiro = new ExecutadorGiro();
    }

    /**
     * Sincroniza as configurações de maquinário com a planilha mestre.
     */
    public void sincronizarConfiguracoes() throws Exception {
        try {
            logger.accept("Iniciando sincronização de configurações...");
            executadorGiro.sincronizarConfiguracoes(caminhoConfigExcel, caminhoBaseExcel);
            logger.accept("✓ Sucesso: Configurações sincronizadas.");
        } catch (Exception e) {
            logger.accept("x Erro na sincronização: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Atualiza a base de maquinário a partir dos arquivos baixados.
     */
    public void atualizarBaseMaquinario() throws Exception {
        try {
            logger.accept("Iniciando atualização da base de maquinários...");
            executadorGiro.atualizarBase(caminhoBaseExcel, pastaBackup, diretorioDownloads, diretorioDestino);
            logger.accept("✓ Sucesso: Base e backup atualizados.");
        } catch (Exception e) {
            logger.accept("x Erro no processo: " + e.getMessage());
            throw e;
        }
    }
}
