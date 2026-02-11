package com.motorjava;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.util.Arrays;

/**
 * VIVO AGING AUTOMATE - Interface Premium
 * Design System: Modern, Accessible, Responsive
 * UX Principles: Clear hierarchy, immediate feedback, progressive disclosure
 */
public class GuiApp extends JFrame {

    // ===== DESIGN TOKENS =====
    private static final Color BG_PRIMARY = new Color(12, 12, 12);
    private static final Color BG_SECONDARY = new Color(22, 22, 22);
    private static final Color BG_ELEVATED = new Color(28, 28, 28);
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(160, 160, 160);
    private static final Color TEXT_TERTIARY = new Color(100, 100, 100);
    private static final Color ACCENT_BLUE = new Color(10, 132, 255);
    private static final Color ACCENT_BLUE_HOVER = new Color(40, 152, 255);
    private static final Color ACCENT_BLUE_PRESSED = new Color(0, 112, 235);
    private static final Color SUCCESS = new Color(52, 199, 89);
    private static final Color ERROR = new Color(255, 69, 58);
    private static final Color WARNING = new Color(255, 159, 10);
    private static final Color BORDER_SUBTLE = new Color(45, 45, 45);
    private static final Color BORDER_STRONG = new Color(60, 60, 60);

    // ===== COMPONENTES =====
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private JTextArea logArea;
    private ModernButton btnMonitor, btnImport;
    private JPanel statusIndicator;
    
    private static final String PASTA_STOCK = "C:\\Users\\user\\Desktop\\ARMANDO POWER BI\\VivoAging\\Equipamentos serializados";

    public GuiApp() {
        configurarJanela();
        construirInterface();
        redirecionarConsole();
        exibirMensagemInicial();
    }

