package net.agiledeveloper.mobtime.domain.notification.session;

import net.agiledeveloper.mobtime.domain.notification.Notification;
import net.agiledeveloper.mobtime.domain.notification.Severity;
import net.agiledeveloper.mobtime.domain.session.Session;

import static net.agiledeveloper.mobtime.domain.notification.Severity.CRITICAL;

public record SessionShutdownNotification(
        Session session,
        String message,
        String value
) implements Notification {

    @Override
    public Severity severity() {
        return CRITICAL;
    }

}
