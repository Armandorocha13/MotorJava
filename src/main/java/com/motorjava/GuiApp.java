package com.motorjava;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.OutputStream;
import java.io.PrintStream;

public class GuiApp extends JFrame {

    private JTextArea logArea;
    // Caminho do arquivo fixo
    private static final String CAMINHO_ARQUIVO = "C:\\Users\\user\\Desktop\\EQUIPAMENTO_SERIALIZADOS_VOLANTE_SP.xlsx";
    
    // Cores do Tema
    private static final Color COR_FUNDO = new Color(18, 18, 18);
    private static final Color COR_CARD = new Color(30, 30, 30);
    private static final Color COR_DESTAQUE = new Color(138, 43, 226); // Roxo Vivo
    private static final Color COR_TEXTO = new Color(240, 240, 240);
    private static final Color COR_SUBTEXTO = new Color(170, 170, 170);
    private static final Color COR_VERDE = new Color(34, 139, 34);

    public GuiApp() {
        // Configuração da Janela
        setTitle("Vivo Aging Automate v2.0");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COR_FUNDO);

        // --- CABEÇALHO ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COR_FUNDO);
        headerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("VIVO AGING AUTOMATE");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(COR_DESTAQUE);
        
        JLabel subtitleLabel = new JLabel("Motor de Processamento de Dados");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(COR_SUBTEXTO);

        JPanel textHeader = new JPanel(new GridLayout(2, 1));
        textHeader.setBackground(COR_FUNDO);
        textHeader.add(titleLabel);
        textHeader.add(subtitleLabel);

        headerPanel.add(textHeader, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);


        // --- ÁREA CENTRAL (Passos) ---
        JPanel stepsPanel = new JPanel(new GridLayout(1, 2, 20, 0)); // 1 linha, 2 colunas, gap 20
        stepsPanel.setBackground(COR_FUNDO);
        stepsPanel.setBorder(new EmptyBorder(10, 30, 20, 30));

        // PASSO 01: Ingestão
        JPanel cardStep1 = criarCardPasso(
            "01", 
            "Serviço de Ingestão", 
            "Monitora a pasta de entrada, identifica arquivos, renomeia e move para a estrutura de pastas correta.",
            "Iniciar Serviço",
            COR_VERDE
        );
        JButton btnStep1 = (JButton) cardStep1.getClientProperty("btnAction");
        btnStep1.addActionListener(e -> {
            btnStep1.setEnabled(false);
            btnStep1.setText("Serviço Rodando...");
            btnStep1.setBackground(COR_CARD.brighter());
            executarAcao(() -> {
                log("--- INICIANDO SERVIÇO DE INGESTÃO ---");
                try {
                    ServicoIngestao.iniciarMonitoramento();
                } catch (Exception ex) {
                    throw new RuntimeException(ex); // Vai pro log
                }
            });
        });

        // PASSO 02: Stock Técnico
        JPanel cardStep2 = criarCardPasso(
            "02", 
            "Atualizar Stock Técnico", 
            "Lê o arquivo Excel processado e atualiza a base de dados SQL 'estoque_vivo_historico'.",
            "Executar Carga",
            COR_DESTAQUE
        );
        JButton btnStep2 = (JButton) cardStep2.getClientProperty("btnAction");
        btnStep2.addActionListener(e -> executarAcao(() -> {
            log("--- INICIANDO CARGA DO STOCK TÉCNICO ---");
            ImportadorArquivo.executarCarga(CAMINHO_ARQUIVO);
        }));

        stepsPanel.add(cardStep1);
        stepsPanel.add(cardStep2);

        // Wrapper para não esticar muito verticalmente
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(COR_FUNDO);
        centerWrapper.add(stepsPanel, BorderLayout.NORTH);
        
        // --- ÁREA DE LOG ---
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(new Color(12, 12, 12));
        logArea.setForeground(new Color(0, 255, 127)); // Verde terminal
        logArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        logArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollLog = new JScrollPane(logArea);
        scrollLog.setBorder(new LineBorder(new Color(40, 40, 40), 1));
        scrollLog.setPreferredSize(new Dimension(0, 250)); // Altura fixa para o log

        // Adiciona título "LOG DO SISTEMA" acima do scroll
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBackground(COR_FUNDO);
        logPanel.setBorder(new EmptyBorder(0, 30, 20, 30));
        
        JLabel logTitle = new JLabel("Output do Sistema");
        logTitle.setForeground(COR_SUBTEXTO);
        logTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logTitle.setBorder(new EmptyBorder(0, 0, 5, 0));
        
        logPanel.add(logTitle, BorderLayout.NORTH);
        logPanel.add(scrollLog, BorderLayout.CENTER);

        centerWrapper.add(logPanel, BorderLayout.CENTER);
        add(centerWrapper, BorderLayout.CENTER);

        // Redireciona System.out
        redirecionarConsole();
    }

    private JPanel criarCardPasso(String numero, String titulo, String descricao, String textoBotao, Color corBotao) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(COR_CARD);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Efeito de borda arredondada (simulado com line border por enquanto no Swing puro)
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(50, 50, 50), 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel numLabel = new JLabel(numero);
        numLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        numLabel.setForeground(new Color(60, 60, 60));
        numLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(titulo);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(COR_TEXTO);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea descLabel = new JTextArea(descricao);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setForeground(COR_SUBTEXTO);
        descLabel.setBackground(COR_CARD);
        descLabel.setWrapStyleWord(true);
        descLabel.setLineWrap(true);
        descLabel.setEditable(false);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descLabel.setMaximumSize(new Dimension(400, 60));

        // Botão Moderno
        JButton btn = new JButton(textoBotao);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(corBotao);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(500, 45));
        
        // Hover effect simples
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                if(btn.isEnabled()) btn.setBackground(corBotao.brighter());
            }
            public void mouseExited(MouseEvent evt) {
                if(btn.isEnabled()) btn.setBackground(corBotao);
            }
        });

        card.add(numLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(descLabel);
        card.add(Box.createVerticalGlue()); // Empurra o botão pra baixo se tiver espaço extra
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(btn);

        // Hack para retornar o botão e poder adicionar listener depois
        card.putClientProperty("btnAction", btn);

        return card;
    }

    private void executarAcao(Runnable acao) {
        new Thread(() -> {
            try {
                acao.run();
            } catch (Exception e) {
                e.printStackTrace();
                log("ERRO: " + e.getMessage());
            }
        }).start();
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> logArea.append(msg + "\n"));
    }

    private void redirecionarConsole() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) {
                atualizarTexto(String.valueOf((char) b));
            }
            @Override
            public void write(byte[] b, int off, int len) {
                atualizarTexto(new String(b, off, len));
            }
            private void atualizarTexto(String texto) {
                SwingUtilities.invokeLater(() -> {
                    logArea.append(texto);
                    logArea.setCaretPosition(logArea.getDocument().getLength());
                });
            }
        };
        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }

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
