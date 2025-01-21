package net.agiledeveloper.mobtime.test.builders;

import net.agiledeveloper.mobtime.domain.command.commands.impl.StartCommand;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

public class Builders {

    private Builders() {}


    public static StartCommand aStartCommand() {
        return new StartCommand(Collections.emptySet(), null);
    }

    public static Duration aDuration() {
        return Duration.of(3, ChronoUnit.SECONDS);
    }

}
