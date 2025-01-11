package mobtime.domain.ports.spi;

import mobtime.domain.time.Duration;

public interface TimerPort {

    default void runFor(Duration duration) {
        runFor(duration, () -> {});
    }

    void runFor(Duration duration, Runnable then);

}
