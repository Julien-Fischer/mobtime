package net.agiledeveloper.mobtime.domain.session;

import java.time.Duration;
import java.time.Instant;

import static net.agiledeveloper.mobtime.utils.TimeFormatter.formatDuration;
import static net.agiledeveloper.mobtime.utils.TimeFormatter.formatInstant;

public record Session(Duration duration, boolean isAutoModeEnabled, Instant createdAt) {

    public static final double DEFAULT_DURATION_SECONDS = 5;
    private static final int DEFAULT_GRACE_DURATION_SECONDS = 2;


    public Session(Duration duration, boolean isAutoModeEnabled) {
        this(duration, isAutoModeEnabled, Instant.now());
    }

    public Duration graceDuration() {
        return Duration.ofSeconds(DEFAULT_GRACE_DURATION_SECONDS);
    }

    @Override
    public String toString() {
        return "[Session]"
                + " createdAt: " + formatInstant(createdAt) + ","
                + " duration: " + formatDuration(duration) + ","
                + " isAutoModeEnabled: " + isAutoModeEnabled;
    }

}
