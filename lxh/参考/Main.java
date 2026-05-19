package lxh;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    private FunctionPanel functionPanel;
    private JPanel centerContainer;
    private JLabel statusLabel;
    private HelpPanel helpPanel;

    public Main() {
        initFrame();
        initComponents();
        initLayout();
        initMenuBar();
        initActions();
    }

    private void initFrame() {
        setTitle("计算机图形学实验平台");
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    private void initComponents() {
        functionPanel = new FunctionPanel();
        centerContainer = new JPanel(new BorderLayout());
        statusLabel = new JLabel("当前功能：未选择");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        helpPanel = new HelpPanel();
        helpPanel.setVisible(false);

        functionPanel.setVisible(false);

        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(Color.WHITE);

        JLabel welcomeLabel = new JLabel("请选择上方“功能栏”中的功能开始演示", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(90, 90, 90));

        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);
        centerContainer.add(welcomePanel, BorderLayout.CENTER);
    }

    private void initLayout() {
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.add(functionPanel);

        add(northPanel, BorderLayout.NORTH);
        add(centerContainer, BorderLayout.CENTER);
        add(helpPanel, BorderLayout.EAST);
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("文件");
        JMenuItem clearItem = new JMenuItem("清空当前面板");
        JMenuItem exitItem = new JMenuItem("退出");

        clearItem.addActionListener(e -> clearCurrentToolPanel());
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(clearItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu functionMenu = new JMenu("功能栏");
        functionMenu.addMenuListener(new javax.swing.event.MenuListener() {
            @Override
            public void menuSelected(javax.swing.event.MenuEvent e) {
                functionPanel.setVisible(true);
                revalidate();
            }

            @Override
            public void menuDeselected(javax.swing.event.MenuEvent e) {
            }

            @Override
            public void menuCanceled(javax.swing.event.MenuEvent e) {
            }
        });

        JMenu helpMenu = new JMenu("显示帮助");

        JMenuItem showDefaultHelpItem = new JMenuItem("打开帮助栏");
        JMenuItem ddaHelpItem = new JMenuItem("DDA绘直线帮助");
        JMenuItem bresHelpItem = new JMenuItem("Bresenham绘制直线帮助");
        JMenuItem arcHelpItem = new JMenuItem("绘制圆帮助");
        JMenuItem boundaryHelpItem = new JMenuItem("边界标志算法填充帮助");
        JMenuItem seedHelpItem = new JMenuItem("扫描线种子填充帮助");

        showDefaultHelpItem.addActionListener(e -> {
            helpPanel.showDefaultHelp();
            showHelpPanel();
        });

        ddaHelpItem.addActionListener(e -> {
            helpPanel.showDDAHelp();
            showHelpPanel();
        });

        bresHelpItem.addActionListener(e -> {
            helpPanel.showBresenhamHelp();
            showHelpPanel();
        });

        arcHelpItem.addActionListener(e -> {
            helpPanel.showCircleHelp();
            showHelpPanel();
        });

        boundaryHelpItem.addActionListener(e -> {
            helpPanel.showBoundaryFillHelp();
            showHelpPanel();
        });

        seedHelpItem.addActionListener(e -> {
            helpPanel.showSeedFillHelp();
            showHelpPanel();
        });

        helpMenu.add(showDefaultHelpItem);
        helpMenu.addSeparator();
        helpMenu.add(ddaHelpItem);
        helpMenu.add(bresHelpItem);
        helpMenu.add(arcHelpItem);
        helpMenu.add(boundaryHelpItem);
        helpMenu.add(seedHelpItem);

        menuBar.add(fileMenu);
        menuBar.add(functionMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void initActions() {
        helpPanel.getHideButton().addActionListener(e -> hideHelpPanel());

        functionPanel.getDdaButton().addActionListener(e -> {
            showToolPanel(new DDALineTool());
            functionPanel.setCurrentFunctionText("DDA绘直线");
            statusLabel.setText("当前功能：DDA绘直线");

            helpPanel.showDDAHelp();
            showHelpPanel();

            functionPanel.setVisible(false);
            revalidate();
        });

        functionPanel.getBresenhamButton().addActionListener(e -> {
            showToolPanel(new BresenhamLineTool());
            functionPanel.setCurrentFunctionText("Bresenham绘制直线");
            statusLabel.setText("当前功能：Bresenham绘制直线");

            helpPanel.showBresenhamHelp();
            showHelpPanel();

            functionPanel.setVisible(false);
            revalidate();
        });

        functionPanel.getArcButton().addActionListener(e -> {
            showToolPanel(new ArcTool());
            functionPanel.setCurrentFunctionText("绘制圆弧");
            statusLabel.setText("当前功能：绘制圆弧");

            helpPanel.showCircleHelp();
            showHelpPanel();

            functionPanel.setVisible(false);
            revalidate();
        });

        functionPanel.getBoundaryFillButton().addActionListener(e -> {
            showToolPanel(new BoundaryFlagFillTool());
            functionPanel.setCurrentFunctionText("边界标志算法填充");
            statusLabel.setText("当前功能：边界标志算法填充");

            helpPanel.showBoundaryFillHelp();
            showHelpPanel();

            functionPanel.setVisible(false);
            revalidate();
        });

        functionPanel.getSeedFillButton().addActionListener(e -> {
            showToolPanel(new SeedScanFillTool());
            functionPanel.setCurrentFunctionText("扫描线种子填充");
            statusLabel.setText("当前功能：扫描线种子填充");

            helpPanel.showSeedFillHelp();
            showHelpPanel();

            functionPanel.setVisible(false);
            revalidate();
        });
    }

    private void showToolPanel(JPanel toolPanel) {
        centerContainer.removeAll();
        centerContainer.add(toolPanel, BorderLayout.CENTER);
        centerContainer.revalidate();
        centerContainer.repaint();
    }

    private void clearCurrentToolPanel() {
        Component component = centerContainer.getComponentCount() > 0
                ? centerContainer.getComponent(0)
                : null;

        if (component instanceof DDALineTool) {
            ((DDALineTool) component).clearAll();
        } else if (component instanceof BresenhamLineTool) {
            ((BresenhamLineTool) component).clearAll();
        } else if (component instanceof ArcTool) {
            ((ArcTool) component).clearAll();
        } else if (component instanceof BoundaryFlagFillTool) {
            ((BoundaryFlagFillTool) component).clearAll();
        } else if (component instanceof SeedScanFillTool) {
            ((SeedScanFillTool) component).clearAll();
        }
    }

    private void showHelpPanel() {
        helpPanel.setVisible(true);
        revalidate();
        repaint();
    }

    private void hideHelpPanel() {
        helpPanel.setVisible(false);
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main frame = new Main();
            frame.setVisible(true);
        });
    }
}