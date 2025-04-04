package net.agiledeveloper.mobtime.domain.command.commands;

import net.agiledeveloper.mobtime.domain.command.parameters.Parameter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

public record OptionSet(Set<Parameter> parameters) implements Iterable<Parameter> {

    public Set<Parameter> parameters() {
        return new HashSet<>(parameters);
    }

    public boolean has(Class<? extends Parameter> parameterName) {
        return parameters().stream()
                .anyMatch(parameter -> parameter.getClass().equals(parameterName));
    }

    public Optional<Parameter> get(Class<? extends Parameter> parameterName) {
        return parameters().stream()
                .filter(parameter -> parameter.getClass().equals(parameterName))
                .findFirst();
    }

    @Override
    public Iterator<Parameter> iterator() {
        return parameters().iterator();
    }

}
