package com.motorjava.ui.vivo;

import com.motorjava.ui.MainFrame;
import com.motorjava.config.Config;
import com.motorjava.service.ImportadorArquivo;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class VivoAgingPanel extends JPanel {

    private JProgressBar mainProgressBar;
    private JLabel statusPercentLabel;
    private JLabel statusTitleLabel;
    private JTextArea termArea;

    public VivoAgingPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(20, 60, 0, 60));
        initComponents();
    }

    private void initComponents() {
        JPanel centerContent = new JPanel();
        centerContent.setLayout(new BoxLayout(centerContent, BoxLayout.Y_AXIS));
        centerContent.setOpaque(false);

        // Dashboard Títulos
        JLabel lblContext = new JLabel("DASHBOARD DE OPERAÇÕES");
        lblContext.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblContext.setForeground(MainFrame.ACCENT_BLUE);
        lblContext.setAlignmentX(CENTER_ALIGNMENT);
        JLabel lblTitle = new JLabel("STATUS VIVO AGING");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 42));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);

        centerContent.add(lblContext);
        centerContent.add(Box.createVerticalStrut(10));
        centerContent.add(lblTitle);
        centerContent.add(Box.createVerticalStrut(40));

        // Card de Progresso
        centerContent.add(criarProgressoCard());
        centerContent.add(Box.createVerticalStrut(40));

        // Grid de Ações
        JPanel grid = new JPanel(new GridLayout(1, 2, 30, 0));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(1100, 260));

        grid.add(criarAcaoCard("ATUALIZAR PLANILHA", "EXECUTAR", e -> executarAtualizacaoVBA()));
        grid.add(criarAcaoCard("IMPORTAR DADOS", "IMPORTAR", e -> executarImportacaoDados()));

        centerContent.add(grid);
        centerContent.add(Box.createVerticalStrut(40));

        // Registro de Atividades
        centerContent.add(criarLogCard());

        add(centerContent, BorderLayout.CENTER);
    }

    private JPanel criarProgressoCard() {
        JPanel card = new MainFrame.RoundedPanel(15, MainFrame.CARD_BG);
        card.setLayout(new BorderLayout());
        card.setMaximumSize(new Dimension(1100, 160));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 5, 0, 0, MainFrame.ACCENT_BLUE),
            new EmptyBorder(25, 30, 25, 30)
        ));

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);
        JLabel lblSub = new JLabel("Progresso Geral do Fluxo");
        lblSub.setForeground(MainFrame.TEXT_GRAY);
        
        statusTitleLabel = new JLabel("AGUARDANDO INÍCIO DO PROCESSO");
        statusTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        statusTitleLabel.setForeground(MainFrame.ACCENT_BLUE);
        
        left.add(lblSub);
        left.add(statusTitleLabel);

        statusPercentLabel = new JLabel("0%");
        statusPercentLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        statusPercentLabel.setForeground(Color.WHITE);

        mainProgressBar = new JProgressBar(0, 100);
        mainProgressBar.setValue(0);
        mainProgressBar.setPreferredSize(new Dimension(0, 12));
        mainProgressBar.setBackground(new Color(25, 30, 40));
        mainProgressBar.setForeground(MainFrame.ACCENT_BLUE);
        mainProgressBar.setUI(new javax.swing.plaf.basic.BasicProgressBarUI() {
            @Override
            protected Color getSelectionBackground() { return Color.WHITE; }
            @Override
            protected Color getSelectionForeground() { return Color.WHITE; }
        });
        mainProgressBar.setBorderPainted(false);

        card.add(left, BorderLayout.WEST);
        card.add(statusPercentLabel, BorderLayout.EAST);
        card.add(mainProgressBar, BorderLayout.SOUTH);

        return card;
    }

    private JPanel criarAcaoCard(String title, String desc, String icon, String btnText, java.awt.event.ActionListener action) {
        JPanel card = new MainFrame.RoundedPanel(15, MainFrame.CARD_BG);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(25, 30, 25, 30));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel lblIcon = new JLabel("<html><div style='background:#1F232B; padding:10px; border-radius:8px; font-size:20px;'>"+icon+"</div></html>");
        JLabel lblBadge = new JLabel("<html><div style='background:#0a201a; padding:2px 8px; border-radius:4px;'><font color='#00ff80' size='2'>DISPONÍVEL</font></div></html>");
        top.add(lblIcon, BorderLayout.WEST);
        top.add(lblBadge, BorderLayout.EAST);

        JPanel center = new JPanel(new GridLayout(2, 1, 0, 5));
        center.setOpaque(false);
        JLabel lblTit = new JLabel(title);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTit.setForeground(Color.WHITE);
        JLabel lblDesc = new JLabel("<html><body style='width: 250px; color:#8c919b; font-size:11px;'>"+desc+"</body></html>");
        center.add(lblTit);
        center.add(lblDesc);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        JLabel lblStatus = new JLabel("<html><font color='#0070F0'>●</font> <font color='#8c919b' size='2'>PRONTO PARA INICIAR</font></html>");
        
        JButton btn = new JButton(btnText) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(MainFrame.ACCENT_BLUE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(140, 42));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);
        
        bottom.add(lblStatus, BorderLayout.WEST);
        bottom.add(btn, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }

    private JPanel criarLogCard() {
        JPanel card = new MainFrame.RoundedPanel(15, MainFrame.CARD_BG);
        card.setLayout(new BorderLayout());
        card.setMaximumSize(new Dimension(1100, 220));
        card.setBorder(new EmptyBorder(15, 25, 15, 25));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel lblTitle = new JLabel("⌨ REGISTRO DE ATIVIDADES");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JButton btnClear = new JButton("LIMPAR LOG");
        btnClear.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnClear.setForeground(MainFrame.TEXT_GRAY);
        btnClear.setContentAreaFilled(false);
        btnClear.setBorder(null);
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClear.addActionListener(e -> termArea.setText(""));
        
        header.add(lblTitle, BorderLayout.WEST);
        header.add(btnClear, BorderLayout.EAST);

        termArea = new JTextArea();
        termArea.setBackground(new Color(10, 12, 17));
        termArea.setForeground(new Color(0, 255, 128));
        termArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        termArea.setEditable(false);
        
        JScrollPane scroll = new JScrollPane(termArea);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(10, 12, 17));

        card.add(header, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        log("SISTEMA OPERANTE - Aguardando comandos.");
        return card;
    }

    private void log(String msg) {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        SwingUtilities.invokeLater(() -> {
            termArea.append("[" + time + "] " + msg + "\n");
        });
    }

    private void updateStatusDisplay(String text, int percent) {
        SwingUtilities.invokeLater(() -> {
            statusTitleLabel.setText(text);
            statusPercentLabel.setText(percent + "%");
            mainProgressBar.setValue(percent);
            
            if (percent == 100) statusTitleLabel.setForeground(new Color(0, 255, 128));
            else if (percent == 0) statusTitleLabel.setForeground(new Color(255, 80, 80));
            else statusTitleLabel.setForeground(MainFrame.ACCENT_BLUE);
        });
    }

    private void executarAtualizacaoVBA() {
        log("Iniciando rotina de automação VBA/VBS...");
        updateStatusDisplay("EXECUTANDO SCRIPT...", 10);
        
        new Thread(() -> {
            try {
                File script = new File(Config.SCRIPT_VIVO_VBS);
                if (!script.exists()) {
                    log("ERRO: Script não encontrado em " + Config.SCRIPT_VIVO_VBS);
                    updateStatusDisplay("ERRO: SCRIPT NÃO LOCALIZADO", 0);
                    return;
                }

                updateStatusDisplay("PROCESSANDO PLANILHA...", 40);
                Process p = Runtime.getRuntime().exec("wscript \"" + Config.SCRIPT_VIVO_VBS + "\"");
                int exitCode = p.waitFor();

                if (exitCode == 0) {
                    log("SUCESSO: Script VBS finalizado.");
                    updateStatusDisplay("PLANILHA ATUALIZADA", 100);
                } else {
                    log("FALHA: Script retornou erro " + exitCode);
                    updateStatusDisplay("ERRO NA EXECUÇÃO", 0);
                }
            } catch (Exception ex) {
                log("EXCEPTION: " + ex.getMessage());
                updateStatusDisplay("FALHA DE SISTEMA", 0);
            }
        }).start();
    }

    private void executarImportacaoDados() {
        log("Iniciando motor de ingestão de dados...");
        updateStatusDisplay("LOCALIZANDO ARQUIVOS...", 15);
        
        new Thread(() -> {
            try {
                File pasta = new File(Config.PATH_VIVO_DATA);
                File arquivoAlvo = new File(pasta, "EQUIPAMENTO_SERIALIZADOS_VOLANTE_SP.xlsx");

                if (!arquivoAlvo.exists()) {
                    log("Arquivo padrão não detectado. Buscando arquivos alternativos...");
                    File[] arquivos = pasta.listFiles((d, name) -> name.toLowerCase().endsWith(".xlsx") || name.toLowerCase().endsWith(".csv"));
                    
                    if (arquivos == null || arquivos.length == 0) {
                        log("ERRO: Nenhum arquivo de dados na pasta /data");
                        updateStatusDisplay("NENHUM ARQUIVO LOCALIZADO", 0);
                        return;
                    }
                    Arrays.sort(arquivos, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
                    arquivoAlvo = arquivos[0];
                }

                log("Processando arquivo: " + arquivoAlvo.getName());
                updateStatusDisplay("IMPORTANDO PARA O BANCO...", 60);
                
                ImportadorArquivo.executarCarga(arquivoAlvo.getAbsolutePath());
                
                log("SUCESSO: " + arquivoAlvo.getName() + " importado.");
                updateStatusDisplay("IMPORTAÇÃO CONCLUÍDA", 100);
                
            } catch (Exception ex) {
                log("ERRO NA CARGA: " + ex.getMessage());
                updateStatusDisplay("FALHA NA IMPORTAÇÃO", 0);
            }
        }).start();
    }
}
