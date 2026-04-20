package com.motorjava.gui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarkLaf;
import com.motorjava.service.maquinas.MaquinasService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ModernDashboard extends JFrame {

    private JTextArea logArea;
    private final MaquinasService maquinasService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    
    private CardLayout mainLayout;
    private JPanel mainContainer;
    
    // Novas Cores: Dark Mode (Preto Total)
    private final Color bgColor = Color.BLACK;
    private final Color sidebarColor = new Color(18, 18, 18);
    private final Color cardColor = new Color(18, 18, 18);
    private final Color primaryColor = Color.WHITE;
    private final Color textColor = new Color(230, 237, 243);
    private final Color textSecondary = new Color(139, 148, 158);
    private final Color successColor = new Color(46, 160, 67);

    public ModernDashboard(MaquinasService maquinasService) {
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

        // Ícone Maquinário (Motor Gerado)
        JButton btnMaq = new JButton();
        btnMaq.setPreferredSize(new Dimension(50, 50));
        btnMaq.setBackground(sidebarColor);
        btnMaq.setBorder(null);
        
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icons/engine.png"));
            Image img = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            btnMaq.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            btnMaq.setText("⚙");
            btnMaq.setForeground(primaryColor);
            btnMaq.setFont(new Font("Inter", Font.BOLD, 22));
        }
        
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
        moduleName.setFont(loadFont("Quicksand.ttf", 12f, Font.BOLD));
        JLabel title = new JLabel("GIRO DE MAQUINÁRIOS");
        title.setForeground(textColor);
        title.setFont(loadFont("Quicksand.ttf", 36f, Font.BOLD));
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
        statusText.setFont(loadFont("Quicksand.ttf", 12f, Font.BOLD));
        statusBox.add(dot);
        statusBox.add(statusText);
        header.add(statusBox, BorderLayout.EAST);
        
        contentArea.add(header, BorderLayout.NORTH);
        
        // --- CARD 1: CONFIGURAÇÃO ---
        RoundedPanel cardConfig = new RoundedPanel(24, cardColor);
        cardConfig.setPreferredSize(new Dimension(420, 280));
        cardConfig.setLayout(new BorderLayout());
        cardConfig.setBorder(new EmptyBorder(35, 35, 35, 35));
        cardConfig.putClientProperty(FlatClientProperties.STYLE, "outline: #333333; outlineWidth: 1;");

        JPanel cardInConfig = new JPanel();
        cardInConfig.setLayout(new BoxLayout(cardInConfig, BoxLayout.Y_AXIS));
        cardInConfig.setOpaque(false);
        
        JLabel cardTitleCfg = new JLabel("Configurar Maquinários");
        cardTitleCfg.setForeground(textColor);
        cardTitleCfg.setFont(loadFont("Quicksand.ttf", 22f, Font.BOLD));
        JLabel cardDescCfg = new JLabel("<html>Sincroniza as definições de configurações com a planilha.<br>Copia dados de configMaquinarios para a base.</html>");
        cardDescCfg.setForeground(textSecondary);
        cardDescCfg.setFont(loadFont("Quicksand.ttf", 14f, Font.PLAIN));
        
        JButton btnCfg = new JButton("SINCRONIZAR CONFIGS");
        btnCfg.setBackground(primaryColor);
        btnCfg.setForeground(bgColor);
        btnExecUt(btnCfg);
        btnCfg.addActionListener(this::handleSincronizar);
        
        cardInConfig.add(cardTitleCfg);
        cardInConfig.add(Box.createVerticalStrut(10));
        cardInConfig.add(cardDescCfg);
        cardInConfig.add(Box.createVerticalGlue());
        cardInConfig.add(btnCfg);
        cardConfig.add(cardInConfig);

        // --- CARD 2: ATUALIZAR TABELAS (EXISTENTE) ---
        RoundedPanel card = new RoundedPanel(24, cardColor);
        card.setPreferredSize(new Dimension(420, 280));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(35, 35, 35, 35));
        
        // Adicionando borda sutil ao card para fundo branco
        card.putClientProperty(FlatClientProperties.STYLE, "outline: #333333; outlineWidth: 1;");

        JPanel cardIn = new JPanel();
        cardIn.setLayout(new BoxLayout(cardIn, BoxLayout.Y_AXIS));
        cardIn.setOpaque(false);
        
        JLabel cardTitle = new JLabel("Atualizar Tabelas");
        cardTitle.setForeground(textColor);
        cardTitle.setFont(loadFont("Quicksand.ttf", 22f, Font.BOLD));
        JLabel cardDesc = new JLabel("<html>Sincroniza os arquivos de Downloads com a Base Original.<br>Inclui limpeza automática de conflitos no Excel.</html>");
        cardDesc.setForeground(textSecondary);
        cardDesc.setFont(loadFont("Quicksand.ttf", 14f, Font.PLAIN));
        
        JButton btnExec = new JButton("EXECUTAR PROCESSAMENTO");
        btnExec.setBackground(primaryColor);
        btnExec.setForeground(bgColor);
        btnExecUt(btnExec);
        btnExec.addActionListener(this::handleExecutar);
        
        cardIn.add(cardTitle);
        cardIn.add(Box.createVerticalStrut(10));
        cardIn.add(cardDesc);
        cardIn.add(Box.createVerticalGlue());
        cardIn.add(btnExec);
        card.add(cardIn);
        
        // --- CARD 3: COPIAR SERIAIS ---
        RoundedPanel cardSeriais = new RoundedPanel(24, cardColor);
        cardSeriais.setPreferredSize(new Dimension(420, 280));
        cardSeriais.setLayout(new BorderLayout());
        cardSeriais.setBorder(new EmptyBorder(35, 35, 35, 35));
        cardSeriais.putClientProperty(FlatClientProperties.STYLE, "outline: #333333; outlineWidth: 1;");

        JPanel cardInSeriais = new JPanel();
        cardInSeriais.setLayout(new BoxLayout(cardInSeriais, BoxLayout.Y_AXIS));
        cardInSeriais.setOpaque(false);
        
        JLabel cardTitleSer = new JLabel("Copiar Seriais");
        cardTitleSer.setForeground(textColor);
        cardTitleSer.setFont(loadFont("Quicksand.ttf", 22f, Font.BOLD));
        JLabel cardDescSer = new JLabel("<html>Copia a lista completa de seriais para a área de transferência.<br>Útil para consultas rápidas no portal.</html>");
        cardDescSer.setForeground(textSecondary);
        cardDescSer.setFont(loadFont("Quicksand.ttf", 14f, Font.PLAIN));
        
        JButton btnSer = new JButton("COPIAR SERIAIS");
        btnSer.setBackground(primaryColor);
        btnSer.setForeground(bgColor);
        btnExecUt(btnSer);
        btnSer.addActionListener(this::handleCopiarSeriais);
        
        cardInSeriais.add(cardTitleSer);
        cardInSeriais.add(Box.createVerticalStrut(10));
        cardInSeriais.add(cardDescSer);
        cardInSeriais.add(Box.createVerticalGlue());
        cardInSeriais.add(btnSer);
        cardSeriais.add(cardInSeriais);

        JPanel centerGrid = new JPanel(new GridLayout(1, 3, 20, 0));
        centerGrid.setOpaque(false);
        centerGrid.add(cardSeriais);
        centerGrid.add(cardConfig);
        centerGrid.add(card);
        contentArea.add(centerGrid, BorderLayout.CENTER);
        
        JPanel logBox = new JPanel(new BorderLayout());
        logBox.setOpaque(false);
        logBox.setPreferredSize(new Dimension(0, 200));
        
        RoundedPanel logBg = new RoundedPanel(16, new Color(18, 18, 18));
        logBg.setLayout(new BorderLayout());
        logBg.setBorder(new EmptyBorder(15, 20, 15, 20));
        logArea = new JTextArea();
        logArea.setBackground(new Color(18, 18, 18));
        logArea.setForeground(textColor);
        logArea.setFont(loadFont("Quicksand.ttf", 12f, Font.PLAIN));
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
        btn.setFont(loadFont("Quicksand.ttf", 14f, Font.BOLD));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");
    }

    private void handleCopiarSeriais(ActionEvent e) {
        String seriais = getSeriaisList();
        StringSelection selection = new StringSelection(seriais);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
        
        addLog("✓ Sucesso: Seriais copiados para a área de transferência.", "success");
        JOptionPane.showMessageDialog(this, "SERIAIS COPIADOS", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleSincronizar(ActionEvent e) {
        new Thread(() -> {
            try {
                addLog("Iniciando sincronização de configurações...", "info");
                maquinasService.sincronizarConfiguracoes();
                addLog("✓ Sucesso: Configurações sincronizadas.", "success");
            } catch (Exception ex) {
                addLog("✖ Falha: " + ex.getMessage(), "error");
            }
        }).start();
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
    private String getSeriaisList() {
        return "LN6K4R02E8VLBX19\nGRX5LJN95DGN96CM\nFLC98V3PSHGMX085\n22015318\n724937154033\n180414\n724934302018\n12319080344\n10179\nXTDM0404150027\nXTDM0404150019\nXTDM0404150009\nXTDM0404150033\nXTDM0404150011\nXTDM0404150035\nXTDM0404150015\nXTDM0404150041\nXTDM0404150036\nXTDM0404150032\nL4E4X82243VLD9WW\n79457\n2KYRJVDPVB4BNG0W\nTTPX0135010098\n16577\nSFATE0FR2S7H9R89\n82058\nXTDM0404150017\nFSDCTNBHJ6TWX2GM\nNNRBFYEB0FBMXTBF\nFROTA25\n19770\n97250\n222849\n97263\n222871\n97187\n82050\n61887\nFNGVDSMD50XYD31T\n724934306019\nN55VYDA7JGN3398A\n180503\n1Z899G3GT8WGHL3S\n724934458014\nDWM980P02RE5BHJH\n6722020229\n724937881056\n191922\n222869\n67242\n724934383045\n81072\n96488\n67260\n79603\n724934303029\n57224\n19697\n99663\n724933751047\n724937535023\n724934383061\n53810\n724934458016\n12319098344\n16680\n724937622006\n724937273020\n192116\n222856\n53812\n96438\n151304\n181902\n57429\n57427\n57462\n724937881045\n45684\n724937153019\n4361287\n724934306015\n191314\n724937154039\n96442\nB2VZ31MY46853NNB\n80LARPHAT4M6Y208\n15219\n76184\n192307\n724934458019\n19766\n67248\n51052\n9211036343\n191924\n724937622027\n180513\n724937273014\n502964\n724934304014\n97429\n181219\n17046\n724934302021\n81056\n192506\n82061\n190905\n57458\n10231053344\n96530\n19741\n10231060344\n724934458004\n724937590048\n57219\n10231001344\nWY14VV6DJRS1H55Y\n192102\n76551\n502985\n81916\n76257\n502669\n19928\n724936558047\n76287\n182020\n724937155031\n16573\n192311\n724932834015\n190401\n222840\n180912\n14151\n57486\nHX6C4A8WT7LALPLS\n6227097343\n81009\n81914\n222915\n61846\n97184\n191805\n190805\n26358\nXNPVVRMW2K81PHZF\n96537\n121457\nEC5GLZD8SN4H9TEA\n81013\n502551\n222921\n222991\n97176\n724937535024\n10230003344\n724937535026\n180604\n502620\n9520\n96433\n170908\n502626\n182009\n180126468\n97195\n222890\n10231049344\n81910\n10230001344\nN0932\n57210\n724937535002\n222755\n502582\n8C2X8FYACY3SZBE2\n15203\n25674\n97260\n502569\n99671\n57192\n502559\n190915\n724934305005\n180126499\n724937718033\n724934306006\n10230024344\n52339\n181013\n10230032344\n222681\n82076\n180126492\n10230035344\n502994\n190501\n222778\n724937881053\nS0499\n222853\n82078\n97388\n190715\n192815\n222698\n81048\n724937881058\n180126500\n724937881048\n67140\n81100\n724937718022\n182102\n26059\n99679\n724937590012\n191204\n121879\n79526\n180126502\n502625\n97196\n220965\n222948\n53823\n63902\n67245\n61872\n724934306002\n190807\n19861\n16678\nTTDM0171510364\n222981\n13845\n222875\n180126467\nDB0H78J22KP5B5DN\n10231027344\n80985\n5220272\n191804\nAT5E5EZ7SKYW16P2\n724934458001\n724934303003\nFJD9G48E51RGD71M\n97200\n97425\n180820\n81101\n724937590035\nS0352\n15766\n222986\n61889\n99579\n724937153029\n222725\n180519\n99709\n222870\n222761\n190409\n222841\n222866\n82071\n724934303009\n724937881032\n222955\n192008\n222893\n82074\n724934304028\n724936676039\n82081\n17170\n9211041343\n3923070193\n10230039344\n180126504\n180126465\n755407\n724937273016\n724937153018\n97422\n222973\n191018\n26340\n57212\n724934458016\n82054\n82064\n724934488017\n182210\n724937881053\n43G1KBXP5EC89K4X\n9YKSBBZ7VCTFHJJ5\n502618\n724934303008\n79156\n222879\n180126490\n222783\n190811\n502986\n61904\n191109\n180504\n80794\n724937881050\n180707\n82056\n97258\n160706\n180614\n79648\n191103\n81887\n502524\n724937881041\n724937881038\n191923\n81915\n191014\nTTDM0171510308\n26357\n13935\n52334\n79548\n222863\n222758\n19764\n180701\n97257\n97256\n10230033344\n12313170344\nV7PFC91M2WZVA10X\n97411\n724937535001\n724934458013\n724934302019\n81045\n502909\n97423\n724936075012\n724934383007\n222859\n16504\n97428\n19227\n192025\n10230020344\n502617\n180126471\n9315188344\n222942\n180126501\n97363\n121708\n222909\n724936676033\n16533\n57144\n97431\n724934304008\n82055\n57179\n78930\n180126443\n180126503\n82077\n222677\n180126445\n82037\n79555\n121469\n502616\n79153\n222987\n80739\n10231093344\n190212\n724937881051\n222777\n10230017344\n222539\n81070\n57504\n180126493\n724937153008\n724937535009\n222700\n222972\n724936676020\n724937881043\n724936676049\n14986\n10230041344\n222766\n82080\n222735\n99844\n97264\n724937622033\n1258037\n9EYD0BWHS6FT1720\n222864\n150302\n192201\n724937822014\n10231098344\n190503\n121459\n67137\n222906\n190714\n222943\n97254\n222545\n80954\n180908\n503000\n16574\n724934302036\n180126505\n222784\n222697\n181303\n121706\n724934383013\n81025\n79157\n222764\n191713\n502908\n96532\n96453\n191013\n99693\n53832\n180126469\n97198\n502881\n52336\n82084\n9315087344\n57512\n192021\n57488\n6227021343\n502622\n25691\n222907\n121712\n502997\nR0622\n724937881039\n190311\n96462\n190220\n222896\n724934306009\n502565\n724937154048\n724937881052\n10231088344\n724936558043\nYBH7CE8S5RKRDDR1\n724934458026\n97430\n724937881029\n724932834017\n1NP64RLR48MAD3VC\n190110\n10231019344\n57188\n724937881042\n724936676030\n180419\n502652\n180126442\n81905\n67226\n53833\n6227042343\n724937153016\n190906\n724937153036\n121874\n03923076193\n192121\n724934304031\n10231062344\n502990\n222899\n222945\n121718\n80992\n724934201035\n181215\n191720\n222983\n20237\n6GSG2YHCV2GNW08M\n180126491\n180126498\n502907\n57445\n724934303019\n222980\n19865\n19529\n53820\n96495\n16673\n190903\n97177\n19496\n96441\n724937273005\n724937535003\n61H2S597JBF9YP4G\n10229015344\n121704\n180126444\n97182\n724937154053\n12313174344\nXLPPP6PF21E5LDKH\n170615\n10231017344\n53867\n724937154054\n724934303035\n222635\n52338\n191116\n502623\n222769\n96512\n222938\n81919\n97248\n181216\n81923\n96452\n99666\n97179\n182113\nPSESJ8WRE8WAX606\n180612\n9315131344\n724937861049\n81053\n502895\n8FPM49S52T3Z9L1C\nBX36Z98XEMBXDSTN\n724937622032\n57472\n724937881055\n724937153004\n181007\n192011\n57499\n81908\n222937\nVCLGW31F4YEH7GRB\n502583\n79658\n192007\n190215\n222842\n724937155023\n97370\n10229027344\n81920\n502619\n99835\n724934304017\n502553\n502958\n10230033344\n724934383007\n180126471\n180126445\n222987\n222700\n14986\n1258037\n724937822014\n10231098344\n222943\n222764\n192021\n502565\n190110\n10231019344\n724937881042\n724936676030\n724937153016\n03923076193\n6GSG2YHCV2GNW08M\n96495\n121704\n10231017344\n222769\n96452\n182113\n724937861049\n8FPM49S52T3Z9L1C\n181007\n502583\n180420";
    }
}
