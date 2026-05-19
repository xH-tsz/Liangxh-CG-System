package lxh;

import javax.swing.*;
import java.awt.*;

public class TranslateTool implements ToolInterface {
    
    private DrawingCanvas canvas;
    private JPanel parameterPanel;
    private JTextField dxField, dyField;
    private JButton applyButton;
    private JLabel hintLabel;

    @Override
    public String getToolName() {
        return "平移";
    }

    @Override
    public String getToolId() {
        return "translate";
    }

    @Override
    public JPanel getParameterPanel() {
        if (parameterPanel == null) {
            parameterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            parameterPanel.setBackground(new Color(245, 245, 245));
            
            dxField = createTextField("50");
            dyField = createTextField("30");
            
            applyButton = new JButton("应用平移");
            applyButton.setPreferredSize(new Dimension(100, 30));
            applyButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            
            hintLabel = new JLabel("提示：输入平移距离，点击应用平移到所有图形");
            hintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            hintLabel.setForeground(new Color(80, 80, 80));
            
            parameterPanel.add(new JLabel("Δx:"));
            parameterPanel.add(dxField);
            parameterPanel.add(new JLabel("Δy:"));
            parameterPanel.add(dyField);
            parameterPanel.add(applyButton);
            parameterPanel.add(hintLabel);
            
            applyButton.addActionListener(e -> applyTranslate());
        }
        return parameterPanel;
    }

    @Override
    public void activate(DrawingCanvas canvas) {
        this.canvas = canvas;
        canvas.setCurrentTool("translate");
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

    private void applyTranslate() {
        if (canvas == null) return;
        try {
            double dx = Double.parseDouble(dxField.getText().trim());
            double dy = Double.parseDouble(dyField.getText().trim());
            canvas.translateShapes(dx, dy);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "请输入有效的数字", "输入错误", JOptionPane.WARNING_MESSAGE);
        }
    }
}