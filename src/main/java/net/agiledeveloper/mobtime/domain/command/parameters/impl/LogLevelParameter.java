package net.agiledeveloper.mobtime.domain.command.parameters.impl;

import net.agiledeveloper.mobtime.domain.command.parameters.ValueParameter;
import net.agiledeveloper.mobtime.utils.AppLogger.Level;

public class LogLevelParameter extends ValueParameter<Level> {

    public LogLevelParameter(Level value) {
        super("log-level", value);
    }

}
