package mobtime.domain.spi;

import java.time.Duration;

public interface TimerPort {

    void runFor(Duration milliseconds);

}
