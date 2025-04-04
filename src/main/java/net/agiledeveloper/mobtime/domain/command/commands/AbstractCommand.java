package net.agiledeveloper.mobtime.domain.command.commands;

import net.agiledeveloper.mobtime.domain.command.parameters.Parameter;

import java.util.Set;

public abstract class AbstractCommand implements Command {

    private final OptionSet optionSet;

    protected AbstractCommand(Set<Parameter> optionSet) {
        this.optionSet = new OptionSet(optionSet);
    }

    @Override
    public OptionSet options() {
        return optionSet;
    }

}
