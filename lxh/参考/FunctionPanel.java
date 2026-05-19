package lxh;

import javax.swing.*;
import java.awt.*;

public class FunctionPanel extends JPanel {

    private JButton ddaButton;
    private JButton bresenhamButton;
    private JButton arcButton;
    private JButton boundaryFillButton;
    private JButton seedFillButton;
    private JLabel currentFunctionLabel;

    public FunctionPanel() {
        initPanel();
        initComponents();
        initLayout();
    }

    private void initPanel() {
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 210, 210)));
        setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
    }

    private void initComponents() {
        currentFunctionLabel = new JLabel("当前演示：未选择");
        currentFunctionLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));

        ddaButton = createButton("DDA绘直线");
        bresenhamButton = createButton("Bresenham绘制直线");
        arcButton = createButton("绘制圆弧");
        boundaryFillButton = createButton("边界标志算法填充");
        seedFillButton = createButton("扫描线种子填充");
    }

    private void initLayout() {
        add(currentFunctionLabel);
        add(ddaButton);
        add(bresenhamButton);
        add(arcButton);
        add(boundaryFillButton);
        add(seedFillButton);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(180, 40));
        button.setFocusPainted(false);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        return button;
    }

    public JButton getDdaButton() {
        return ddaButton;
    }

    public JButton getBresenhamButton() {
        return bresenhamButton;
    }

    public JButton getArcButton() {
        return arcButton;
    }

    public JButton getBoundaryFillButton() {
        return boundaryFillButton;
    }

    public JButton getSeedFillButton() {
        return seedFillButton;
    }

    public void setCurrentFunctionText(String text) {
        currentFunctionLabel.setText("当前演示：" + text);
    }
}