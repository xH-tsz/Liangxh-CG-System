package lxh;

import javax.swing.*;
import java.awt.*;

public class ScaleTool implements ToolInterface {
    
    private DrawingCanvas canvas;
    private JPanel parameterPanel;
    private JTextField factorField;
    private JButton applyButton;
    private JLabel hintLabel;

    @Override
    public String getToolName() {
        return "放缩";
    }

    @Override
    public String getToolId() {
        return "scale";
    }

    @Override
    public JPanel getParameterPanel() {
        if (parameterPanel == null) {
            parameterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            parameterPanel.setBackground(new Color(245, 245, 245));
            
            factorField = createTextField("1.5");
            
            applyButton = new JButton("应用放缩");
            applyButton.setPreferredSize(new Dimension(100, 30));
            applyButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            
            hintLabel = new JLabel("提示：输入放缩倍数（>0），点击应用放缩到所有图形");
            hintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            hintLabel.setForeground(new Color(80, 80, 80));
            
            parameterPanel.add(new JLabel("放缩倍数:"));
            parameterPanel.add(factorField);
            parameterPanel.add(applyButton);
            parameterPanel.add(hintLabel);
            
            applyButton.addActionListener(e -> applyScale());
        }
        return parameterPanel;
    }

    @Override
    public void activate(DrawingCanvas canvas) {
        this.canvas = canvas;
        canvas.setCurrentTool("scale");
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

    private void applyScale() {
        if (canvas == null) return;
        try {
            double factor = Double.parseDouble(factorField.getText().trim());
            if (factor <= 0) {
                JOptionPane.showMessageDialog(null, "放缩倍数必须大于0", "输入错误", JOptionPane.WARNING_MESSAGE);
                return;
            }
            canvas.scaleShapes(factor);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "请输入有效的数字", "输入错误", JOptionPane.WARNING_MESSAGE);
        }
    }
}