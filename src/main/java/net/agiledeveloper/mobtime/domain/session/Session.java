package net.agiledeveloper.mobtime.domain.session;

import java.time.Duration;
import java.time.Instant;

public record Session(Duration duration, Instant createdAt) {

    public static final double DEFAULT_DURATION_SECONDS = 15 * 60;
    private static final int DEFAULT_GRACE_DURATION_SECONDS = 2;


    public Session(Duration duration) {
        this(duration, Instant.now());
    }

    public Duration graceDuration() {
        return Duration.ofSeconds(DEFAULT_GRACE_DURATION_SECONDS);
    }

}
