package net.agiledeveloper.mobtime.infra.notification;

import net.agiledeveloper.mobtime.domain.notification.Notification;
import net.agiledeveloper.mobtime.domain.notification.session.SessionRefreshNotification;
import net.agiledeveloper.mobtime.domain.ports.spi.NotificationPort;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class CompositeNotificationAdapter implements NotificationPort {

    private final Set<NotificationPort> notificationPorts;


    public CompositeNotificationAdapter(NotificationPort... notificationPorts) {
        this.notificationPorts = stream(notificationPorts).collect(Collectors.toSet());
    }


    @Override
    public void handleShutdownNotification(Notification notification) {
        forEach(port -> port.handleShutdownNotification(notification));
    }

    @Override
    public void handleCloseNotification(Notification notification) {
        forEach(port -> port.handleCloseNotification(notification));
    }

    @Override
    public void handleRefreshNotification(SessionRefreshNotification notification) {
        forEach(port -> port.handleRefreshNotification(notification));
    }

    @Override
    public void handleStartNotification(Notification notification) {
        forEach(port -> port.handleStartNotification(notification));
    }

    @Override
    public void handleOpenNotification(Notification notification) {
        forEach(port -> port.handleOpenNotification(notification));
    }


    private void forEach(Consumer<NotificationPort> action) {
        notificationPorts.forEach(action);
    }

}
