package mobtime.infra;

import javax.swing.*;
import java.awt.*;

public class WindowFactory {

    private WindowFactory() {}

    private static final String DEFAULT_TITLE = "MobTime";

    public static JFrame createWindow(String message) {
        return createWindow(message, DEFAULT_TITLE);
    }

    public static JFrame createWindow(String message, String title) {
        var frame = new JFrame(title);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.setSize(400, 60);
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);
        JComponent frameContent = createContent(message);
        frame.add(frameContent, SwingConstants.CENTER);
        frame.pack();
        return frame;
    }

    private static JComponent createContent(String message) {
        var label = new JLabel(message, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(Color.BLACK);
        label.setForeground(Color.GREEN);
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        label.setFont(new Font("Monospaced", Font.BOLD, 14));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

}
