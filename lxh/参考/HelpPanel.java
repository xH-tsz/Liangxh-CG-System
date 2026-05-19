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
                "3. 绘制圆（基于圆弧/对称思想生成整个圆）\n" +
                "4. 边界标志算法填充凸多边形\n" +
                "5. 扫描线种子算法填充任意封闭区域\n\n" +
                "通用功能：\n" +
                "- 鼠标移动时实时显示当前坐标\n" +
                "- 鼠标滚轮可缩放画布\n" +
                "- 可点击“清空面板”清除当前图形"
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
                "三、显示效果\n" +
                "1. 起点用红色标记。\n" +
                "2. 终点用蓝色标记。\n" +
                "3. 会显示起点和终点坐标文字。\n\n" +
                "四、辅助功能\n" +
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
                "三、显示效果\n" +
                "1. 起点用红色标记。\n" +
                "2. 终点用蓝色标记。\n" +
                "3. 会显示起点和终点坐标文字。\n\n" +
                "四、辅助功能\n" +
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
                "本模块的思想是：先按圆弧/局部点生成思想进行递推，再利用圆的对称性补全整个圆。\n\n" +
                "一、坐标输入绘制\n" +
                "1. 在顶部输入圆心 x、圆心 y、半径 r。\n" +
                "2. 点击“画圆”按钮。\n" +
                "3. 系统根据圆的对称性绘制整个圆。\n\n" +
                "二、鼠标交互绘制\n" +
                "1. 在画布中单击一次，确定圆心。\n" +
                "2. 移动鼠标时，可看到预览圆。\n" +
                "3. 再单击一次，确定半径并完成绘制。\n\n" +
                "三、显示效果\n" +
                "1. 圆心用红色标记。\n" +
                "2. 半径参考点用蓝色标记。\n" +
                "3. 会显示圆心坐标和半径信息。\n\n" +
                "四、辅助功能\n" +
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
                "3. 在点击“绘制凸多边形”之前，系统只显示点，不会实时连线。\n\n" +
                "二、颜色选择\n" +
                "1. 可先选择“边界颜色”。\n" +
                "2. 再选择“填充颜色”。\n" +
                "3. 两种颜色分别用于边界绘制和内部填充。\n\n" +
                "三、绘制与填充\n" +
                "1. 点击“绘制凸多边形”，系统按照点击/输入顺序连接所有点并自动闭合。\n" +
                "2. 若当前点集按顺序连接后不是凸多边形，系统会给出提示。\n" +
                "3. 点击“边界标志算法填充”，系统使用填充颜色对内部区域进行填充。\n\n" +
                "四、辅助功能\n" +
                "1. 可撤销最后一个点。\n" +
                "2. 可清空面板重新选择点。\n" +
                "3. 鼠标滚轮支持缩放查看填充细节。\n" +
                "4. 鼠标移动时实时显示当前逻辑坐标。"
        );
        helpTextArea.setCaretPosition(0);
    }

    public void showSeedFillHelp() {
        titleLabel.setText("扫描线种子填充帮助");
        helpTextArea.setText(
                "【扫描线种子算法填充任意封闭区域】\n\n" +
                "本模块支持三种封闭边界来源：\n" +
                "1. 任意多边形\n" +
                "2. 圆\n" +
                "3. 自由手绘封闭图形\n\n" +
                "一、任意多边形模式\n" +
                "1. 可鼠标点击添加多个点，也可输入坐标后点击“添加坐标点”。\n" +
                "2. 点击“绘制封闭边界”后，系统按顺序连接并闭合。\n\n" +
                "二、圆模式\n" +
                "1. 可输入圆心和半径后点击“绘制封闭边界”。\n" +
                "2. 也可在画布中点击两次，第一次确定圆心，第二次确定半径。\n\n" +
                "三、自由手绘模式\n" +
                "1. 按住鼠标左键拖动进行自由手绘。\n" +
                "2. 松开鼠标后，系统会自动首尾闭合。\n" +
                "3. 点击“绘制封闭边界”确认边界。\n\n" +
                "四、种子填充\n" +
                "1. 点击“选择种子点”。\n" +
                "2. 在封闭区域内部点击一个点作为种子点。\n" +
                "3. 点击“扫描线种子填充”完成填充。\n\n" +
                "五、辅助功能\n" +
                "1. 可选择边界颜色与填充颜色。\n" +
                "2. 鼠标滚轮可缩放查看效果。\n" +
                "3. 可撤销、清空并重新绘制。"
        );
        helpTextArea.setCaretPosition(0);
    }
}