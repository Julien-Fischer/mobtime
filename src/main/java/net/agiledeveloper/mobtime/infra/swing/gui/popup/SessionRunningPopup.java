package net.agiledeveloper.mobtime.infra.swing.gui.popup;

import net.agiledeveloper.App;
import net.agiledeveloper.mobtime.domain.Ratio;
import net.agiledeveloper.mobtime.domain.notification.Notification;
import net.agiledeveloper.mobtime.domain.notification.session.SessionCloseNotification;
import net.agiledeveloper.mobtime.domain.notification.session.SessionRefreshNotification;
import net.agiledeveloper.mobtime.domain.session.FocusMode;
import net.agiledeveloper.mobtime.infra.swing.gui.Coordinate;
import net.agiledeveloper.mobtime.infra.swing.gui.GUIEvent;
import net.agiledeveloper.mobtime.infra.swing.gui.Location;
import net.agiledeveloper.mobtime.infra.swing.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static net.agiledeveloper.mobtime.infra.swing.theme.Theme.MESSAGE_INFO;
import static net.agiledeveloper.mobtime.infra.swing.theme.Theme.WINDOW_BG;

public class SessionRunningPopup extends SessionPopup {

    public static final Location DEFAULT_LOCATION = Location.NORTH;

    private static final String DEFAULT_TITLE = "MobTime";

    private MouseListener onHover;

    private JComponent mainContainer;
    private JComponent doneButton;
    private JComponent nextButton;
    private JComponent mobButtonsContainer;
    private JComponent closeButtonContainer;
    private JLabel messageLabel;
    private JLabel counterLabel;
    private Gauge gauge;

    private Coordinate currentLocation;

    private int mouseX;
    private int mouseY;


    public SessionRunningPopup(Notification notification, boolean minimized) {
        this(notification, minimized, null);
    }

    public SessionRunningPopup(Notification notification, boolean minimized, Coordinate currentLocation) {
        super(DEFAULT_TITLE);
        this.currentLocation = currentLocation;

        var session = notification.session();
        init(notification);
        if (!(notification instanceof SessionCloseNotification)) {
            setFocusMode(session.focusMode());
        }
        if (minimized) {
            minimize();
        }
        addComponentListener(createLocationChangeListener());
        pack();
    }


    public void updateProgress(SessionRefreshNotification notification) {
        gauge.setProgress(notification.progress());
        this.setMessage(notification);
    }

    public void setMessage(Notification notification) {
        var color = Theme.of(notification.severity());
        style(messageLabel, notification.message(), color);
        style(counterLabel, notification.value(), color);
        gauge.setBackground(color);
        gauge.repaint();
    }

    public void setLabelForeground(Color color) {
        messageLabel.setForeground(color);
        counterLabel.setForeground(color);
    }

    public void setFocusMode(FocusMode mode) {
        switch (mode) {
            case ZEN:
                counterLabel.setVisible(false);
                setGaugeVisible(false);
                break;
            case CHILL:
                counterLabel.setVisible(false);
                setGaugeVisible(true);
                break;
            case NORMAL:
                // Fall through
            default:
                counterLabel.setVisible(true);
                setButtonsVisible(true);
                setGaugeVisible(true);
        }
        if (mode != FocusMode.NORMAL) {
            ignoreHover();
            acceptHover();
        }
    }

    public void setGaugeVisible(boolean visible) {
        gauge.setVisible(visible);
    }

    public void setButtonsVisible(boolean visible) {
        mobButtonsContainer.setVisible(visible);
    }

    public void setPosition(Location location) {
        setLocationRelativeTo(null);

        if (currentLocation != null) {
            setLocation(toPoint(currentLocation));
        } else {
            Point loc = location.relativeTo(this.getSize(), Toolkit.getDefaultToolkit().getScreenSize());
            setLocation(loc);
        }
    }

    public Coordinate getCurrentLocation() {
        return currentLocation;
    }

    public void derogate() {
        setVisible(true);
        counterLabel.setText("Prolongating driving session...");
        counterLabel.setForeground(MESSAGE_INFO);
        blink(counterLabel);
        pack();
    }


    private Point toPoint(Coordinate coordinate) {
        return new Point(coordinate.x(), coordinate.y());
    }

    private JComponent createButtonsContainer() {
        nextButton = new MobButton("Next", GUIEvent.NEXT).onClick(this::onButtonClick);
        doneButton = new MobButton("Done", GUIEvent.DONE).onClick(this::onButtonClick);
        var separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setForeground(WINDOW_BG);
        separator.setBackground(WINDOW_BG);
        return borderWrap(
                borderWrap(nextButton, separator),
                doneButton
        );
    }

    private JComponent createContainer() {
        closeButtonContainer = new GlassPanel(new BorderLayout());
        closeButtonContainer.setVisible(false);
        closeButtonContainer.add(new GlassButton("X").onClick(e -> close()));
        var notificationPanel = new GlassPanel(new BorderLayout());
        notificationPanel.add(messageLabel, BorderLayout.CENTER);
        notificationPanel.add(counterLabel, BorderLayout.EAST);
        var gaugeContainer = new GlassPanel(null);
        gaugeContainer.add(gauge);
        var container = new JPanel(new BorderLayout());
        container.setBackground(WINDOW_BG);
        container.add(notificationPanel, BorderLayout.CENTER);
        container.add(wrap(mobButtonsContainer, closeButtonContainer), BorderLayout.EAST);
        container.add(gaugeContainer, BorderLayout.SOUTH);
        return container;
    }

    private void style(JLabel label, String message, Color color) {
        label.setText(message);
        label.setForeground(color);
        label.repaint();
    }

    private void close() {
        App.logger.log("Closing popup");
    }

    private void init(Notification notification) {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setLayout(new BorderLayout());
        setResizable(false);
        gauge = new Gauge();
        messageLabel = new GlassLabel(Component.LEFT_ALIGNMENT, 20);
        counterLabel = new GlassLabel(Component.RIGHT_ALIGNMENT);
        mobButtonsContainer = createButtonsContainer();
        mainContainer = createContainer();
        add(mainContainer, BorderLayout.CENTER);
        this.setMessage(notification);
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
    }

    private MouseListener createHoverListener() {
        return new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                counterLabel.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                counterLabel.setVisible(false);
            }
        };
    }

    private ComponentListener createLocationChangeListener() {
        return new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                var component = e.getComponent();
                currentLocation = new Coordinate(component.getX(), component.getY());
            }
        };
    }

    private void ignoreHover() {
        mainContainer.removeMouseListener(onHover);
        onHover = null;
    }

    private void acceptHover() {
        onHover = createHoverListener();
        mainContainer.addMouseListener(onHover);
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
        return container;
    }


    private static class GlassLabel extends JLabel {

        public GlassLabel(float alignment) {
            this(alignment, 5);
        }

        public GlassLabel(float alignment, int marginRight) {
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, marginRight));
            setFont(new Font("Monospaced", Font.BOLD, 14));
            setAlignmentX(alignment);
        }

    }

    private class Gauge extends JPanel {

        private static final int HEIGHT = 1;

        public Gauge() {
            setBackground(MESSAGE_INFO);
            setPreferredSize(new Dimension(0, 0));
        }

        public void setProgress(Ratio ratio) {
            var width = (1 - ratio.value()) * mainContainer.getWidth();
            setSize(new Dimension((int) width, HEIGHT));
            repaint();
        }

    }

}
