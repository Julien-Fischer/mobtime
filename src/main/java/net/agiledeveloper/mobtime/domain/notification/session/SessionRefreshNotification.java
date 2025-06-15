package net.agiledeveloper.mobtime.domain.notification.session;

import net.agiledeveloper.mobtime.domain.Ratio;
import net.agiledeveloper.mobtime.domain.notification.Notification;
import net.agiledeveloper.mobtime.domain.notification.Severity;
import net.agiledeveloper.mobtime.domain.session.Session;
import net.agiledeveloper.mobtime.domain.session.Username;

import java.time.Duration;

import static net.agiledeveloper.mobtime.domain.notification.Severity.INFO;
import static net.agiledeveloper.mobtime.domain.notification.Severity.SUCCESS;
import static net.agiledeveloper.mobtime.utils.TimeFormatter.formatDuration;

public record SessionRefreshNotification(
        Session session,
        Username username,
        Duration remainingTime,
        String durationString
) implements Notification {

    public SessionRefreshNotification(Session session) {
        this(session, session.username(), session.remainingTime(), formatDuration(session.remainingTime()));
    }


    public Ratio progress() {
        return session.progress();
    }

    @Override
    public Severity severity() {
        return session.isOverSoon() ? INFO : SUCCESS;
    }

    @Override
    public String message() {
        return username.value();
    }

    @Override
    public String value() {
        return durationString;
    }

}
