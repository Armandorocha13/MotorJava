package com.motorjava.gui;

import com.motorjava.consumoihs.logica.MotorProcessamento;
import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.function.BiConsumer;

public class PainelConsumoIHS extends JPanel {

    private JTextArea areaLogs;
    private JDialog dialogoLogs;
    private JProgressBar barNormalizar, barConsolidar;
    private JButton btnNormalizar, btnConsolidar, btnVerHtml, btnVerExcel, btnLog;

    // Cores (Consistentes com o Axis)
    private final Color bgColor = Color.BLACK;
    private final Color sidebarColor = new Color(18, 18, 18);
    private final Color cardColor = new Color(18, 18, 18);
    private final Color textColor = new Color(230, 237, 243);
    private final Color textSecondary = new Color(139, 148, 158);
    private final Color primaryColor = Color.WHITE;

    // Caminhos
    private static final String PASTA_RAIZ = "C:\\Users\\user\\Desktop\\ARQUVOS\\RELATORIOS\\EXCEL\\ConsumoIHS\\";
    private static final String CAMINHO_BASE = PASTA_RAIZ + "CONSUMO IHS.xlsx";
    private static final String CAMINHO_HTML = PASTA_RAIZ + "html_output\\CONSOLIDACAO_PADRAO_ANIEL.html";
    private static final String CAMINHO_EXCEL = PASTA_RAIZ + "excel_output\\CONSOLIDACAO_FINAL.xlsx";

    public PainelConsumoIHS() {
        configurarLogs();
        setupUI();
    }

    private void configurarLogs() {
        // O diálogo de logs continua sendo um pop-up para não poluir a tela principal
        dialogoLogs = new JDialog((Frame)null, "Logs de Processamento - IHS", false);
        dialogoLogs.setSize(600, 400);
        dialogoLogs.getContentPane().setBackground(sidebarColor);

        areaLogs = new JTextArea();
        areaLogs.setEditable(false);
        areaLogs.setFont(new Font("Consolas", Font.PLAIN, 12));
        areaLogs.setBackground(sidebarColor);
        areaLogs.setForeground(textColor);
        areaLogs.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JScrollPane scroll = new JScrollPane(areaLogs);
        scroll.setBorder(null);
        dialogoLogs.add(scroll);
    }

