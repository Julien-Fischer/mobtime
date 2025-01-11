package mobtime.domain.session;

import mobtime.domain.Duration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static mobtime.utils.TimeUtils.now;

public record Session(Duration duration, Instant created) {

    public Session(Duration duration) {
        this(duration, Instant.now());
    }

    public Duration actualDuration() {
        return new Duration(now() - duration().getMillis(), ChronoUnit.MILLIS);
    }

}
