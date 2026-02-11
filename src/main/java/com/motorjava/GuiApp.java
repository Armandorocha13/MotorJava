package com.motorjava;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * CLASSE PRINCIPAL: INTERFACE GRÁFICA (DASHBOARD)
 * ---------------------------------------------------------
 * Esta classe é responsável por criar a interface visual que o usuário vê.
 * Ela serve como um "Painel de Controle" para o Motor de Ingestão.
 *
 * Funcionalidades:
 * 1. Exibe o status atual do serviço (Parado, Monitorando, Processando).
 * 2. Mostra uma barra de progresso visual.
 * 3. Exibe logs filtrados e coloridos para facilitar o entendimento.
 * 4. Permite iniciar o processo de monitoramento com um clique.
 *
 * Autor: Equipe de Desenvolvimento
 * Versão: 4.0 - Dashboard Clean
 */
public class GuiApp extends JFrame {

    // Componentes da Interface (Janela, Textos, Barra de Progresso)
    private JTextPane logArea;      // Área onde os logs aparecem
    private JProgressBar progressBar; // Barra de progresso
    private JLabel statusLabel;     // Texto de status (ex: "Aguardando...")
    private StyledDocument doc;     // Documento para estilizar os logs (cores)

    // --- CORES DO TEMA (Modo Escuro Moderno) ---
    private static final Color COR_FUNDO = new Color(18, 18, 18);
    private static final Color COR_PAINEL = new Color(28, 28, 28);
    private static final Color COR_DESTAQUE = new Color(138, 43, 226); // Roxo Vivo
    private static final Color COR_TEXTO_PRINCIPAL = new Color(255, 255, 255);
    private static final Color COR_TEXTO_SECUNDARIO = new Color(170, 170, 170);
    private static final Color COR_VERDE = new Color(46, 204, 113);
    private static final Color COR_VERMELHO = new Color(231, 76, 60);
    private static final Color COR_AMARELO = new Color(241, 196, 15);

    public GuiApp() {
        // 1. CONFIGURAÇÃO DA JANELA PRINCIPAL
        setTitle("Vivo Aging Automate v4.0 - Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Fecha o programa ao fechar a janela
        setLocationRelativeTo(null); // Centraliza na tela
        setLayout(new BorderLayout());
        getContentPane().setBackground(COR_FUNDO);

        // 2. MONTAGEM DAS SEÇÕES DA TELA
        configurarCabecalho();
        configurarPainelCentral();
        
        // 3. REDIRECIONAMENTO DE LOGS
        // Faz com que os System.out.println apareçam na tela do app, não só no terminal
        redirecionarConsoleComFiltro();
    }

    /**
     * Configura a parte superior da tela (Título e Botão de Iniciar)
     */
    private void configurarCabecalho() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COR_FUNDO);
        header.setBorder(new EmptyBorder(25, 40, 25, 40));

        // Títulos
        JLabel title = new JLabel("VIVO AGING AUTOMATE");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(COR_DESTAQUE);

