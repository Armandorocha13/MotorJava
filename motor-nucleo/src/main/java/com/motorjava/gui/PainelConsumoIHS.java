package com.motorjava.gui;

import com.motorjava.consumoihs.logica.MotorProcessamento;
import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.function.BiConsumer;

/**
 * Painel de Automação para o Módulo de Consumo IHS.
 * Traduzido e seguindo princípios de POO.
 */
public class PainelConsumoIHS extends JPanel {

    private JTextArea areaLogs;
    private JDialog dialogoLogs;
    private JProgressBar barraNormalizar, barraConsolidar;
    private JButton btnNormalizar, btnConsolidar, btnVerHtml, btnVerExcel, btnLog;

    // Cores do Sistema
    private final Color corFundo = Color.BLACK;
    private final Color corBarraLateral = new Color(18, 18, 18);
    private final Color corCard = new Color(18, 18, 18);
    private final Color corTexto = new Color(230, 237, 243);
    private final Color corTextoSecundario = new Color(139, 148, 158);
    private final Color corPrimaria = Color.WHITE;

    // Caminhos de Arquivo
    private static final String PASTA_RAIZ = "C:\\Users\\user\\Desktop\\ARQUVOS\\RELATORIOS\\EXCEL\\ConsumoIHS\\";
    private static final String CAMINHO_BASE = PASTA_RAIZ + "CONSUMO IHS.xlsx";
    private static final String CAMINHO_HTML = PASTA_RAIZ + "html_output\\CONSOLIDACAO_PADRAO_ANIEL.html";
    private static final String CAMINHO_EXCEL = PASTA_RAIZ + "excel_output\\CONSOLIDACAO_FINAL.xlsx";

    public PainelConsumoIHS() {
        configurarLogs();
        configurarInterface();
    }

    private void configurarLogs() {
        dialogoLogs = new JDialog((Frame)null, "Logs de Processamento - IHS", false);
        dialogoLogs.setSize(600, 400);
        dialogoLogs.getContentPane().setBackground(corBarraLateral);

        areaLogs = new JTextArea();
        areaLogs.setEditable(false);
        areaLogs.setFont(new Font("Consolas", Font.PLAIN, 12));
        areaLogs.setBackground(corBarraLateral);
        areaLogs.setForeground(corTexto);
        areaLogs.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JScrollPane scroll = new JScrollPane(areaLogs);
        scroll.setBorder(null);
        dialogoLogs.add(scroll);
    }

    private void configurarInterface() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        JPanel principal = new JPanel(new BorderLayout(0, 40));
        principal.setOpaque(false);
        principal.setBorder(new EmptyBorder(20, 0, 30, 0));

        JPanel grid = new JPanel(new GridLayout(1, 2, 30, 0));
        grid.setOpaque(false);
        
        barraNormalizar = criarBarraProgresso();
        btnNormalizar = criarBotao("EXECUTAR LIMPEZA");
        grid.add(criarCard("Normalização", "Limpa e prepara a planilha base para o processamento.", 
            btnNormalizar, barraNormalizar));
        
        barraConsolidar = criarBarraProgresso();
        btnConsolidar = criarBotao("GERAR RELATÓRIOS");
        btnConsolidar.setEnabled(false);
        grid.add(criarCard("Consolidação", "Gera relatórios padronizados Aniel em HTML e Excel.", 
            btnConsolidar, barraConsolidar));

        principal.add(grid, BorderLayout.NORTH);

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setOpaque(false);

        JPanel resultados = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        resultados.setOpaque(false);
        btnVerHtml = criarBotao("VER HTML");
        btnVerExcel = criarBotao("VER EXCEL");
        btnVerHtml.setEnabled(false);
        btnVerExcel.setEnabled(false);
        resultados.add(btnVerHtml);
        resultados.add(btnVerExcel);
        
        rodape.add(resultados, BorderLayout.CENTER);

        btnLog = new JButton("LOG");
        btnLog.setFont(new Font("Quicksand", Font.BOLD, 10));
        btnLog.setPreferredSize(new Dimension(70, 30));
        btnLog.setBackground(corCard);
        btnLog.setForeground(corTextoSecundario);
        btnLog.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLog.putClientProperty(FlatClientProperties.STYLE, "arc: 10; outline: #333333; outlineWidth: 1;");
        btnLog.addActionListener(e -> {
            dialogoLogs.setLocationRelativeTo(this);
            dialogoLogs.setVisible(true);
        });
        
        JPanel logWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        logWrapper.setOpaque(false);
        logWrapper.add(btnLog);
        rodape.add(logWrapper, BorderLayout.SOUTH);

        principal.add(rodape, BorderLayout.SOUTH);
        add(principal, BorderLayout.CENTER);

        // --- EVENTOS ---
        btnNormalizar.addActionListener(e -> executarProcesso("Processando Limpeza...", barraNormalizar, (r) -> {
            MotorProcessamento.normalizarExcel(CAMINHO_BASE, r);
        }, () -> btnConsolidar.setEnabled(true)));

