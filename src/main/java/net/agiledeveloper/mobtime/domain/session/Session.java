package net.agiledeveloper.mobtime.domain.session;

import java.time.Duration;
import java.time.Instant;

import static net.agiledeveloper.mobtime.utils.TimeFormatter.formatDuration;
import static net.agiledeveloper.mobtime.utils.TimeFormatter.formatInstant;

public record Session(
        Duration duration,
        boolean isAutoModeEnabled,
        FocusMode focusMode,
        Instant createdAt
) {

    private static final int DEFAULT_DURATION_SECONDS = 15 * 60;
    private static final int DEFAULT_GRACE_DURATION_SECONDS = 2;

    public static final Duration DEFAULT_DURATION = Duration.ofSeconds(DEFAULT_DURATION_SECONDS);
    public static final FocusMode DEFAULT_FOCUS_MODE = FocusMode.NORMAL;


    public Session(Duration duration, boolean isAutoModeEnabled, FocusMode mode) {
        this(duration, isAutoModeEnabled, mode, Instant.now());
    }

    public Duration graceDuration() {
        return Duration.ofSeconds(DEFAULT_GRACE_DURATION_SECONDS);
    }

    public boolean hasFocus(FocusMode mode) {
        return (focusMode == mode);
    }

    @Override
    public String toString() {
        return "[Session]"
                + " createdAt: " + formatInstant(createdAt) + ","
                + " duration: " + formatDuration(duration) + ","
                + " isAutoModeEnabled: " + isAutoModeEnabled + ","
                + " focusMode: " + focusMode;
    }

}