        JLabel subtitle = new JLabel("Monitoramento & Ingestão Inteligente");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(COR_TEXTO_SECUNDARIO);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1));
        titleBlock.setBackground(COR_FUNDO);
        titleBlock.add(title);
        titleBlock.add(subtitle);

        // Botão de Start
        JButton btnStart = new JButton("INICIAR SERVIÇO");
        estilizarBotao(btnStart, COR_VERDE);
        btnStart.setPreferredSize(new Dimension(180, 45));
        
        // Ação do Botão
        btnStart.addActionListener(e -> {
            btnStart.setEnabled(false); // Desativa para não clicar duas vezes
            btnStart.setText("RODANDO...");
            btnStart.setBackground(COR_PAINEL.brighter());
            iniciarServico(); // Chama o método que inicia o motor
        });

        header.add(titleBlock, BorderLayout.WEST);
        header.add(btnStart, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
    }

    /**
     * Configura a área central com o Status e o Log
     */
    private void configurarPainelCentral() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(COR_FUNDO);
        centerPanel.setBorder(new EmptyBorder(0, 40, 20, 40));

        // --- PAINEL DE STATUS ---
        JPanel statusCard = new JPanel(new BorderLayout());
        statusCard.setBackground(COR_PAINEL);
        statusCard.setBorder(new EmptyBorder(30, 30, 30, 30));
        statusCard.setMaximumSize(new Dimension(2000, 150));

        statusLabel = new JLabel("Aguardando Início do Serviço...");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        statusLabel.setForeground(COR_TEXTO_PRINCIPAL);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(false); // Só ativa animação quando começar
        progressBar.setBackground(COR_PAINEL);
        progressBar.setForeground(COR_DESTAQUE);
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(0, 8));

        statusCard.add(statusLabel, BorderLayout.CENTER);
        statusCard.add(progressBar, BorderLayout.SOUTH);

        centerPanel.add(statusCard);

        // --- PAINEL DE LOGS ---
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBackground(COR_PAINEL);
        logPanel.setBorder(new EmptyBorder(25, 0, 0, 0));

        JLabel logTitle = new JLabel("  Histórico de Eventos Relevantes");
        logTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logTitle.setForeground(COR_TEXTO_SECUNDARIO);
        logTitle.setBorder(new EmptyBorder(10, 10, 10, 10));

        logArea = new JTextPane();
        logArea.setEditable(false); // Usuário não pode editar o log
        logArea.setBackground(COR_PAINEL);
        logArea.setForeground(COR_TEXTO_PRINCIPAL);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        doc = logArea.getStyledDocument();

        JScrollPane scrollLog = new JScrollPane(logArea); // Adiciona barra de rolagem
        scrollLog.setBorder(null);
        scrollLog.setPreferredSize(new Dimension(0, 200));

        logPanel.add(logTitle, BorderLayout.NORTH);
        logPanel.add(scrollLog, BorderLayout.CENTER);

        centerPanel.add(logPanel);
        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Aplica estilo visual aos botões
     */
    private void estilizarBotao(JButton btn, Color cor) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Inicia o Monitoramento em uma nova Thread para não travar a interface visual
     */
    private void iniciarServico() {
        new Thread(() -> {
            addLog("Serviço Iniciado. Monitorando Downloads...", COR_TEXTO_SECUNDARIO);
            setStatus("Monitorando pasta de Downloads...", false);
            
            try {
                // Chama a lógica pesada que fica no ServicoIngestao
                ServicoIngestao.iniciarMonitoramento();
            } catch (Exception e) {
                setStatus("Erro Fatal no Serviço", false);
                addLog("ERRO: " + e.getMessage(), COR_VERMELHO);
            }
        }).start();
    }

    // --- MÉTODOS AUXILIARES: Atualizam a tela de forma segura ---

    private void setStatus(String msg, boolean carregando) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(msg);
            progressBar.setIndeterminate(carregando);
            if (!carregando) progressBar.setValue(0);
        });
    }

    private void addLog(String text, Color color) {
        SwingUtilities.invokeLater(() -> {
            try {
                SimpleAttributeSet keyWord = new SimpleAttributeSet();
                StyleConstants.setForeground(keyWord, color);
                
                String time = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
                doc.insertString(doc.getLength(), "[" + time + "] " + text + "\n", keyWord);
                logArea.setCaretPosition(doc.getLength()); // Rola automatimanete para o fim
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }

    // --- FILTRO DE LOGS ---
    // Captura tudo que o programa imprime (System.out) e decide o que mostrar na tenla
    private void redirecionarConsoleComFiltro() {
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
                String s = new String(b, off, len);
                for (char c : s.toCharArray()) {
                    if (c == '\n') {
                        processarLinha(buffer.toString());
                        buffer.setLength(0);
                    } else {
                        buffer.append(c);
                    }
                }
            }
        };
        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }

    /**
     * Analisa cada linha de log e decide:
     * 1. Se deve ser mostrada na tela.
     * 2. Qual cor usar.
     * 3. Se deve atualizar o Status principal.
     */
    private void processarLinha(String linha) {
        linha = linha.trim();
        if (linha.isEmpty()) return;

        // EVENTOS POSITIVOS
        if (linha.contains("🆕 Detetado")) {
            setStatus("Arquivo Detectado! Iniciando Triagem...", true);
            addLog(linha, COR_AMARELO);
        } 
        else if (linha.contains("PASSO 1")) {
            setStatus("Padronizando e Renomeando Arquivo...", true);
            addLog("Iniciando Ingestão e Padronização", COR_TEXTO_PRINCIPAL);
        }
        else if (linha.contains("PASSO 2")) {
            setStatus("Importando dados para o Banco...", true);
            addLog("Iniciando Carga no Bando de Dados", COR_TEXTO_PRINCIPAL);
        }
        else if (linha.contains("✅ Carga automática finalizada") || linha.contains("Ciclo de processamento concluído")) {
            setStatus("Processo Concluído com Sucesso!", false);
            addLog("SUCESSO: Ciclo Finalizado.", COR_VERDE);
            SwingUtilities.invokeLater(() -> {
                progressBar.setValue(100);
                // Espera 3 segundos e volta a monitorar
                Timer timer = new Timer(3000, e -> {
                    setStatus("Aguardando novos arquivos...", false);
                    progressBar.setValue(0);
                });
                timer.setRepeats(false);
                timer.start();
            });
        }
        // EVENTOS DE ERRO
        else if (linha.contains("❌") || linha.contains("ERRO") || linha.contains("Exception")) {
            setStatus("Erro no Processamento", false);
            addLog(linha, COR_VERMELHO);
        }
        // OUTROS EVENTOS RELEVANTES
        else if (linha.contains("✅") || linha.contains("movido")) {
            addLog(linha, COR_VERDE);
        }
        // O resto é ignorado para manter a tela limpa
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
