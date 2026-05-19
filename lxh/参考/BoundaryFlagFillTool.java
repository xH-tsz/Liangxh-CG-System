package lxh;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class BoundaryFlagFillTool extends JPanel {

    private JTextField xField;
    private JTextField yField;

    private JButton addPointButton;
    private JButton drawPolygonButton;
    private JButton fillButton;
    private JButton undoButton;
    private JButton clearButton;
    private JButton borderColorButton;
    private JButton fillColorButton;

    private JLabel coordLabel;
    private JLabel scaleLabel;
    private JLabel pointCountLabel;
    private JLabel borderColorLabel;
    private JLabel fillColorLabel;
    private JLabel clickHintLabel;

    private Color borderColor;
    private Color fillColor;

    private BoundaryInnerCanvas canvas;

    public BoundaryFlagFillTool() {
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
        xField = createTextField("100");
        yField = createTextField("100");

        addPointButton = createButton("添加坐标点", 110);
        drawPolygonButton = createButton("绘制凸多边形", 130);
        fillButton = createButton("边界标志算法填充", 150);
        undoButton = createButton("撤销最后一个点", 130);
        clearButton = createButton("清空面板", 100);
        borderColorButton = createButton("选择边界颜色", 120);
        fillColorButton = createButton("选择填充颜色", 120);

        coordLabel = new JLabel("当前坐标：(0, 0)");
        coordLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));

        scaleLabel = new JLabel("缩放比例：1.00 倍");
        scaleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));

        pointCountLabel = new JLabel("点数：0");
        pointCountLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));

        clickHintLabel = new JLabel("可鼠标点选顶点，也可输入坐标后点击“添加坐标点”；绘制前只显示点，不实时连线");
        clickHintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        clickHintLabel.setForeground(new Color(80, 80, 80));

        borderColor = Color.BLACK;
        fillColor = Color.ORANGE;

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

        canvas = new BoundaryInnerCanvas();
    }

    private void initLayout() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        topPanel.setBackground(new Color(245, 245, 245));
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 210, 210)));

        topPanel.add(new JLabel("x:"));
        topPanel.add(xField);
        topPanel.add(new JLabel("y:"));
        topPanel.add(yField);
        topPanel.add(addPointButton);

        topPanel.add(borderColorButton);
        topPanel.add(borderColorLabel);
        topPanel.add(fillColorButton);
        topPanel.add(fillColorLabel);

        topPanel.add(drawPolygonButton);
        topPanel.add(fillButton);
        topPanel.add(undoButton);
        topPanel.add(clearButton);

        topPanel.add(pointCountLabel);
        topPanel.add(coordLabel);
        topPanel.add(scaleLabel);
        topPanel.add(clickHintLabel);

        add(topPanel, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);
    }

    private void initActions() {
        addPointButton.addActionListener(e -> {
            try {
                int x = Integer.parseInt(xField.getText().trim());
                int y = Integer.parseInt(yField.getText().trim());
                canvas.addPoint(new Point(x, y));
                updatePointCount();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "请输入有效的整数坐标。",
                        "输入错误",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });

        borderColorButton.addActionListener(e -> {
            Color selected = JColorChooser.showDialog(this, "选择边界颜色", borderColor);
            if (selected != null) {
                borderColor = selected;
                borderColorLabel.setBackground(borderColor);
                borderColorLabel.setForeground(getContrastColor(borderColor));
                canvas.setBorderColor(borderColor);
                repaint();
            }
        });

        fillColorButton.addActionListener(e -> {
            Color selected = JColorChooser.showDialog(this, "选择填充颜色", fillColor);
            if (selected != null) {
                fillColor = selected;
                fillColorLabel.setBackground(fillColor);
                fillColorLabel.setForeground(getContrastColor(fillColor));
                canvas.setFillColor(fillColor);
                repaint();
            }
        });

        drawPolygonButton.addActionListener(e -> {
            if (canvas.getPointCount() < 3) {
                JOptionPane.showMessageDialog(
                        this,
                        "至少需要 3 个点才能构成多边形。",
                        "提示",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (!canvas.isConvexPolygon()) {
                JOptionPane.showMessageDialog(
                        this,
                        "当前按输入/点击顺序连接后不是凸多边形，请重新选点。",
                        "提示",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            canvas.drawPolygonOnly();
        });

        fillButton.addActionListener(e -> {
            if (!canvas.isPolygonDrawn()) {
                JOptionPane.showMessageDialog(
                        this,
                        "请先点击“绘制凸多边形”。",
                        "提示",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            canvas.fillPolygonByBoundaryFlag();
        });

        undoButton.addActionListener(e -> {
            canvas.undoLastPoint();
            updatePointCount();
        });

        clearButton.addActionListener(e -> clearAll());
    }

    private JTextField createTextField(String text) {
        JTextField textField = new JTextField(text);
        textField.setPreferredSize(new Dimension(60, 28));
        textField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        return textField;
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

    private void updatePointCount() {
        pointCountLabel.setText("点数：" + canvas.getPointCount());
    }

    public void clearAll() {
        canvas.clearAll();
        updatePointCount();
        coordLabel.setText("当前坐标：(0, 0)");
        scaleLabel.setText("缩放比例：1.00 倍");
    }

    private class BoundaryInnerCanvas extends JPanel {

        private final List<Point> pointList;

        private boolean polygonDrawn;
        private boolean filled;

        private double scale;
        private double offsetX;
        private double offsetY;

        private Color currentBorderColor;
        private Color currentFillColor;

        BoundaryInnerCanvas() {
            pointList = new ArrayList<>();
            polygonDrawn = false;
            filled = false;

            scale = 1.0;
            offsetX = 0.0;
            offsetY = 0.0;

            currentBorderColor = borderColor;
            currentFillColor = fillColor;

            setBackground(Color.WHITE);
            initMouseActions();
        }

        private void initMouseActions() {
            MouseAdapter mouseAdapter = new MouseAdapter() {

                @Override
                public void mouseMoved(MouseEvent e) {
                    Point logicalPoint = screenToLogical(e.getPoint());
                    coordLabel.setText("当前坐标：(" + logicalPoint.x + ", " + logicalPoint.y + ")");
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!SwingUtilities.isLeftMouseButton(e)) {
                        return;
                    }

                    Point logicalPoint = screenToLogical(e.getPoint());
                    addPoint(logicalPoint);
                    updatePointCount();
                }

                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    double oldScale = scale;

                    if (e.getWheelRotation() < 0) {
                        scale = Math.min(scale * 1.1, 20.0);
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

        void addPoint(Point p) {
            pointList.add(p);
            polygonDrawn = false;
            filled = false;
            repaint();
        }

        void undoLastPoint() {
            if (!pointList.isEmpty()) {
                pointList.remove(pointList.size() - 1);
                polygonDrawn = false;
                filled = false;
                repaint();
            }
        }

        void clearAll() {
            pointList.clear();
            polygonDrawn = false;
            filled = false;
            scale = 1.0;
            offsetX = 0.0;
            offsetY = 0.0;
            currentBorderColor = borderColor;
            currentFillColor = fillColor;
            repaint();
        }

        int getPointCount() {
            return pointList.size();
        }

        void setBorderColor(Color color) {
            currentBorderColor = color;
        }

        void setFillColor(Color color) {
            currentFillColor = color;
        }

        boolean isPolygonDrawn() {
            return polygonDrawn;
        }

        void drawPolygonOnly() {
            polygonDrawn = true;
            filled = false;
            repaint();
        }

        void fillPolygonByBoundaryFlag() {
            filled = true;
            repaint();
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

        boolean isConvexPolygon() {
            int n = pointList.size();
            if (n < 3) {
                return false;
            }

            long prevCross = 0;

            for (int i = 0; i < n; i++) {
                Point a = pointList.get(i);
                Point b = pointList.get((i + 1) % n);
                Point c = pointList.get((i + 2) % n);

                long cross = crossProduct(a, b, c);

                if (cross != 0) {
                    if (prevCross != 0 && cross * prevCross < 0) {
                        return false;
                    }
                    prevCross = cross;
                }
            }

            return true;
        }

        private long crossProduct(Point a, Point b, Point c) {
            long x1 = b.x - a.x;
            long y1 = b.y - a.y;
            long x2 = c.x - b.x;
            long y2 = c.y - b.y;
            return x1 * y2 - y1 * x2;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.translate(offsetX, offsetY);
            g2.scale(scale, scale);

            if (filled && polygonDrawn && pointList.size() >= 3) {
                fillByScanLine(g2);
            }

            if (polygonDrawn && pointList.size() >= 2) {
                drawPolygonEdges(g2);
            }

            drawPointMarkers(g2);

            g2.dispose();

            Graphics2D gScreen = (Graphics2D) g.create();
            drawPointLabels(gScreen);
            gScreen.dispose();
        }

        private void drawPointMarkers(Graphics2D g2) {
            g2.setColor(currentBorderColor);
            for (Point p : pointList) {
                g2.fillRect(p.x - 2, p.y - 2, 5, 5);
            }
        }

        private void drawPointLabels(Graphics2D gScreen) {
            gScreen.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            gScreen.setColor(Color.RED);

            for (int i = 0; i < pointList.size(); i++) {
                Point screenPoint = logicalToScreen(pointList.get(i));
                Point logicalPoint = pointList.get(i);
                gScreen.drawString(
                        "P" + (i + 1) + "(" + logicalPoint.x + "," + logicalPoint.y + ")",
                        screenPoint.x + 8,
                        screenPoint.y - 8
                );
            }
        }

        private void drawPolygonEdges(Graphics2D g2) {
            g2.setColor(currentBorderColor);

            int n = pointList.size();
            for (int i = 0; i < n; i++) {
                Point p1 = pointList.get(i);
                Point p2 = pointList.get((i + 1) % n);
                drawLinePixels(g2, p1, p2);
            }
        }

        private void drawLinePixels(Graphics g, Point startPoint, Point endPoint) {
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

        private void fillByScanLine(Graphics2D g2) {
            int n = pointList.size();
            int minY = Integer.MAX_VALUE;
            int maxY = Integer.MIN_VALUE;

            for (Point p : pointList) {
                minY = Math.min(minY, p.y);
                maxY = Math.max(maxY, p.y);
            }

            g2.setColor(currentFillColor);

            for (int y = minY; y <= maxY; y++) {
                List<Integer> intersections = new ArrayList<>();

                for (int i = 0; i < n; i++) {
                    Point p1 = pointList.get(i);
                    Point p2 = pointList.get((i + 1) % n);

                    if (p1.y == p2.y) {
                        continue;
                    }

                    int ymin = Math.min(p1.y, p2.y);
                    int ymax = Math.max(p1.y, p2.y);

                    if (y >= ymin && y < ymax) {
                        double x = p1.x + (double) (y - p1.y) * (p2.x - p1.x) / (p2.y - p1.y);
                        intersections.add((int) Math.round(x));
                    }
                }

                intersections.sort(Integer::compareTo);

                for (int i = 0; i + 1 < intersections.size(); i += 2) {
                    int xStart = intersections.get(i);
                    int xEnd = intersections.get(i + 1);

                    for (int x = xStart; x <= xEnd; x++) {
                        g2.fillRect(x, y, 1, 1);
                    }
                }
            }

            g2.setColor(currentBorderColor);
            drawPolygonEdges(g2);
        }
    }
}