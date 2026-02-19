package com.motorjava;

import com.motorjava.service.ImportadorArquivo;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * VIVO AGING ENTERPRISE DASHBOARD
 * Interface modernizada baseada no Design System V2
 */
public class GuiApp extends JFrame {

    // ===== PALETA DE CORES (VIVO DARK THEME) =====
    private static final Color BG_DARKEST = new Color(15, 15, 18);   // Sidebar
    private static final Color BG_DARKER  = new Color(22, 22, 24);   // Main BG
    private static final Color BG_CARD    = new Color(30, 30, 33);   // Cards
    private static final Color ACCENT_PURPLE = new Color(168, 56, 255); // Roxo Vivo
    private static final Color ACCENT_GREEN  = new Color(0, 255, 127); // Verde Sucesso
    private static final Color TEXT_WHITE = new Color(245, 245, 245);
    private static final Color TEXT_GRAY  = new Color(140, 140, 150);
    private static final Color TEXT_MUTED = new Color(80, 80, 90);

    // ===== FONTES =====
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_MONO   = new Font("JetBrains Mono", Font.PLAIN, 12);

    // ===== COMPONENTES DE ESTADO =====
    private JProgressBar mainProgressBar;
    private JLabel statusPercentLabel;
    private JLabel statusTextLabel;
    private JTextArea termArea;
    private JLabel uptimeLabel;
    private long startTime;

    // ===== CONFIGURAÇÕES DE NEGÓCIO =====
    private static final String PASTA_STOCK = "C:\\Users\\user\\Desktop\\MotorJava\\data";

    public GuiApp() {
        startTime = System.currentTimeMillis();
        configurarJanela();
        inicializarComponentes();
        redirecionarConsole();
        iniciarTimerFooter();
        log("Sistema inicializado e pronto.");
    }

    private void configurarJanela() {
        setTitle("Vivo Aging Enterprise");
        setSize(1280, 800);
        setMinimumSize(new Dimension(1024, 768));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setUndecorated(false); // Manter a barra de título padrão do Windows por conveniência
    }

    private void inicializarComponentes() {
        // 1. SIDEBAR (Esquerda)
        add(criarSidebar(), BorderLayout.WEST);

        // 2. MAIN CONTENT (Centro)
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_DARKER);
        mainPanel.add(criarHeaderPrincipal(), BorderLayout.NORTH);
        
        // Wrapper com scroll para o conteúdo central
        JPanel contentScrollWrapper = new JPanel(new BorderLayout());
        contentScrollWrapper.setBackground(BG_DARKER);
        contentScrollWrapper.add(criarDashboardCentral(), BorderLayout.CENTER);
        
