package net.agiledeveloper.mobtime.infra.swing;

import net.agiledeveloper.mobtime.domain.notification.Notification;
import net.agiledeveloper.mobtime.domain.notification.session.SessionRefreshNotification;
import net.agiledeveloper.mobtime.domain.session.FocusMode;
import net.agiledeveloper.mobtime.utils.AppLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import static net.agiledeveloper.mobtime.infra.swing.Palette.*;

public class SwingPopup extends JFrame {

    public static final Color DEFAUlT_COLOR = Color.GREEN;
    public static final Location DEFAULT_LOCATION = Location.NORTH;

    private static final String DEFAULT_TITLE = "MobTime";

    private transient Consumer<GUIEvent> onClickCallback;
    private JComponent mainContainer;
    private JComponent doneButton;
    private JComponent nextButton;
    private JComponent mobButtonsContainer;
    private JComponent closeButtonContainer;
    private JLabel messageLabel;
    private JLabel valueLabel;
    private Gauge gauge;

    private int mouseX;
    private int mouseY;


    public SwingPopup(Notification notification, boolean minimized) {
        super(DEFAULT_TITLE);
        var session = notification.session();
        init(notification);
        setFocusMode(session.focusMode());
        if (minimized) {
            minimize();
        }
        pack();
    }

    public void updateProgress(SessionRefreshNotification notification, Color color) {
        gauge.setProgress(notification.progress());
        this.setMessage(notification, color);
    }

    public void setMessage(Notification notification, Color color) {
        messageLabel.setText(notification.message());
        messageLabel.setForeground(color.darker());
        messageLabel.repaint();
        valueLabel.setText(notification.value());
        valueLabel.setForeground(color);
        valueLabel.repaint();
        gauge.setBackground(color);
        gauge.repaint();
    }

    public void setLabelForeground(Color color) {
        messageLabel.setForeground(color);
        valueLabel.setForeground(color);
    }

    public void setFocusMode(FocusMode mode) {
        switch (mode) {
            case ZEN:
                doneButton.setVisible(false);
                valueLabel.setVisible(false);
                setGaugeVisible(false);
                break;
            case CHILL:
                doneButton.setVisible(false);
                valueLabel.setVisible(false);
                setGaugeVisible(true);
                break;
            case NORMAL:
                // Fall through
            default:
                valueLabel.setVisible(true);
                setButtonsVisible(true);
                setGaugeVisible(true);
        }
    }

    public void setGaugeVisible(boolean visible) {
        gauge.setVisible(visible);
    }

    public void setButtonsVisible(boolean visible) {
        mobButtonsContainer.setVisible(visible);
    }

    public void onClick(Consumer<GUIEvent> onClickCallback) {
        this.onClickCallback = onClickCallback;
    }

    public void setPosition(Location location) {
        setLocationRelativeTo(null);
        Point loc = location.relativeTo(this.getSize(), Toolkit.getDefaultToolkit().getScreenSize());
        setLocation(loc);
    }


    private JLabel createLabel(float alignment) {
        return createLabel(alignment, 5);
    }

    private JLabel createLabel(float alignment, int marginRight) {
        var label = new JLabel();
        label.setOpaque(false);
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, marginRight));
        label.setFont(new Font("Monospaced", Font.BOLD, 14));
        label.setAlignmentX(alignment);
        return label;
    }

    private JComponent createButtonsContainer() {
        doneButton = new GUIButton("Done", GUIEvent.DONE);
        nextButton = new GUIButton("Next", GUIEvent.NEXT);
        var separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setForeground(WINDOW_BG);
        separator.setBackground(WINDOW_BG);
        var container = borderWrap(
                borderWrap(doneButton, separator),
                nextButton
        );
        container.setBackground(Color.RED);
        return container;
    }

    private JComponent createContainer() {
        closeButtonContainer = new GlassPanel(new BorderLayout());
        closeButtonContainer.setVisible(false);
        closeButtonContainer.add(new Button("X").onClick(e -> close()));
        var notificationPanel = new GlassPanel(new BorderLayout());
        notificationPanel.add(messageLabel, BorderLayout.CENTER);
        notificationPanel.add(valueLabel, BorderLayout.EAST);
        var gaugeContainer = new GlassPanel(null);
        gaugeContainer.add(gauge);
        var container = new JPanel(new BorderLayout());
        container.setBackground(WINDOW_BG);
        container.add(notificationPanel, BorderLayout.CENTER);
        container.add(wrap(mobButtonsContainer, closeButtonContainer), BorderLayout.EAST);
        container.add(gaugeContainer, BorderLayout.SOUTH);
        return container;
    }

    private void close() {
        AppLogger.log("Closing popup");
        System.exit(0);
    }

    private void init(Notification notification) {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setLayout(new BorderLayout());
        setResizable(false);
        gauge = new Gauge();
        messageLabel = createLabel(Component.LEFT_ALIGNMENT, 20);
        valueLabel = createLabel(Component.RIGHT_ALIGNMENT);
        mobButtonsContainer = createButtonsContainer();
        mainContainer = createContainer();
        add(mainContainer, BorderLayout.CENTER);
        this.setMessage(notification, DEFAUlT_COLOR);
    }

    private void minimize() {
        setUndecorated(true);
        mainContainer.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
        mainContainer.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getXOnScreen();
                int y = e.getYOnScreen();
                setLocation(x - mouseX, y - mouseY);
            }
        });
        closeButtonContainer.setVisible(false);
        doneButton.setVisible(false);
    }


    private JPanel wrap(Component... components) {
        var wrapper = new GlassPanel();
        for (Component component : components) {
            wrapper.add(component);
        }
        return wrapper;
    }

    private JComponent borderWrap(JComponent center, JComponent east) {
        var container = new GlassPanel(new BorderLayout());
        container.add(center, BorderLayout.CENTER);
        container.add(east, BorderLayout.EAST);
        container.setBackground(Color.RED);
        return container;
    }

    private static class GlassPanel extends JPanel {

        public GlassPanel() {
            this(new FlowLayout(FlowLayout.LEFT));
        }

        public GlassPanel(LayoutManager layout) {
            super(layout);
            this.setOpaque(false);
        }

    }

    private static class Button extends JButton {

        public Button(String label) {
            super(label);
            setBackground(BUTTON_BG);
            setForeground(BUTTON_FG);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }

        public Button onClick(ActionListener listener) {
            addActionListener(listener);
            return this;
        }

    }

    private class GUIButton extends Button {

        public GUIButton(String label, GUIEvent event) {
            super(label);
            addActionListener(e -> dispatch(event));
            setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        }

        private void dispatch(GUIEvent event) {
            if (onClickCallback != null) {
                onClickCallback.accept(event);
            }
            close();
        }

    }

    private class Gauge extends JPanel {

        private static final int HEIGHT = 1;

        public Gauge() {
            setBackground(MESSAGE_INFO);
            setPreferredSize(new Dimension(0, 0));
        }

        public void setProgress(double ratio) {
            var width = (1 - ratio) * mainContainer.getWidth();
            setSize(new Dimension((int) width, HEIGHT));
            repaint();
        }

    }

}
