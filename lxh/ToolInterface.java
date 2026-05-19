package lxh;

import javax.swing.*;

public interface ToolInterface {
    String getToolName();
    String getToolId();
    JPanel getParameterPanel();
    void activate(DrawingCanvas canvas);
    void deactivate();
}