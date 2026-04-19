package com.motorjava;

import com.motorjava.gui.ModernDashboard;
import com.motorjava.service.maquinas.MaquinasService;
import javax.swing.SwingUtilities;

/**
 * MOTOR JAVA - NATIVE APPLICATION
 * Local entry point for the automation system.
 */
public class MainApp {
    public static void main(String[] args) {
        // Logger compartilhado para o console e GUI
        java.util.function.Consumer<String> logger = msg -> System.out.println("[MOTOR] " + msg);

        // Inicialização dos Serviços
        MaquinasService maquinasService = new MaquinasService(logger);

        // Launch GUI
        SwingUtilities.invokeLater(() -> {
            ModernDashboard dashboard = new ModernDashboard(maquinasService);
            dashboard.setVisible(true);
        });
        
        System.out.println("MOTOR JAVA SYSTEM v4.0 - NATIVE CORE RE-INITIALIZED");
    }
}


