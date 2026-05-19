package lxh;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ToolManager {

    private static final Map<String, Supplier<ToolInterface>> toolRegistry = new HashMap<>();
    private static ToolInterface currentTool;
    private static DrawingCanvas currentCanvas;

    static {
        registerTool("dda_line", DDADrawTool::new);
        registerTool("bresenham_line", BresenhamDrawTool::new);
        registerTool("circle", CircleDrawTool::new);
        registerTool("polygon", PolygonDrawTool::new);
        registerTool("freehand", FreehandDrawTool::new);
        registerTool("translate", TranslateTool::new);
        registerTool("rotate", RotateTool::new);
        registerTool("scale", ScaleTool::new);
        registerTool("clip", ClipTool::new);
        registerTool("eraser", EraserTool::new);
        registerTool("color_edit", ColorEditTool::new);
        registerTool("boundary_fill", BoundaryFillTool::new);
        registerTool("scanline_fill", ScanlineSeedFillTool::new);
    }

    private static void registerTool(String id, Supplier<ToolInterface> supplier) {
        toolRegistry.put(id, supplier);
    }

    public static ToolInterface createTool(String id) {
        Supplier<ToolInterface> supplier = toolRegistry.get(id);
        return supplier != null ? supplier.get() : null;
    }

    public static void setCurrentTool(ToolInterface tool) {
        if (currentTool != null) {
            currentTool.deactivate();
        }
        currentTool = tool;
        if (currentTool != null && currentCanvas != null) {
            currentTool.activate(currentCanvas);
        }
    }

    public static ToolInterface getCurrentTool() {
        return currentTool;
    }

    public static void setCanvas(DrawingCanvas canvas) {
        currentCanvas = canvas;
        if (currentTool != null) {
            currentTool.activate(canvas);
        }
    }

    public static DrawingCanvas getCanvas() {
        return currentCanvas;
    }

    public static boolean hasTool(String id) {
        return toolRegistry.containsKey(id);
    }
}
