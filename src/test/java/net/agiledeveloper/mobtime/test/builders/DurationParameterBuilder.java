package net.agiledeveloper.mobtime.test.builders;

import net.agiledeveloper.mobtime.domain.command.parameters.impl.DurationParameter;
import net.agiledeveloper.mobtime.test.lib.Builder;

import static net.agiledeveloper.mobtime.test.builders.Builders.aDuration;

public class DurationParameterBuilder implements Builder<DurationParameter> {

    private DurationParameterBuilder() {}

    public static DurationParameterBuilder aDurationParameter() {
        return new DurationParameterBuilder();
    }

    @Override
    public DurationParameter build() {
        return new DurationParameter(aDuration());
    }

}
