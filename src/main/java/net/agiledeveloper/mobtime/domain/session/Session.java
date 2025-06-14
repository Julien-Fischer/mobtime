package net.agiledeveloper.mobtime.domain.session;

import java.time.Duration;
import java.time.Instant;

import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;
import static net.agiledeveloper.mobtime.domain.session.FocusMode.NORMAL;
import static net.agiledeveloper.mobtime.utils.TimeFormatter.formatDuration;
import static net.agiledeveloper.mobtime.utils.TimeFormatter.formatInstant;

public record Session(
        Duration duration,
        boolean shouldAutomaticallyPassKeyboard,
        FocusMode focusMode,
        String username,
        Instant createdAt
) {

    public static final Duration DEFAULT_DURATION = ofMinutes(15);
    public static final Duration DEFAULT_GRACE_DURATION = ofSeconds(2);
    public static final String DEFAULT_USERNAME = "Driver";
    public static final FocusMode DEFAULT_FOCUS_MODE = NORMAL;


    public Session(Duration duration, boolean shouldAutomaticallyPassKeyboard, FocusMode mode, String username) {
        this(duration, shouldAutomaticallyPassKeyboard, mode, username, Instant.now());
    }

    public Duration graceDuration() {
        return DEFAULT_GRACE_DURATION;
    }

    public boolean hasFocus(FocusMode mode) {
        return (focusMode == mode);
    }

    @Override
    public String toString() {
        return "[Session]"
                + " createdAt: " + formatInstant(createdAt) + ","
                + " duration:  " + formatDuration(duration) + ","
                + " auto-next: " + shouldAutomaticallyPassKeyboard + ","
                + " focusMode: " + focusMode;
    }

}
