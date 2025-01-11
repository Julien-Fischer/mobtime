package net.agiledeveloper.mobtime.domain.command.commands;

import net.agiledeveloper.mobtime.domain.command.parameters.Parameter;

import java.util.Optional;
import java.util.Set;

public interface Command {

    void execute();

    default boolean is(Class<? extends Command> className) {
        return this.getClass() == className;
    }

    Set<Parameter> parameters();

    default boolean has(Class<? extends Parameter> parameterName) {
        return parameters().stream()
                .anyMatch(parameter -> parameter.getClass().equals(parameterName));
    }

    default Optional<Parameter> get(Class<? extends Parameter> parameterName) {
        return parameters().stream()
                .filter(parameter -> parameter.getClass().equals(parameterName))
                .findFirst();
    }

}
