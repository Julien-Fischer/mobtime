package net.agiledeveloper.mobtime.domain.command.parameters.impl;

import net.agiledeveloper.mobtime.domain.command.parameters.ValueParameter;

import java.time.Duration;

public class DurationParameter extends ValueParameter<Duration> {

    public DurationParameter(Duration value) {
        super("duration", value);
    }

    @Override
    public String toString() {
        return PREFIX + name() + SEPARATOR + value().toMinutes();
    }
}
