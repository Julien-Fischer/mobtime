package net.agiledeveloper.mobtime.domain.session;

import net.agiledeveloper.mobtime.domain.Ratio;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;
import static net.agiledeveloper.mobtime.domain.session.EndMode.AUTOMATICALLY_PASS_KEYBOARD;
import static net.agiledeveloper.mobtime.domain.session.FocusMode.NORMAL;
import static net.agiledeveloper.mobtime.utils.TimeFormatter.formatDuration;
import static net.agiledeveloper.mobtime.utils.TimeFormatter.formatInstant;

public class Session {

    public static final Duration DEFAULT_DURATION = ofMinutes(15);
    public static final Duration DEFAULT_GRACE_DURATION = ofSeconds(2);
    public static final Username DEFAULT_USERNAME = new Username("Driver");
    public static final FocusMode DEFAULT_FOCUS_MODE = NORMAL;
    public static final Ratio LOW_TIME_THRESHOLD = new Ratio(0.25);

    private final Clock clock;

    private final SessionId id;
    private final Duration initialDuration;
    private final EndMode endMode;
    private final FocusMode focusMode;
    private final Username username;
    private final Instant createdAt;

    private Instant startedAt;
    private Instant deadline;


    public Session(
            Clock clock,
            Duration initialDuration,
            EndMode endMode,
            FocusMode focusMode,
            Username username
    ) {
        this(SessionId.random(), clock, initialDuration, endMode, focusMode, username);
    }

    public Session(
            SessionId id,
            Clock clock,
            Duration initialDuration,
            EndMode endMode,
            FocusMode focusMode,
            Username username
    ) {
        this.id = id;
        this.clock = clock;
        this.initialDuration = initialDuration;
        this.endMode = endMode;
        this.focusMode = focusMode;
        this.username = username;
        this.createdAt = clock.instant();
    }


    public void start() {
        this.startedAt = clock.instant();
        this.deadline = startedAt.plus(initialDuration());
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

    public boolean isGracePeriodOver() {
        Duration elapsed = initialDuration.minus(remainingTime());
        return !graceDuration().minus(elapsed).isPositive();
    }

    public boolean isOverSoon() {
        return progress().lessThan(LOW_TIME_THRESHOLD);
    }

    public boolean isOver() {
        return progress().lessOrEqualTo(Ratio.ZERO);
    }

    public Ratio progress() {
        return Ratio.of(
                remainingTime().getSeconds(),
                initialDuration().getSeconds()
        );
    }

    public Duration remainingTime() {
        if (!hasStarted()) {
            throw new SessionNotStartedException("Session has not started yet");
        }
        return Duration.between(clock.instant(), deadline);
    }

    public Username username() {
        return username;
    }

    public Duration initialDuration() {
        return initialDuration;
    }

    public FocusMode focusMode() {
        return focusMode;
    }

    public boolean hasStarted() {
        return startedAt != null;
    }

    public Instant startedAt() {
        return startedAt;
    }

    public SessionId id() {
        return id;
    }


    @Override
    public String toString() {
        return "[Session]"
                + "\n> id:              " + id
                + "\n> createdAt:       " + formatInstant(createdAt)
                + "\n> initialDuration: " + formatDuration(initialDuration)
                + "\n> startedAt:       " + ifPresent(() -> formatInstant(startedAt))
                + "\n> remainingTime:   " + ifPresent(() -> formatDuration(remainingTime()))
                + "\n> progress:        " + ifPresent(() -> progress().toString())
                + "\n> auto-next:       " + endMode + ","
                + "\n> focusMode:       " + focusMode;
    }


    private String ifPresent(Supplier<String> supplier) {
        return hasStarted() ? supplier.get() : null;
    }

    public static class SessionNotStartedException extends RuntimeException {
        public SessionNotStartedException(String message) {
            super(message);
        }
    }

}
