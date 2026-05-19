package lxh;

import javax.swing.*;
import java.awt.*;

public class FreehandDrawTool implements ToolInterface {

    private DrawingCanvas canvas;
    private JPanel parameterPanel;
    private JButton finishButton, undoButton, borderColorButton;
    private JLabel hintLabel, borderColorLabel;
    private Color borderColor;

    @Override
    public String getToolName() {
        return "任意图形";
    }

    @Override
    public String getToolId() {
        return "freehand";
    }

    @Override
    public JPanel getParameterPanel() {
        if (parameterPanel == null) {
            parameterPanel = new JPanel();
            parameterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));
            parameterPanel.setBackground(new Color(245, 245, 245));
            parameterPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 210, 210)));

            borderColor = Color.BLACK;

            finishButton = new JButton("完成绘制");
            finishButton.setPreferredSize(new Dimension(110, 38));
            finishButton.setFocusPainted(false);
            finishButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));

            undoButton = new JButton("撤销");
            undoButton.setPreferredSize(new Dimension(80, 38));
            undoButton.setFocusPainted(false);
            undoButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));

            borderColorButton = new JButton("选择边界颜色");
            borderColorButton.setPreferredSize(new Dimension(120, 38));
            borderColorButton.setFocusPainted(false);
            borderColorButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));

            borderColorLabel = new JLabel("边界颜色");
            borderColorLabel.setOpaque(true);
            borderColorLabel.setBackground(borderColor);
            borderColorLabel.setForeground(getContrastColor(borderColor));
            borderColorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            borderColorLabel.setPreferredSize(new Dimension(70, 28));

            hintLabel = new JLabel("按住鼠标左键拖动手绘 | Ctrl+左键框选");
            hintLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
            hintLabel.setForeground(new Color(80, 80, 80));

            parameterPanel.add(borderColorButton);
            parameterPanel.add(borderColorLabel);
            parameterPanel.add(finishButton);
            parameterPanel.add(undoButton);
            parameterPanel.add(hintLabel);

            finishButton.addActionListener(e -> finishFreehand());
            undoButton.addActionListener(e -> undoFreehand());
            borderColorButton.addActionListener(e -> showBorderColorChooser());
        }
        return parameterPanel;
    }

    @Override
    public void activate(DrawingCanvas canvas) {
        this.canvas = canvas;
        canvas.setCurrentTool("freehand");
        canvas.setCurrentStrokeColor(borderColor);
    }

    @Override
    public void deactivate() {
        canvas = null;
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
            if (canvas != null) {
                canvas.setCurrentStrokeColor(borderColor);
            }
        }
    }

    private void finishFreehand() {
        if (canvas == null)
            return;
        canvas.finalizeFreehand();
    }

    private void undoFreehand() {
        if (canvas == null)
            return;
        canvas.undoLastFreehand();
    }
}