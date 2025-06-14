package net.agiledeveloper.mobtime.domain.session;

import net.agiledeveloper.mobtime.domain.Ratio;

import java.time.Duration;
import java.time.Instant;

import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;
import static net.agiledeveloper.mobtime.domain.session.EndMode.AUTOMATICALLY_PASS_KEYBOARD;
import static net.agiledeveloper.mobtime.domain.session.FocusMode.NORMAL;
import static net.agiledeveloper.mobtime.utils.TimeFormatter.formatDuration;
import static net.agiledeveloper.mobtime.utils.TimeFormatter.formatInstant;

public record Session(
        Duration initialDuration,
        EndMode endMode,
        FocusMode focusMode,
        Username username,
        Instant createdAt
) {

    public static final Duration DEFAULT_DURATION = ofMinutes(15);
    public static final Duration DEFAULT_GRACE_DURATION = ofSeconds(2);
    public static final Username DEFAULT_USERNAME = new Username("Driver");
    public static final FocusMode DEFAULT_FOCUS_MODE = NORMAL;
    public static final Ratio LOW_TIME_THRESHOLD = new Ratio(0.25);


    public Session(Duration duration, EndMode endMode, FocusMode mode, Username username) {
        this(duration, endMode, mode, username, Instant.now());
    }

    public boolean shouldAutomaticallyPassKeyboard() {
        return endMode == AUTOMATICALLY_PASS_KEYBOARD;
    }

    public Duration graceDuration() {
        return DEFAULT_GRACE_DURATION;
    }

    public boolean hasFocus(FocusMode mode) {
        return (focusMode == mode);
    }

    public boolean isGracePeriodOver(Duration remainingTime) {
        Duration elapsed = initialDuration.minus(remainingTime);
        return !graceDuration().minus(elapsed).isPositive();
    }

    public boolean isOverSoon(Duration remainingTime) {
        return progress(remainingTime)
                .lessThan(LOW_TIME_THRESHOLD);
    }

    public Ratio progress(Duration remainingTime) {
        return Ratio.of(remainingTime.toMillis(), initialDuration.toMillis());
    }

    @Override
    public String toString() {
        return "[Session]"
                + " createdAt:       " + formatInstant(createdAt) + ","
                + " initialDuration: " + formatDuration(initialDuration) + ","
                + " auto-next:       " + endMode + ","
                + " focusMode:       " + focusMode;
    }

}
