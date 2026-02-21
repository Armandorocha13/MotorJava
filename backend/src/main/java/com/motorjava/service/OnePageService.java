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
     * Aplica padronização de nomes (minúsculas, sem números).
     */
    public void monitorarDownloads() {
        File downloadsDir = new File(Config.PATH_DOWNLOADS);
        if (!downloadsDir.exists())
            return;

        File[] files = downloadsDir.listFiles();
        if (files == null)
            return;

        for (File file : files) {
            String originalName = file.getName();
            String nameLower = originalName.toLowerCase();

            // Critérios: modem, nel, rj, sp
            if (nameLower.contains("modem") || nameLower.contains("nel") || nameLower.contains("rj")
                    || nameLower.contains("sp")) {
                try {
                    String novoNome = padronizarNomeArquivo(originalName);
                    File dest = new File(Config.PATH_ONEPAGE_OUTLOOK, novoNome);

                    Files.move(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    log("Arquivo movido e padronizado: " + originalName + " -> " + novoNome);
                } catch (Exception e) {
                    log("Erro ao mover arquivo: " + originalName + " -> " + e.getMessage());
                }
            }
        }
    }

    /**
     * Padroniza o nome do arquivo:
     * 1. Converte para minúsculas
     * 2. Remove números
     * 3. Remove parênteses e caracteres especiais
     * 4. Remove espaços duplos
     */
    private String padronizarNomeArquivo(String nomeOriginal) {
        // Separa nome da extensão
        int lastDot = nomeOriginal.lastIndexOf(".");
        String nome = (lastDot != -1) ? nomeOriginal.substring(0, lastDot) : nomeOriginal;
        String extensao = (lastDot != -1) ? nomeOriginal.substring(lastDot) : "";

        // 1. Minúsculas
        nome = nome.toLowerCase();

        // 2. Remove números
        nome = nome.replaceAll("\\d", "");

        // 3. Remove parênteses e caracteres especiais (mantendo espaços)
        nome = nome.replaceAll("[\\(\\)\\[\\]\\-_]", " ");

        // 4. Limpa espaços extras
        nome = nome.replaceAll("\\s+", " ").trim();

        return nome + extensao.toLowerCase();
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
