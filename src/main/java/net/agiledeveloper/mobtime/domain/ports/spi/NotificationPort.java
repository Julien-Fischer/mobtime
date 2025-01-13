package net.agiledeveloper.mobtime.domain.ports.spi;

import net.agiledeveloper.mobtime.domain.notification.Notification;

public interface NotificationPort {

    void send(Notification notification);

}
