package net.agiledeveloper.mobtime.domain.notification.session;

import net.agiledeveloper.mobtime.domain.Ratio;
import net.agiledeveloper.mobtime.domain.notification.Notification;
import net.agiledeveloper.mobtime.domain.session.Session;

import java.time.Duration;

public record SessionRefreshNotification(
        Session session,
        String message,
        String value,
        boolean hasLittleTimeLeft,
        Duration remainingTime
) implements Notification {

    public Ratio progress() {
        return session.progress(remainingTime);
    }

}
