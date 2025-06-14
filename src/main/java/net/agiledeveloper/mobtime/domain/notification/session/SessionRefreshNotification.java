package net.agiledeveloper.mobtime.domain.notification.session;

import net.agiledeveloper.mobtime.domain.Ratio;
import net.agiledeveloper.mobtime.domain.notification.Notification;
import net.agiledeveloper.mobtime.domain.notification.Severity;
import net.agiledeveloper.mobtime.domain.session.Session;
import net.agiledeveloper.mobtime.domain.session.Username;

import java.time.Duration;

import static net.agiledeveloper.mobtime.domain.notification.Severity.INFO;
import static net.agiledeveloper.mobtime.domain.notification.Severity.SUCCESS;

public record SessionRefreshNotification(
        Session session,
        Username username,
        String value,
        Duration remainingTime
) implements Notification {

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

}
