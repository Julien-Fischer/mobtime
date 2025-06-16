package net.agiledeveloper.mobtime.domain.ports.spi;

import net.agiledeveloper.mobtime.domain.notification.Notification;
import net.agiledeveloper.mobtime.domain.notification.session.*;

public interface NotificationPort {

    void handleOpenNotification(SessionOpenNotification notification);

    void handleStartNotification(SessionStartNotification notification);

    void handleRefreshNotification(SessionRefreshNotification notification);

    void handleCloseNotification(SessionCloseNotification notification);

    void handleShutdownNotification(SessionShutdownNotification notification);


    default void send(Notification notification) {
        switch (notification) {
            case SessionOpenNotification sessionOpenNotification          -> handleOpenNotification(sessionOpenNotification);
            case SessionStartNotification sessionStartNotification        -> handleStartNotification(sessionStartNotification);
            case SessionRefreshNotification refreshNotification           -> handleRefreshNotification(refreshNotification);
            case SessionCloseNotification sessionCloseNotification        -> handleCloseNotification(sessionCloseNotification);
            case SessionShutdownNotification sessionShutdownNotification  -> handleShutdownNotification(sessionShutdownNotification);
            case null, default -> throw new UnsupportedOperationException("Unknown notification type: " + notification);
        }
    }

}
