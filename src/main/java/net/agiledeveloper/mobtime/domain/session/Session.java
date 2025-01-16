package net.agiledeveloper.mobtime.domain.session;

import java.time.Duration;
import java.time.Instant;

import static net.agiledeveloper.mobtime.utils.TimeFormatter.formatDuration;
import static net.agiledeveloper.mobtime.utils.TimeFormatter.formatInstant;

public record Session(
        Duration duration,
        boolean isAutoModeEnabled,
        boolean isZenModeEnabled,
        Instant createdAt
) {

    private static final int DEFAULT_DURATION_SECONDS = 15 * 60;
    private static final int DEFAULT_GRACE_DURATION_SECONDS = 2;

    public static final Duration DEFAULT_DURATION = Duration.ofSeconds(DEFAULT_DURATION_SECONDS);


    public Session(Duration duration, boolean isAutoModeEnabled, boolean isZenModeEnabled) {
        this(duration, isAutoModeEnabled, isZenModeEnabled, Instant.now());
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
