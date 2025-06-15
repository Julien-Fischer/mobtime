package net.agiledeveloper.mobtime.domain.ports.spi;

import net.agiledeveloper.mobtime.domain.notification.Notification;
import net.agiledeveloper.mobtime.domain.notification.session.*;

public interface NotificationPort {

    void handleShutdownNotification(Notification notification);

    void handleCloseNotification(Notification notification);

    void handleRefreshNotification(SessionRefreshNotification notification);

    void handleStartNotification(Notification notification);

    void handleOpenNotification(Notification notification);


    default void send(Notification notification) {
        switch (notification) {
            case SessionOpenNotification ignored                -> handleOpenNotification(notification);
            case SessionStartNotification ignored               -> handleStartNotification(notification);
            case SessionRefreshNotification refreshNotification -> handleRefreshNotification(refreshNotification);
            case SessionCloseNotification ignored               -> handleCloseNotification(notification);
            case SessionShutdownNotification ignored            -> handleShutdownNotification(notification);
            case null, default -> throw new UnsupportedOperationException("Unknown notification type: " + notification);
        }
    }

}
