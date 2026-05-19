package lxh;

import javax.swing.*;
import java.awt.*;

public class ClipTool implements ToolInterface {
    
    private DrawingCanvas canvas;
    private JPanel parameterPanel;
    private JButton selectWindowButton;
    private JButton cohenButton;
    private JButton cyrusButton;
    private JButton liangButton;
    private JLabel hintLabel;

    @Override
    public String getToolName() {
        return "裁剪";
    }

    @Override
    public String getToolId() {
        return "clip";
    }

    @Override
    public JPanel getParameterPanel() {
        if (parameterPanel == null) {
            parameterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            parameterPanel.setBackground(new Color(245, 245, 245));
            
            selectWindowButton = new JButton("选择裁剪区域");
            selectWindowButton.setPreferredSize(new Dimension(130, 30));
            selectWindowButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            
            cohenButton = new JButton("Sutherland-Cohen");
            cohenButton.setPreferredSize(new Dimension(160, 30));
            cohenButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            
            cyrusButton = new JButton("Cyrus-Beck");
            cyrusButton.setPreferredSize(new Dimension(120, 30));
            cyrusButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            
            liangButton = new JButton("梁友栋");
            liangButton.setPreferredSize(new Dimension(100, 30));
            liangButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            
            hintLabel = new JLabel("提示：先点击选择裁剪区域，在画布上拖动绘制矩形，然后选择裁剪算法");
            hintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            hintLabel.setForeground(new Color(80, 80, 80));
            
            parameterPanel.add(selectWindowButton);
            parameterPanel.add(new JLabel("算法:"));
            parameterPanel.add(cohenButton);
            parameterPanel.add(cyrusButton);
            parameterPanel.add(liangButton);
            parameterPanel.add(hintLabel);
            
            selectWindowButton.addActionListener(e -> selectClipWindow());
            cohenButton.addActionListener(e -> clipWithAlgorithm(0));
            cyrusButton.addActionListener(e -> clipWithAlgorithm(1));
            liangButton.addActionListener(e -> clipWithAlgorithm(2));
        }
        return parameterPanel;
    }

    @Override
    public void activate(DrawingCanvas canvas) {
        this.canvas = canvas;
        canvas.setCurrentTool("clip");
    }

    @Override
    public void deactivate() {
        canvas = null;
    }

    private void selectClipWindow() {
        if (canvas == null) return;
        canvas.enterClipWindowMode();
    }

    private void clipWithAlgorithm(int algorithm) {
        if (canvas == null) return;
        if (canvas.getClipWindow() == null) {
            JOptionPane.showMessageDialog(null, "请先选择裁剪区域", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        canvas.clipShapes(algorithm);
    }
}