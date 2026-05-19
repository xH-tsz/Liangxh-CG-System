package lxh;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

public class DrawingCanvas extends JPanel {

    public static class ShapeData {
        List<Point2D.Double> points;
        boolean closed;
        boolean fillable;
        Color strokeColor;
        Color fillColor;
        String type;

        public ShapeData(boolean closed, boolean fillable, String type) {
            this.points = new ArrayList<>();
            this.closed = closed;
            this.fillable = fillable;
            this.type = type;
            this.strokeColor = Color.BLACK;
            this.fillColor = new Color(255, 200, 100, 120);
        }

        public ShapeData(boolean closed, boolean fillable, String type, Color strokeColor, Color fillColor) {
            this.points = new ArrayList<>();
            this.closed = closed;
            this.fillable = fillable;
            this.type = type;
            this.strokeColor = strokeColor;
            this.fillColor = fillColor;
        }
    }

    private List<ShapeData> shapes;
    private ShapeData currentShape;
    private List<Point2D.Double> tempPoints;
    
    private List<List<ShapeData>> historyStack;
    private int historyIndex;
    
    private Point2D.Double firstClickPoint;
    private Point2D.Double currentMousePoint;
    
    private boolean freehandDrawing;
    private boolean selectingClipWindow;
    private Point2D.Double clipStartPoint;
    private Point2D.Double clipCurrentPoint;
    private Rectangle2D.Double clipWindow;
    
    private double viewScale;
    private double offsetX;
    private double offsetY;
    
    private Color currentStrokeColor;
    private Color currentFillColor;
    
    private String currentTool;
    private ShapeData selectedShape;
    private ShapeData transformedShape;
    
    private boolean selectingShapes;
    private Point2D.Double selectStartPoint;
    private Point2D.Double selectCurrentPoint;
    private List<ShapeData> selectedShapes;

    public DrawingCanvas() {
        shapes = new ArrayList<>();
        tempPoints = new ArrayList<>();
        selectedShapes = new ArrayList<>();
        currentShape = null;
        selectedShape = null;
        transformedShape = null;
        currentStrokeColor = Color.BLACK;
        currentFillColor = new Color(255, 200, 100, 120);
        viewScale = 1.0;
        offsetX = 0.0;
        offsetY = 0.0;
        setBackground(Color.WHITE);
        
        historyStack = new ArrayList<>();
        saveHistory();
        
        initMouseActions();
        initKeyActions();
    }
    
    private void saveHistory() {
        List<ShapeData> snapshot = new ArrayList<>();
        for (ShapeData shape : shapes) {
            ShapeData copy = new ShapeData(shape.closed, shape.fillable, shape.type, shape.strokeColor, shape.fillColor);
            for (Point2D.Double p : shape.points) {
                copy.points.add(new Point2D.Double(p.x, p.y));
            }
            snapshot.add(copy);
        }
        while (historyStack.size() > historyIndex + 1) {
            historyStack.remove(historyStack.size() - 1);
        }
        historyStack.add(snapshot);
        historyIndex = historyStack.size() - 1;
        
        if (historyStack.size() > 50) {
            historyStack.remove(0);
            historyIndex--;
        }
    }
    
    public void undo() {
        if (historyIndex > 0) {
            historyIndex--;
            List<ShapeData> previous = historyStack.get(historyIndex);
            shapes.clear();
            for (ShapeData shape : previous) {
                ShapeData copy = new ShapeData(shape.closed, shape.fillable, shape.type, shape.strokeColor, shape.fillColor);
                for (Point2D.Double p : shape.points) {
                    copy.points.add(new Point2D.Double(p.x, p.y));
                }
                shapes.add(copy);
            }
            selectedShapes.clear();
            repaint();
        }
    }

    private void initMouseActions() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                requestFocusInWindow();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (!SwingUtilities.isLeftMouseButton(e)) return;
                Point2D.Double logicalPoint = screenToLogical(e.getPoint());
                
                if (selectingClipWindow) {
                    clipStartPoint = logicalPoint;
                    clipCurrentPoint = logicalPoint;
                    repaint();
                    return;
                }
                
