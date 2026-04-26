package com.motorjava.gui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarkLaf;
import com.motorjava.service.maquinas.ServicoMaquinas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.io.InputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PainelModerno extends JFrame {

    private final ServicoMaquinas maquinasService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    
    private CardLayout mainLayout;
    private JPanel mainContainer;
    private CardLayout moduleLayout;
    private JPanel moduleContainer;
    private JLabel moduleNameLabel;
    private JLabel titleLabel;
    private JButton btnHeaderAction;

    private JProgressBar barMaqConfig, barMaqExec, barMaqSeriais;
    private JDialog dialogoLogsMaq;
    private JTextArea areaLogsMaq;
    
    // Novas Cores: Dark Mode (Preto Total)
    private final Color bgColor = Color.BLACK;
    private final Color sidebarColor = new Color(18, 18, 18);
    private final Color cardColor = new Color(18, 18, 18);
    private final Color primaryColor = Color.WHITE;
    private final Color textColor = new Color(230, 237, 243);
    private final Color textSecondary = new Color(139, 148, 158);
    private final Color successColor = new Color(46, 160, 67);

    public PainelModerno(ServicoMaquinas maquinasService) {
        this.maquinasService = maquinasService;
        setupUI();
    }

    private Font loadFont(String name, float size, int style) {
        try {
            InputStream is = getClass().getResourceAsStream("/fonts/" + name);
            if (is != null) {
                Font f = Font.createFont(Font.TRUETYPE_FONT, is);
                return f.deriveFont(style, size);
            }
        } catch (Exception e) {
            // Silently fail or use default
        }
        return new Font("sans-serif", style, (int)size);
    }

    private void setupUI() {
        FlatDarkLaf.setup(); // Mudar para Tema Escuro
        
        // Inicializar Fontes
        Font quicksand = loadFont("Quicksand.ttf", 14f, Font.PLAIN);
        UIManager.put("defaultFont", quicksand);
        
        UIManager.put("Button.arc", 12);
        UIManager.put("Component.arc", 12);
        
        setTitle("AXIS - CENTRO DA AUTOMAÇÃO");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(Frame.MAXIMIZED_BOTH);
        
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
        
        Font logoFont = loadFont("Barrio-Regular.ttf", 160f, Font.PLAIN);
        Font quicksandBold = loadFont("Quicksand.ttf", 14f, Font.BOLD);
        Font quicksandButton = loadFont("Quicksand.ttf", 16f, Font.BOLD);

        JLabel logo = new JLabel("AXIS");
        logo.setForeground(primaryColor);
        logo.setFont(logoFont);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel sub = new JLabel("O centro da automação");
        sub.setForeground(textSecondary);
        sub.setFont(quicksandBold);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        sub.setBorder(new EmptyBorder(0, 0, 60, 0));
        
        JButton btnAcessar = new JButton("ACESSAR SISTEMA");
        btnAcessar.setPreferredSize(new Dimension(300, 60));
        btnAcessar.setMaximumSize(new Dimension(300, 60));
        btnAcessar.setBackground(primaryColor);
        btnAcessar.setForeground(bgColor);
        btnAcessar.setFont(quicksandButton);
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
        miniLogo.setFont(loadFont("Barrio-Regular.ttf", 18f, Font.PLAIN));

        // Botões da Barra Lateral
        JButton btnMaq = criarBotaoLateral("/icons/engine.png", "⚙");
        JButton btnIhs = criarBotaoLateral("/icons/upload.png", "▲");

        sidebar.add(miniLogo);
        sidebar.add(btnMaq);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(btnIhs);
        sidebar.add(Box.createVerticalStrut(15));
        

        // Ações de Navegação
        btnMaq.addActionListener(e -> showModule("MAQUINARIO"));
        btnIhs.addActionListener(e -> showModule("CONSUMO_IHS"));
        
        JPanel contentArea = new JPanel(new BorderLayout(0, 30));
        contentArea.setBackground(bgColor);
        contentArea.setBorder(new EmptyBorder(40, 30, 40, 30));
        
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        moduleNameLabel = new JLabel("RELATÓRIO");
        moduleNameLabel.setForeground(textSecondary);
        moduleNameLabel.setFont(loadFont("Quicksand.ttf", 12f, Font.BOLD));
        JPanel nameActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        nameActionPanel.setOpaque(false);

        titleLabel = new JLabel("GIRO DE MAQUINÁRIOS");
        titleLabel.setForeground(textColor);
        titleLabel.setFont(loadFont("Quicksand.ttf", 36f, Font.BOLD));
        
        btnHeaderAction = new JButton("ABRIR RELATÓRIO");
        btnHeaderAction.setFont(loadFont("Quicksand.ttf", 10f, Font.BOLD));
        btnHeaderAction.setBackground(cardColor);
        btnHeaderAction.setForeground(textColor);
        btnHeaderAction.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHeaderAction.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");
        btnHeaderAction.addActionListener(e -> {
            try {
                String pbiPath = "";
                if (titleLabel.getText().contains("GIRO")) {
                    pbiPath = com.motorjava.config.GerenciadorConfiguracao.get("path.pbi.giro");
                } else {
                    pbiPath = "C:\\Users\\user\\Desktop\\ARQUVOS\\RELATORIOS\\EXCEL\\ConsumoIHS\\html_output\\CONSOLIDACAO_PADRAO_ANIEL.html";
                }
                Desktop.getDesktop().open(new File(pbiPath));
            } catch (Exception ex) {
                addLog("✖ Falha ao abrir relatório: " + ex.getMessage(), "error");
            }
        });

        nameActionPanel.add(titleLabel);
        nameActionPanel.add(btnHeaderAction);

        titlePanel.add(moduleNameLabel);
        titlePanel.add(nameActionPanel);
        header.add(titlePanel, BorderLayout.WEST);
        
        JPanel statusBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        statusBox.setOpaque(false);
        JLabel dot = new JLabel("●");
        dot.setForeground(successColor);
        dot.setFont(new Font("Inter", Font.BOLD, 16));
        JLabel statusText = new JLabel("ONLINE");
        statusText.setForeground(textSecondary);
        statusText.setFont(loadFont("Quicksand.ttf", 12f, Font.BOLD));
        statusBox.add(dot);
        statusBox.add(statusText);
        header.add(statusBox, BorderLayout.EAST);
        
        contentArea.add(header, BorderLayout.NORTH);
        
        moduleLayout = new CardLayout();
        moduleContainer = new JPanel(moduleLayout);
        moduleContainer.setOpaque(false);
        
        moduleContainer.add(createMaquinarioPanel(), "MAQUINARIO");
        moduleContainer.add(new PainelConsumoIHS(), "CONSUMO_IHS");
        
        contentArea.add(moduleContainer, BorderLayout.CENTER);
        
        // logBoxContainer removido pois agora usamos log em pop-up
        
        system.add(sidebar, BorderLayout.WEST);
        system.add(contentArea, BorderLayout.CENTER);
        
        return system;
    }

    private JPanel createMaquinarioPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // --- CARDS ---
        barMaqSeriais = criarBarraCard();
        JButton btnSer = criarBotaoCard("COPIAR SERIAIS");
        btnSer.addActionListener(this::handleCopiarSeriais);
        JPanel cardSeriais = createMaqCard("Copiar Seriais", "Copia a lista completa de seriais para a área de transferência.", btnSer, barMaqSeriais);

        barMaqConfig = criarBarraCard();
        JButton btnCfg = criarBotaoCard("SINCRONIZAR CONFIGS");
        btnCfg.addActionListener(this::handleSincronizar);
        JPanel cardConfig = createMaqCard("Configurar Maquinários", "Sincroniza as definições de configurações com a planilha.", btnCfg, barMaqConfig);

        barMaqExec = criarBarraCard();
        JButton btnExec = criarBotaoCard("EXECUTAR PROCESSAMENTO");
        btnExec.addActionListener(this::handleExecutar);
        JPanel cardExec = createMaqCard("Atualizar Tabelas", "Sincroniza os arquivos de Downloads com a Base Original.", btnExec, barMaqExec);

        JPanel grid = new JPanel(new GridLayout(1, 3, 30, 0));
        grid.setOpaque(false);
        grid.add(cardSeriais);
        grid.add(cardConfig);
        grid.add(cardExec);
        
        // Container para o Grid que permite expansão horizontal mas não vertical
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setOpaque(false);
        mainArea.add(grid, BorderLayout.NORTH);
        
        panel.add(mainArea, BorderLayout.CENTER);

        // BOTAO DE LOG MAQUINARIO (Rodapé)
        configurarLogsMaq();
        JButton btnLogMaq = new JButton("LOG");
        btnLogMaq.setFont(loadFont("Quicksand.ttf", 10f, Font.BOLD));
        btnLogMaq.setPreferredSize(new Dimension(70, 30));
        btnLogMaq.setBackground(cardColor);
        btnLogMaq.setForeground(textSecondary);
        btnLogMaq.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogMaq.putClientProperty(FlatClientProperties.STYLE, "arc: 10; outline: #333333; outlineWidth: 1;");
        btnLogMaq.addActionListener(e -> {
            dialogoLogsMaq.setLocationRelativeTo(this);
            dialogoLogsMaq.setVisible(true);
        });

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(20, 0, 0, 0));

        JPanel logWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        logWrapper.setOpaque(false);
        logWrapper.add(btnLogMaq);
        footer.add(logWrapper, BorderLayout.SOUTH);
        
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMaqCard(String title, String desc, JButton btn, JProgressBar bar) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(0, 260)); // Altura fixa, largura dinâmica
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(cardColor);
        card.setBorder(new EmptyBorder(25, 25, 25, 25));
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 20; outline: #333333; outlineWidth: 1;");

        JLabel lt = new JLabel(title);
        lt.setForeground(textColor);
        lt.setFont(loadFont("Quicksand.ttf", 18f, Font.BOLD));
        lt.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel ld = new JLabel("<html><div style='text-align: center;'>" + desc + "</div></html>");
        ld.setForeground(textSecondary);
        ld.setFont(loadFont("Quicksand.ttf", 13f, Font.PLAIN));
        ld.setAlignmentX(Component.CENTER_ALIGNMENT);
        ld.setBorder(new EmptyBorder(8, 0, 12, 0));

        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        bar.setAlignmentX(Component.CENTER_ALIGNMENT);
        bar.setMaximumSize(new Dimension(240, 6));
        bar.setVisible(false);

        card.add(lt);
        card.add(ld);
        card.add(Box.createVerticalGlue());
        card.add(btn);
        card.add(Box.createVerticalStrut(12));
        card.add(bar);

        return card;
    }

    private JButton criarBotaoCard(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(primaryColor);
        btn.setForeground(bgColor);
        btn.setPreferredSize(new Dimension(240, 45));
        btn.setMaximumSize(new Dimension(240, 45));
        btn.setFont(loadFont("Quicksand.ttf", 13f, Font.BOLD));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");
        return btn;
    }

    private void configurarLogsMaq() {
        dialogoLogsMaq = new JDialog(this, "Logs de Processamento - Maquinário", false);
        dialogoLogsMaq.setSize(600, 400);
        dialogoLogsMaq.getContentPane().setBackground(sidebarColor);

        areaLogsMaq = new JTextArea();
        areaLogsMaq.setEditable(false);
        areaLogsMaq.setFont(new Font("Consolas", Font.PLAIN, 12));
        areaLogsMaq.setBackground(sidebarColor);
        areaLogsMaq.setForeground(textColor);
        areaLogsMaq.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JScrollPane scroll = new JScrollPane(areaLogsMaq);
        scroll.setBorder(null);
        dialogoLogsMaq.add(scroll);
    }

    private JProgressBar criarBarraCard() {
        JProgressBar p = new JProgressBar(0, 100);
        p.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");
        return p;
    }

    private void showModule(String module) {
        moduleLayout.show(moduleContainer, module);
        if (module.equals("MAQUINARIO")) {
            moduleNameLabel.setText("RELATÓRIO");
            titleLabel.setText("GIRO DE MAQUINÁRIOS");
            btnHeaderAction.setText("ABRIR RELATÓRIO");
            btnHeaderAction.setVisible(true);
        } else if (module.equals("CONSUMO_IHS")) {
            moduleNameLabel.setText("AUTOMAÇÃO");
            titleLabel.setText("CONSUMO IHS");
            btnHeaderAction.setText("VER RESULTADO HTML");
            btnHeaderAction.setVisible(false); // PainelConsumoIHS tem seus próprios botões
        }
        revalidate();
        repaint();
    }

    private void btnExecUt(JButton btn) {
        btn.setPreferredSize(new Dimension(320, 50));
        btn.setMaximumSize(new Dimension(320, 50));
        btn.setFont(loadFont("Quicksand.ttf", 14f, Font.BOLD));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");
    }

    private JButton criarBotaoLateral(String iconPath, String fallback) {
        JButton b = new JButton();
        b.setPreferredSize(new Dimension(50, 50));
        b.setMinimumSize(new Dimension(50, 50));
        b.setMaximumSize(new Dimension(50, 50));
        b.setBackground(sidebarColor);
        b.setBorder(null);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");
        
        try {
            java.net.URL url = getClass().getResource(iconPath);
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                Image img = icon.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
                b.setIcon(new ImageIcon(img));
            } else {
                throw new Exception("Icon not found");
            }
        } catch (Exception e) {
            b.setText(fallback);
            b.setForeground(primaryColor);
            b.setFont(new Font("Inter", Font.BOLD, 22));
        }
        return b;
    }

    private void addLogMaq(String msg, String type) {
        String time = dateFormat.format(new Date());
        areaLogsMaq.append("[" + time + "] " + msg + "\n");
        areaLogsMaq.setCaretPosition(areaLogsMaq.getDocument().getLength());
        addLog(msg, type); // Mantém log no sistema também por segurança
    }

    private void handleCopiarSeriais(ActionEvent e) {
        barMaqSeriais.setValue(0);
        barMaqSeriais.setVisible(true);
        addLogMaq("▶ Gerando lista de seriais...", "info");

        new SwingWorker<Void, Integer>() {
            @Override protected Void doInBackground() throws Exception {
                publish(30);
                String seriais = getSeriaisList();
                StringSelection selection = new StringSelection(seriais);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
                publish(100);
                return null;
            }
            @Override protected void process(java.util.List<Integer> chunks) {
                barMaqSeriais.setValue(chunks.get(chunks.size() - 1));
            }
            @Override protected void done() {
                addLogMaq("✔ Seriais copiados para o clipboard!", "success");
                JOptionPane.showMessageDialog(PainelModerno.this, "Seriais copiados com sucesso!");
            }
        }.execute();
    }

    private void handleSincronizar(ActionEvent e) {
        barMaqConfig.setValue(0);
        barMaqConfig.setVisible(true);
        addLogMaq("▶ Iniciando sincronização de configurações...", "info");

        new SwingWorker<Void, Integer>() {
            @Override protected Void doInBackground() throws Exception {
                publish(20);
                maquinasService.sincronizarConfiguracoes();
                publish(100);
                return null;
            }
            @Override protected void process(java.util.List<Integer> chunks) {
                barMaqConfig.setValue(chunks.get(chunks.size() - 1));
            }
            @Override protected void done() {
                try {
                    get();
                    barMaqConfig.setValue(100);
                    addLogMaq("✔ Sincronização concluída!", "success");
                    JOptionPane.showMessageDialog(PainelModerno.this, "Configurações sincronizadas!");
                } catch (Exception ex) {
                    addLogMaq("✖ Falha na sincronização: " + ex.getMessage(), "error");
                    barMaqConfig.setValue(0);
                    JOptionPane.showMessageDialog(PainelModerno.this, "Erro: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void handleExecutar(ActionEvent e) {
        barMaqExec.setValue(0);
        barMaqExec.setVisible(true);
        addLogMaq("▶ Iniciando rotina de processamento local...", "info");
        
        new SwingWorker<Void, Integer>() {
            @Override protected Void doInBackground() throws Exception {
                publish(10);
                maquinasService.atualizarBaseMaquinario();
                publish(100);
                return null;
            }
            @Override protected void process(java.util.List<Integer> chunks) {
                barMaqExec.setValue(chunks.get(chunks.size() - 1));
            }
            @Override protected void done() {
                try {
                    get();
                    barMaqExec.setValue(100);
                    addLogMaq("✔ Base e backup atualizados com sucesso!", "success");
                    JOptionPane.showMessageDialog(PainelModerno.this, "Processamento concluído!");
                } catch (Exception ex) {
                    addLogMaq("✖ Falha no processamento: " + ex.getMessage(), "error");
                    barMaqExec.setValue(0);
                    JOptionPane.showMessageDialog(PainelModerno.this, "Erro: " + ex.getMessage());
                }
            }
        }.execute();
    }


    public void addLog(String msg, String type) {
        // Log para o console padrão removido da GUI, mas mantido no System.out
        System.out.println(String.format("[%s] [%s] %s", dateFormat.format(new Date()), type.toUpperCase(), msg));
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
   private String getSeriaisList() {
    return "180420\n502583\n181007\n8FPM49S52T3Z9L1C\n724937861049\n182113\n96452\n222769\n10231017344\n121704\n96495\n6GSG2YHCV2GNW08M\n03923076193\n724937153016\n724936676030\n724937881042\n10231019344\n190110\n502565\n192021\n222764\n222943\n10231098344\n724937822014\n1258037\n222700\n222987\n180126445\n180126471\n724934383007\n10230033344\n502958\n502553\n724934304017\n99835\n502619\n81920\n10229027344\n97370\n724937155023\n222842\n190215\n192007\n79658\n502583\nVCLGW31F4YEH7GRB\n222937\n81908\n57499\n192011\n181007\n724937153004\n724937881055\n57472\n724937622032\nBX36Z98XEMBXDSTN\n8FPM49S52T3Z9L1C\n502895\n81053\n724937861049\n09315131344\n180612\nPSESJ8WRE8WAX606\n182113\n97179\n99666\n96452\n81923\n181216\n97248\n81919\n222938\n96512\n222769\n502623\n191116\n52338\n222635\n724934383035\n724937154054\n53867\n10231017344\n170615\nXLPPP6PF21E5LDKH\n12313174344\n724937154053\n97182\n180126444\n121704\n10229015344\n61H2S597JBF9YP4G\n724937535003\n724937273005\n96441\n19496\n97177\n190903\n16673\n96495\n53820\n19529\n19865\n222980\n724934303019\n57445\n502907\n180126498\n180126491\n6GSG2YHCV2GNW08M\n20237\n222983\n191720\n181215\n724934201035\n80992\n121718\n222945\n222899\n502990\n10231062344\n724934304031\n192121\n03923076193\n121874\n724937153036\n190906\n724937153016\n06227042343\n53833\n67226\n81905\n180126442\n502652\n180419\n724936676030\n724937881042\n57188\n10231019344\n190110\n1NP64RLR48MAD3VC\n724932834017\n724937881029\n97430\n724934458026\nYBH7CE8S5RKRDDR1\n724936558043\n10231088344\n724937881052\n724937154048\n502565\n724934306009\n222896\n190220\n96462\n190311\n724937881039\nR0622\n502997\n121712\n222907\n25691\n502622\n06227021343\n57488\n192021\n57512\n09315087344\n82084\n52336\n502881\n97198\n180126469\n53832\n99693\n191013\n96453\n96532\n502908\n191713\n222764\n79157\n81025\n724934383013\n121706\n181303\n222697\n222784\n180126505\n724934302036\n16574\n503000\n180908\n80954\n222545\n97254\n222943\n190714\n222906\n67137\n121459\n190503\n10231098344\n724937822014\n192201\n150302\n222864\n9EYD0BWHS6FT1720\n1258037\n724937622033\n97264\n99844\n222735\n82080\n222766\n10230041344\n724936676049\n724937881043\n724936676020\n222972\n222700\n724937535009\n724937153008\n180126493\n57504\n81070\n222539\n10230017344\n222777\n724937881051\n190212\n10231093344\n80739\n222987\n79153\n502616\n121469\n79555\n82037\n180126445\n222677\n82077\n180126503\n180126443\n78930\n57179\n82055\n724934304008\n97431\n57144\n16533\n724936676033\n222909\n121708\n97363\n180126501\n222942\n09315188344\n180126471\n502617\n10230020344\n192025\n19227\n97428\n16504\n222859\n724934383007\n724936075012\n97423\n502909\n81045\n724934302019\n724934458013\n724937535001\n97411\nV7PFC91M2WZVA10X\n12313170344\n10230033344\n97256\n97257\n180701\n19764\n222758\n222863\n79548\n52334\n13935\n26357\nTTDM0171510308\n191014\n81915\n191923\n724937881038\n724937881041\n502524\n81887\n191103\n79648\n180614\n160706\n97258\n82056\n180707\n724937881050\n80794\n180504\n191109\n61904\n502986\n190911\n222783\n180126490\n222879\n79156\n724934303008\n502618\n9YKSBBZ7VCTFHJJ5\n43G1KBXP5EC89K4X\n724937881053\n182210\n724934488017\n82064\n82054\n724934458016\n57212\n26340\n191018\n222973\n97422\n724937153018\n724937273016\n755407\n180126465\n180126504\n10230039344\n03923070193\n09211041343\n17170\n82081\n724936676039\n724934304028\n82074\n222893\n192008\n222955\n724937881032\n724934303009\n82071\n222866\n222841\n190409\n222761\n222870\n99709\n180519\n222725\n724937153029\n99579\n61889\n222986\n15766\nS0352\n724937590035\n81101\n180820\n97425\n97200\nFJD9G48E51RGD71M\n724934303003\n724934458001\nAT5E5EZ7SKYW16P2\n191804\n5220272\n80985\n10231027344\nDB0H78J22KP5B5DN\n180126467\n222875\n13845\n222981\nTTDM0171510364\n16678\n19861\n190807\n724934306002\n61872\n67245\n63902\n53823\n222948\n220965\n97196\n502625\n180126502\n79526\n121879\n191204\n724937590012\n99679\n26059\n182102\n724937718022\n81100\n67140\n724937881048\n180126500\n724937881058\n81048\n222698\n192815\n190715\n97388\n82078\n222853\nS0499\n724937881053\n222778\n190501\n502994\n10230035344\n180126492\n82076\n222681\n10230032344\n181013\n52339\n10230024344\n724934306006\n724937718033\n180126499\n724934305005\n190915\n502559\n57192\n99671\n502569\n97260\n25674\n15203\n8C2X8FYACY3SZBE2\n502582\n222755\n724937535002\n57210\nN0932\n10230001344\n81910\n10231049344\n222890\n97195\n180126468\n182009\n502626\n170908\n96433\n9520\n502620\n180604\n724937535026\n10230003344\n724937535024\n97176\n222991\n222921\n502551\n81013\nEC5GLZD8SN4H9TEA\n121457\n96537\nXNPVVRMW2K81PHZF\n26358\n190805\n191805\n97184\n61846\n222915\n81914\n81009\n06227097343\nHX6C4A8WT7LALPLS\n57486\n14151\n180912\n222840\n190401\n724932834015\n192311\n16573\n724937155031\n182020\n76287\n724936558047\n19928\n502669\n76257\n81916\n502985\n76551\n192102\nWY14VV6DJRS1H55Y\n10231001344\n57219\n724937590048\n724934458004\n10231060344\n19741\n96530\n10231053344\n57458\n190905\n82061\n192506\n81056\n724934302021\n17046\n181219\n97429\n724934304014\n502964\n724937273014\n180513\n724937622027\n191924\n09211036343\n51052\n67248\n19766\n724934458019\n192307\n76184\n15219\n80LARPHAT4M6Y208\nB2VZ31MY46853NNB\n96442\n724937154039\n191314\n724934306015\n4361287\n724937153019\n45684\n724937881045\n57462\n57427\n57429\n181902\n151304\n96438\n53812\n222856\n192116\n724937273020\n724937622006\n16680\n12319098344\n724934458016\n53810\n724934383061\n724937535023\n724933751047\n99663\n19697\n57224\n724934303029\n79603\n67260\n96488\n81072\n724934383045\n67242\n222869\n191922\n724937881056\n6722020229\nDWM980P02RE5BHJH\n724934458014\n1Z899G3GT8WGHL3S\n180503\nN55VYDA7JGN3398A\n724934306019\nFNGVDSMD50XYD31T\n61887\n82050\n97187\n222871\n97263\n222849\n97250\n19770\nFROTA25\nNNRBFYEB0FBMXTBF\nFSDCTNBHJ6TWX2GM\nXTDM0404150017\n82058\nSFATE0FR2S7H9R89\n16577\nTTPX0135010098\n2KYRJVDPVB4BNG0W\n79457\nL4E4X82243VLD9WW\nXTDM0404150032\nXTDM0404150036\nXTDM0404150041\nXTDM0404150015\nXTDM0404150035\nXTDM0404150011\nXTDM0404150033\nXTDM0404150009\nXTDM0404150019\nXTDM0404150027\n10179\n12319080344\n724934302018\n180614\n724937154033\n22015318\nFLC98V3PSHGMX085\nGRX5LJN95DGN96CM\nLN6K4R02E8VLBX19";
}
}
