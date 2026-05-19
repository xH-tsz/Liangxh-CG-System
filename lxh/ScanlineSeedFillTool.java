package lxh;

import javax.swing.*;
import java.awt.*;

public class ScanlineSeedFillTool implements ToolInterface {
    
    private DrawingCanvas canvas;
    private JPanel parameterPanel;
    private JButton selectSeedButton;
    private JButton fillButton;
    private JButton borderColorButton;
    private JButton fillColorButton;
    private JLabel borderColorLabel;
    private JLabel fillColorLabel;
    private JLabel hintLabel;
    private Color borderColor;
    private Color fillColor;
    private boolean seedMode;

    @Override
    public String getToolName() {
        return "扫描线种子填充";
    }

    @Override
    public String getToolId() {
        return "scanline_fill";
    }

    @Override
    public JPanel getParameterPanel() {
        if (parameterPanel == null) {
            parameterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
            parameterPanel.setBackground(new Color(245, 245, 245));
            
            borderColor = Color.BLACK;
            fillColor = Color.ORANGE;
            seedMode = false;
            
            selectSeedButton = createButton("选择种子点", 110);
            fillButton = createButton("扫描线种子填充", 130);
            
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
            
            hintLabel = new JLabel("提示：框选封闭图形后，先选择种子点再点击填充");
            hintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            hintLabel.setForeground(new Color(80, 80, 80));
            
            parameterPanel.add(borderColorButton);
            parameterPanel.add(borderColorLabel);
            parameterPanel.add(fillColorButton);
            parameterPanel.add(fillColorLabel);
            parameterPanel.add(selectSeedButton);
            parameterPanel.add(fillButton);
            parameterPanel.add(hintLabel);
            
            borderColorButton.addActionListener(e -> showBorderColorChooser());
            fillColorButton.addActionListener(e -> showFillColorChooser());
            selectSeedButton.addActionListener(e -> enterSeedMode());
            fillButton.addActionListener(e -> performFill());
        }
        return parameterPanel;
    }

    @Override
    public void activate(DrawingCanvas canvas) {
        this.canvas = canvas;
        canvas.setCurrentTool("scanline_fill");
        seedMode = false;
    }

    @Override
    public void deactivate() {
        canvas = null;
        seedMode = false;
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
            
            if (canvas != null && !seedMode) {
                canvas.fillSelectedShapesByScanlineSeed(borderColor, fillColor);
            }
        }
    }

    private void enterSeedMode() {
        if (canvas != null) {
            seedMode = true;
            canvas.setSeedMode(true);
            hintLabel.setText("请在封闭区域内部点击一个种子点");
        }
    }

    private void performFill() {
        if (canvas != null) {
            boolean success = canvas.fillSelectedShapesByScanlineSeed(borderColor, fillColor);
            if (!success) {
                if (!seedMode) {
                    JOptionPane.showMessageDialog(null, "请先选择种子点，或框选图形后直接填充", "提示", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "种子点不在可填充区域内部，或者区域未闭合。", "提示", JOptionPane.WARNING_MESSAGE);
                }
            }
            seedMode = false;
            canvas.setSeedMode(false);
            hintLabel.setText("提示：框选封闭图形后，先选择种子点再点击填充");
        }
    }
}