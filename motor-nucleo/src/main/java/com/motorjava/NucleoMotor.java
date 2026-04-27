package com.motorjava;

import com.motorjava.gui.PainelModerno;
import com.motorjava.service.maquinas.ServicoMaquinas;
import javax.swing.SwingUtilities;

/**
 * NÚCLEO DO MOTOR - APLICAÇÃO NATIVA
 * Ponto de entrada local para o sistema de automação.
 */
public class NucleoMotor {
    public static void main(String[] args) {
        // Logger compartilhado para o console e interface gráfica
        java.util.function.Consumer<String> logger = msg -> System.out.println("[MOTOR] " + msg);

        // Inicialização dos Serviços
        ServicoMaquinas servicoMaquinas = new ServicoMaquinas(logger);

        // Iniciar Interface Gráfica
        SwingUtilities.invokeLater(() -> {
            PainelModerno dashboard = new PainelModerno(servicoMaquinas);
            dashboard.setVisible(true);
        });
        
        System.out.println("SISTEMA MOTOR JAVA v4.0 - NÚCLEO NATIVO REINICIALIZADO");
    }
}


