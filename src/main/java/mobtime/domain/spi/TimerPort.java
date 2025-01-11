package mobtime.domain.spi;

import java.time.Duration;

public interface TimerPort {

    default void runFor(Duration duration) {
        runFor(duration, () -> {});
    }

    void runFor(Duration milliseconds, Runnable then);

}