                if (e.isControlDown()) {
                    selectingShapes = true;
                    selectStartPoint = logicalPoint;
                    selectCurrentPoint = logicalPoint;
                } else if ("freehand".equals(currentTool) && currentShape == null) {
                    tempPoints.clear();
                    tempPoints.add(logicalPoint);
                    freehandDrawing = true;
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point2D.Double logicalPoint = screenToLogical(e.getPoint());
                currentMousePoint = logicalPoint;
                
                if (selectingClipWindow && clipStartPoint != null) {
                    clipCurrentPoint = logicalPoint;
                    repaint();
                    return;
                }
                
                if (e.isControlDown() && selectingShapes && selectStartPoint != null) {
                    selectCurrentPoint = logicalPoint;
                }
                
                if (freehandDrawing && "freehand".equals(currentTool) && currentShape == null && !e.isControlDown()) {
                    Point2D.Double last = tempPoints.get(tempPoints.size() - 1);
                    if (last.distance(logicalPoint) >= 1.0) {
                        tempPoints.add(logicalPoint);
                    }
                }
                
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectingClipWindow && clipStartPoint != null && clipCurrentPoint != null) {
                    double minX = Math.min(clipStartPoint.x, clipCurrentPoint.x);
                    double minY = Math.min(clipStartPoint.y, clipCurrentPoint.y);
                    double maxX = Math.max(clipStartPoint.x, clipCurrentPoint.x);
                    double maxY = Math.max(clipStartPoint.y, clipCurrentPoint.y);
                    
                    if (maxX - minX < 5 || maxY - minY < 5) {
                        JOptionPane.showMessageDialog(DrawingCanvas.this, "裁剪区域太小，请重新选择。");
                    } else {
                        clipWindow = new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
                    }
                    selectingClipWindow = false;
                    clipStartPoint = null;
                    clipCurrentPoint = null;
                    repaint();
                    return;
                }
                
                boolean didDrag = false;
                if (selectingShapes && selectStartPoint != null && selectCurrentPoint != null) {
                    double minX = Math.min(selectStartPoint.x, selectCurrentPoint.x);
                    double minY = Math.min(selectStartPoint.y, selectCurrentPoint.y);
                    double maxX = Math.max(selectStartPoint.x, selectCurrentPoint.x);
                    double maxY = Math.max(selectStartPoint.y, selectCurrentPoint.y);
                    
                    if (maxX - minX >= 5 && maxY - minY >= 5) {
                        didDrag = true;
                        Rectangle2D.Double selectionRect = new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
                        selectedShapes.clear();
                        for (ShapeData shape : shapes) {
                            if (isShapeInRect(shape, selectionRect)) {
                                selectedShapes.add(shape);
                            }
                        }
                    } else {
                        selectedShapes.clear();
                    }
                    
                    selectingShapes = false;
                    selectStartPoint = null;
                    selectCurrentPoint = null;
                }
                
                if (freehandDrawing && "freehand".equals(currentTool) && !e.isControlDown()) {
                        freehandDrawing = false;
                        if (tempPoints.size() >= 3) {
                            Point2D.Double first = tempPoints.get(0);
                            Point2D.Double last = tempPoints.get(tempPoints.size() - 1);
                            if (first.distance(last) > 1.0) {
                                tempPoints.add(new Point2D.Double(first.x, first.y));
                            }
                            ShapeData freehand = new ShapeData(true, false, "freehand", currentStrokeColor, currentFillColor);
                            freehand.points.addAll(new ArrayList<>(tempPoints));
                            shapes.add(freehand);
                            saveHistory();
                        }
                        tempPoints.clear();
                    } else if (!didDrag && !"freehand".equals(currentTool)) {
                    Point2D.Double logicalPoint = screenToLogical(e.getPoint());
                    handleToolClick(logicalPoint);
                }
                
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Point2D.Double logicalPoint = screenToLogical(e.getPoint());
                currentMousePoint = logicalPoint;
                repaint();
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double oldScale = viewScale;
                if (e.getWheelRotation() < 0) {
                    viewScale = Math.min(viewScale * 1.1, 20.0);
                } else {
                    viewScale = Math.max(viewScale / 1.1, 0.2);
                }
                
                Point cursor = e.getPoint();
                double logicalX = (cursor.x - offsetX) / oldScale;
                double logicalY = (cursor.y - offsetY) / oldScale;
                
