package lxh;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class SeedScanFillTool extends JPanel {

    private JComboBox<String> modeComboBox;

    private JTextField xField;
    private JTextField yField;
    private JTextField centerXField;
    private JTextField centerYField;
    private JTextField radiusField;

    private JButton addPointButton;
    private JButton drawBoundaryButton;
    private JButton selectSeedButton;
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
    private JLabel hintLabel;

    private Color borderColor;
    private Color fillColor;

    private JPanel dynamicInputPanel;
    private JPanel actionPanel;
    private JPanel infoPanel;

    private SeedInnerCanvas canvas;

    public SeedScanFillTool() {
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
        modeComboBox = new JComboBox<>(new String[]{
                "任意多边形模式",
                "圆模式",
                "自由手绘模式"
        });
        modeComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        modeComboBox.setPreferredSize(new Dimension(150, 30));

        xField = createTextField("100");
        yField = createTextField("100");

        centerXField = createTextField("300");
        centerYField = createTextField("200");
        radiusField = createTextField("100");

        addPointButton = createButton("添加坐标点", 110);
        drawBoundaryButton = createButton("绘制封闭边界", 130);
        selectSeedButton = createButton("选择种子点", 110);
        fillButton = createButton("扫描线种子填充", 130);
        undoButton = createButton("撤销", 80);
        clearButton = createButton("清空面板", 100);
        borderColorButton = createButton("选择边界颜色", 120);
        fillColorButton = createButton("选择填充颜色", 120);

        coordLabel = new JLabel("当前坐标：(0, 0)");
        coordLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));

        scaleLabel = new JLabel("缩放比例：1.00 倍");
        scaleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));

        pointCountLabel = new JLabel("点数：0");
        pointCountLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));

        hintLabel = new JLabel("当前为多边形模式：可鼠标点选或输入坐标加点；绘制前只显示点");
        hintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        hintLabel.setForeground(new Color(80, 80, 80));

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

        dynamicInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        dynamicInputPanel.setBackground(new Color(245, 245, 245));

        actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        actionPanel.setBackground(new Color(245, 245, 245));

        infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        infoPanel.setBackground(new Color(245, 245, 245));

        canvas = new SeedInnerCanvas();
    }

    private void initLayout() {
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBackground(new Color(245, 245, 245));
        topContainer.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 210, 210)));

        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        modePanel.setBackground(new Color(245, 245, 245));
        modePanel.add(new JLabel("模式:"));
        modePanel.add(modeComboBox);

        actionPanel.add(borderColorButton);
        actionPanel.add(borderColorLabel);
        actionPanel.add(fillColorButton);
        actionPanel.add(fillColorLabel);
        actionPanel.add(drawBoundaryButton);
        actionPanel.add(selectSeedButton);
        actionPanel.add(fillButton);
        actionPanel.add(undoButton);
        actionPanel.add(clearButton);

        infoPanel.add(pointCountLabel);
        infoPanel.add(coordLabel);
        infoPanel.add(scaleLabel);
        infoPanel.add(hintLabel);

        topContainer.add(modePanel);
        topContainer.add(dynamicInputPanel);
        topContainer.add(actionPanel);
        topContainer.add(infoPanel);

        add(topContainer, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);

        updateModeControls();
    }

    private void initActions() {
        modeComboBox.addActionListener(e -> {
            canvas.switchMode(modeComboBox.getSelectedIndex());
            updateModeControls();
        });

        addPointButton.addActionListener(e -> {
            try {
                int x = Integer.parseInt(xField.getText().trim());
                int y = Integer.parseInt(yField.getText().trim());
                canvas.addPolygonPoint(new Point(x, y));
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

        drawBoundaryButton.addActionListener(e -> {
            int mode = modeComboBox.getSelectedIndex();

            if (mode == 0) {
                if (canvas.getPolygonPointCount() < 3) {
                    JOptionPane.showMessageDialog(this, "至少需要 3 个点。", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                canvas.drawPolygonBoundary();
            } else if (mode == 1) {
                try {
                    int xc = Integer.parseInt(centerXField.getText().trim());
                    int yc = Integer.parseInt(centerYField.getText().trim());
                    int r = Integer.parseInt(radiusField.getText().trim());

                    if (r <= 0) {
                        JOptionPane.showMessageDialog(this, "半径必须为正整数。", "提示", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    canvas.setCircleAndDraw(new Point(xc, yc), r);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "请输入有效的圆参数。", "输入错误", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                if (!canvas.hasFreehandPath()) {
                    JOptionPane.showMessageDialog(this, "请先在画布中自由手绘一个封闭图形。", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                canvas.confirmFreehandBoundary();
            }
        });

        selectSeedButton.addActionListener(e -> {
            if (!canvas.isBoundaryReady()) {
                JOptionPane.showMessageDialog(this, "请先绘制封闭边界。", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            canvas.enterSeedMode();
            hintLabel.setText("请在封闭区域内部点击一个种子点");
        });

        fillButton.addActionListener(e -> {
            if (!canvas.isBoundaryReady()) {
                JOptionPane.showMessageDialog(this, "请先绘制封闭边界。", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!canvas.hasSeedPoint()) {
                JOptionPane.showMessageDialog(this, "请先选择种子点。", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean ok = canvas.scanlineSeedFill();
            if (!ok) {
                JOptionPane.showMessageDialog(this, "种子点不在可填充区域内部，或者区域未闭合。", "提示", JOptionPane.WARNING_MESSAGE);
            }
        });

        undoButton.addActionListener(e -> {
            canvas.undoLast();
            updatePointCount();
        });

        clearButton.addActionListener(e -> clearAll());
    }

    private void updateModeControls() {
        int mode = modeComboBox.getSelectedIndex();

        dynamicInputPanel.removeAll();

        if (mode == 0) {
            dynamicInputPanel.add(new JLabel("点 x:"));
            dynamicInputPanel.add(xField);
            dynamicInputPanel.add(new JLabel("点 y:"));
            dynamicInputPanel.add(yField);
            dynamicInputPanel.add(addPointButton);

            hintLabel.setText("当前为多边形模式：可鼠标点选或输入坐标加点；绘制前只显示点");
        } else if (mode == 1) {
            dynamicInputPanel.add(new JLabel("圆心 x:"));
            dynamicInputPanel.add(centerXField);
            dynamicInputPanel.add(new JLabel("圆心 y:"));
            dynamicInputPanel.add(centerYField);
            dynamicInputPanel.add(new JLabel("半径 r:"));
            dynamicInputPanel.add(radiusField);

            hintLabel.setText("当前为圆模式：可输入圆心和半径，或在画布中点击两次确定圆");
        } else {
            hintLabel.setText("当前为自由手绘模式：按住鼠标左键拖动手绘，松开后自动首尾闭合");
        }

        dynamicInputPanel.revalidate();
        dynamicInputPanel.repaint();

        updatePointCount();
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
        int mode = modeComboBox.getSelectedIndex();
        if (mode == 0) {
            pointCountLabel.setText("点数：" + canvas.getPolygonPointCount());
        } else if (mode == 1) {
            pointCountLabel.setText("点数：圆模式");
        } else {
            pointCountLabel.setText("点数：手绘模式");
        }
    }

    public void clearAll() {
        canvas.clearAll();
        updatePointCount();
        coordLabel.setText("当前坐标：(0, 0)");
        scaleLabel.setText("缩放比例：1.00 倍");
        updateModeControls();
    }

    private class SeedInnerCanvas extends JPanel {

        private static final int MODE_POLYGON = 0;
        private static final int MODE_CIRCLE = 1;
        private static final int MODE_FREEHAND = 2;

        private int mode;

        private final List<Point> polygonPoints;
        private final List<Point> freehandPoints;

        private boolean boundaryReady;
        private boolean seedMode;
        private Point seedPoint;

        private boolean freehandDrawing;
        private Point circleFirstPoint;

        private Point circleCenter;
        private int circleRadius;

        private double scale;
        private double offsetX;
        private double offsetY;

        private Color currentBorderColor;
        private Color currentFillColor;

        private BufferedImage canvasImage;

        SeedInnerCanvas() {
            mode = MODE_POLYGON;
            polygonPoints = new ArrayList<>();
            freehandPoints = new ArrayList<>();
            boundaryReady = false;
            seedMode = false;
            seedPoint = null;
            freehandDrawing = false;
            circleFirstPoint = null;
            circleCenter = null;
            circleRadius = 0;
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

                    if (seedMode) {
                        seedPoint = logicalPoint;
                        seedMode = false;
                        repaint();
                        hintLabel.setText("种子点已选择，可点击“扫描线种子填充”");
                        return;
                    }

                    if (mode == MODE_POLYGON) {
                        if (!boundaryReady) {
                            addPolygonPoint(logicalPoint);
                            updatePointCount();
                        }
                    } else if (mode == MODE_CIRCLE) {
                        if (!boundaryReady) {
                            if (circleFirstPoint == null) {
                                circleFirstPoint = logicalPoint;
                            } else {
                                circleCenter = circleFirstPoint;
                                circleRadius = (int) Math.round(circleFirstPoint.distance(logicalPoint));
                                circleFirstPoint = null;
                                if (circleRadius > 0) {
                                    drawCircleBoundary();
                                }
                            }
                            repaint();
                        }
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (mode == MODE_FREEHAND && SwingUtilities.isLeftMouseButton(e) && !boundaryReady && !seedMode) {
                        freehandPoints.clear();
                        Point logicalPoint = screenToLogical(e.getPoint());
                        freehandPoints.add(logicalPoint);
                        freehandDrawing = true;
                        repaint();
                    }
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    Point logicalPoint = screenToLogical(e.getPoint());
                    coordLabel.setText("当前坐标：(" + logicalPoint.x + ", " + logicalPoint.y + ")");

                    if (mode == MODE_FREEHAND && freehandDrawing && !boundaryReady) {
                        Point last = freehandPoints.get(freehandPoints.size() - 1);
                        if (!last.equals(logicalPoint)) {
                            freehandPoints.add(logicalPoint);
                        }
                        repaint();
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (mode == MODE_FREEHAND && freehandDrawing) {
                        freehandDrawing = false;
                        if (freehandPoints.size() >= 2) {
                            Point first = freehandPoints.get(0);
                            Point last = freehandPoints.get(freehandPoints.size() - 1);
                            if (!last.equals(first)) {
                                freehandPoints.add(new Point(first.x, first.y));
                            }
                        }
                        repaint();
                    }
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

        void switchMode(int newMode) {
            mode = newMode;
            clearAll();
        }

        void addPolygonPoint(Point p) {
            polygonPoints.add(p);
            boundaryReady = false;
            seedMode = false;
            seedPoint = null;
            canvasImage = null;
            repaint();
        }

        int getPolygonPointCount() {
            return polygonPoints.size();
        }

        void setBorderColor(Color color) {
            currentBorderColor = color;
            redrawBoundaryImageIfNeeded();
        }

        void setFillColor(Color color) {
            currentFillColor = color;
            repaint();
        }

        void drawPolygonBoundary() {
            boundaryReady = true;
            seedMode = false;
            seedPoint = null;
            buildBoundaryImage();
            repaint();
        }

        void setCircleAndDraw(Point center, int radius) {
            circleCenter = center;
            circleRadius = radius;
            circleFirstPoint = null;
            drawCircleBoundary();
        }

        private void drawCircleBoundary() {
            boundaryReady = true;
            seedMode = false;
            seedPoint = null;
            buildBoundaryImage();
            repaint();
        }

        boolean hasFreehandPath() {
            return freehandPoints.size() >= 3;
        }

        void confirmFreehandBoundary() {
            boundaryReady = true;
            seedMode = false;
            seedPoint = null;
            buildBoundaryImage();
            repaint();
        }

        boolean isBoundaryReady() {
            return boundaryReady;
        }

        void enterSeedMode() {
            seedMode = true;
            seedPoint = null;
            repaint();
        }

        boolean hasSeedPoint() {
            return seedPoint != null;
        }

        void undoLast() {
            if (mode == MODE_POLYGON) {
                if (!polygonPoints.isEmpty() && !boundaryReady) {
                    polygonPoints.remove(polygonPoints.size() - 1);
                }
            } else if (mode == MODE_FREEHAND) {
                if (!freehandPoints.isEmpty() && !boundaryReady) {
                    freehandPoints.remove(freehandPoints.size() - 1);
                }
            } else if (mode == MODE_CIRCLE) {
                if (!boundaryReady) {
                    circleFirstPoint = null;
                    circleCenter = null;
                    circleRadius = 0;
                }
            }
            seedPoint = null;
            repaint();
        }

        void clearAll() {
            polygonPoints.clear();
            freehandPoints.clear();
            boundaryReady = false;
            seedMode = false;
            seedPoint = null;
            freehandDrawing = false;
            circleFirstPoint = null;
            circleCenter = null;
            circleRadius = 0;
            canvasImage = null;
            scale = 1.0;
            offsetX = 0.0;
            offsetY = 0.0;
            currentBorderColor = borderColor;
            currentFillColor = fillColor;
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

        private void buildBoundaryImage() {
            int w = Math.max(getWidth(), 1);
            int h = Math.max(getHeight(), 1);
            canvasImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2 = canvasImage.createGraphics();
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, w, h);
            g2.dispose();

            if (mode == MODE_POLYGON) {
                for (int i = 0; i < polygonPoints.size(); i++) {
                    Point p1 = polygonPoints.get(i);
                    Point p2 = polygonPoints.get((i + 1) % polygonPoints.size());
                    drawLinePixelsToImage(p1, p2);
                }
            } else if (mode == MODE_CIRCLE) {
                drawCircleToImage(circleCenter, circleRadius);
            } else if (mode == MODE_FREEHAND) {
                for (int i = 0; i < freehandPoints.size() - 1; i++) {
                    drawLinePixelsToImage(freehandPoints.get(i), freehandPoints.get(i + 1));
                }
            }
        }

        private void redrawBoundaryImageIfNeeded() {
            if (boundaryReady) {
                buildBoundaryImage();
                repaint();
            }
        }

        private void drawLinePixelsToImage(Point startPoint, Point endPoint) {
            if (canvasImage == null) {
                return;
            }

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
                setPixelSafe(x1, y1, currentBorderColor.getRGB());

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

        private void drawCircleToImage(Point center, int radius) {
            if (canvasImage == null || center == null || radius <= 0) {
                return;
            }

            int xc = center.x;
            int yc = center.y;

            int x = 0;
            int y = radius;
            int d = 1 - radius;

            plotCirclePoints(xc, yc, x, y);

            while (x < y) {
                x++;
                if (d < 0) {
                    d = d + 2 * x + 1;
                } else {
                    y--;
                    d = d + 2 * (x - y) + 1;
                }
                plotCirclePoints(xc, yc, x, y);
            }
        }

        private void plotCirclePoints(int xc, int yc, int x, int y) {
            setPixelSafe(xc + x, yc + y, currentBorderColor.getRGB());
            setPixelSafe(xc - x, yc + y, currentBorderColor.getRGB());
            setPixelSafe(xc + x, yc - y, currentBorderColor.getRGB());
            setPixelSafe(xc - x, yc - y, currentBorderColor.getRGB());
            setPixelSafe(xc + y, yc + x, currentBorderColor.getRGB());
            setPixelSafe(xc - y, yc + x, currentBorderColor.getRGB());
            setPixelSafe(xc + y, yc - x, currentBorderColor.getRGB());
            setPixelSafe(xc - y, yc - x, currentBorderColor.getRGB());
        }

        private void setPixelSafe(int x, int y, int rgb) {
            if (canvasImage == null) {
                return;
            }
            if (x >= 0 && x < canvasImage.getWidth() && y >= 0 && y < canvasImage.getHeight()) {
                canvasImage.setRGB(x, y, rgb);
            }
        }

        boolean scanlineSeedFill() {
            if (canvasImage == null || seedPoint == null) {
                return false;
            }

            int w = canvasImage.getWidth();
            int h = canvasImage.getHeight();

            if (seedPoint.x < 0 || seedPoint.x >= w || seedPoint.y < 0 || seedPoint.y >= h) {
                return false;
            }

            int borderRGB = currentBorderColor.getRGB();
            int fillRGB = currentFillColor.getRGB();

            int seedRGB = canvasImage.getRGB(seedPoint.x, seedPoint.y);
            if (seedRGB == borderRGB || seedRGB == fillRGB) {
                return false;
            }

            Deque<Point> stack = new ArrayDeque<>();
            stack.push(new Point(seedPoint.x, seedPoint.y));

            while (!stack.isEmpty()) {
                Point p = stack.pop();
                int x = p.x;
                int y = p.y;

                if (y < 0 || y >= h) {
                    continue;
                }

                int left = x;
                while (left >= 0) {
                    int rgb = canvasImage.getRGB(left, y);
                    if (rgb == borderRGB || rgb == fillRGB) {
                        break;
                    }
                    left--;
                }
                left++;

                int right = x;
                while (right < w) {
                    int rgb = canvasImage.getRGB(right, y);
                    if (rgb == borderRGB || rgb == fillRGB) {
                        break;
                    }
                    right++;
                }
                right--;

                if (left > right) {
                    continue;
                }

                for (int i = left; i <= right; i++) {
                    canvasImage.setRGB(i, y, fillRGB);
                }

                scanNeighborLine(stack, left, right, y - 1, borderRGB, fillRGB, w, h);
                scanNeighborLine(stack, left, right, y + 1, borderRGB, fillRGB, w, h);
            }

            repaint();
            return true;
        }

        private void scanNeighborLine(Deque<Point> stack, int left, int right, int y,
                                      int borderRGB, int fillRGB, int w, int h) {
            if (y < 0 || y >= h) {
                return;
            }

            int x = left;
            while (x <= right) {
                boolean foundSeed = false;

                while (x <= right) {
                    int rgb = canvasImage.getRGB(x, y);
                    if (rgb != borderRGB && rgb != fillRGB) {
                        foundSeed = true;
                        break;
                    }
                    x++;
                }

                if (foundSeed) {
                    stack.push(new Point(x, y));

                    while (x <= right) {
                        int rgb = canvasImage.getRGB(x, y);
                        if (rgb == borderRGB || rgb == fillRGB) {
                            break;
                        }
                        x++;
                    }
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.translate(offsetX, offsetY);
            g2.scale(scale, scale);

            if (canvasImage != null) {
                g2.drawImage(canvasImage, 0, 0, null);
            }

            if (!boundaryReady) {
                if (mode == MODE_POLYGON) {
                    drawPolygonPreview(g2);
                } else if (mode == MODE_CIRCLE) {
                    drawCirclePreview(g2);
                } else {
                    drawFreehandPreview(g2);
                }
            }

            drawPointMarkers(g2);
            drawSeedMarker(g2);

            g2.dispose();

            Graphics2D gScreen = (Graphics2D) g.create();
            drawPolygonLabels(gScreen);
            gScreen.dispose();
        }

        private void drawPolygonPreview(Graphics2D g2) {
            g2.setColor(currentBorderColor);
            for (Point p : polygonPoints) {
                g2.fillRect(p.x - 2, p.y - 2, 5, 5);
            }
        }

        private void drawCirclePreview(Graphics2D g2) {
            g2.setColor(currentBorderColor);

            if (circleFirstPoint != null) {
                g2.fillRect(circleFirstPoint.x - 2, circleFirstPoint.y - 2, 5, 5);
            }
        }

        private void drawFreehandPreview(Graphics2D g2) {
            g2.setColor(currentBorderColor);
            for (int i = 0; i < freehandPoints.size() - 1; i++) {
                drawLinePreview(g2, freehandPoints.get(i), freehandPoints.get(i + 1));
            }
        }

        private void drawLinePreview(Graphics2D g2, Point p1, Point p2) {
            int x1 = p1.x;
            int y1 = p1.y;
            int x2 = p2.x;
            int y2 = p2.y;

            int dx = Math.abs(x2 - x1);
            int dy = Math.abs(y2 - y1);

            int sx = x1 < x2 ? 1 : -1;
            int sy = y1 < y2 ? 1 : -1;
            int err = dx - dy;

            while (true) {
                g2.fillRect(x1, y1, 1, 1);

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

        private void drawPointMarkers(Graphics2D g2) {
            g2.setColor(currentBorderColor);

            if (mode == MODE_POLYGON) {
                for (Point p : polygonPoints) {
                    g2.fillRect(p.x - 2, p.y - 2, 5, 5);
                }
            } else if (mode == MODE_CIRCLE) {
                if (circleCenter != null && !boundaryReady) {
                    g2.fillRect(circleCenter.x - 2, circleCenter.y - 2, 5, 5);
                }
            }
        }

        private void drawSeedMarker(Graphics2D g2) {
            if (seedPoint == null) {
                return;
            }

            g2.setColor(Color.RED);
            g2.fillOval(seedPoint.x - 4, seedPoint.y - 4, 8, 8);
            g2.drawLine(seedPoint.x - 6, seedPoint.y, seedPoint.x + 6, seedPoint.y);
            g2.drawLine(seedPoint.x, seedPoint.y - 6, seedPoint.x, seedPoint.y + 6);
        }

        private void drawPolygonLabels(Graphics2D gScreen) {
            if (mode != MODE_POLYGON) {
                return;
            }

            gScreen.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            gScreen.setColor(Color.RED);

            for (int i = 0; i < polygonPoints.size(); i++) {
                Point screenPoint = logicalToScreen(polygonPoints.get(i));
                Point logicalPoint = polygonPoints.get(i);
                gScreen.drawString(
                        "P" + (i + 1) + "(" + logicalPoint.x + "," + logicalPoint.y + ")",
                        screenPoint.x + 8,
                        screenPoint.y - 8
                );
            }
        }
    }
}