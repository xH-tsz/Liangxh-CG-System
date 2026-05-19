package lxh;

import javax.swing.*;
import java.awt.*;

public class ColorEditTool implements ToolInterface {
    
    private DrawingCanvas canvas;
    private JPanel parameterPanel;
    private JButton strokeColorButton;
    private JButton fillColorButton;
    private JLabel hintLabel;
    private Color currentStrokeColor;
    private Color currentFillColor;

    @Override
    public String getToolName() {
        return "颜色编辑";
    }

    @Override
    public String getToolId() {
        return "color_edit";
    }

    @Override
    public JPanel getParameterPanel() {
        if (parameterPanel == null) {
            parameterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            parameterPanel.setBackground(new Color(245, 245, 245));
            
            currentStrokeColor = Color.BLACK;
            currentFillColor = new Color(255, 200, 100, 120);
            
            strokeColorButton = new JButton("");
            strokeColorButton.setPreferredSize(new Dimension(30, 30));
            strokeColorButton.setBackground(currentStrokeColor);
            strokeColorButton.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            
            fillColorButton = new JButton("");
            fillColorButton.setPreferredSize(new Dimension(30, 30));
            fillColorButton.setBackground(currentFillColor);
            fillColorButton.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            
            hintLabel = new JLabel("提示：拖拽框选图形后，点击颜色按钮修改颜色");
            hintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            hintLabel.setForeground(new Color(80, 80, 80));
            
            parameterPanel.add(new JLabel("轮廓颜色:"));
            parameterPanel.add(strokeColorButton);
            parameterPanel.add(new JLabel("填充颜色:"));
            parameterPanel.add(fillColorButton);
            parameterPanel.add(hintLabel);
            
            strokeColorButton.addActionListener(e -> showStrokeColorChooser());
            fillColorButton.addActionListener(e -> showFillColorChooser());
        }
        return parameterPanel;
    }

    @Override
    public void activate(DrawingCanvas canvas) {
        this.canvas = canvas;
        canvas.setCurrentTool("color_edit");
    }

    @Override
    public void deactivate() {
        canvas = null;
    }

    private void showStrokeColorChooser() {
        Color newColor = JColorChooser.showDialog(null, "选择轮廓颜色", currentStrokeColor);
        if (newColor != null) {
            currentStrokeColor = newColor;
            strokeColorButton.setBackground(currentStrokeColor);
            if (canvas != null) {
                canvas.updateSelectedShapesStrokeColor(currentStrokeColor);
            }
        }
    }

    private void showFillColorChooser() {
        Color newColor = JColorChooser.showDialog(null, "选择填充颜色", currentFillColor);
        if (newColor != null) {
            currentFillColor = newColor;
            fillColorButton.setBackground(currentFillColor);
            if (canvas != null) {
                canvas.updateSelectedShapesFillColor(currentFillColor, 0);
            }
        }
    }
}