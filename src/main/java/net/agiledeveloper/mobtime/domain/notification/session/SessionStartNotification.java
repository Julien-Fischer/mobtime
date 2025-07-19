package net.agiledeveloper.mobtime.domain.notification.session;

import net.agiledeveloper.mobtime.domain.notification.Notification;
import net.agiledeveloper.mobtime.domain.notification.Severity;
import net.agiledeveloper.mobtime.domain.session.Session;

import static net.agiledeveloper.mobtime.domain.notification.Severity.SUCCESS;

public record SessionStartNotification(Session session, String message, String value) implements Notification {

    public SessionStartNotification(Session session, String message) {
        this(session, message, "");
    }

    @Override
    public Severity severity() {
        return SUCCESS;
    }

}
