package net.agiledeveloper.mobtime.domain.ports.spi;

import net.agiledeveloper.mobtime.domain.Notification;

public interface NotificationPort {

    void send(Notification notification);

}