        mainPanel.add(contentScrollWrapper, BorderLayout.CENTER);
        mainPanel.add(criarFooter(), BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    // =================================================================================
    // SECTION: SIDEBAR
    // =================================================================================
    private JPanel criarSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(BG_DARKEST);
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(40,40,40)));

        // Logo Area
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 30));
        logoPanel.setBackground(BG_DARKEST);
        
        JLabel iconLogo = new JLabel("\u26A1"); // ⚡ High Voltage
        iconLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLogo.setForeground(ACCENT_PURPLE);
        
        JPanel textLogo = new JPanel(new GridLayout(2, 1));
        textLogo.setBackground(BG_DARKEST);
        JLabel title = new JLabel("VIVO AGING");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TEXT_WHITE);
        JLabel subtitle = new JLabel("AUTOMATED SYSTEM");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        subtitle.setForeground(TEXT_GRAY);
        textLogo.add(title);
        textLogo.add(subtitle);

        logoPanel.add(iconLogo);
        logoPanel.add(textLogo);

        // Menu Items
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(BG_DARKEST);
        menuPanel.setBorder(new EmptyBorder(20, 15, 20, 15));

        menuPanel.add(criarBotaoMenu("Painel", "\uD83D\uDCBB", true));         // 💻
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(Box.createVerticalStrut(10));

        // Bottom - Sair
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(BG_DARKEST);
        bottomPanel.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        JButton btnSair = new JButton("  SAIR");
        estilizarBotaoOutline(btnSair);
        btnSair.setPreferredSize(new Dimension(200, 45));
        btnSair.addActionListener(e -> System.exit(0));
        bottomPanel.add(btnSair);

        sidebar.add(logoPanel, BorderLayout.NORTH);
        sidebar.add(menuPanel, BorderLayout.CENTER);
        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton criarBotaoMenu(String texto, String icone, boolean ativo) {
        JButton btn = new JButton(icone + "   " + texto);
        // Fallback font strategy for icons + text
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(230, 50));
        
        if (ativo) {
            btn.setOpaque(true);
            btn.setBackground(ACCENT_PURPLE);
            btn.setForeground(Color.WHITE);
            btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        } else {
            btn.setOpaque(false);
            btn.setForeground(TEXT_GRAY);
            btn.setBorder(new EmptyBorder(10, 20, 10, 20));
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { btn.setForeground(TEXT_WHITE); }
                public void mouseExited(MouseEvent e) { btn.setForeground(TEXT_GRAY); }
            });
        }
        return btn;
    }

    // =================================================================================
    // SECTION: MAIN CONTENT
    // =================================================================================
    private JPanel criarHeaderPrincipal() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_DARKER);
        header.setBorder(new EmptyBorder(30, 40, 20, 40));

        JPanel titles = new JPanel(new GridLayout(2, 1));
        titles.setBackground(BG_DARKER);
        JLabel h1 = new JLabel("Painel de Controle");
        h1.setFont(FONT_HEADER);
        h1.setForeground(TEXT_WHITE);
        JLabel h2 = new JLabel("Bem-vindo ao sistema de processamento VIVO AGING");
        h2.setFont(FONT_BODY);
        h2.setForeground(TEXT_GRAY);
        titles.add(h1);
        titles.add(h2);

        JPanel statusBadge = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusBadge.setBackground(BG_DARKER);
        
        JLabel lblOnline = new JLabel("\u25CF SISTEMA ONLINE"); // ● Circle
        lblOnline.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblOnline.setForeground(ACCENT_GREEN);
        lblOnline.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(0, 100, 50), 1, true),
            new EmptyBorder(8, 15, 8, 15)
        ));
        
        statusBadge.add(lblOnline);

        header.add(titles, BorderLayout.WEST);
        header.add(statusBadge, BorderLayout.EAST);
        return header;
    }

    private JPanel criarDashboardCentral() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_DARKER);
        panel.setBorder(new EmptyBorder(0, 40, 20, 40));

        // 1. STATUS CARD (Grande)
        panel.add(criarCardStatus());
        panel.add(Box.createVerticalStrut(25));

        // 2. AÇÕES (Grid 2 colunas)
        JPanel actionsGrid = new JPanel(new GridLayout(1, 2, 25, 0));
        actionsGrid.setBackground(BG_DARKER);
        actionsGrid.setMaximumSize(new Dimension(2000, 220)); // Altura fixa

        actionsGrid.add(criarCardAcao(
            "ATUALIZAR PLANILHA", 
            "Sincronize os dados mais recentes do sistema central.",
            "\uD83D\uDD04", // 🔄 Update/Sync
            "EXECUTAR AGORA",
            new Color(60, 20, 90),
            e -> executarAtualizacaoExcel()
        ));
        
        actionsGrid.add(criarCardAcao(
            "IMPORTAR DADOS", 
            "Carregue novos arquivos brutos (.csv, .xlsx) para a fila.",
            "\uD83D\uDCC4", // 📄 Page
            "SELECIONAR ARQUIVO",
            new Color(20, 60, 90),
             e -> executarImportacao()
        ));

        panel.add(actionsGrid);
        panel.add(Box.createVerticalStrut(25));

        // 3. LOG / TERMINAL
        panel.add(criarTerminalLog());

        return panel;
    }

    // --- Sub-components do Dashboard ---

    private JPanel criarCardStatus() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(new RoundedBorder(15, new Color(45, 45, 50)));
        card.setMaximumSize(new Dimension(2000, 180));
        
        // Conteúdo Padding
        JPanel content = new JPanel(new GridLayout(2, 1));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Topo: Labels
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        
        JPanel topLeft = new JPanel(new GridLayout(2, 1, 0, 5));
        topLeft.setOpaque(false);
        JLabel lblTag = new JLabel("MONITORAMENTO");
        lblTag.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTag.setForeground(ACCENT_PURPLE);
        lblTag.setBorder(new EmptyBorder(0,0,5,0));
        
        JLabel lblTitle = new JLabel("STATUS ATUAL");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(TEXT_WHITE);
        
        topLeft.add(lblTag);
        topLeft.add(lblTitle);

        statusPercentLabel = new JLabel("0%");
        statusPercentLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        statusPercentLabel.setForeground(TEXT_WHITE);

        top.add(topLeft, BorderLayout.WEST);
        top.add(statusPercentLabel, BorderLayout.EAST);

        // Baixo: ProgressBar (CORRIGIDO TAMANHO) + Texto Info
        JPanel bottom = new JPanel(new BorderLayout(0, 10));
        bottom.setOpaque(false);
        
        mainProgressBar = new JProgressBar(0, 100);
        mainProgressBar.setPreferredSize(new Dimension(0, 8)); // Reducing thickness
        mainProgressBar.setForeground(ACCENT_PURPLE);
        mainProgressBar.setBackground(new Color(50, 50, 55));
        mainProgressBar.setBorderPainted(false);
        
        // Wrapper para impedir que a barra estique verticalmente
        JPanel pBarWrapper = new JPanel(new BorderLayout());
        pBarWrapper.setOpaque(false);
        pBarWrapper.add(mainProgressBar, BorderLayout.NORTH);
        
        statusTextLabel = new JLabel("\u26AA Aguardando comando..."); // ⚪ Medium White Circle
        statusTextLabel.setForeground(TEXT_GRAY);
        statusTextLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));

        bottom.add(pBarWrapper, BorderLayout.CENTER);
        bottom.add(statusTextLabel, BorderLayout.SOUTH);

        content.add(top);
        content.add(bottom);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarCardAcao(String titulo, String desc, String icone, String btnText, Color hoverColor, ActionListener action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(new RoundedBorder(15, new Color(45, 45, 50)));

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Icon Box
        JLabel lblIcon = new JLabel(icone);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        lblIcon.setForeground(TEXT_WHITE);
        lblIcon.setOpaque(true);
        lblIcon.setBackground(new Color(50, 50, 55));
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcon.setPreferredSize(new Dimension(50, 50));
        
        // Textos
        JPanel texts = new JPanel(new GridLayout(2, 1, 0, 5));
        texts.setOpaque(false);
        texts.setBorder(new EmptyBorder(0, 15, 0, 0));
        
        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTit.setForeground(TEXT_WHITE);
        
        JTextArea txtDesc = new JTextArea(desc);
        txtDesc.setFont(FONT_SMALL);
        txtDesc.setForeground(TEXT_GRAY);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setOpaque(false);
        txtDesc.setEditable(false);

        texts.add(lblTit);
        texts.add(txtDesc);

        // Header Panel (Icon + Texts)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(lblIcon, BorderLayout.WEST);
        headerPanel.add(texts, BorderLayout.CENTER);

        // Footer Action (Button look-alike)
        JButton btnAction = new JButton(btnText);
        btnAction.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAction.setForeground(ACCENT_PURPLE);
        btnAction.setContentAreaFilled(false);
        btnAction.setBorderPainted(false);
        btnAction.setFocusPainted(false);
        btnAction.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAction.setHorizontalAlignment(SwingConstants.RIGHT);
        btnAction.addActionListener(action);

        content.add(headerPanel, BorderLayout.CENTER);
        content.add(btnAction, BorderLayout.SOUTH);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarTerminalLog() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // Header do Terminal
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel lblTitle = new JLabel("\u2692 REGISTRO DE ATIVIDADES"); // ⚒ Hammer and Pick
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(TEXT_MUTED);
        
        JButton btnClear = new JButton("LIMPAR CONSOLE");
        btnClear.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnClear.setForeground(TEXT_MUTED);
        btnClear.setContentAreaFilled(false);
        btnClear.setBorder(null);
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClear.addActionListener(e -> termArea.setText(""));
        
        header.add(lblTitle, BorderLayout.WEST);
        header.add(btnClear, BorderLayout.EAST);

        // Area de Texto
        termArea = new JTextArea();
        termArea.setFont(FONT_MONO);
        termArea.setBackground(new Color(15, 15, 18)); // Quase preto
        termArea.setForeground(ACCENT_GREEN);
        termArea.setCaretColor(TEXT_WHITE);
        termArea.setEditable(false);
        termArea.setBorder(new EmptyBorder(15, 15, 15, 15));

        JScrollPane scroll = new JScrollPane(termArea);
        scroll.setBorder(new RoundedBorder(10, new Color(45,45,45)));
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        // Custom Scrollbar visual
        scroll.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(60, 60, 60);
                this.trackColor = new Color(20, 20, 20);
            }
        });

        panel.add(header, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        
        // Forçar tamanho mínimo
        panel.setPreferredSize(new Dimension(0, 300));
        
        return panel;
    }

    // =================================================================================
    // SECTION: FOOTER
    // =================================================================================
    private JPanel criarFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BG_DARKEST);
        footer.setBorder(new EmptyBorder(8, 40, 8, 40));
        footer.setPreferredSize(new Dimension(0, 30));

        JPanel stats = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        stats.setOpaque(false);
        
        stats.add(criarLabelStats("CPU: ", "24%", new Color(0, 200, 100)));
        stats.add(criarLabelStats("RAM: ", "4.2GB / 16GB", new Color(0, 200, 100)));
        uptimeLabel = criarLabelStats("UPTIME: ", "00:00:00", new Color(180, 100, 255));
        stats.add(uptimeLabel);

        JLabel version = new JLabel("V2.4.0-STABLE | VIVO AGING ENTERPRISE");
        version.setFont(new Font("Segoe UI", Font.BOLD, 10));
        version.setForeground(TEXT_MUTED);

        footer.add(stats, BorderLayout.WEST);
        footer.add(version, BorderLayout.EAST);
        return footer;
    }

    private JLabel criarLabelStats(String prefix, String val, Color dotColor) {
        JLabel l = new JLabel("● " + prefix + val);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(TEXT_GRAY);
        return l;
    }

    // =================================================================================
    // SECTION: LOGICA DE NEGOCIO
    // =================================================================================

    private void executarAtualizacaoExcel() {
        log("Iniciando protocolo de atualização de dados...");
        setStatus("Executando Script...", 10);
        
        new Thread(() -> {
            try {
                String caminhoScript = "C:\\Users\\user\\Desktop\\MotorJava\\scripts\\atualizar_dados.vbs";
                File script = new File(caminhoScript);
                
                if (!script.exists()) {
                    log("ERRO: Script não encontrado em " + caminhoScript);
                    setStatus("Erro Fatal", 0);
                    return;
                }

                // Simula progresso visual
                setStatus("Processando Script VBS...", 30);
                Process p = Runtime.getRuntime().exec("wscript \"" + caminhoScript + "\"");
                int exitCode = p.waitFor();
                
                if (exitCode == 0) {
                    log("SUCESSO: Script executado com êxito.");
                    setStatus("Planilha Atualizada", 100);
                } else {
                    log("FALHA: Script retornou código " + exitCode);
                    setStatus("Erro na Execução", 0);
                }
                
            } catch (Exception ex) {
                log("EXCEPTION: " + ex.getMessage());
                setStatus("Erro de Sistema", 0);
            }
        }).start();
    }

    private void executarImportacao() {
        log("Preparando motor de ingestão de dados...");
        setStatus("Buscando Arquivos...", 15);
        
        new Thread(() -> {
            try {
                File pasta = new File(PASTA_STOCK);
                if (!pasta.exists() || !pasta.isDirectory()) {
                    log("CRÍTICO: Pasta de origem não acessível: " + PASTA_STOCK);
                    setStatus("Erro de I/O", 0);
                    return;
                }

                File arquivoAlvo = new File(pasta, "EQUIPAMENTO_SERIALIZADOS_VOLANTE_SP.xlsx");
                
                if (!arquivoAlvo.exists()) {
                    log("AVISO: Arquivo padrão não detectado. Buscando alternativas...");
                    File[] arquivos = pasta.listFiles((d, name) -> name.toLowerCase().endsWith(".xlsx") || name.toLowerCase().endsWith(".csv"));
                    
                    if (arquivos == null || arquivos.length == 0) {
                        log("ERRO: Nenhum arquivo de dados compatível encontrado.");
                        setStatus("Sem Dados", 0);
                        return;
                    }
                    
                    Arrays.sort(arquivos, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
                    arquivoAlvo = arquivos[0];
                    log("INFO: Selecionado automaticamente: " + arquivoAlvo.getName());
                }

                // Simulação de etapas
                setStatus("Carregando " + arquivoAlvo.getName() + "...", 40);
                Thread.sleep(500); 
                
                setStatus("Processando ETL...", 65);
                ImportadorArquivo.executarCarga(arquivoAlvo.getAbsolutePath());
                
                setStatus("Finalizando Transação...", 90);
                Thread.sleep(500);

                log("IMPORTAÇÃO CONCLUÍDA: Dados persistidos no banco.");
                setStatus("Operação Completa", 100);
                
            } catch (Exception ex) {
                log("ERRO FATAL NA IMPORTAÇÃO: " + ex.getMessage());
                setStatus("Falha Crítica", 0);
            }
        }).start();
    }

    // =================================================================================
    // SECTION: UTILITARIOS E HELPERS
    // =================================================================================

    private void setStatus(String texto, int percent) {
        SwingUtilities.invokeLater(() -> {
            statusTextLabel.setText("⚡ " + texto);
            statusPercentLabel.setText(percent + "%");
            
            // Animação simples da barra
            mainProgressBar.setValue(percent);
            
            if (percent == 100) {
                statusTextLabel.setForeground(ACCENT_GREEN);
            } else if (percent == 0) {
                statusTextLabel.setForeground(new Color(255, 80, 80));
            } else {
                statusTextLabel.setForeground(TEXT_WHITE);
            }
        });
    }

    private void log(String msg) {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        SwingUtilities.invokeLater(() -> {
            termArea.append("[" + time + "] " + msg + "\n");
            termArea.setCaretPosition(termArea.getDocument().getLength());
        });
    }

    private void redirecionarConsole() {
        OutputStream out = new OutputStream() {
            private StringBuilder buffer = new StringBuilder();
            @Override
            public void write(int b) {
                if (b == '\n') {
                    log(buffer.toString());
                    buffer.setLength(0);
                } else buffer.append((char) b);
            }
        };
        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }

    private void iniciarTimerFooter() {
        new Timer(1000, e -> {
            long now = System.currentTimeMillis();
            long diff = now - startTime;
            long s = (diff / 1000) % 60;
            long m = (diff / (1000 * 60)) % 60;
            long h = (diff / (1000 * 60 * 60));
            uptimeLabel.setText(String.format("● UPTIME: %02d:%02d:%02d", h, m, s));
        }).start();
    }

    private void estilizarBotaoOutline(JButton btn) {
        btn.setForeground(TEXT_GRAY);
        btn.setBackground(BG_DARKEST);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(new LineBorder(new Color(60,60,60), 1, true));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // Classe interna para Bordas Arredondadas
    private static class RoundedBorder extends AbstractBorder {
        private int r;
        private Color c;
        RoundedBorder(int radius, Color color) { r = radius; c = color; }
        public void paintBorder(Component cmp, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c);
            g2.drawRoundRect(x, y, w-1, h-1, r, r);
            g2.dispose();
        }
        public Insets getBorderInsets(Component c) { return new Insets(r+1, r+1, r+1, r+1); }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new GuiApp().setVisible(true));
    }
}