    private void configurarJanela() {
        setTitle("Vivo Aging Automate");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BG_PRIMARY);
        setLayout(new BorderLayout(0, 0));
    }

    private void construirInterface() {
        add(criarHeader(), BorderLayout.NORTH);
        add(criarPainelCentral(), BorderLayout.CENTER);
        add(criarRodape(), BorderLayout.SOUTH);
    }

    // ========== HEADER ==========
    private JPanel criarHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_PRIMARY);
        header.setBorder(new EmptyBorder(35, 45, 25, 45));

        // Título com gradiente visual
        JLabel titulo = new JLabel("VIVO AGING");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titulo.setForeground(ACCENT_BLUE);

        JLabel subtitulo = new JLabel("Sistema de Processamento Automatizado");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitulo.setForeground(TEXT_SECONDARY);

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setBackground(BG_PRIMARY);
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(5));
        textos.add(subtitulo);

        header.add(textos, BorderLayout.WEST);
        return header;
    }

    // ========== PAINEL CENTRAL ==========
    private JPanel criarPainelCentral() {
        JPanel central = new JPanel(new BorderLayout(0, 25));
        central.setBackground(BG_PRIMARY);
        central.setBorder(new EmptyBorder(0, 45, 25, 45));

        central.add(criarStatusCard(), BorderLayout.NORTH);
        central.add(criarPainelAcoes(), BorderLayout.CENTER);
        central.add(criarPainelLog(), BorderLayout.SOUTH);

        return central;
    }

    private JPanel criarStatusCard() {
        JPanel card = new JPanel(new BorderLayout(20, 0));
        card.setBackground(BG_ELEVATED);
        card.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(12, BORDER_SUBTLE),
            new EmptyBorder(22, 28, 22, 28)
        ));

        // Indicador visual de status (círculo colorido)
        statusIndicator = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SUCCESS);
                g2.fillOval(0, 0, 12, 12);
            }
        };
        statusIndicator.setPreferredSize(new Dimension(12, 12));
        statusIndicator.setOpaque(false);

        statusLabel = new JLabel("Sistema Pronto");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        statusLabel.setForeground(TEXT_PRIMARY);

        JPanel statusContent = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        statusContent.setBackground(BG_ELEVATED);
        statusContent.add(statusIndicator);
        statusContent.add(statusLabel);

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(false);
        progressBar.setBackground(new Color(35, 35, 35));
        progressBar.setForeground(ACCENT_BLUE);
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(0, 4));

        JPanel wrapper = new JPanel(new BorderLayout(0, 15));
        wrapper.setBackground(BG_ELEVATED);
        wrapper.add(statusContent, BorderLayout.CENTER);
        wrapper.add(progressBar, BorderLayout.SOUTH);

        card.add(wrapper, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarPainelAcoes() {
        JPanel painel = new JPanel(new GridLayout(1, 2, 25, 0));
        painel.setBackground(BG_PRIMARY);
        painel.setBorder(new EmptyBorder(10, 0, 10, 0));

        btnMonitor = new ModernButton(
            "Monitorar Downloads",
            "Detecta automaticamente novos arquivos e os organiza na pasta de processamento",
            "📡"
        );
        btnMonitor.addActionListener(e -> iniciarMonitoramento());

        btnImport = new ModernButton(
            "Importar Dados",
            "Processa o arquivo mais recente e importa os dados para o banco de dados",
            "💾"
        );
        btnImport.addActionListener(e -> executarImportacao());

        painel.add(btnMonitor);
        painel.add(btnImport);

        return painel;
    }

    private JPanel criarPainelLog() {
        JPanel painel = new JPanel(new BorderLayout(0, 12));
        painel.setBackground(BG_PRIMARY);
        painel.setPreferredSize(new Dimension(0, 200));

        JLabel lblLog = new JLabel("REGISTRO DE ATIVIDADES");
        lblLog.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblLog.setForeground(TEXT_TERTIARY);
        lblLog.setBorder(new EmptyBorder(0, 2, 0, 0));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
        logArea.setBackground(BG_SECONDARY);
        logArea.setForeground(TEXT_SECONDARY);
        logArea.setLineWrap(false);
        logArea.setBorder(new EmptyBorder(15, 15, 15, 15));

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(new RoundedBorder(8, BORDER_SUBTLE));
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        painel.add(lblLog, BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);

        return painel;
    }

    // ========== RODAPÉ ==========
    private JPanel criarRodape() {
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.CENTER));
        rodape.setBackground(BG_PRIMARY);
        rodape.setBorder(new EmptyBorder(12, 0, 18, 0));

        JLabel lblVersao = new JLabel("v3.0 Premium • Desenvolvido para Vivo");
        lblVersao.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblVersao.setForeground(TEXT_TERTIARY);

        rodape.add(lblVersao);
        return rodape;
    }

    // ========== AÇÕES ==========
    private void iniciarMonitoramento() {
        btnMonitor.setEnabled(false);
        atualizarStatus("Monitorando Downloads...", true, WARNING);
        
        new Thread(() -> {
            try {
                ServicoIngestao.iniciarMonitoramento();
            } catch (Exception ex) {
                mostrarErro("Falha no monitoramento: " + ex.getMessage());
                SwingUtilities.invokeLater(() -> btnMonitor.setEnabled(true));
            }
        }).start();
    }

    private void executarImportacao() {
        btnImport.setEnabled(false);
        atualizarStatus("Buscando arquivo...", true, ACCENT_BLUE);
        
        new Thread(() -> {
            try {
                File pasta = new File(PASTA_STOCK);
                if (!pasta.exists() || !pasta.isDirectory()) {
                    mostrarErro("Pasta não encontrada");
                    return;
                }

                File[] arquivos = pasta.listFiles((d, name) -> name.matches("\\d{3}\\..*"));
                if (arquivos == null || arquivos.length == 0) {
                    mostrarAviso("Nenhum arquivo encontrado. Execute o monitoramento primeiro.");
                    return;
                }

                Arrays.sort(arquivos, (f1, f2) -> f2.getName().compareTo(f1.getName()));
                File arquivo = arquivos[0];

                atualizarStatus("Processando " + arquivo.getName() + "...", true, ACCENT_BLUE);
                
                ImportadorArquivo.executarCarga(arquivo.getAbsolutePath());
                
                mostrarSucesso("Importação concluída com sucesso!");
                
            } catch (Exception ex) {
                mostrarErro("Falha na importação: " + ex.getMessage());
            } finally {
                SwingUtilities.invokeLater(() -> btnImport.setEnabled(true));
            }
        }).start();
    }

    // ========== FEEDBACK VISUAL ==========
    private void atualizarStatus(String msg, boolean loading, Color cor) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(msg);
            statusLabel.setForeground(TEXT_PRIMARY);
            progressBar.setIndeterminate(loading);
            if (!loading) progressBar.setValue(0);
            
            // Atualiza cor do indicador
            statusIndicator.repaint();
            Graphics2D g2 = (Graphics2D) statusIndicator.getGraphics();
            if (g2 != null) {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(statusIndicator.getBackground());
                g2.fillRect(0, 0, 12, 12);
                g2.setColor(cor);
                g2.fillOval(0, 0, 12, 12);
            }
        });
    }

    private void mostrarSucesso(String msg) {
        SwingUtilities.invokeLater(() -> {
            atualizarStatus(msg, false, SUCCESS);
            Timer timer = new Timer(4000, e -> atualizarStatus("Sistema Pronto", false, SUCCESS));
            timer.setRepeats(false);
            timer.start();
        });
    }

    private void mostrarErro(String msg) {
        SwingUtilities.invokeLater(() -> {
            atualizarStatus("Erro: " + msg, false, ERROR);
            Timer timer = new Timer(6000, e -> atualizarStatus("Sistema Pronto", false, SUCCESS));
            timer.setRepeats(false);
            timer.start();
        });
    }

    private void mostrarAviso(String msg) {
        SwingUtilities.invokeLater(() -> {
            atualizarStatus(msg, false, WARNING);
            Timer timer = new Timer(5000, e -> atualizarStatus("Sistema Pronto", false, SUCCESS));
            timer.setRepeats(false);
            timer.start();
        });
    }

    private void exibirMensagemInicial() {
        SwingUtilities.invokeLater(() -> {
            logArea.append("✓ Sistema inicializado com sucesso\n");
            logArea.append("→ Pronto para processar dados\n");
        });
    }

    // ========== REDIRECIONAMENTO DE CONSOLE ==========
    private void redirecionarConsole() {
        OutputStream out = new OutputStream() {
            private StringBuilder buffer = new StringBuilder();
            
            @Override
            public void write(int b) {
                if (b == '\n') {
                    processarLinha(buffer.toString());
                    buffer.setLength(0);
                } else {
                    buffer.append((char) b);
                }
            }
            
            @Override
            public void write(byte[] b, int off, int len) {
                String texto = new String(b, off, len);
                for (char c : texto.toCharArray()) {
                    write(c);
                }
            }
        };
        
        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }

    private void processarLinha(String linha) {
        final String linhaFinal = linha.trim();
        if (linhaFinal.isEmpty()) return;

        if (linhaFinal.contains("✅") || linhaFinal.contains("❌") || 
            linhaFinal.contains("⚠️") || linhaFinal.contains("🚀") ||
            linhaFinal.contains("SUCESSO") || linhaFinal.contains("ERRO") ||
            linhaFinal.contains("Processados") || linhaFinal.contains("Total")) {
            
            SwingUtilities.invokeLater(() -> {
                logArea.append(linhaFinal + "\n");
                logArea.setCaretPosition(logArea.getDocument().getLength());
            });
        }
    }

    // ========== COMPONENTE: BOTÃO MODERNO ==========
    private class ModernButton extends JButton {
        private Color currentBg = BG_ELEVATED;
        private boolean isHovered = false;
        private boolean isPressed = false;

        public ModernButton(String titulo, String descricao, String emoji) {
            setLayout(new BorderLayout(15, 15));
            setBackground(BG_ELEVATED);
            setBorder(new RoundedBorder(14, BORDER_SUBTLE));
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setContentAreaFilled(false);

            // Emoji Icon
            JLabel iconLabel = new JLabel(emoji);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            iconLabel.setVerticalAlignment(SwingConstants.TOP);

            // Título
            JLabel lblTitulo = new JLabel(titulo);
            lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblTitulo.setForeground(TEXT_PRIMARY);

            // Descrição
            JTextArea lblDesc = new JTextArea(descricao);
            lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblDesc.setForeground(TEXT_SECONDARY);
            lblDesc.setBackground(BG_ELEVATED);
            lblDesc.setLineWrap(true);
            lblDesc.setWrapStyleWord(true);
            lblDesc.setEditable(false);
            lblDesc.setFocusable(false);
            lblDesc.setBorder(null);

            JPanel textPanel = new JPanel(new BorderLayout(0, 8));
            textPanel.setBackground(BG_ELEVATED);
            textPanel.add(lblTitulo, BorderLayout.NORTH);
            textPanel.add(lblDesc, BorderLayout.CENTER);

            JPanel content = new JPanel(new BorderLayout(15, 0));
            content.setBackground(BG_ELEVATED);
            content.setBorder(new EmptyBorder(28, 24, 28, 24));
            content.add(iconLabel, BorderLayout.WEST);
            content.add(textPanel, BorderLayout.CENTER);

            add(content, BorderLayout.CENTER);

            // Animações
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    if (isEnabled()) {
                        isHovered = true;
                        currentBg = new Color(35, 35, 35);
                        lblTitulo.setForeground(ACCENT_BLUE_HOVER);
                        repaint();
                    }
                }
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    isPressed = false;
                    currentBg = BG_ELEVATED;
                    if (isEnabled()) {
                        lblTitulo.setForeground(TEXT_PRIMARY);
                    }
                    repaint();
                }
                public void mousePressed(MouseEvent e) {
                    if (isEnabled()) {
                        isPressed = true;
                        currentBg = new Color(30, 30, 30);
                        repaint();
                    }
                }
                public void mouseReleased(MouseEvent e) {
                    if (isEnabled()) {
                        isPressed = false;
                        currentBg = isHovered ? new Color(35, 35, 35) : BG_ELEVATED;
                        repaint();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Background com sombra sutil
            if (isHovered) {
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 14, 14);
            }
            
            g2.setColor(currentBg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ========== COMPONENTE: BORDA ARREDONDADA ==========
    private class RoundedBorder extends AbstractBorder {
        private int radius;
        private Color color;

        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.draw(new RoundRectangle2D.Double(x, y, width - 1, height - 1, radius, radius));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(1, 1, 1, 1);
        }
    }

    // ========== MAIN ==========
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            GuiApp app = new GuiApp();
            app.setVisible(true);
        });
    }
}
