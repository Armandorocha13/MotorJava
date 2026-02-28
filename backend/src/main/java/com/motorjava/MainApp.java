package com.motorjava;

import com.motorjava.api.LocalServer;
import com.motorjava.service.OnePageService;
import com.motorjava.service.VivoService;
import com.motorjava.service.EmisService;

/**
 * MOTOR JAVA - BACKEND ENGINE
 * Headless entry point for the automation system.
 */
public class MainApp {
    public static void main(String[] args) {
        System.out.println("   _____                 ___ ___");
        System.out.println("  |     | ___  _ _  _ _ |   |   | ___  _ _  ___");
        System.out.println("  | | | || . ||  _||  _||- -|- -|| . || | || . |");
        System.out.println("  |_|_|_||___||_|  |_|  |___|___||_|  |\\_/ |___|");
        System.out.println("  MOTOR JAVA SYSTEM v4.0 - BACKEND ACTIVE\n");

        // Logger compartilhado para o console
        java.util.function.Consumer<String> logger = msg -> System.out.println("[MOTOR] " + msg);

        // Inicialização dos Serviços (Clean Code)
        OnePageService onePageService = new OnePageService(logger);
        VivoService vivoService = new VivoService(logger);
        EmisService emisService = new EmisService(logger);

        try {
            LocalServer server = new LocalServer(onePageService, vivoService, emisService);
            server.start();

            System.out.println("\n[STATUS] Backend API escutando na porta 8080");
            System.out.println("[INFO] Comandos disponíveis: /outlook, /aniel, /wms, /vivo/*, /emis/*");
        } catch (Exception e) {
            System.err.println("[FALHA] Não foi possível iniciar o motor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