        btnConsolidar.addActionListener(e -> executarProcesso("Consolidando Dados...", barraConsolidar, (r) -> {
            MotorProcessamento.consolidarDados(CAMINHO_BASE, r);
        }, () -> {
            btnVerHtml.setEnabled(true);
            btnVerExcel.setEnabled(true);
        }));

        btnVerHtml.addActionListener(e -> abrirArquivo(CAMINHO_HTML));
        btnVerExcel.addActionListener(e -> abrirArquivo(CAMINHO_EXCEL));
    }

    private Font carregarFonte(String nome, float tamanho, int estilo) {
        try {
            java.io.InputStream is = getClass().getResourceAsStream("/fonts/" + nome);
            if (is != null) {
                Font f = Font.createFont(Font.TRUETYPE_FONT, is);
                return f.deriveFont(estilo, tamanho);
            }
        } catch (Exception e) {}
        return new Font("sans-serif", estilo, (int)tamanho);
    }

    private JPanel criarCard(String titulo, String desc, JButton botao, JProgressBar barra) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(corCard);
        card.setBorder(new EmptyBorder(40, 40, 40, 40));
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 24; outline: #333333; outlineWidth: 1;");

        JLabel lt = new JLabel(titulo); 
        lt.setForeground(corPrimaria); 
        lt.setFont(carregarFonte("Quicksand.ttf", 24f, Font.BOLD));
        lt.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel ld = new JLabel("<html><div style='text-align: center;'>" + desc + "</div></html>"); 
        ld.setForeground(corTextoSecundario);
        ld.setFont(carregarFonte("Quicksand.ttf", 15f, Font.PLAIN));
        ld.setAlignmentX(Component.CENTER_ALIGNMENT);
        ld.setMaximumSize(new Dimension(300, 100));
        ld.setBorder(new EmptyBorder(15, 0, 35, 0));
        
        botao.setAlignmentX(Component.CENTER_ALIGNMENT);
        botao.setFont(carregarFonte("Quicksand.ttf", 15f, Font.BOLD));
        
        barra.setAlignmentX(Component.CENTER_ALIGNMENT);
        barra.setMaximumSize(new Dimension(220, 10));
        barra.setVisible(false);
        
        card.add(lt); 
        card.add(ld);
        card.add(Box.createVerticalGlue()); 
        card.add(botao);
        card.add(Box.createVerticalStrut(15));
        card.add(barra);
        
        return card;
    }

    private JProgressBar criarBarraProgresso() {
        JProgressBar p = new JProgressBar(0, 100);
        p.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");
        return p;
    }

    private JButton criarBotao(String texto) {
        JButton b = new JButton(texto);
        b.setPreferredSize(new Dimension(220, 55));
        b.setMaximumSize(new Dimension(220, 55));
        b.setBackground(corPrimaria);
        b.setForeground(corFundo);
        b.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void executarProcesso(String titulo, JProgressBar barraCard, AcaoProcessamento acao, Runnable finalizacao) {
        btnNormalizar.setEnabled(false); 
        btnConsolidar.setEnabled(false);
        barraCard.setValue(0);
        barraCard.setVisible(true);
        
        areaLogs.setText("");
        new SwingWorker<Void, PassoProcesso>() {
            @Override protected Void doInBackground() throws Exception {
                acao.executar((msg, progresso) -> publish(new PassoProcesso(msg, progresso))); 
                return null;
            }
            @Override protected void process(java.util.List<PassoProcesso> passos) {
                PassoProcesso p = passos.get(passos.size()-1);
                barraCard.setValue(p.progresso);
                registrarLog(p.mensagem);
            }
            @Override protected void done() {
                try { 
                    get(); 
                    barraCard.setValue(100);
                    finalizacao.run(); 
                } catch (Exception e) { 
                    barraCard.setValue(0);
                    registrarLog("ERRO: " + e.getMessage());
                }
                btnNormalizar.setEnabled(true);
            }
        }.execute();
    }

    private void registrarLog(String msg) {
        String tempo = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        areaLogs.append("[" + tempo + "] " + msg + "\n");
        areaLogs.setCaretPosition(areaLogs.getDocument().getLength());
    }

    private void abrirArquivo(String caminho) { 
        try { 
            Desktop.getDesktop().open(new File(caminho)); 
        } catch (Exception e) {
            registrarLog("Erro ao abrir arquivo: " + e.getMessage());
        } 
    }

    @FunctionalInterface 
    interface AcaoProcessamento { 
        void executar(BiConsumer<String, Integer> rastreador) throws Exception; 
    }

    private static class PassoProcesso { 
        String mensagem; 
        int progresso; 
        PassoProcesso(String m, int p) { this.mensagem = m; this.progresso = p; } 
    }
}
