package lxh;

import javax.swing.*;
import java.awt.*;

public class CircleDrawTool implements ToolInterface {

  private DrawingCanvas canvas;
  private JPanel parameterPanel;
  private JTextField cxField, cyField, rField;
  private JButton drawButton;
  private JLabel hintLabel;

  @Override
  public String getToolName() {
    return "绘制圆";
  }

  @Override
  public String getToolId() {
    return "circle";
  }

  @Override
  public JPanel getParameterPanel() {
    if (parameterPanel == null) {
      parameterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
      parameterPanel.setBackground(new Color(245, 245, 245));

      cxField = createTextField("300");
      cyField = createTextField("200");
      rField = createTextField("100");

      drawButton = new JButton("绘制圆");
      drawButton.setPreferredSize(new Dimension(90, 30));
      drawButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));

      hintLabel = new JLabel("提示：在画布上点击两次确定圆心和半径");
      hintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
      hintLabel.setForeground(new Color(80, 80, 80));

      parameterPanel.add(new JLabel("圆心 x:"));
      parameterPanel.add(cxField);
      parameterPanel.add(new JLabel("y:"));
      parameterPanel.add(cyField);
      parameterPanel.add(new JLabel("半径:"));
      parameterPanel.add(rField);
      parameterPanel.add(drawButton);
      parameterPanel.add(hintLabel);

      drawButton.addActionListener(e -> drawCircleFromInput());
    }
    return parameterPanel;
  }

  @Override
  public void activate(DrawingCanvas canvas) {
    this.canvas = canvas;
    canvas.setCurrentTool("circle");
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

  private void drawCircleFromInput() {
    if (canvas == null)
      return;
    try {
      int cx = Integer.parseInt(cxField.getText().trim());
      int cy = Integer.parseInt(cyField.getText().trim());
      int r = Integer.parseInt(rField.getText().trim());
      if (r <= 0) {
        JOptionPane.showMessageDialog(null, "半径必须为正整数", "输入错误", JOptionPane.WARNING_MESSAGE);
        return;
      }
      canvas.addCircle(cx, cy, r);
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(null, "请输入有效的整数", "输入错误", JOptionPane.WARNING_MESSAGE);
    }
  }
}