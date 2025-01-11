package net.agiledeveloper.mobtime.infra;

import net.agiledeveloper.mobtime.domain.Notification;
import net.agiledeveloper.mobtime.domain.ports.spi.NotificationPort;

import javax.swing.*;
import java.awt.*;

public class SwingNotificationAdapter implements NotificationPort {

    @Override
    public void send(Notification notification) {
        SwingUtilities.invokeLater(() -> {
            var frame = WindowFactory.createWindow(notification.message());
            setWindowLocation(frame);
            frame.setVisible(true);
        });
    }

    private static void setWindowLocation(JFrame frame) {
        frame.setLocationRelativeTo(null);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - frame.getWidth()) / 2;
        int y = 0;
        frame.setLocation(x, y);
    }


}
