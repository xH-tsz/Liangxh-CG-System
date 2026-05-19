package lxh;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    private FunctionPanel functionPanel;
    private DrawingCanvas drawingCanvas;
    private JPanel parameterPanel;
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
        drawingCanvas = new DrawingCanvas();
        parameterPanel = new JPanel();
        parameterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));
        parameterPanel.setBackground(new Color(245, 245, 245));
        parameterPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 210, 210)));

        ToolManager.setCanvas(drawingCanvas);

        statusLabel = new JLabel("当前工具：未选择");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        helpPanel = new HelpPanel();
        helpPanel.setVisible(false);
    }

    private void initLayout() {
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.add(functionPanel);
        northPanel.add(parameterPanel);

        add(northPanel, BorderLayout.NORTH);
        add(drawingCanvas, BorderLayout.CENTER);
        add(helpPanel, BorderLayout.EAST);
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("文件");
        JMenuItem clearItem = new JMenuItem("清空当前面板");
        JMenuItem exitItem = new JMenuItem("退出");

        clearItem.addActionListener(e -> drawingCanvas.clearAll());
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(clearItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu drawMenu = new JMenu("绘制功能");
        JMenuItem ddaItem = new JMenuItem("DDA绘直线");
        JMenuItem bresenhamItem = new JMenuItem("Bresenham绘直线");
        JMenuItem circleItem = new JMenuItem("绘制圆");
        JMenuItem polygonItem = new JMenuItem("任意多边形");
        JMenuItem freehandItem = new JMenuItem("任意图形");

        ddaItem.addActionListener(e -> switchTool("dda_line"));
        bresenhamItem.addActionListener(e -> switchTool("bresenham_line"));
        circleItem.addActionListener(e -> switchTool("circle"));
        polygonItem.addActionListener(e -> switchTool("polygon"));
        freehandItem.addActionListener(e -> switchTool("freehand"));

        drawMenu.add(ddaItem);
        drawMenu.add(bresenhamItem);
        drawMenu.add(circleItem);
        drawMenu.add(polygonItem);
        drawMenu.add(freehandItem);

        JMenu transformMenu = new JMenu("变换功能");
        JMenuItem translateItem = new JMenuItem("平移");
        JMenuItem rotateItem = new JMenuItem("旋转");
        JMenuItem scaleItem = new JMenuItem("放缩");
        JMenuItem clipItem = new JMenuItem("裁剪");

        translateItem.addActionListener(e -> switchTool("translate"));
        rotateItem.addActionListener(e -> switchTool("rotate"));
        scaleItem.addActionListener(e -> switchTool("scale"));
        clipItem.addActionListener(e -> switchTool("clip"));

        transformMenu.add(translateItem);
        transformMenu.add(rotateItem);
        transformMenu.add(scaleItem);
        transformMenu.add(clipItem);
        transformMenu.addSeparator();
        JMenuItem eraserItem = new JMenuItem("橡皮擦");
        eraserItem.addActionListener(e -> switchTool("eraser"));
        transformMenu.add(eraserItem);

        JMenu helpMenu = new JMenu("显示帮助");

        JMenuItem showDefaultHelpItem = new JMenuItem("打开帮助栏");
        JMenuItem ddaHelpItem = new JMenuItem("DDA绘直线帮助");
        JMenuItem bresHelpItem = new JMenuItem("Bresenham绘制直线帮助");
        JMenuItem arcHelpItem = new JMenuItem("绘制圆帮助");
        JMenuItem boundaryHelpItem = new JMenuItem("边界标志算法填充帮助");
        JMenuItem seedHelpItem = new JMenuItem("扫描线种子填充帮助");
        JMenuItem transformHelpItem = new JMenuItem("图形平移放缩旋转帮助");
        JMenuItem cuttingHelpItem = new JMenuItem("图形裁剪帮助");

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

        transformHelpItem.addActionListener(e -> {
            helpPanel.showTransformHelp();
            showHelpPanel();
        });

        cuttingHelpItem.addActionListener(e -> {
            helpPanel.showCuttingHelp();
            showHelpPanel();
        });

        helpMenu.add(showDefaultHelpItem);
        helpMenu.addSeparator();
        helpMenu.add(ddaHelpItem);
        helpMenu.add(bresHelpItem);
        helpMenu.add(arcHelpItem);
        helpMenu.add(boundaryHelpItem);
        helpMenu.add(seedHelpItem);
        helpMenu.add(transformHelpItem);
        helpMenu.add(cuttingHelpItem);

        JMenu clearMenu = new JMenu("清除功能");
        JMenuItem eraserMenu = new JMenuItem("橡皮擦");
        JMenuItem clearAllMenu = new JMenuItem("清空面板");

        eraserMenu.addActionListener(e -> switchTool("eraser"));
        clearAllMenu.addActionListener(e -> {
            drawingCanvas.clearAll();
            functionPanel.setCurrentFunctionText("已清空");
            statusLabel.setText("已清空面板");
        });

        clearMenu.add(eraserMenu);
        clearMenu.add(clearAllMenu);

        JMenu colorMenu = new JMenu("颜色功能");
        JMenuItem colorEditItem = new JMenuItem("颜色编辑");
        colorEditItem.addActionListener(e -> switchTool("color_edit"));
        colorMenu.add(colorEditItem);

        JMenu fillMenu = new JMenu("填充功能");
        JMenuItem boundaryFillItem = new JMenuItem("边界标志算法填充");
        boundaryFillItem.addActionListener(e -> switchTool("boundary_fill"));
        JMenuItem scanlineFillItem = new JMenuItem("扫描线种子填充");
        scanlineFillItem.addActionListener(e -> switchTool("scanline_fill"));
        fillMenu.add(boundaryFillItem);
        fillMenu.add(scanlineFillItem);

        menuBar.add(fileMenu);
        menuBar.add(drawMenu);
        menuBar.add(transformMenu);
        menuBar.add(clearMenu);
        menuBar.add(colorMenu);
        menuBar.add(fillMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void initActions() {
        helpPanel.getHideButton().addActionListener(e -> hideHelpPanel());
    }

    private void switchTool(String toolId) {
        ToolInterface tool = ToolManager.createTool(toolId);
        if (tool != null) {
            ToolManager.setCurrentTool(tool);
            updateParameterPanel(tool);
            functionPanel.setCurrentFunctionText(tool.getToolName());
            statusLabel.setText("当前工具：" + tool.getToolName());
        }
    }

    private void updateParameterPanel(ToolInterface tool) {
        parameterPanel.removeAll();
        parameterPanel.add(tool.getParameterPanel());
        parameterPanel.revalidate();
        parameterPanel.repaint();
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