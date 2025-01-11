package net.agiledeveloper.mobtime.domain.ports.spi;

import net.agiledeveloper.mobtime.domain.Duration;

public interface TimerPort {

    default void runFor(Duration duration) {
        runFor(duration, () -> {});
    }

    void runFor(Duration duration, Runnable then);

}
