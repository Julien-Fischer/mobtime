package net.agiledeveloper.mobtime.domain.notification.session;

import net.agiledeveloper.mobtime.domain.notification.Notification;
import net.agiledeveloper.mobtime.domain.notification.Severity;
import net.agiledeveloper.mobtime.domain.session.Session;

import static net.agiledeveloper.mobtime.domain.notification.Severity.INFO;

public record SessionOpenNotification(
        Session session,
        String message,
        String value
) implements Notification {

    @Override
    public Severity severity() {
        return INFO;
    }

}
