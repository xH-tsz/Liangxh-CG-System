package lxh;

import javax.swing.*;
import java.awt.*;

public class RotateTool implements ToolInterface {
    
    private DrawingCanvas canvas;
    private JPanel parameterPanel;
    private JTextField angleField;
    private JComboBox<String> directionCombo;
    private JButton applyButton;
    private JLabel hintLabel;

    @Override
    public String getToolName() {
        return "旋转";
    }

    @Override
    public String getToolId() {
        return "rotate";
    }

    @Override
    public JPanel getParameterPanel() {
        if (parameterPanel == null) {
            parameterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            parameterPanel.setBackground(new Color(245, 245, 245));
            
            angleField = createTextField("45");
            
            directionCombo = new JComboBox<>(new String[]{"顺时针", "逆时针"});
            directionCombo.setPreferredSize(new Dimension(100, 26));
            directionCombo.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            
            applyButton = new JButton("应用旋转");
            applyButton.setPreferredSize(new Dimension(100, 30));
            applyButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            
            hintLabel = new JLabel("提示：输入旋转角度，选择方向，点击应用旋转到所有图形");
            hintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            hintLabel.setForeground(new Color(80, 80, 80));
            
            parameterPanel.add(new JLabel("角度:"));
            parameterPanel.add(angleField);
            parameterPanel.add(new JLabel("°"));
            parameterPanel.add(directionCombo);
            parameterPanel.add(applyButton);
            parameterPanel.add(hintLabel);
            
            applyButton.addActionListener(e -> applyRotate());
        }
        return parameterPanel;
    }

    @Override
    public void activate(DrawingCanvas canvas) {
        this.canvas = canvas;
        canvas.setCurrentTool("rotate");
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

    private void applyRotate() {
        if (canvas == null) return;
        try {
            double angle = Double.parseDouble(angleField.getText().trim());
            boolean clockwise = "顺时针".equals(directionCombo.getSelectedItem());
            canvas.rotateShapes(angle, clockwise);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "请输入有效的数字", "输入错误", JOptionPane.WARNING_MESSAGE);
        }
    }
}