package com.motorjava.ui;

import com.motorjava.ui.ihs.IHSPanel;
import com.motorjava.ui.vivo.VivoAgingPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public class MainFrame extends JFrame {

    // Cores Hexadecimais Fiéis à Imagem
    public static final Color BG_COLOR = new Color(5, 7, 10);
    public static final Color ACCENT_BLUE = new Color(0, 112, 240); // #0070F0
    public static final Color CARD_BG = new Color(11, 14, 20);      // #0B0E14
    public static final Color TEXT_WHITE = new Color(245, 245, 245);
    public static final Color TEXT_GRAY = new Color(140, 145, 155);

    private JPanel contentPanel;
    private VivoAgingPanel vivoPanel;
    private IHSPanel ihsPanel;

    public MainFrame() {
        setTitle("Power Automation Hub");
        setSize(1350, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout());

        initComponents();
    }

    private void initComponents() {
        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 100));
        header.setBorder(new EmptyBorder(10, 50, 10, 50));

        // Logo
        JPanel logoArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 30));
        logoArea.setOpaque(false);
        JLabel icon = new JLabel(" B ");
        icon.setOpaque(true);
        icon.setBackground(ACCENT_BLUE);
        icon.setForeground(Color.WHITE);
        icon.setFont(new Font("Segoe UI", Font.BOLD, 22));
        icon.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        
        JLabel titleLabel = new JLabel("POWER AUTOMATION HUB");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_WHITE);
        logoArea.add(icon);
        logoArea.add(titleLabel);

        // Menu
        JPanel menuArea = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 35));
        menuArea.setOpaque(false);
        JButton btnVivo = createMenuButton("Vivo Aging", true);
        JButton btnIhs = createMenuButton("One Page Report IHS", false);
        menuArea.add(btnVivo);
        menuArea.add(btnIhs);

        // Right Actions (Admin + Avatar)
        JPanel rightArea = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 30));
        rightArea.setOpaque(false);
        
        JButton btnAdmin = new JButton("MODO: ADMINISTRADOR");
        btnAdmin.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnAdmin.setForeground(ACCENT_BLUE);
        btnAdmin.setContentAreaFilled(false);
        btnAdmin.setFocusPainted(false);
        btnAdmin.setBorder(BorderFactory.createLineBorder(ACCENT_BLUE, 1));
        btnAdmin.setPreferredSize(new Dimension(180, 40));
        
        // Avatar Circular Customizado
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(60, 65, 75));
                g2.fill(new Ellipse2D.Double(0, 0, 42, 42));
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                String initials = "JD";
                int x = (42 - fm.stringWidth(initials)) / 2;
                int y = ((42 - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initials, x, y);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(42, 42));
        avatar.setOpaque(false);

        rightArea.add(btnAdmin);
        rightArea.add(avatar);

        header.add(logoArea, BorderLayout.WEST);
        header.add(menuArea, BorderLayout.CENTER);
        header.add(rightArea, BorderLayout.EAST);

        // --- CONTEÚDO ---
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setOpaque(false);
        
        vivoPanel = new VivoAgingPanel();
        ihsPanel = new IHSPanel();
        
        contentPanel.add(vivoPanel, "VIVO");
        contentPanel.add(ihsPanel, "IHS");

        // --- FOOTER ---
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setPreferredSize(new Dimension(0, 60));
        footer.setBorder(new EmptyBorder(0, 50, 10, 50));

        JLabel version = new JLabel("<html><span style='background:#101a30; color:#0070F0; padding:3px 8px; border-radius:4px;'>v3.0.0</span> <font color='#8c919b'> Desenvolvimento Senior</font></html>");
        footer.add(version, BorderLayout.WEST);

        JLabel status = new JLabel("<html><font color='#8c919b'>Sistema Operante</font> <font color='#00ff80'>●</font></html>");
        footer.add(status, BorderLayout.EAST);

        // Lógica de Troca
        btnVivo.addActionListener(e -> {
            ((CardLayout)contentPanel.getLayout()).show(contentPanel, "VIVO");
            btnVivo.setForeground(ACCENT_BLUE);
            btnIhs.setForeground(TEXT_GRAY);
        });
        btnIhs.addActionListener(e -> {
            ((CardLayout)contentPanel.getLayout()).show(contentPanel, "IHS");
            btnIhs.setForeground(ACCENT_BLUE);
            btnVivo.setForeground(TEXT_GRAY);
        });

        add(header, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    private JButton createMenuButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(active ? ACCENT_BLUE : TEXT_GRAY);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Classe Utilitária para Paineis Arredondados
    public static class RoundedPanel extends JPanel {
        private int radius;
        private Color color;
        public RoundedPanel(int radius, Color color) { 
            this.radius = radius; this.color = color;
            setOpaque(false); 
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
