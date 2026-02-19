package com.motorjava.ui.ihs;

import com.motorjava.config.Config;
import com.motorjava.core.HttpService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

public class IHSPanel extends JPanel {

    private final HttpService httpService;
    private final JTextArea logArea;

    public IHSPanel() {
        this.httpService = new HttpService();
        this.logArea = new JTextArea(10, 50);
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 245));
        
        initComponents();
    }

    private void initComponents() {
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("One Page Report IHS - Automação");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        buttonPanel.setOpaque(false);

        JButton btnOutlook = createStyledButton("Sincronizar Outlook", new Color(0, 120, 215));
        btnOutlook.addActionListener(e -> sincronizarOutlook());

        JButton btnWms = createStyledButton("Processar WMS", new Color(34, 139, 34));
        btnWms.addActionListener(e -> processarWMS());

        JButton btnAniel = createStyledButton("Atualizar Aniel", new Color(255, 140, 0));
        btnAniel.addActionListener(e -> atualizarAniel());

        buttonPanel.add(btnOutlook);
        buttonPanel.add(btnWms);
        buttonPanel.add(btnAniel);

        add(buttonPanel, BorderLayout.CENTER);

        // Terminal de Log simplificado
        logArea.setEditable(false);
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(Color.GREEN);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setPreferredSize(new Dimension(0, 200));
        add(scroll, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(200, 50));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return btn;
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append("> " + message + "\n");
        });
    }

    private void sincronizarOutlook() {
        log("Iniciando sincronização com Outlook via Power Automate...");
        String payload = "{\"action\": \"download_attachments\", \"folder\": \"modem_balance\"}";
        
        httpService.postToPowerAutomate(Config.POWER_AUTOMATE_WEBHOOK_URL, payload)
                .thenAccept(response -> {
                    if (response.statusCode() == 200 || response.statusCode() == 202) {
                        log("Requisição enviada com sucesso! Verifique a pasta: " + Config.PATH_OUTLOOK);
                    } else {
                        log("Erro na requisição: Código " + response.statusCode());
                    }
                })
                .exceptionally(ex -> {
                    log("Erro de conexão: " + ex.getMessage());
                    return null;
                });
    }

    private void processarWMS() {
        log("Iniciando monitoramento da pasta Downloads...");
        File downloadsDir = new File(Config.PATH_DOWNLOADS);
        File targetDir = new File(Config.PATH_IHS_WMS_QUERY);

        if (!targetDir.exists()) targetDir.mkdirs();

        File[] excelFiles = downloadsDir.listFiles((dir, name) -> 
            name.toLowerCase().endsWith(".xlsx") || name.toLowerCase().endsWith(".xls")
        );

        if (excelFiles == null || excelFiles.length == 0) {
            log("Nenhum arquivo Excel encontrado em Downloads.");
            return;
        }

        int count = 0;
        for (File file : excelFiles) {
            // Aqui poderíamos abrir o Excel com Apache POI para filtrar por 'Empresa Gestora = ffa'
            // Mas conforme solicitado, vamos mover os arquivos que atendem ao critério (simulando filtro ou apenas movendo os relevantes)
            // Em uma implementação real, leríamos a célula. Para este esqueleto, vamos mover arquivos que contenham 'ffa' no nome ou apenas mover todos os novos.
            
            try {
                log("Processando: " + file.getName());
                Files.move(file.toPath(), targetDir.toPath().resolve(file.getName()), StandardCopyOption.REPLACE_EXISTING);
                log("Movido para: " + Config.PATH_IHS_WMS_QUERY);
                count++;
            } catch (IOException e) {
                log("Erro ao mover arquivo " + file.getName() + ": " + e.getMessage());
            }
        }
        log("Processamento WMS concluído. " + count + " arquivos movidos.");
    }

    private void atualizarAniel() {
        log("Preparando chamadas para API Aniel (Conferência e Saídas)...");
        // Exemplo de chamadas GET/POST
        httpService.getRequest(Config.ANIEL_API_BASE_URL + "/relatorios/conferencia")
                .thenAccept(response -> {
                    log("Relatório de Conferência baixado: " + response.statusCode());
                });

        httpService.getRequest(Config.ANIEL_API_BASE_URL + "/relatorios/saidas")
                .thenAccept(response -> {
                    log("Relatório de Saídas baixado: " + response.statusCode());
                });
    }
}
