package net.agiledeveloper.mobtime.infra.swing;

import net.agiledeveloper.mobtime.domain.notification.Notification;
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
    private JComponent mobButtonsContainer;
    private JComponent closeButtonContainer;
    private JLabel messageLabel;
    private JLabel valueLabel;

    private final transient Locator locator = new Locator();

    private int mouseX;
    private int mouseY;


    public SwingPopup(Notification notification, boolean minimized) {
        super(DEFAULT_TITLE);
        init(notification);
        if (minimized) {
            minimize();
        }
        pack();
    }


    public void setMessage(Notification notification, Color color) {
        messageLabel.setText(notification.message());
        messageLabel.setForeground(color.darker());
        messageLabel.repaint();
        valueLabel.setText(notification.value());
        valueLabel.setForeground(color);
        valueLabel.repaint();
    }

    public void setLabelForeground(Color color) {
        messageLabel.setForeground(color);
        valueLabel.setForeground(color);
    }

    public void setButtonsVisible(boolean visible) {
        mobButtonsContainer.setVisible(visible);
    }

    public void onClick(Consumer<GUIEvent> onClickCallback) {
        this.onClickCallback = onClickCallback;
    }

    public void setPosition(Location location) {
        setLocationRelativeTo(null);
        setLocation(locator.getLocation(this.getSize(), location));
    }




    private JLabel createLabel(float alignment) {
        return createLabel(alignment, 5);
    }

    private JLabel createLabel(float alignment, int marginRight) {
        var label = new JLabel();
        label.setOpaque(false);
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, marginRight));
        label.setFont(new Font("Monospaced", Font.BOLD, 14));
        label.setAlignmentX(alignment);
        return label;
    }

    private JComponent createButtonsContainer() {
        var button1 = new GUIButton("Done", GUIEvent.DONE);
        var button2 = new GUIButton("Next", GUIEvent.NEXT);
        return wrap(button1, button2);
    }

    private JComponent createContainer() {
        closeButtonContainer = new JPanel(new BorderLayout());
        closeButtonContainer.setVisible(false);
        closeButtonContainer.setOpaque(false);
        closeButtonContainer.add(new Button("X").onClick(e -> close()));
        var notificationPanel = new JPanel(new BorderLayout());
        notificationPanel.setOpaque(false);
        notificationPanel.add(messageLabel, BorderLayout.CENTER);
        notificationPanel.add(valueLabel, BorderLayout.EAST);
        var container = new JPanel(new BorderLayout());
        container.setOpaque(true);
        container.setBackground(WINDOW_BG);
        container.add(notificationPanel, BorderLayout.CENTER);
        container.add(wrap(mobButtonsContainer, closeButtonContainer), BorderLayout.EAST);
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
        closeButtonContainer.setVisible(true);
        mobButtonsContainer.setVisible(false);
    }


    private JPanel wrap(Component... components) {
        var wrapper = new JPanel();
        wrapper.setOpaque(false);
        for (Component component : components) {
            wrapper.add(component);
        }
        return wrapper;
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
        }

        private void dispatch(GUIEvent event) {
            if (onClickCallback != null) {
                onClickCallback.accept(event);
            }
        }

    }


    private static class Locator {

        private final Dimension anchor;

        public Locator() {
            this(Toolkit.getDefaultToolkit().getScreenSize());
        }

        public Locator(Dimension anchor) {
            this.anchor = anchor;
        }

        private Point getLocation(Dimension element, Location location) {
            return switch (location) {
                case NORTH       -> getNorthLocation(element);
                case NORTH_EAST  -> getNorthEastLocation(element);
                case EAST        -> getEastLocation(element);
                case SOUTH_EAST  -> getSouthEastLocation(element);
                case SOUTH       -> getSouthLocation(element);
                case SOUTH_WEST  -> getSouthWestLocation(element);
                case WEST        -> getWestLocation(element);
                case NORTH_WEST  -> getNorthWestLocation(element);
                case CENTER      -> getCenterLocation(element);
            };
        }

        private Point getNorthLocation(Dimension element) {
            var x = (int) (anchor.width - element.getWidth()) / 2;
            return new Point(x, 0);
        }


        private Point getNorthEastLocation(Dimension element) {
            var x = (int) (anchor.width - element.getWidth());
            return new Point(x, 0);
        }

        private Point getEastLocation(Dimension element) {
            var x = (int) (anchor.width - element.getWidth());
            var y = (int) (anchor.height - element.getHeight()) / 2;
            return new Point(x, y);
        }

        private Point getSouthEastLocation(Dimension element) {
            var x = (int) (anchor.width - element.getWidth());
            var y = (int) (anchor.height - element.getHeight());
            return new Point(x, y);
        }

        private Point getSouthLocation(Dimension element) {
            var x = (int) (anchor.width - element.getWidth()) / 2;
            var y = (int) (anchor.height - element.getHeight());
            return new Point(x, y);
        }

        private Point getSouthWestLocation(Dimension element) {
            var y = (int) (anchor.height - element.getHeight());
            return new Point(0, y);
        }

        private Point getWestLocation(Dimension element) {
            var y = (int) (anchor.height - element.getHeight()) / 2;
            return new Point(0, y);
        }

        private Point getNorthWestLocation(Dimension ignored) {
            return new Point(0, 0);
        }

        private Point getCenterLocation(Dimension element) {
            var x = (int) (anchor.width - element.getWidth()) / 2;
            var y = (int) (anchor.height - element.getHeight()) / 2;
            return new Point(x, y);
        }

    }

}
