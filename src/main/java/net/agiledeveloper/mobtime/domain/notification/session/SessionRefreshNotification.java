package net.agiledeveloper.mobtime.domain.notification.session;

import net.agiledeveloper.mobtime.domain.Ratio;
import net.agiledeveloper.mobtime.domain.notification.Notification;
import net.agiledeveloper.mobtime.domain.session.Session;
import net.agiledeveloper.mobtime.domain.session.Username;

import java.time.Duration;

public record SessionRefreshNotification(
        Session session,
        Username username,
        String value,
        Duration remainingTime
) implements Notification {

    public Ratio progress() {
        return session.progress(remainingTime);
    }

    public boolean hasLittleTimeLeft() {
        return session.isOverSoon(remainingTime);
    }

    @Override
    public String message() {
        return username.value();
    }

}
