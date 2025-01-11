package mobtime.domain.command.parameters.impl;

import mobtime.domain.command.parameters.ValueParameter;
import mobtime.domain.time.Duration;

public class DurationParameter extends ValueParameter<Duration> {

    private final Duration value;

    public DurationParameter(Duration value) {
        super("duration");
        this.value = value;
    }

    public Duration value() {
        return value;
    }

}
