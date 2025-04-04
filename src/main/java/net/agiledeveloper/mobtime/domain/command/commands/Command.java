package net.agiledeveloper.mobtime.domain.command.commands;

import net.agiledeveloper.mobtime.domain.command.parameters.Parameter;

import java.util.Optional;

public interface Command {

    void execute();

    default boolean is(Class<? extends Command> className) {
        return this.getClass() == className;
    }

    OptionSet options();

    default boolean hasOption(Class<? extends Parameter> parameterName) {
        return options().hasOption(parameterName);
    }

    default Optional<Parameter> getOption(Class<? extends Parameter> parameterName) {
        return options().getOption(parameterName);
    }

}
