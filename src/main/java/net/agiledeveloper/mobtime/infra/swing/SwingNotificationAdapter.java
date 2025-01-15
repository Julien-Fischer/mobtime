package net.agiledeveloper.mobtime.infra.swing;

import net.agiledeveloper.mobtime.domain.notification.Notification;
import net.agiledeveloper.mobtime.domain.notification.session.SessionCloseNotification;
import net.agiledeveloper.mobtime.domain.notification.session.SessionOpenNotification;
import net.agiledeveloper.mobtime.domain.notification.session.SessionRefreshNotification;
import net.agiledeveloper.mobtime.domain.notification.session.SessionShutdownNotification;
import net.agiledeveloper.mobtime.domain.ports.api.SessionPort;
import net.agiledeveloper.mobtime.domain.ports.spi.NotificationPort;

import javax.swing.*;
import java.awt.*;

public class SwingNotificationAdapter implements NotificationPort {

    private final SessionPort mobPort;

    private SwingPopup currentFrame;
    private Color currentColor;


    public SwingNotificationAdapter(SessionPort mobPort) {
        this.mobPort = mobPort;
    }


    @Override
    public void send(Notification notification) {
        if (notification instanceof SessionOpenNotification) {
            currentColor = Color.YELLOW;
            showPopup(notification);
            notifySessionStart(notification);
        } else if (notification instanceof SessionRefreshNotification) {
            currentColor = Color.GREEN;
            displayMessage(notification);
        } else if (notification instanceof SessionCloseNotification) {
            currentColor = Color.MAGENTA;
            notifySessionEnd(notification);
        } else if (notification instanceof SessionShutdownNotification) {
            currentColor = Color.MAGENTA;
            shutdown(notification);
        }
    }

    private void showPopup(Notification notification) {
        createPopupFor(notification);
    }

    private void notifySessionEnd(Notification notification) {
        SwingUtilities.invokeLater(() -> {
            currentFrame.dispose();
            currentFrame = new SwingPopup(notification);
            currentFrame.setLabelForeground(currentColor);
            currentFrame.setVisible(true);
            currentFrame.pack();
            currentFrame.onClick(this::onGuiEvent);
            setWindowLocation(currentFrame);
        });
    }

    private void notifySessionStart(Notification notification) {
        SwingUtilities.invokeLater(() -> {
            if (currentFrame == null) {
                createPopupFor(notification);
            } else {
                currentFrame.setMessage(notification, currentColor);
            }
        });
    }

    private void displayMessage(Notification notification) {
        SwingUtilities.invokeLater(() ->
                currentFrame.setMessage(notification, currentColor)
        );
    }

    private void shutdown(Notification notification) {
        displayMessage(notification);
        currentFrame.setButtonsVisible(false);
    }

    private void createPopupFor(Notification notification) {
        currentFrame = new SwingPopup(notification);
        currentFrame.onClick(this::onGuiEvent);
        currentFrame.setVisible(true);
        setWindowLocation(currentFrame);
        currentFrame.setFocusableWindowState(false);
    }

    private void onGuiEvent(GUIEvent event) {
        switch (event) {
            case NEXT:
                mobPort.next();
                break;
            case DONE:
                mobPort.done();
                break;
            default:
                throw new UnsupportedOperationException("Unsupported UI event: " + event);
        }
    }

    private static void setWindowLocation(JFrame frame) {
        frame.setLocationRelativeTo(null);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - frame.getWidth()) / 2;
        int y = 0;
        frame.setLocation(x, y);
    }

}
