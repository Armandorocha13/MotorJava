package com.motorjava.service.ferramentaria;

import java.util.function.Consumer;

public class FerramentariaService {
    private final Consumer<String> logger;

    public FerramentariaService(Consumer<String> logger) {
        this.logger = logger;
    }

    public void extrairDadosDoPortal() {
        logger.accept("Iniciando extração de dados do Portal Ferramentaria...");
        // Logica de extração será implementada aqui
        logger.accept("Extração concluída com sucesso (Simulação).");
    }

    public void outroProcesso() {
        logger.accept("Executando processo secundário de Ferramentaria...");
        // Logica a definir
        logger.accept("Processo concluído.");
    }
}