    private void setupUI() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        // --- CENTRO ---
        JPanel main = new JPanel(new BorderLayout(0, 40));
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(20, 0, 30, 0));

        // CARDS
        JPanel grid = new JPanel(new GridLayout(1, 2, 30, 0));
        grid.setOpaque(false);
        
        barNormalizar = criarBarraProgresso();
        btnNormalizar = criarBotao("EXECUTAR LIMPEZA");
        grid.add(createCard("Normalização", "Limpa e prepara a planilha base para o processamento.", 
            btnNormalizar, barNormalizar));
        
        barConsolidar = criarBarraProgresso();
        btnConsolidar = criarBotao("GERAR RELATÓRIOS");
        btnConsolidar.setEnabled(false);
        grid.add(createCard("Consolidação", "Gera relatórios padronizados Aniel em HTML e Excel.", 
            btnConsolidar, barConsolidar));

        main.add(grid, BorderLayout.NORTH);

        // BOTÕES DE RESULTADO + LOG
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        JPanel results = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        results.setOpaque(false);
        btnVerHtml = criarBotao("VER HTML");
        btnVerExcel = criarBotao("VER EXCEL");
        btnVerHtml.setEnabled(false);
        btnVerExcel.setEnabled(false);
        results.add(btnVerHtml);
        results.add(btnVerExcel);
        
        footer.add(results, BorderLayout.CENTER);

        btnLog = new JButton("LOG");
        btnLog.setFont(new Font("Quicksand", Font.BOLD, 10));
        btnLog.setPreferredSize(new Dimension(70, 30));
        btnLog.setBackground(cardColor);
        btnLog.setForeground(textSecondary);
        btnLog.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLog.putClientProperty(FlatClientProperties.STYLE, "arc: 10; outline: #333333; outlineWidth: 1;");
        btnLog.addActionListener(e -> {
            dialogoLogs.setLocationRelativeTo(this);
            dialogoLogs.setVisible(true);
        });
        
        JPanel logWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        logWrapper.setOpaque(false);
        logWrapper.add(btnLog);
        footer.add(logWrapper, BorderLayout.SOUTH);

        main.add(footer, BorderLayout.SOUTH);

        add(main, BorderLayout.CENTER);

        // --- EVENTOS ---
        btnNormalizar.addActionListener(e -> executar("Processando Limpeza...", barNormalizar, (r) -> {
            MotorProcessamento.normalizarExcel(CAMINHO_BASE, r);
        }, () -> btnConsolidar.setEnabled(true)));

        btnConsolidar.addActionListener(e -> executar("Consolidando Dados...", barConsolidar, (r) -> {
            MotorProcessamento.consolidarDados(CAMINHO_BASE, r);
        }, () -> {
            btnVerHtml.setEnabled(true);
            btnVerExcel.setEnabled(true);
        }));

        btnVerHtml.addActionListener(e -> abrir(CAMINHO_HTML));
        btnVerExcel.addActionListener(e -> abrir(CAMINHO_EXCEL));
    }

    private Font loadFont(String name, float size, int style) {
        try {
            java.io.InputStream is = getClass().getResourceAsStream("/fonts/" + name);
            if (is != null) {
                Font f = Font.createFont(Font.TRUETYPE_FONT, is);
                return f.deriveFont(style, size);
            }
        } catch (Exception e) {}
        return new Font("sans-serif", style, (int)size);
    }

    private JPanel createCard(String t, String d, JButton b, JProgressBar p) {
        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBackground(cardColor);
        c.setBorder(new EmptyBorder(40, 40, 40, 40));
        c.putClientProperty(FlatClientProperties.STYLE, "arc: 24; outline: #333333; outlineWidth: 1;");

        JLabel lt = new JLabel(t); 
        lt.setForeground(primaryColor); 
        lt.setFont(loadFont("Quicksand.ttf", 24f, Font.BOLD));
        lt.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel ld = new JLabel("<html><div style='text-align: center;'>" + d + "</div></html>"); 
        ld.setForeground(textSecondary);
        ld.setFont(loadFont("Quicksand.ttf", 15f, Font.PLAIN));
        ld.setAlignmentX(Component.CENTER_ALIGNMENT);
        ld.setMaximumSize(new Dimension(300, 100));
        ld.setBorder(new EmptyBorder(15, 0, 35, 0));
        
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setFont(loadFont("Quicksand.ttf", 15f, Font.BOLD));
        
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.setMaximumSize(new Dimension(220, 10));
        p.setVisible(false);
        
        c.add(lt); 
        c.add(ld);
        c.add(Box.createVerticalGlue()); 
        c.add(b);
        c.add(Box.createVerticalStrut(15));
        c.add(p);
        
        return c;
    }

    private JProgressBar criarBarraProgresso() {
        JProgressBar p = new JProgressBar(0, 100);
        p.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");
        return p;
    }

    private JButton criarBotao(String txt) {
        JButton b = new JButton(txt);
        b.setPreferredSize(new Dimension(220, 55));
        b.setMaximumSize(new Dimension(220, 55));
        b.setBackground(primaryColor);
        b.setForeground(bgColor);
        b.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void executar(String msg, JProgressBar cardBar, Acao r, Runnable done) {
        btnNormalizar.setEnabled(false); btnConsolidar.setEnabled(false);
        cardBar.setValue(0);
        cardBar.setVisible(true);
        
        areaLogs.setText("");
        new SwingWorker<Void, Step>() {
            @Override protected Void doInBackground() throws Exception {
                r.exec((m, p) -> publish(new Step(m, p))); return null;
            }
            @Override protected void process(java.util.List<Step> steps) {
                Step s = steps.get(steps.size()-1);
                cardBar.setValue(s.p);
                registrarLog(s.m);
            }
            @Override protected void done() {
                try { 
                    get(); 
                    cardBar.setValue(100);
                    done.run(); 
                }
                catch (Exception e) { 
                    cardBar.setValue(0);
                    registrarLog("ERRO: " + e.getMessage());
                }
                btnNormalizar.setEnabled(true);
            }
        }.execute();
    }

    private void registrarLog(String msg) {
        String time = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        areaLogs.append("[" + time + "] " + msg + "\n");
        areaLogs.setCaretPosition(areaLogs.getDocument().getLength());
    }

    private void abrir(String p) { try { Desktop.getDesktop().open(new File(p)); } catch (Exception e) {} }
    @FunctionalInterface interface Acao { void exec(BiConsumer<String, Integer> r) throws Exception; }
    private static class Step { String m; int p; Step(String m, int p) { this.m = m; this.p = p; } }
}
