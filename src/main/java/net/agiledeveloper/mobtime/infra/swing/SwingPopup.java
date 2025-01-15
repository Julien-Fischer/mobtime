package net.agiledeveloper.mobtime.infra.swing;

import net.agiledeveloper.mobtime.domain.notification.Notification;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class SwingPopup extends JFrame {

    public static final Color DEFAUlT_COLOR = Color.GREEN;

    private static final String DEFAULT_TITLE = "MobTime";

    private transient Consumer<GUIEvent> onClickCallback;
    private JLabel messageLabel;
    private JLabel valueLabel;
    private JComponent buttonsContainer;


    public SwingPopup(Notification notification) {
        this(DEFAULT_TITLE, notification);
    }

    public SwingPopup(String title, Notification notification) {
        super(title);
        init(notification);
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
        buttonsContainer.setVisible(visible);
    }

    public void onClick(Consumer<GUIEvent> onClickCallback) {
        this.onClickCallback = onClickCallback;
    }


    private JLabel createLabel(float alignment) {
        return createLabel(alignment, 5);
    }

    private JLabel createLabel(float alignment, int marginRight) {
        var label = new JLabel();
        label.setOpaque(false);
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, marginRight));
        label.setFont(new Font("Monospaced", Font.BOLD, 14));
        label.setAlignmentX(alignment);
        return label;
    }

    private JComponent createButtonsContainer() {
        var button1 = new Button("Done", GUIEvent.DONE);
        var button2 = new Button("Next", GUIEvent.NEXT);
        return wrap(button1, button2);
    }

    private JComponent createContainer() {
        var container = new JPanel(new BorderLayout());
        var notificationPanel = new JPanel(new BorderLayout());
        notificationPanel.setOpaque(false);
        notificationPanel.add(messageLabel, BorderLayout.CENTER);
        notificationPanel.add(valueLabel, BorderLayout.EAST);
        container.setOpaque(true);
        container.setBackground(Color.BLACK);
        container.add(notificationPanel, BorderLayout.CENTER);
        container.add(buttonsContainer, BorderLayout.EAST);
        return container;
    }

    private void init(Notification notification) {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setLayout(new BorderLayout());
        setResizable(false);
        messageLabel = createLabel(Component.LEFT_ALIGNMENT, 20);
        valueLabel = createLabel(Component.RIGHT_ALIGNMENT);
        buttonsContainer = createButtonsContainer();
        add(createContainer(), BorderLayout.CENTER);
        this.setMessage(notification, DEFAUlT_COLOR);
        pack();
    }


    private JPanel wrap(Component... components) {
        var wrapper = new JPanel();
        wrapper.setOpaque(false);
        for (Component component : components) {
            wrapper.add(component);
        }
        return wrapper;
    }

    private class Button extends JButton {

        public Button(String label, GUIEvent event) {
            super(label);
            setBackground(new Color(80, 80, 80));
            setForeground(new Color(200, 200, 200));
            addActionListener(e -> dispatch(event));
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }

        private void dispatch(GUIEvent event) {
            if (onClickCallback != null) {
                onClickCallback.accept(event);
            }
        }

    }

}
