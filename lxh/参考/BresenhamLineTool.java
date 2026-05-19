package lxh;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

public class BresenhamLineTool extends JPanel {

    private JTextField startXField;
    private JTextField startYField;
    private JTextField endXField;
    private JTextField endYField;
    private JButton drawButton;
    private JButton clearButton;
    private JLabel coordLabel;
    private JLabel scaleLabel;
    private JLabel clickHintLabel;

    private BresenhamInnerCanvas canvas;

    public BresenhamLineTool() {
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
        startXField = createTextField("100");
        startYField = createTextField("100");
        endXField = createTextField("300");
        endYField = createTextField("300");

        drawButton = new JButton("画直线");
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

        clickHintLabel = new JLabel("鼠标操作：单击一次确定起点，再单击一次确定终点");
        clickHintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        clickHintLabel.setForeground(new Color(80, 80, 80));

        canvas = new BresenhamInnerCanvas();
    }

    private void initLayout() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        topPanel.setBackground(new Color(245, 245, 245));
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 210, 210)));

        topPanel.add(new JLabel("起点 x:"));
        topPanel.add(startXField);
        topPanel.add(new JLabel("起点 y:"));
        topPanel.add(startYField);
        topPanel.add(new JLabel("终点 x:"));
        topPanel.add(endXField);
        topPanel.add(new JLabel("终点 y:"));
        topPanel.add(endYField);
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
                int x1 = Integer.parseInt(startXField.getText().trim());
                int y1 = Integer.parseInt(startYField.getText().trim());
                int x2 = Integer.parseInt(endXField.getText().trim());
                int y2 = Integer.parseInt(endYField.getText().trim());

                canvas.addLine(new Point(x1, y1), new Point(x2, y2));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "请输入有效的整数坐标。",
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

    private class LineRecord {
        Point start;
        Point end;

        LineRecord(Point start, Point end) {
            this.start = start;
            this.end = end;
        }
    }

    private class BresenhamInnerCanvas extends JPanel {

        private final List<LineRecord> lineList;

        private Point firstClickPoint;
        private Point currentMousePoint;

        private double scale;
        private double offsetX;
        private double offsetY;

        BresenhamInnerCanvas() {
            lineList = new ArrayList<>();
            firstClickPoint = null;
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

                    if (firstClickPoint == null) {
                        firstClickPoint = logicalPoint;
                        currentMousePoint = logicalPoint;
                    } else {
                        addLine(firstClickPoint, logicalPoint);
                        firstClickPoint = null;
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

        void addLine(Point start, Point end) {
            lineList.add(new LineRecord(start, end));
            repaint();
        }

        void clearAll() {
            lineList.clear();
            firstClickPoint = null;
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

            for (LineRecord line : lineList) {
                drawBresenhamLine(g2, line.start, line.end);
            }

            if (firstClickPoint != null && currentMousePoint != null) {
                drawBresenhamLine(g2, firstClickPoint, currentMousePoint);
            }

            g2.dispose();

            Graphics2D gScreen = (Graphics2D) g.create();

            for (LineRecord line : lineList) {
                drawMarkers(gScreen, line.start, line.end);
            }

            if (firstClickPoint != null) {
                drawSingleStartMarker(gScreen, firstClickPoint);
            }

            gScreen.dispose();
        }

        private void drawBresenhamLine(Graphics g, Point startPoint, Point endPoint) {
            int x1 = startPoint.x;
            int y1 = startPoint.y;
            int x2 = endPoint.x;
            int y2 = endPoint.y;

            int dx = Math.abs(x2 - x1);
            int dy = Math.abs(y2 - y1);

            int sx = x1 < x2 ? 1 : -1;
            int sy = y1 < y2 ? 1 : -1;
            int err = dx - dy;

            while (true) {
                g.fillRect(x1, y1, 1, 1);

                if (x1 == x2 && y1 == y2) {
                    break;
                }

                int e2 = 2 * err;

                if (e2 > -dy) {
                    err -= dy;
                    x1 += sx;
                }

                if (e2 < dx) {
                    err += dx;
                    y1 += sy;
                }
            }
        }

        private void drawMarkers(Graphics2D g, Point startPoint, Point endPoint) {
            Point s = logicalToScreen(startPoint);
            Point e = logicalToScreen(endPoint);

            int r = 4;
            g.setFont(new Font("微软雅黑", Font.PLAIN, 12));

            g.setColor(Color.RED);
            g.fill(new Ellipse2D.Double(s.x - r, s.y - r, r * 2, r * 2));
            g.drawString("起点(" + startPoint.x + ", " + startPoint.y + ")", s.x + 8, s.y - 8);

            g.setColor(Color.BLUE);
            g.fill(new Ellipse2D.Double(e.x - r, e.y - r, r * 2, r * 2));
            g.drawString("终点(" + endPoint.x + ", " + endPoint.y + ")", e.x + 8, e.y - 8);
        }

        private void drawSingleStartMarker(Graphics2D g, Point startPoint) {
            Point s = logicalToScreen(startPoint);

            int r = 4;
            g.setFont(new Font("微软雅黑", Font.PLAIN, 12));

            g.setColor(Color.RED);
            g.fill(new Ellipse2D.Double(s.x - r, s.y - r, r * 2, r * 2));
            g.drawString("起点(" + startPoint.x + ", " + startPoint.y + ")", s.x + 8, s.y - 8);
        }
    }
}