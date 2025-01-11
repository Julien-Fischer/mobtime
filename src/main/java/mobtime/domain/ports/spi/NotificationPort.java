package mobtime.domain.ports.spi;

import mobtime.domain.Notification;

public interface NotificationPort {

    void send(Notification notification);

}
