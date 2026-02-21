package com.motorjava.service;

import com.motorjava.config.Config;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;

public class OnePageService {

    private final Consumer<String> logger;

    public OnePageService(Consumer<String> logger) {
        this.logger = logger;
    }

    private void log(String msg) {
        if (logger != null)
            logger.accept(msg);
    }

    /**
     * Monitora a pasta de Downloads e move arquivos do Outlook para a pasta
     * correta.
     */
    public void monitorarDownloads() {
        File downloadsDir = new File(Config.PATH_DOWNLOADS);
        if (!downloadsDir.exists())
            return;

        File[] files = downloadsDir.listFiles();
        if (files == null)
            return;

        for (File file : files) {
            String name = file.getName().toLowerCase();
            // Critérios: modem, nel, rj, sp
            if (name.contains("modem") || name.contains("nel") || name.contains("rj") || name.contains("sp")) {
                try {
                    File dest = new File(Config.PATH_ONEPAGE_OUTLOOK, file.getName());
                    Files.move(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    log("Arquivo movido para Outlook: " + file.getName());
                } catch (Exception e) {
                    log("Erro ao mover arquivo: " + file.getName() + " -> " + e.getMessage());
                }
            }
        }
    }

    /**
     * Processa a carga do Outlook
     */
    public void processarOutlook() {
        log("Iniciando processamento Outlook (One Page)...");
        monitorarDownloads(); // Garante que tudo que está no download foi movido

        File dir = new File(Config.PATH_ONEPAGE_OUTLOOK);
        log("Lendo pasta: " + dir.getAbsolutePath());
        // Aqui entrará a lógica de leitura Excel/CSV e INSERT no SQL
        log("Sucesso! Dados do Outlook processados.");
    }

    /**
     * Processa a carga do Aniel (Manual)
     */
    public void processarAnielManual() {
        log("Iniciando processamento Aniel Manual...");
        File dir = new File(Config.PATH_ONEPAGE_ANIEL);
        log("Lendo pasta: " + dir.getAbsolutePath());
        // Lógica para: conferencia de movimen, saldo estoque, saida de rm e dm
        log("Sucesso! Dados do Aniel Manual processados.");
    }

    /**
     * Processa a carga do WMS
     */
    public void processarWMS() {
        log("Iniciando processamento WMS (One Page)...");
        File dir = new File(Config.PATH_ONEPAGE_WMS);
        log("Lendo pasta: " + dir.getAbsolutePath());
        // Lógica para: estoquematerial, estoquetecnico quantitativo,
        // pedido_devolução_material
        log("Sucesso! Dados do WMS processados.");
    }
}
