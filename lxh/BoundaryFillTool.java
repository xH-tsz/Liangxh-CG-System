package lxh;

import javax.swing.*;
import java.awt.*;

public class BoundaryFillTool implements ToolInterface {
    
    private DrawingCanvas canvas;
    private JPanel parameterPanel;
    private JButton fillButton;
    private JButton borderColorButton;
    private JButton fillColorButton;
    private JLabel borderColorLabel;
    private JLabel fillColorLabel;
    private JLabel hintLabel;
    private Color borderColor;
    private Color fillColor;

    @Override
    public String getToolName() {
        return "边界标志填充";
    }

    @Override
    public String getToolId() {
        return "boundary_fill";
    }

    @Override
    public JPanel getParameterPanel() {
        if (parameterPanel == null) {
            parameterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
            parameterPanel.setBackground(new Color(245, 245, 245));
            
            borderColor = Color.BLACK;
            fillColor = Color.ORANGE;
            
            fillButton = createButton("边界标志算法填充", 150);
            
            borderColorButton = createButton("选择边界颜色", 120);
            fillColorButton = createButton("选择填充颜色", 120);
            
            borderColorLabel = new JLabel("边界颜色");
            borderColorLabel.setOpaque(true);
            borderColorLabel.setBackground(borderColor);
            borderColorLabel.setForeground(getContrastColor(borderColor));
            borderColorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            borderColorLabel.setPreferredSize(new Dimension(70, 28));
            
            fillColorLabel = new JLabel("填充颜色");
            fillColorLabel.setOpaque(true);
            fillColorLabel.setBackground(fillColor);
            fillColorLabel.setForeground(getContrastColor(fillColor));
            fillColorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            fillColorLabel.setPreferredSize(new Dimension(70, 28));
            
            hintLabel = new JLabel("提示：框选封闭图形后点击填充按钮");
            hintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            hintLabel.setForeground(new Color(80, 80, 80));
            
            parameterPanel.add(borderColorButton);
            parameterPanel.add(borderColorLabel);
            parameterPanel.add(fillColorButton);
            parameterPanel.add(fillColorLabel);
            parameterPanel.add(fillButton);
            parameterPanel.add(hintLabel);
            
            borderColorButton.addActionListener(e -> showBorderColorChooser());
            fillColorButton.addActionListener(e -> showFillColorChooser());
            fillButton.addActionListener(e -> performFill());
        }
        return parameterPanel;
    }

    @Override
    public void activate(DrawingCanvas canvas) {
        this.canvas = canvas;
        canvas.setCurrentTool("boundary_fill");
    }

    @Override
    public void deactivate() {
        canvas = null;
    }

    private JButton createButton(String text, int width) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, 38));
        button.setFocusPainted(false);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        return button;
    }

    private Color getContrastColor(Color color) {
        int gray = (color.getRed() * 299 + color.getGreen() * 587 + color.getBlue() * 114) / 1000;
        return gray >= 128 ? Color.BLACK : Color.WHITE;
    }

    private void showBorderColorChooser() {
        Color selected = JColorChooser.showDialog(null, "选择边界颜色", borderColor);
        if (selected != null) {
            borderColor = selected;
            borderColorLabel.setBackground(borderColor);
            borderColorLabel.setForeground(getContrastColor(borderColor));
        }
    }

    private void showFillColorChooser() {
        Color selected = JColorChooser.showDialog(null, "选择填充颜色", fillColor);
        if (selected != null) {
            fillColor = selected;
            fillColorLabel.setBackground(fillColor);
            fillColorLabel.setForeground(getContrastColor(fillColor));
        }
    }

    private void performFill() {
        if (canvas != null) {
            canvas.fillSelectedShapesByBoundaryFlag(borderColor, fillColor);
        }
    }
}