package lxh;

import javax.swing.*;
import java.awt.*;

public class EraserTool implements ToolInterface {

    private JPanel panel;
    private DrawingCanvas canvas;
    private String cursorMode = "crosshair";

    public EraserTool() {
        panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(new Color(245, 245, 245));

        JLabel label = new JLabel("橡皮擦模式：点击任意图形即可删除");
        label.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        panel.add(label);

        JLabel hint = new JLabel("提示：在图形附近点击即可删除该图形");
        hint.setFont(new Font("微软雅黑", Font.ITALIC, 12));
        hint.setForeground(Color.GRAY);
        panel.add(hint);
    }

    @Override
    public String getToolName() {
        return "橡皮擦";
    }

    @Override
    public String getToolId() {
        return "eraser";
    }

    @Override
    public JPanel getParameterPanel() {
        return panel;
    }

    @Override
    public void activate(DrawingCanvas canvas) {
        this.canvas = canvas;
        if (canvas != null) {
            canvas.setCurrentTool("eraser");
            canvas.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        }
    }

    @Override
    public void deactivate() {
        if (canvas != null) {
            canvas.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }
}