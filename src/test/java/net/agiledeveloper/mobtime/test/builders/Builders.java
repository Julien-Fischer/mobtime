package net.agiledeveloper.mobtime.test.builders;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class Builders {

    private Builders() {}


    public static Duration aDuration() {
        return Duration.of(3, ChronoUnit.SECONDS);
    }

}