                offsetX = cursor.x - logicalX * viewScale;
                offsetY = cursor.y - logicalY * viewScale;
                repaint();
            }
        };
        
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        addMouseWheelListener(mouseAdapter);
    }

    private void handleToolClick(Point2D.Double p) {
        if ("dda_line".equals(currentTool) || "bresenham_line".equals(currentTool)) {
            if (firstClickPoint == null) {
                firstClickPoint = p;
            } else {
                ShapeData line = new ShapeData(false, false, currentTool, currentStrokeColor, currentFillColor);
                line.points.add(new Point2D.Double(firstClickPoint.x, firstClickPoint.y));
                line.points.add(new Point2D.Double(p.x, p.y));
                shapes.add(line);
                saveHistory();
                firstClickPoint = null;
            }
        } else if ("circle".equals(currentTool)) {
            if (firstClickPoint == null) {
                firstClickPoint = p;
            } else {
                double r = firstClickPoint.distance(p);
                if (r > 0) {
                    ShapeData circle = buildCircle(firstClickPoint.x, firstClickPoint.y, r);
                    shapes.add(circle);
                    saveHistory();
                }
                firstClickPoint = null;
            }
        } else if ("polygon".equals(currentTool)) {
            tempPoints.add(p);
            repaint();
        } else if ("eraser".equals(currentTool)) {
            if (eraseAtPoint(p)) {
                saveHistory();
            }
        } else if (seedMode) {
            seedPoint = p;
            repaint();
        }
    }

    public boolean eraseAtPoint(Point2D.Double p) {
        double tolerance = 10.0;
        for (int i = shapes.size() - 1; i >= 0; i--) {
            ShapeData shape = shapes.get(i);
            
            if (shape.closed && shape.fillable && shape.points.size() >= 3) {
                if (isPointInPolygon(p, shape.points)) {
                    shapes.remove(i);
                    repaint();
                    return true;
                }
            }
            
            for (int j = 0; j < shape.points.size(); j++) {
                Point2D.Double point = shape.points.get(j);
                if (point.distance(p) <= tolerance) {
                    shapes.remove(i);
                    repaint();
                    return true;
                }
                
                if (j < shape.points.size() - 1) {
                    Point2D.Double nextPoint = shape.points.get(j + 1);
                    if (isPointOnLine(p, point, nextPoint, tolerance)) {
                        shapes.remove(i);
                        repaint();
                        return true;
                    }
                }
            }
            
            if (shape.closed && shape.points.size() > 2) {
                Point2D.Double lastPoint = shape.points.get(shape.points.size() - 1);
                Point2D.Double firstPoint = shape.points.get(0);
                if (isPointOnLine(p, lastPoint, firstPoint, tolerance)) {
                    shapes.remove(i);
                    repaint();
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean isPointInPolygon(Point2D.Double point, List<Point2D.Double> polygon) {
        int n = polygon.size();
        boolean inside = false;
        for (int i = 0, j = n - 1; i < n; j = i++) {
            double xi = polygon.get(i).x, yi = polygon.get(i).y;
            double xj = polygon.get(j).x, yj = polygon.get(j).y;
            
            if (((yi > point.y) != (yj > point.y)) &&
                (point.x < (xj - xi) * (point.y - yi) / (yj - yi) + xi)) {
                inside = !inside;
            }
        }
        return inside;
    }

    private boolean isPointOnLine(Point2D.Double point, Point2D.Double lineStart, Point2D.Double lineEnd, double tolerance) {
        double dist = pointToLineDistance(point, lineStart, lineEnd);
        return dist <= tolerance;
    }

    private double pointToLineDistance(Point2D.Double point, Point2D.Double lineStart, Point2D.Double lineEnd) {
        double A = point.x - lineStart.x;
        double B = point.y - lineStart.y;
        double C = lineEnd.x - lineStart.x;
        double D = lineEnd.y - lineStart.y;

        double dot = A * C + B * D;
        double lenSq = C * C + D * D;
        double param = -1;

        if (lenSq != 0) {
            param = dot / lenSq;
        }

        double xx, yy;

        if (param < 0) {
            xx = lineStart.x;
            yy = lineStart.y;
        } else if (param > 1) {
            xx = lineEnd.x;
            yy = lineEnd.y;
        } else {
            xx = lineStart.x + param * C;
            yy = lineStart.y + param * D;
        }

        double dx = point.x - xx;
        double dy = point.y - yy;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private ShapeData buildCircle(double cx, double cy, double r) {
        ShapeData circle = new ShapeData(true, false, "circle", currentStrokeColor, currentFillColor);
        for (int i = 0; i < 360; i++) {
            double theta = 2.0 * Math.PI * i / 360.0;
            double x = cx + r * Math.cos(theta);
            double y = cy + r * Math.sin(theta);
            circle.points.add(new Point2D.Double(x, y));
        }
        return circle;
    }

    public void addLine(int x1, int y1, int x2, int y2, boolean useDDA) {
        ShapeData line = new ShapeData(false, false, useDDA ? "dda_line" : "bresenham_line", currentStrokeColor, currentFillColor);
        line.points.add(new Point2D.Double(x1, y1));
        line.points.add(new Point2D.Double(x2, y2));
        shapes.add(line);
        saveHistory();
        repaint();
    }

    public void addCircle(int cx, int cy, int r) {
        ShapeData circle = buildCircle(cx, cy, r);
        shapes.add(circle);
        saveHistory();
        repaint();
    }

    public void finalizePolygon() {
        if (tempPoints.size() < 3) {
            JOptionPane.showMessageDialog(null, "至少需要 3 个顶点", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        ShapeData polygon = new ShapeData(true, false, "polygon", currentStrokeColor, currentFillColor);
        polygon.points.addAll(new ArrayList<>(tempPoints));
        shapes.add(polygon);
        tempPoints.clear();
        saveHistory();
        repaint();
    }
    
    public void addPolygonPoint(int x, int y) {
        tempPoints.add(new Point2D.Double(x, y));
        repaint();
    }

    public void finalizeFreehand() {
        if (tempPoints.size() >= 3) {
            ShapeData freehand = new ShapeData(true, false, "freehand", currentStrokeColor, currentFillColor);
            freehand.points.addAll(new ArrayList<>(tempPoints));
            shapes.add(freehand);
            tempPoints.clear();
            saveHistory();
            repaint();
        }
    }

    public void undoLastFreehand() {
        if (!shapes.isEmpty()) {
            ShapeData lastShape = shapes.get(shapes.size() - 1);
            if ("freehand".equals(lastShape.type)) {
                shapes.remove(shapes.size() - 1);
                repaint();
            }
        }
    }

    public void undoLastPoint() {
        if (!tempPoints.isEmpty()) {
            tempPoints.remove(tempPoints.size() - 1);
            repaint();
        }
    }

    public void enterClipWindowMode() {
        selectingClipWindow = true;
        clipStartPoint = null;
        clipCurrentPoint = null;
        clipWindow = null;
    }

    public Rectangle2D.Double getClipWindow() {
        return clipWindow;
    }

    public void clipShapes(int algorithm) {
        if (clipWindow == null || shapes.isEmpty()) return;
        
        List<ShapeData> newShapes = new ArrayList<>();
        
        for (ShapeData shape : shapes) {
            ShapeData clipped = clipShape(shape, algorithm);
            if (clipped != null && !clipped.points.isEmpty()) {
                clipped.strokeColor = Color.RED;
                clipped.fillColor = new Color(180, 255, 120, 120);
                newShapes.add(clipped);
            }
        }
        
        if (!newShapes.isEmpty()) {
            shapes.clear();
            shapes.addAll(newShapes);
        }
        clipWindow = null;
        repaint();
    }

    private ShapeData clipShape(ShapeData shape, int algorithm) {
        if (shape.points.size() < 2) return null;
        
        ShapeData result = new ShapeData(shape.closed, shape.fillable, shape.type);
        List<LineSegment> segments = buildSegments(shape);
        
        for (LineSegment seg : segments) {
            Point2D.Double[] clipped;
            if (algorithm == 0) {
                clipped = cohenSutherlandClip(seg.start, seg.end, clipWindow);
            } else if (algorithm == 1) {
                clipped = cyrusBeckClip(seg.start, seg.end, clipWindow);
            } else {
                clipped = liangBarskyClip(seg.start, seg.end, clipWindow);
            }
            
            if (clipped != null) {
                if (result.points.isEmpty()) {
                    result.points.add(clipped[0]);
                }
                result.points.add(clipped[1]);
            }
        }
        
        if (result.points.size() >= 2) {
            return result;
        }
        return null;
    }

    private List<LineSegment> buildSegments(ShapeData shape) {
        List<LineSegment> result = new ArrayList<>();
        for (int i = 0; i < shape.points.size() - 1; i++) {
            result.add(new LineSegment(shape.points.get(i), shape.points.get(i + 1)));
        }
        if (shape.closed && shape.points.size() > 2) {
            result.add(new LineSegment(shape.points.get(shape.points.size() - 1), shape.points.get(0)));
        }
        return result;
    }

    private Point2D.Double[] cohenSutherlandClip(Point2D.Double p1, Point2D.Double p2, Rectangle2D.Double rect) {
        double xmin = rect.getMinX(), xmax = rect.getMaxX(), ymin = rect.getMinY(), ymax = rect.getMaxY();
        double x1 = p1.x, y1 = p1.y, x2 = p2.x, y2 = p2.y;
        
        int code1 = computeRegionCode(x1, y1, xmin, xmax, ymin, ymax);
        int code2 = computeRegionCode(x2, y2, xmin, xmax, ymin, ymax);
        
        while (true) {
            if ((code1 | code2) == 0) return new Point2D.Double[]{new Point2D.Double(x1, y1), new Point2D.Double(x2, y2)};
            if ((code1 & code2) != 0) return null;
            
            int outCode = code1 != 0 ? code1 : code2;
            double x = 0, y = 0;
            
            if ((outCode & 8) != 0) { x = x1 + (x2 - x1) * (ymax - y1) / (y2 - y1); y = ymax; }
            else if ((outCode & 4) != 0) { x = x1 + (x2 - x1) * (ymin - y1) / (y2 - y1); y = ymin; }
            else if ((outCode & 2) != 0) { y = y1 + (y2 - y1) * (xmax - x1) / (x2 - x1); x = xmax; }
            else if ((outCode & 1) != 0) { y = y1 + (y2 - y1) * (xmin - x1) / (x2 - x1); x = xmin; }
            
            if (outCode == code1) { x1 = x; y1 = y; code1 = computeRegionCode(x1, y1, xmin, xmax, ymin, ymax); }
            else { x2 = x; y2 = y; code2 = computeRegionCode(x2, y2, xmin, xmax, ymin, ymax); }
        }
    }

    private int computeRegionCode(double x, double y, double xmin, double xmax, double ymin, double ymax) {
        int code = 0;
        if (x < xmin) code |= 1;
        else if (x > xmax) code |= 2;
        if (y < ymin) code |= 4;
        else if (y > ymax) code |= 8;
        return code;
    }

    private Point2D.Double[] cyrusBeckClip(Point2D.Double p1, Point2D.Double p2, Rectangle2D.Double rect) {
        double xmin = rect.getMinX(), xmax = rect.getMaxX(), ymin = rect.getMinY(), ymax = rect.getMaxY();
        double dx = p2.x - p1.x, dy = p2.y - p1.y;
        double tEnter = 0.0, tLeave = 1.0;
        
        double[][] boundaries = {{-1, 0, xmin, ymin}, {1, 0, xmax, ymin}, {0, -1, xmin, ymin}, {0, 1, xmin, ymax}};
        
        for (double[] boundary : boundaries) {
            double nx = boundary[0], ny = boundary[1], px = boundary[2], py = boundary[3];
            double numerator = -((p1.x - px) * nx + (p1.y - py) * ny);
            double denominator = dx * nx + dy * ny;
            
            if (Math.abs(denominator) < 1e-9) {
                if (numerator < 0) return null;
            } else {
                double t = numerator / denominator;
                if (denominator < 0) tEnter = Math.max(tEnter, t);
                else tLeave = Math.min(tLeave, t);
            }
            
            if (tEnter > tLeave) return null;
        }
        
        return new Point2D.Double[]{
            new Point2D.Double(p1.x + tEnter * dx, p1.y + tEnter * dy),
            new Point2D.Double(p1.x + tLeave * dx, p1.y + tLeave * dy)
        };
    }

    private Point2D.Double[] liangBarskyClip(Point2D.Double p1, Point2D.Double p2, Rectangle2D.Double rect) {
        double xmin = rect.getMinX(), xmax = rect.getMaxX(), ymin = rect.getMinY(), ymax = rect.getMaxY();
        double dx = p2.x - p1.x, dy = p2.y - p1.y;
        double u1 = 0.0, u2 = 1.0;
        
        double[] p = {-dx, dx, -dy, dy};
        double[] q = {p1.x - xmin, xmax - p1.x, p1.y - ymin, ymax - p1.y};
        
        for (int i = 0; i < 4; i++) {
            if (Math.abs(p[i]) < 1e-9) {
                if (q[i] < 0) return null;
            } else {
                double r = q[i] / p[i];
                if (p[i] < 0) u1 = Math.max(u1, r);
                else u2 = Math.min(u2, r);
            }
            if (u1 > u2) return null;
        }
        
        return new Point2D.Double[]{
            new Point2D.Double(p1.x + u1 * dx, p1.y + u1 * dy),
            new Point2D.Double(p1.x + u2 * dx, p1.y + u2 * dy)
        };
    }

    public void translateShapes(double dx, double dy) {
        for (ShapeData shape : shapes) {
            for (Point2D.Double p : shape.points) {
                p.x += dx;
                p.y += dy;
            }
        }
        repaint();
    }

    public void scaleShapes(double factor) {
        if (factor <= 0) return;
        
        Point2D.Double center = getCanvasCenter();
        for (ShapeData shape : shapes) {
            for (Point2D.Double p : shape.points) {
                p.x = center.x + (p.x - center.x) * factor;
                p.y = center.y + (p.y - center.y) * factor;
            }
        }
        repaint();
    }

    public void rotateShapes(double angleDegree, boolean clockwise) {
        double rad = Math.toRadians(angleDegree);
        if (!clockwise) rad = -rad;
        
        Point2D.Double center = getCanvasCenter();
        for (ShapeData shape : shapes) {
            for (Point2D.Double p : shape.points) {
                double dx = p.x - center.x;
                double dy = p.y - center.y;
                double x = center.x + dx * Math.cos(rad) - dy * Math.sin(rad);
                double y = center.y + dx * Math.sin(rad) + dy * Math.cos(rad);
                p.x = x;
                p.y = y;
            }
        }
        repaint();
    }

    private Point2D.Double getCanvasCenter() {
        return new Point2D.Double(getWidth() / 2.0 / viewScale, getHeight() / 2.0 / viewScale);
    }

    public void clearAll() {
        shapes.clear();
        tempPoints.clear();
        selectedShapes.clear();
        currentShape = null;
        selectedShape = null;
        transformedShape = null;
        firstClickPoint = null;
        currentMousePoint = null;
        clipWindow = null;
        viewScale = 1.0;
        offsetX = 0.0;
        offsetY = 0.0;
        repaint();
    }

    public void updateSelectedShapesStrokeColor(Color color) {
        for (ShapeData shape : selectedShapes) {
            shape.strokeColor = color;
        }
        repaint();
    }

    public void updateSelectedShapesFillColor(Color color, int algorithm) {
        for (ShapeData shape : selectedShapes) {
            if (shape.fillable) {
                shape.fillColor = color;
            }
        }
        repaint();
    }

    public void fillSelectedShapesByBoundaryFlag(Color borderColor, Color fillColor) {
        for (ShapeData shape : selectedShapes) {
            if (shape.points.size() >= 3) {
                shape.fillColor = fillColor;
                shape.fillable = true;
            }
        }
        repaint();
    }

    private Point2D.Double seedPoint;
    private boolean seedMode = false;

    public void setSeedMode(boolean enabled) {
        this.seedMode = enabled;
        this.seedPoint = null;
        repaint();
    }

    public boolean fillSelectedShapesByScanlineSeed(Color borderColor, Color fillColor) {
        if (selectedShapes.isEmpty()) {
            return false;
        }
        
        if (seedPoint == null) {
            for (ShapeData shape : selectedShapes) {
                if (shape.fillable && shape.points.size() >= 3) {
                    shape.fillColor = fillColor;
                }
            }
        } else {
            for (ShapeData shape : selectedShapes) {
                if (shape.points.size() >= 3) {
                    shape.fillColor = fillColor;
                    shape.fillable = true;
                }
            }
            seedPoint = null;
            seedMode = false;
        }
        repaint();
        return true;
    }

    public void setCurrentTool(String tool) {
        this.currentTool = tool;
        firstClickPoint = null;
        tempPoints.clear();
        selectingClipWindow = false;
    }

    public String getCurrentTool() {
        return currentTool;
    }

    public void setCurrentStrokeColor(Color color) {
        this.currentStrokeColor = color;
    }

    public void setCurrentFillColor(Color color) {
        this.currentFillColor = color;
    }

    public Color getCurrentStrokeColor() {
        return currentStrokeColor;
    }

    public Color getCurrentFillColor() {
        return currentFillColor;
    }

    public int getTempPointCount() {
        return tempPoints.size();
    }

    public double getScale() {
        return viewScale;
    }

    public Point2D.Double screenToLogical(Point screenPoint) {
        double x = (screenPoint.x - offsetX) / viewScale;
        double y = (screenPoint.y - offsetY) / viewScale;
        return new Point2D.Double(x, y);
    }

    private Point2D.Double logicalToScreen(Point2D.Double logicalPoint) {
        double x = logicalPoint.x * viewScale + offsetX;
        double y = logicalPoint.y * viewScale + offsetY;
        return new Point2D.Double(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(offsetX, offsetY);
        g2.scale(viewScale, viewScale);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        for (ShapeData shape : shapes) {
            drawShape(g2, shape);
        }
        
        drawPreview(g2);
        
        if (clipWindow != null || selectingClipWindow) {
            drawClipWindow(g2);
        }
        
        drawSelectionBox(g2);
        drawSelectedShapes(g2);
        
        g2.dispose();
    }

    private void drawShape(Graphics2D g2, ShapeData shape) {
        if (shape == null || shape.points.isEmpty()) return;
        
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(1.5f));
        
        if (shape.closed && shape.fillable) {
            Path2D.Double path = new Path2D.Double();
            Point2D.Double first = shape.points.get(0);
            path.moveTo(first.x, first.y);
            for (int i = 1; i < shape.points.size(); i++) {
                path.lineTo(shape.points.get(i).x, shape.points.get(i).y);
            }
            path.closePath();
            g2.setColor(shape.fillColor);
            g2.fill(path);
        }
        
        g2.setColor(shape.strokeColor);
        
        for (int i = 0; i < shape.points.size() - 1; i++) {
            drawLine(g2, shape.points.get(i), shape.points.get(i + 1));
        }
        
        if (shape.closed && shape.points.size() > 2) {
            drawLine(g2, shape.points.get(shape.points.size() - 1), shape.points.get(0));
        }
        
        g2.setStroke(oldStroke);
    }

    private void drawPreview(Graphics2D g2) {
        if ("dda_line".equals(currentTool) || "bresenham_line".equals(currentTool)) {
            if (firstClickPoint != null && currentMousePoint != null) {
                g2.setColor(Color.GRAY);
                g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, new float[]{8.0f, 6.0f}, 0.0f));
                drawLine(g2, firstClickPoint, currentMousePoint);
            }
        } else if ("circle".equals(currentTool)) {
            if (firstClickPoint != null && currentMousePoint != null) {
                double r = firstClickPoint.distance(currentMousePoint);
                ShapeData preview = buildCircle(firstClickPoint.x, firstClickPoint.y, r);
                g2.setColor(Color.GRAY);
                g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, new float[]{8.0f, 6.0f}, 0.0f));
                for (int i = 0; i < preview.points.size() - 1; i++) {
                    drawLine(g2, preview.points.get(i), preview.points.get(i + 1));
                }
            }
        } else if ("polygon".equals(currentTool)) {
            for (Point2D.Double p : tempPoints) {
                g2.setColor(Color.RED);
                g2.fill(new Ellipse2D.Double(p.x - 3, p.y - 3, 6, 6));
            }
        } else if ("freehand".equals(currentTool) && !tempPoints.isEmpty()) {
            g2.setColor(Color.GRAY);
            for (int i = 0; i < tempPoints.size() - 1; i++) {
                drawLine(g2, tempPoints.get(i), tempPoints.get(i + 1));
            }
        }
        
        if (seedMode && seedPoint != null) {
            g2.setColor(Color.RED);
            g2.fill(new Ellipse2D.Double(seedPoint.x - 5, seedPoint.y - 5, 10, 10));
            g2.setColor(Color.WHITE);
            g2.fill(new Ellipse2D.Double(seedPoint.x - 3, seedPoint.y - 3, 6, 6));
            g2.setColor(Color.RED);
            g2.draw(new Ellipse2D.Double(seedPoint.x - 5, seedPoint.y - 5, 10, 10));
        }
    }

    private void drawClipWindow(Graphics2D g2) {
        Stroke oldStroke = g2.getStroke();
        g2.setColor(new Color(30, 120, 220));
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, new float[]{8.0f, 6.0f}, 0.0f));
        
        if (clipWindow != null) {
            g2.draw(clipWindow);
        } else if (selectingClipWindow && clipStartPoint != null && clipCurrentPoint != null) {
            double minX = Math.min(clipStartPoint.x, clipCurrentPoint.x);
            double minY = Math.min(clipStartPoint.y, clipCurrentPoint.y);
            double maxX = Math.max(clipStartPoint.x, clipCurrentPoint.x);
            double maxY = Math.max(clipStartPoint.y, clipCurrentPoint.y);
            g2.draw(new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY));
        }
        
        g2.setStroke(oldStroke);
    }

    private void drawLine(Graphics2D g2, Point2D.Double start, Point2D.Double end) {
        double dx = end.x - start.x;
        double dy = end.y - start.y;
        int steps = (int) Math.max(Math.abs(dx), Math.abs(dy));
        
        if (steps == 0) {
            g2.fill(new Rectangle2D.Double(start.x - 0.5, start.y - 0.5, 1, 1));
            return;
        }
        
        double xIncrement = dx / steps;
        double yIncrement = dy / steps;
        double x = start.x;
        double y = start.y;
        
        for (int i = 0; i <= steps; i++) {
            g2.fill(new Rectangle2D.Double(Math.round(x) - 0.5, Math.round(y) - 0.5, 1, 1));
            x += xIncrement;
            y += yIncrement;
        }
    }

    private void initKeyActions() {
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) {
                    if (!selectedShapes.isEmpty()) {
                        shapes.removeAll(selectedShapes);
                        saveHistory();
                        selectedShapes.clear();
                        repaint();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    if (!selectedShapes.isEmpty()) {
                        selectedShapes.clear();
                        repaint();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_Z && e.isControlDown()) {
                    undo();
                }
            }
        });
    }

    private boolean isShapeInRect(ShapeData shape, Rectangle2D.Double rect) {
        for (Point2D.Double point : shape.points) {
            if (rect.contains(point)) {
                return true;
            }
        }
        return false;
    }

    private void drawSelectionBox(Graphics2D g2) {
        if (selectingShapes && selectStartPoint != null && selectCurrentPoint != null) {
            double minX = Math.min(selectStartPoint.x, selectCurrentPoint.x);
            double minY = Math.min(selectStartPoint.y, selectCurrentPoint.y);
            double maxX = Math.max(selectStartPoint.x, selectCurrentPoint.x);
            double maxY = Math.max(selectStartPoint.y, selectCurrentPoint.y);
            
            g2.setColor(new Color(50, 150, 255, 50));
            g2.fill(new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY));
            
            g2.setColor(new Color(50, 150, 255));
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, new float[]{8.0f, 6.0f}, 0.0f));
            g2.draw(new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY));
        }
    }

    private void drawSelectedShapes(Graphics2D g2) {
        if (selectedShapes.isEmpty()) return;
        
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(2.5f));
        
        for (ShapeData shape : selectedShapes) {
            g2.setColor(new Color(255, 100, 100));
            for (int i = 0; i < shape.points.size() - 1; i++) {
                drawLine(g2, shape.points.get(i), shape.points.get(i + 1));
            }
            if (shape.closed && shape.points.size() > 2) {
                drawLine(g2, shape.points.get(shape.points.size() - 1), shape.points.get(0));
            }
        }
        
        g2.setStroke(oldStroke);
    }

    private static class LineSegment {
        Point2D.Double start, end;
        LineSegment(Point2D.Double start, Point2D.Double end) {
            this.start = start;
            this.end = end;
        }
    }
}
