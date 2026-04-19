package com.motorjava.gui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import com.motorjava.service.maquinas.MaquinasService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ModernDashboard extends JFrame {

    private JTextArea logArea;
    private final MaquinasService maquinasService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    
    private CardLayout mainLayout;
    private JPanel mainContainer;
    
    // Novas Cores: Light Mode (Branco com detalhes em Preto)
    private final Color bgColor = Color.WHITE;
    private final Color sidebarColor = new Color(243, 244, 246);
    private final Color cardColor = Color.WHITE;
    private final Color primaryColor = Color.BLACK;
    private final Color textColor = new Color(17, 24, 39);
    private final Color textSecondary = new Color(107, 114, 128);
    private final Color successColor = new Color(22, 163, 74);

    public ModernDashboard(MaquinasService maquinasService) {
        this.maquinasService = maquinasService;
        setupUI();
    }

    private void setupUI() {
        FlatLightLaf.setup(); // Mudar para Tema Claro
        UIManager.put("Button.arc", 12);
        UIManager.put("Component.arc", 12);
        
        setTitle("AXIS CONTROL - MOTOR JAVA v4.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        mainLayout = new CardLayout();
        mainContainer = new JPanel(mainLayout);
        mainContainer.setBackground(bgColor);
        
        mainContainer.add(createLandingPage(), "HOME");
        mainContainer.add(createMainSystem(), "DASHBOARD");
        
        setContentPane(mainContainer);
    }

    private JPanel createLandingPage() {
        JPanel landing = new JPanel(new GridBagLayout());
        landing.setBackground(bgColor);
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        
        Font logoFont = new Font("Impact", Font.PLAIN, 160);
        try {
            InputStream is = getClass().getResourceAsStream("/fonts/Barrio-Regular.ttf");
            if (is != null) {
                Font customFont = Font.createFont(Font.TRUETYPE_FONT, is);
                logoFont = customFont.deriveFont(160f);
            }
        } catch (Exception e) {}

        JLabel logo = new JLabel("AXIS");
        logo.setForeground(primaryColor);
        logo.setFont(logoFont);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel sub = new JLabel("O centro da automação");
        sub.setForeground(textSecondary);
        sub.setFont(new Font("Inter", Font.BOLD, 14));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        sub.setBorder(new EmptyBorder(0, 0, 60, 0));
        
        JButton btnAcessar = new JButton("ACESSAR SISTEMA");
        btnAcessar.setPreferredSize(new Dimension(300, 60));
        btnAcessar.setMaximumSize(new Dimension(300, 60));
        btnAcessar.setBackground(primaryColor);
        btnAcessar.setForeground(Color.WHITE);
        btnAcessar.setFont(new Font("Inter", Font.BOLD, 16));
        btnAcessar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAcessar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAcessar.addActionListener(e -> mainLayout.show(mainContainer, "DASHBOARD"));
        
        content.add(logo);
        content.add(sub);
        content.add(btnAcessar);
        
        landing.add(content);
        return landing;
    }

    private JPanel createMainSystem() {
        JPanel system = new JPanel(new BorderLayout());
        system.setBackground(bgColor);
        
        // --- SIDEBAR SLIM (ICON ONLY) ---
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(80, 0));
        sidebar.setBackground(sidebarColor);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS)); // Layout Vertical
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(0, 0, 0, 20)));
        
        // Logo AXIS (Pequeno no topo)
        JLabel miniLogo = new JLabel("AXIS");
        miniLogo.setForeground(primaryColor);
        miniLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        miniLogo.setBorder(new EmptyBorder(30, 0, 40, 0));
        
        // Carregar Barrio para o mini logo
        try {
            InputStream is = getClass().getResourceAsStream("/fonts/Barrio-Regular.ttf");
            if (is != null) {
                Font customFont = Font.createFont(Font.TRUETYPE_FONT, is);
                miniLogo.setFont(customFont.deriveFont(18f));
            } else {
                miniLogo.setFont(new Font("Impact", Font.PLAIN, 18));
            }
        } catch (Exception e) {
            miniLogo.setFont(new Font("Impact", Font.PLAIN, 18));
        }

        // Ícone Maquinário (Traçado/Outline)
        JButton btnMaq = new JButton("❑"); // Ícone de traçado geométrico
        btnMaq.setPreferredSize(new Dimension(50, 50));
        btnMaq.setMaximumSize(new Dimension(50, 50));
        btnMaq.setBackground(primaryColor);
        btnMaq.setForeground(Color.WHITE);
        btnMaq.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 22));
        btnMaq.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMaq.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnMaq.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");
        
        sidebar.add(miniLogo);
        sidebar.add(btnMaq);
        
        JPanel contentArea = new JPanel(new BorderLayout(0, 30));
        contentArea.setBackground(bgColor);
        contentArea.setBorder(new EmptyBorder(40, 50, 40, 50));
        
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        JLabel moduleName = new JLabel("RELATÓRIO");
        moduleName.setForeground(textSecondary);
        moduleName.setFont(new Font("Inter", Font.BOLD, 12));
        JLabel title = new JLabel("Base Maquinário");
        title.setForeground(textColor);
        title.setFont(new Font("Inter", Font.BOLD, 36));
        titlePanel.add(moduleName);
        titlePanel.add(title);
        header.add(titlePanel, BorderLayout.WEST);
        
        JPanel statusBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        statusBox.setOpaque(false);
        JLabel dot = new JLabel("●");
        dot.setForeground(successColor);
        dot.setFont(new Font("Inter", Font.BOLD, 16));
        JLabel statusText = new JLabel("ONLINE");
        statusText.setForeground(textSecondary);
        statusText.setFont(new Font("Inter", Font.BOLD, 12));
        statusBox.add(dot);
        statusBox.add(statusText);
        header.add(statusBox, BorderLayout.EAST);
        
        contentArea.add(header, BorderLayout.NORTH);
        
        RoundedPanel card = new RoundedPanel(24, cardColor);
        card.setPreferredSize(new Dimension(420, 280));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(35, 35, 35, 35));
        
        // Adicionando borda sutil ao card para fundo branco
        card.putClientProperty(FlatClientProperties.STYLE, "outline: #E5E7EB; outlineWidth: 1;");

        JPanel cardIn = new JPanel();
        cardIn.setLayout(new BoxLayout(cardIn, BoxLayout.Y_AXIS));
        cardIn.setOpaque(false);
        
        JLabel cardTitle = new JLabel("Atualizar Tabelas");
        cardTitle.setForeground(textColor);
        cardTitle.setFont(new Font("Inter", Font.BOLD, 22));
        JLabel cardDesc = new JLabel("<html>Sincroniza os arquivos de Downloads com a Base Original.<br>Inclui limpeza automática de conflitos no Excel.</html>");
        cardDesc.setForeground(textSecondary);
        cardDesc.setFont(new Font("Inter", Font.PLAIN, 14));
        
        JButton btnExec = new JButton("EXECUTAR PROCESSAMENTO →");
        btnExec.setBackground(primaryColor);
        btnExec.setForeground(Color.WHITE);
        btnExecUt(btnExec);
        btnExec.addActionListener(this::handleExecutar);
        
        cardIn.add(cardTitle);
        cardIn.add(Box.createVerticalStrut(10));
        cardIn.add(cardDesc);
        cardIn.add(Box.createVerticalGlue());
        cardIn.add(btnExec);
        card.add(cardIn);
        
        JPanel centerGrid = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        centerGrid.setOpaque(false);
        centerGrid.add(card);
        contentArea.add(centerGrid, BorderLayout.CENTER);
        
        JPanel logBox = new JPanel(new BorderLayout());
        logBox.setOpaque(false);
        logBox.setPreferredSize(new Dimension(0, 200));
        
        RoundedPanel logBg = new RoundedPanel(16, new Color(249, 250, 251));
        logBg.setLayout(new BorderLayout());
        logBg.setBorder(new EmptyBorder(15, 20, 15, 20));
        logArea = new JTextArea();
        logArea.setBackground(new Color(249, 250, 251));
        logArea.setForeground(textColor);
        logArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
        logArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(null);
        logBg.add(scroll);
        logBox.add(logBg, BorderLayout.CENTER);
        contentArea.add(logBox, BorderLayout.SOUTH);
        
        system.add(sidebar, BorderLayout.WEST);
        system.add(contentArea, BorderLayout.CENTER);
        
        return system;
    }

    private void btnExecUt(JButton btn) {
        btn.setPreferredSize(new Dimension(320, 50));
        btn.setMaximumSize(new Dimension(320, 50));
        btn.setFont(new Font("Inter", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");
    }

    private void handleExecutar(ActionEvent e) {
        new Thread(() -> {
            try {
                addLog("Iniciando rotina de processamento local...", "info");
                maquinasService.atualizarBaseMaquinario();
                addLog("✓ Sucesso: Base e backup atualizados.", "success");
            } catch (Exception ex) {
                addLog("✖ Falha: " + ex.getMessage(), "error");
            }
        }).start();
    }

    public void addLog(String msg, String type) {
        String time = dateFormat.format(new Date());
        SwingUtilities.invokeLater(() -> {
            logArea.append(String.format("[%s] %s\n", time, msg));
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color backgroundColor;

        public RoundedPanel(int radius, Color backgroundColor) {
            this.radius = radius;
            this.backgroundColor = backgroundColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
            
            // Borda sutil para modo claro
            g2.setColor(new Color(0, 0, 0, 15));
            g2.setStroke(new BasicStroke(1));
            g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
            
            g2.dispose();
        }
    }
}
