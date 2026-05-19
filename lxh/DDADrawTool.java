package lxh;

import javax.swing.*;
import java.awt.*;

public class DDADrawTool implements ToolInterface {
    
    private DrawingCanvas canvas;
    private JPanel parameterPanel;
    private JTextField x1Field, y1Field, x2Field, y2Field;
    private JButton drawButton;
    private JLabel hintLabel;

    @Override
    public String getToolName() {
        return "DDA绘直线";
    }

    @Override
    public String getToolId() {
        return "dda_line";
    }

    @Override
    public JPanel getParameterPanel() {
        if (parameterPanel == null) {
            parameterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            parameterPanel.setBackground(new Color(245, 245, 245));
            
            x1Field = createTextField("100");
            y1Field = createTextField("100");
            x2Field = createTextField("300");
            y2Field = createTextField("300");
            
            drawButton = new JButton("绘制直线");
            drawButton.setPreferredSize(new Dimension(100, 30));
            drawButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            
            hintLabel = new JLabel("提示：在画布上点击两次确定直线起点和终点");
            hintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            hintLabel.setForeground(new Color(80, 80, 80));
            
            parameterPanel.add(new JLabel("起点 x:"));
            parameterPanel.add(x1Field);
            parameterPanel.add(new JLabel("y:"));
            parameterPanel.add(y1Field);
            parameterPanel.add(new JLabel("终点 x:"));
            parameterPanel.add(x2Field);
            parameterPanel.add(new JLabel("y:"));
            parameterPanel.add(y2Field);
            parameterPanel.add(drawButton);
            parameterPanel.add(hintLabel);
            
            drawButton.addActionListener(e -> drawLineFromInput());
        }
        return parameterPanel;
    }

    @Override
    public void activate(DrawingCanvas canvas) {
        this.canvas = canvas;
        canvas.setCurrentTool("dda_line");
    }

    @Override
    public void deactivate() {
        canvas = null;
    }

    private JTextField createTextField(String text) {
        JTextField field = new JTextField(text);
        field.setPreferredSize(new Dimension(60, 26));
        field.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        return field;
    }

    private void drawLineFromInput() {
        if (canvas == null) return;
        try {
            int x1 = Integer.parseInt(x1Field.getText().trim());
            int y1 = Integer.parseInt(y1Field.getText().trim());
            int x2 = Integer.parseInt(x2Field.getText().trim());
            int y2 = Integer.parseInt(y2Field.getText().trim());
            canvas.addLine(x1, y1, x2, y2, true);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "请输入有效的整数坐标", "输入错误", JOptionPane.WARNING_MESSAGE);
        }
    }
}