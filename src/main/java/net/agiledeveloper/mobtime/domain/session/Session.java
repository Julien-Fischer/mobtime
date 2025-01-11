package net.agiledeveloper.mobtime.domain.session;

import net.agiledeveloper.mobtime.domain.Duration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static net.agiledeveloper.mobtime.utils.TimeUtils.now;

public record Session(Duration duration, Instant created) {

    public Session(Duration duration) {
        this(duration, Instant.now());
    }

    public Duration actualDuration() {
        return new Duration(now() - duration().getMillis(), ChronoUnit.MILLIS);
    }

}
