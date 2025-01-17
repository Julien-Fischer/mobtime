package net.agiledeveloper.mobtime.domain.notification.session;

import net.agiledeveloper.mobtime.domain.notification.Notification;

import java.time.Duration;

public record SessionRefreshNotification(
        String message, String value, boolean hasLittleTimeLeft, Duration sessionDuration, Duration remainingTime
) implements Notification {

    public double progress() {
        return ((double) remainingTime.toMillis()) / sessionDuration.toMillis();
    }

}
