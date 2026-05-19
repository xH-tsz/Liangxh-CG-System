package lxh;

import javax.swing.*;
import java.awt.*;

public class PolygonDrawTool implements ToolInterface {

    private DrawingCanvas canvas;
    private JPanel parameterPanel;
    private JTextField xField, yField;
    private JButton addPointButton, finishButton, undoButton;
    private JLabel hintLabel, pointCountLabel;

    @Override
    public String getToolName() {
        return "绘制多边形";
    }

    @Override
    public String getToolId() {
        return "polygon";
    }

    @Override
    public JPanel getParameterPanel() {
        if (parameterPanel == null) {
            parameterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            parameterPanel.setBackground(new Color(245, 245, 245));

            xField = createTextField("100");
            yField = createTextField("100");

            addPointButton = new JButton("添加坐标点");
            addPointButton.setPreferredSize(new Dimension(100, 30));
            addPointButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));

            finishButton = new JButton("绘制多边形");
            finishButton.setPreferredSize(new Dimension(100, 30));
            finishButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));

            undoButton = new JButton("撤销");
            undoButton.setPreferredSize(new Dimension(80, 30));
            undoButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));

            pointCountLabel = new JLabel("点数：0");
            pointCountLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));

            hintLabel = new JLabel("提示：在画布上点击确定顶点，或输入坐标后点击添加");
            hintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            hintLabel.setForeground(new Color(80, 80, 80));

            parameterPanel.add(new JLabel("点 x:"));
            parameterPanel.add(xField);
            parameterPanel.add(new JLabel("y:"));
            parameterPanel.add(yField);
            parameterPanel.add(addPointButton);
            parameterPanel.add(finishButton);
            parameterPanel.add(undoButton);
            parameterPanel.add(pointCountLabel);
            parameterPanel.add(hintLabel);

            addPointButton.addActionListener(e -> addPointFromInput());
            finishButton.addActionListener(e -> finishPolygon());
            undoButton.addActionListener(e -> undoPoint());
        }
        return parameterPanel;
    }

    @Override
    public void activate(DrawingCanvas canvas) {
        this.canvas = canvas;
        canvas.setCurrentTool("polygon");
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

    private void addPointFromInput() {
        if (canvas == null)
            return;
        try {
            int x = Integer.parseInt(xField.getText().trim());
            int y = Integer.parseInt(yField.getText().trim());
            canvas.addPolygonPoint(x, y);
            updatePointCount();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "请输入有效的整数坐标", "输入错误", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void finishPolygon() {
        if (canvas == null)
            return;
        canvas.finalizePolygon();
        updatePointCount();
    }

    private void undoPoint() {
        if (canvas == null)
            return;
        canvas.undoLastPoint();
        updatePointCount();
    }

    private void updatePointCount() {
        if (canvas != null) {
            pointCountLabel.setText("点数：" + canvas.getTempPointCount());
        }
    }
}