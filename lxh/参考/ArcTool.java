package lxh;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

public class ArcTool extends JPanel {

    private JTextField centerXField;
    private JTextField centerYField;
    private JTextField radiusField;
    private JButton drawButton;
    private JButton clearButton;
    private JLabel coordLabel;
    private JLabel scaleLabel;
    private JLabel clickHintLabel;

    private CircleInnerCanvas canvas;

    public ArcTool() {
        initPanel();
        initComponents();
        initLayout();
        initActions();
    }

    private void initPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
    }

    private void initComponents() {
        centerXField = createTextField("300");
        centerYField = createTextField("200");
        radiusField = createTextField("100");

        drawButton = new JButton("画圆");
        drawButton.setPreferredSize(new Dimension(90, 38));
        drawButton.setFocusPainted(false);
        drawButton.setFont(new Font("微软雅黑", Font.PLAIN, 13));

        clearButton = new JButton("清空面板");
        clearButton.setPreferredSize(new Dimension(100, 38));
        clearButton.setFocusPainted(false);
        clearButton.setFont(new Font("微软雅黑", Font.PLAIN, 13));

        coordLabel = new JLabel("当前坐标：(0, 0)");
        coordLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));

        scaleLabel = new JLabel("缩放比例：1.00 倍");
        scaleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));

        clickHintLabel = new JLabel("鼠标操作：单击一次确定圆心，再单击一次确定半径");
        clickHintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        clickHintLabel.setForeground(new Color(80, 80, 80));

        canvas = new CircleInnerCanvas();
    }

    private void initLayout() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        topPanel.setBackground(new Color(245, 245, 245));
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 210, 210)));

        topPanel.add(new JLabel("圆心 x:"));
        topPanel.add(centerXField);
        topPanel.add(new JLabel("圆心 y:"));
        topPanel.add(centerYField);
        topPanel.add(new JLabel("半径 r:"));
        topPanel.add(radiusField);
        topPanel.add(drawButton);
        topPanel.add(clearButton);
        topPanel.add(coordLabel);
        topPanel.add(scaleLabel);
        topPanel.add(clickHintLabel);

        add(topPanel, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);
    }

    private void initActions() {
        drawButton.addActionListener(e -> {
            try {
                int xc = Integer.parseInt(centerXField.getText().trim());
                int yc = Integer.parseInt(centerYField.getText().trim());
                int r = Integer.parseInt(radiusField.getText().trim());

                if (r <= 0) {
                    JOptionPane.showMessageDialog(
                            this,
                            "半径必须为正整数。",
                            "输入错误",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                canvas.addCircle(new Point(xc, yc), r);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "请输入有效的整数。",
                        "输入错误",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });

        clearButton.addActionListener(e -> clearAll());
    }

    private JTextField createTextField(String text) {
        JTextField textField = new JTextField(text);
        textField.setPreferredSize(new Dimension(58, 28));
        textField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        return textField;
    }

    public void clearAll() {
        canvas.clearAll();
    }

    private class CircleRecord {
        Point center;
        int radius;

        CircleRecord(Point center, int radius) {
            this.center = center;
            this.radius = radius;
        }
    }

    private class CircleInnerCanvas extends JPanel {

        private final List<CircleRecord> circleList;

        private Point firstClickCenter;
        private Point currentMousePoint;

        private double scale;
        private double offsetX;
        private double offsetY;

        CircleInnerCanvas() {
            circleList = new ArrayList<>();
            firstClickCenter = null;
            currentMousePoint = null;

            scale = 1.0;
            offsetX = 0.0;
            offsetY = 0.0;

            setBackground(Color.WHITE);
            initMouseActions();
        }

        private void initMouseActions() {
            MouseAdapter mouseAdapter = new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!SwingUtilities.isLeftMouseButton(e)) {
                        return;
                    }

                    Point logicalPoint = screenToLogical(e.getPoint());

                    if (firstClickCenter == null) {
                        firstClickCenter = logicalPoint;
                        currentMousePoint = logicalPoint;
                    } else {
                        int radius = (int) Math.round(firstClickCenter.distance(logicalPoint));
                        if (radius > 0) {
                            addCircle(firstClickCenter, radius);
                        }
                        firstClickCenter = null;
                        currentMousePoint = null;
                    }

                    repaint();
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    Point logicalPoint = screenToLogical(e.getPoint());
                    currentMousePoint = logicalPoint;
                    updateCoordLabel(logicalPoint);
                    repaint();
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    Point logicalPoint = screenToLogical(e.getPoint());
                    currentMousePoint = logicalPoint;
                    updateCoordLabel(logicalPoint);
                    repaint();
                }

                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    double oldScale = scale;

                    if (e.getWheelRotation() < 0) {
                        scale = Math.min(scale * 1.1, 10.0);
                    } else {
                        scale = Math.max(scale / 1.1, 0.2);
                    }

                    Point cursor = e.getPoint();

                    double logicalX = (cursor.x - offsetX) / oldScale;
                    double logicalY = (cursor.y - offsetY) / oldScale;

                    offsetX = cursor.x - logicalX * scale;
                    offsetY = cursor.y - logicalY * scale;

                    scaleLabel.setText(String.format("缩放比例：%.2f 倍", scale));
                    repaint();
                }
            };

            addMouseListener(mouseAdapter);
            addMouseMotionListener(mouseAdapter);
            addMouseWheelListener(mouseAdapter);
        }

        void addCircle(Point center, int radius) {
            circleList.add(new CircleRecord(center, radius));
            repaint();
        }

        void clearAll() {
            circleList.clear();
            firstClickCenter = null;
            currentMousePoint = null;
            scale = 1.0;
            offsetX = 0.0;
            offsetY = 0.0;
            scaleLabel.setText("缩放比例：1.00 倍");
            coordLabel.setText("当前坐标：(0, 0)");
            repaint();
        }

        private void updateCoordLabel(Point logicalPoint) {
            coordLabel.setText("当前坐标：(" + logicalPoint.x + ", " + logicalPoint.y + ")");
        }

        private Point screenToLogical(Point screenPoint) {
            int x = (int) Math.round((screenPoint.x - offsetX) / scale);
            int y = (int) Math.round((screenPoint.y - offsetY) / scale);
            return new Point(x, y);
        }

        private Point logicalToScreen(Point logicalPoint) {
            int x = (int) Math.round(logicalPoint.x * scale + offsetX);
            int y = (int) Math.round(logicalPoint.y * scale + offsetY);
            return new Point(x, y);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.translate(offsetX, offsetY);
            g2.scale(scale, scale);
            g2.setColor(Color.BLACK);

            for (CircleRecord circle : circleList) {
                drawMidpointCircle(g2, circle.center, circle.radius);
            }

            if (firstClickCenter != null && currentMousePoint != null) {
                int previewRadius = (int) Math.round(firstClickCenter.distance(currentMousePoint));
                if (previewRadius > 0) {
                    drawMidpointCircle(g2, firstClickCenter, previewRadius);
                }
            }

            g2.dispose();

            Graphics2D gScreen = (Graphics2D) g.create();

            for (CircleRecord circle : circleList) {
                drawCenterMarker(gScreen, circle.center, circle.radius);
            }

            if (firstClickCenter != null) {
                drawSingleCenterMarker(gScreen, firstClickCenter);
            }

            gScreen.dispose();
        }

        private void drawMidpointCircle(Graphics g, Point center, int radius) {
            int xc = center.x;
            int yc = center.y;

            int x = 0;
            int y = radius;
            int d = 1 - radius;

            plotCirclePoints(g, xc, yc, x, y);

            while (x < y) {
                x++;
                if (d < 0) {
                    d = d + 2 * x + 1;
                } else {
                    y--;
                    d = d + 2 * (x - y) + 1;
                }
                plotCirclePoints(g, xc, yc, x, y);
            }
        }

        private void plotCirclePoints(Graphics g, int xc, int yc, int x, int y) {
            g.fillRect(xc + x, yc + y, 1, 1);
            g.fillRect(xc - x, yc + y, 1, 1);
            g.fillRect(xc + x, yc - y, 1, 1);
            g.fillRect(xc - x, yc - y, 1, 1);

            g.fillRect(xc + y, yc + x, 1, 1);
            g.fillRect(xc - y, yc + x, 1, 1);
            g.fillRect(xc + y, yc - x, 1, 1);
            g.fillRect(xc - y, yc - x, 1, 1);
        }

        private void drawCenterMarker(Graphics2D g, Point center, int radius) {
            Point c = logicalToScreen(center);
            int r = 4;

            g.setFont(new Font("微软雅黑", Font.PLAIN, 12));

            g.setColor(Color.RED);
            g.fill(new Ellipse2D.Double(c.x - r, c.y - r, r * 2, r * 2));
            g.drawString("圆心(" + center.x + ", " + center.y + ")", c.x + 8, c.y - 8);

            g.setColor(new Color(0, 120, 255));
            Point edge = logicalToScreen(new Point(center.x + radius, center.y));
            g.fill(new Ellipse2D.Double(edge.x - r, edge.y - r, r * 2, r * 2));
            g.drawString("半径=" + radius, edge.x + 8, edge.y - 8);
        }

        private void drawSingleCenterMarker(Graphics2D g, Point center) {
            Point c = logicalToScreen(center);
            int r = 4;

            g.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            g.setColor(Color.RED);
            g.fill(new Ellipse2D.Double(c.x - r, c.y - r, r * 2, r * 2));
            g.drawString("圆心(" + center.x + ", " + center.y + ")", c.x + 8, c.y - 8);
        }
    }
}