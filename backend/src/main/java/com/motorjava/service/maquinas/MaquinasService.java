package com.motorjava.service.maquinas;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;

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
        logger.accept("Varrer pasta de Downloads: " + downloadsDirStr);
        File dir = new File(downloadsDirStr);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IOException("Pasta de Downloads não encontrada: " + downloadsDirStr);
        }

        File targetFolder = new File(targetDirStr);
        if (!targetFolder.exists()) {
            logger.accept("Criando pasta destino: " + targetDirStr);
            Files.createDirectories(targetFolder.toPath());
        }

        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        int count = 0;
        for (File file : files) {
            if (file.isFile()) {
                String nome = file.getName().toLowerCase();
                // palavras-chaves: ffa controle locacao
                if (nome.contains("ffa") && nome.contains("controle")
                        && (nome.contains("locacao") || nome.contains("locação"))) {
                    moverERenomear(file, "MAQUINAS LOCADAS E PROPRIAS");
                    count++;
                }
            }
        }

        if (count == 0) {
            logger.accept("Nenhum arquivo 'ffa controle locacao' encontrado em Downloads.");
        } else {
            logger.accept(count + " arquivo(s) processado(s) com sucesso para o relatório de Maquinários.");
        }
    }

    private void moverERenomear(File sourceFile, String novoNomeBase) throws IOException {
        String originalName = sourceFile.getName();
        String extensao = "";
        int i = originalName.lastIndexOf('.');
        if (i > 0) {
            extensao = originalName.substring(i);
        }

        String novoNome = novoNomeBase + extensao;
        Path targetPath = Paths.get(targetDirStr, novoNome);

        logger.accept("Movendo arquivo " + originalName + " para " + targetPath.toString());
        Files.move(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
    }
}
