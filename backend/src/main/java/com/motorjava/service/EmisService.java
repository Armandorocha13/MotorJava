package com.motorjava.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;

public class EmisService {
    private final Consumer<String> logger;
    private final String downloadsDirStr;
    private final String targetDirStr;

    public EmisService(Consumer<String> logger) {
        this.logger = logger;
        this.downloadsDirStr = System.getProperty("user.home") + "\\Downloads";
        this.targetDirStr = "C:\\Users\\user\\Desktop\\ARMANDO POWER BI\\Emis e terminais";
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
                if (nome.contains("terminais")) {
                    moverERenomear(file, "TERMINAIS");
                    count++;
                } else if (nome.contains("emis")) {
                    moverERenomear(file, "EMIS");
                    count++;
                }
            }
        }

        if (count == 0) {
            logger.accept("Nenhum arquivo relacionado a EMIS ou TERMINAIS foi encontrado em Downloads.");
        } else {
            logger.accept(count + " arquivo(s) processado(s) com sucesso.");
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

    public void importarBancoDados() {
        logger.accept("Iniciando importação EMIS/TERMINAIS para o Banco de Dados (Em desenvolvimento)");
        // Lógica para importar será feita posteriormente
        logger.accept("Importação concluída.");
    }
}
