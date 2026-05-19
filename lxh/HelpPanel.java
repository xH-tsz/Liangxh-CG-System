package lxh;

import javax.swing.*;
import java.awt.*;

public class HelpPanel extends JPanel {

    private JButton hideButton;
    private JLabel titleLabel;
    private JTextArea helpTextArea;

    public HelpPanel() {
        initPanel();
        initComponents();
        initLayout();
        showDefaultHelp();
    }

    private void initPanel() {
        setPreferredSize(new Dimension(300, 0));
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(210, 210, 210)));
        setLayout(new BorderLayout());
    }

    private void initComponents() {
        hideButton = new JButton("收起");
        hideButton.setFocusPainted(false);
        hideButton.setFont(new Font("微软雅黑", Font.PLAIN, 13));

        titleLabel = new JLabel("帮助栏");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));

        helpTextArea = new JTextArea();
        helpTextArea.setEditable(false);
        helpTextArea.setLineWrap(true);
        helpTextArea.setWrapStyleWord(true);
        helpTextArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        helpTextArea.setBackground(new Color(245, 245, 245));
        helpTextArea.setMargin(new Insets(10, 10, 10, 10));
    }

    private void initLayout() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 245, 245));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        topPanel.add(hideButton, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(helpTextArea);
        scrollPane.setBorder(null);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public JButton getHideButton() {
        return hideButton;
    }

    public void showDefaultHelp() {
        titleLabel.setText("帮助栏");
        helpTextArea.setText(
                "请选择一个功能查看对应操作说明。\n\n" +
                "当前系统支持：\n" +
                "1. DDA 绘直线\n" +
                "2. Bresenham 绘制直线\n" +
                "3. 绘制圆\n" +
                "4. 边界标志算法填充凸多边形\n" +
                "5. 扫描线种子算法填充任意封闭区域\n" +
                "6. 图形平移、放缩、旋转\n" +
                "7. 图形裁剪\n\n" +
                "通用功能：\n" +
                "1. 鼠标移动时实时显示当前坐标。\n" +
                "2. 鼠标滚轮可以缩放画布。\n" +
                "3. 点击“清空面板”可以清除当前图形。"
        );
        helpTextArea.setCaretPosition(0);
    }

    public void showDDAHelp() {
        titleLabel.setText("DDA绘直线帮助");
        helpTextArea.setText(
                "【DDA 绘直线】\n\n" +
                "一、坐标输入绘制\n" +
                "1. 在顶部输入起点 x、起点 y、终点 x、终点 y。\n" +
                "2. 点击“画直线”按钮。\n" +
                "3. 系统使用 DDA 算法绘制直线。\n\n" +
                "二、鼠标交互绘制\n" +
                "1. 在画布中单击一次，确定起点。\n" +
                "2. 移动鼠标时，可看到预览线段。\n" +
                "3. 再单击一次，确定终点并完成绘制。\n\n" +
                "三、辅助功能\n" +
                "1. 鼠标移动时实时显示当前坐标。\n" +
                "2. 鼠标滚轮可对画布进行放大和缩小。\n" +
                "3. 点击“清空面板”可清除全部图形并恢复默认缩放。"
        );
        helpTextArea.setCaretPosition(0);
    }

    public void showBresenhamHelp() {
        titleLabel.setText("Bresenham绘制直线帮助");
        helpTextArea.setText(
                "【Bresenham 绘制直线】\n\n" +
                "一、坐标输入绘制\n" +
                "1. 在顶部输入起点 x、起点 y、终点 x、终点 y。\n" +
                "2. 点击“画直线”按钮。\n" +
                "3. 系统使用 Bresenham 算法绘制直线。\n\n" +
                "二、鼠标交互绘制\n" +
                "1. 在画布中单击一次，确定起点。\n" +
                "2. 移动鼠标时，可看到预览线段。\n" +
                "3. 再单击一次，确定终点并完成绘制。\n\n" +
                "三、辅助功能\n" +
                "1. 鼠标移动时实时显示当前坐标。\n" +
                "2. 鼠标滚轮可对画布进行放大和缩小。\n" +
                "3. 点击“清空面板”可清除全部图形并恢复默认缩放。"
        );
        helpTextArea.setCaretPosition(0);
    }

    public void showCircleHelp() {
        titleLabel.setText("绘制圆帮助");
        helpTextArea.setText(
                "【绘制圆】\n\n" +
                "一、坐标输入绘制\n" +
                "1. 在顶部输入圆心 x、圆心 y、半径 r。\n" +
                "2. 点击“画圆”按钮。\n" +
                "3. 系统根据圆的对称性绘制整个圆。\n\n" +
                "二、鼠标交互绘制\n" +
                "1. 在画布中单击一次，确定圆心。\n" +
                "2. 移动鼠标时，可看到预览圆。\n" +
                "3. 再单击一次，确定半径并完成绘制。\n\n" +
                "三、辅助功能\n" +
                "1. 鼠标移动时实时显示当前坐标。\n" +
                "2. 鼠标滚轮可对画布进行放大和缩小。\n" +
                "3. 点击“清空面板”可清除全部图形并恢复默认缩放。"
        );
        helpTextArea.setCaretPosition(0);
    }

    public void showBoundaryFillHelp() {
        titleLabel.setText("边界标志算法填充帮助");
        helpTextArea.setText(
                "【边界标志算法填充凸多边形】\n\n" +
                "一、点选顶点\n" +
                "1. 可以直接在画布中用鼠标左键连续点击多个点。\n" +
                "2. 也可以在顶部输入 x、y 坐标后点击“添加坐标点”。\n" +
                "3. 在点击“绘制凸多边形”之前，系统只显示点。\n\n" +
                "二、颜色选择\n" +
                "1. 可以选择边界颜色。\n" +
                "2. 可以选择填充颜色。\n\n" +
                "三、绘制和填充\n" +
                "1. 至少选择 3 个点。\n" +
                "2. 点击“绘制凸多边形”。\n" +
                "3. 点击“边界标志算法填充”。\n\n" +
                "四、辅助功能\n" +
                "1. 鼠标移动时实时显示当前坐标。\n" +
                "2. 鼠标滚轮可以缩放画布。\n" +
                "3. 可以撤销最后一个点。\n" +
                "4. 可以清空面板。"
        );
        helpTextArea.setCaretPosition(0);
    }

    public void showSeedFillHelp() {
        titleLabel.setText("扫描线种子填充帮助");
        helpTextArea.setText(
                "【扫描线种子填充】\n\n" +
                "一、绘制模式\n" +
                "本模块支持三种封闭区域绘制方式：\n" +
                "1. 任意多边形模式。\n" +
                "2. 圆模式。\n" +
                "3. 自由手绘模式。\n\n" +
                "二、填充步骤\n" +
                "1. 先绘制封闭边界。\n" +
                "2. 点击“选择种子点”。\n" +
                "3. 在封闭区域内部点击一个种子点。\n" +
                "4. 点击“扫描线种子填充”。\n\n" +
                "三、辅助功能\n" +
                "1. 可以选择边界颜色和填充颜色。\n" +
                "2. 可以撤销。\n" +
                "3. 可以清空面板。\n" +
                "4. 鼠标滚轮可以缩放画布。"
        );
        helpTextArea.setCaretPosition(0);
    }

    public void showTransformHelp() {
        titleLabel.setText("图形平移放缩旋转帮助");
        helpTextArea.setText(
                "【图形平移、放缩、旋转】\n\n" +
                "一、绘制图形\n" +
                "1. 在顶部下拉框中选择绘制类型。\n" +
                "2. 支持 DDA 绘制直线、绘制圆、绘制任意多边形、绘制任意形状的图形。\n" +
                "3. 直线、圆、多边形支持坐标输入，也支持鼠标点选。\n" +
                "4. 任意形状图形只支持鼠标手绘。\n\n" +
                "二、变换操作\n" +
                "1. 支持平移操作。\n" +
                "2. 支持放缩操作。\n" +
                "3. 支持旋转操作。\n" +
                "4. 手动模式下输入数据后点击按钮执行。\n" +
                "5. 鼠标模式下按住 Ctrl 和鼠标左键拖动。"
        );
        helpTextArea.setCaretPosition(0);
    }

    public void showCuttingHelp() {
        titleLabel.setText("图形裁剪帮助");
        helpTextArea.setText(
                "【图形裁剪】\n\n" +
                "一、绘制图形\n" +
                "1. 在顶部下拉框中选择绘制类型。\n" +
                "2. 支持 DDA 绘制直线、绘制圆、绘制任意多边形、绘制任意形状的图形。\n" +
                "3. 直线、圆、多边形支持坐标输入和鼠标点选。\n" +
                "4. 任意形状图形支持鼠标手绘。\n\n" +
                "二、选择裁剪区域\n" +
                "1. 先绘制一个图形。\n" +
                "2. 点击“选择裁剪区域”。\n" +
                "3. 在画布中按住鼠标左键拖动。\n" +
                "4. 松开鼠标后生成矩形裁剪窗口。\n\n" +
                "三、裁剪算法\n" +
                "本模块提供三种裁剪算法按钮：\n" +
                "1. Sutherland-Cohen 裁剪。\n" +
                "2. Cyrus-Beck 裁剪。\n" +
                "3. 梁友栋算法裁剪。\n\n" +
                "四、裁剪效果\n" +
                "1. 对直线，裁剪后只显示窗口内部线段。\n" +
                "2. 对圆、多边形和任意形状，裁剪后只显示裁剪窗口内部的边界和填充区域。\n" +
                "3. 可以设置原图颜色、原图填充颜色、裁剪后颜色和裁剪后填充颜色。\n\n" +
                "五、辅助功能\n" +
                "1. 鼠标移动时实时显示当前坐标。\n" +
                "2. 鼠标滚轮可以缩放画布。\n" +
                "3. 点击“清空面板”可以清除当前图形和裁剪结果。"
        );
        helpTextArea.setCaretPosition(0);
    }
}