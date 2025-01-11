package mobtime.domain.session;

import mobtime.domain.time.Duration;

public record Session(Duration duration, Runnable callback) {
}
