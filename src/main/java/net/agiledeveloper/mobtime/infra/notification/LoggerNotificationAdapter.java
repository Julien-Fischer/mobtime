package net.agiledeveloper.mobtime.infra.notification;

import net.agiledeveloper.mobtime.domain.notification.Notification;
import net.agiledeveloper.mobtime.domain.notification.session.SessionRefreshNotification;
import net.agiledeveloper.mobtime.domain.ports.spi.NotificationPort;
import net.agiledeveloper.mobtime.utils.AppLogger;

import static net.agiledeveloper.mobtime.utils.TimeFormatter.formatDuration;

public class LoggerNotificationAdapter implements NotificationPort {

    private final AppLogger logger;


    public LoggerNotificationAdapter(AppLogger logger) {
        this.logger = logger;
    }


    @Override
    public void handleShutdownNotification(Notification notification) {
        logger.logSeparator();
        logger.log(notification.message() + "!", notification.value());
    }

    @Override
    public void handleCloseNotification(Notification notification) {
        logger.logSeparator();
        logger.log(notification.message(), "Use mob next to switch driver or mob done to end the mob session");
    }

    @Override
    public void handleRefreshNotification(SessionRefreshNotification notification) {
        logger.log("  Session ending in " + notification.value());
    }

    @Override
    public void handleStartNotification(Notification notification) {
        logger.log("  Now driving ");
    }

    @Override
    public void handleOpenNotification(Notification notification) {
        var session = notification.session();
        var durationString = formatDuration(session.initialDuration());
        logger.logSeparator();
        logger.log("Opening mob session (duration = %s, id = %s)".formatted(durationString, session.id()));
    }

}
