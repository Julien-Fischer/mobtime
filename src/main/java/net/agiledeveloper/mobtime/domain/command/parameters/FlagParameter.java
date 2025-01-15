package net.agiledeveloper.mobtime.domain.command.parameters;

import net.agiledeveloper.mobtime.domain.command.commands.AbstractParameter;

public abstract class FlagParameter extends AbstractParameter {

    protected FlagParameter(String name) {
        super(name);
    }

    public String toString() {
        return SEPARATOR + name();
    }

}
