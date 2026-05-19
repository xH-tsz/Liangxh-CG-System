package lxh;

import javax.swing.*;
import java.awt.*;

public class FunctionPanel extends JPanel {

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
        currentFunctionLabel = new JLabel("当前工具：未选择");
        currentFunctionLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
    }

    private void initLayout() {
        add(currentFunctionLabel);
    }

    public void setCurrentFunctionText(String text) {
        currentFunctionLabel.setText("当前工具：" + text);
    }
}