package net.agiledeveloper.mobtime.test;

import net.agiledeveloper.mobtime.domain.Duration;
import net.agiledeveloper.mobtime.domain.command.commands.impl.StartCommand;
import net.agiledeveloper.mobtime.domain.command.parameters.Parameter;
import net.agiledeveloper.mobtime.domain.command.parameters.impl.DryRunParameter;
import net.agiledeveloper.mobtime.domain.command.parameters.impl.DurationParameter;
import net.agiledeveloper.mobtime.domain.session.Session;

import java.time.temporal.ChronoUnit;
import java.util.Collections;

public class Builders {

    private Builders() {}


    public static StartCommand aStartCommand() {
        return new StartCommand(Collections.emptySet(), null);
    }

    public static Parameter aDurationParameter() {
        return new DurationParameter(aDuration());
    }

    public static Parameter aParameter() {
        return new DryRunParameter();
    }

    public static Duration aDuration() {
        return new Duration(3, ChronoUnit.SECONDS);
    }

    public static Session aSession() {
        return new Session(aDuration());
    }

}
