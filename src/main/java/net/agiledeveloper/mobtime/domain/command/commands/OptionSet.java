package net.agiledeveloper.mobtime.domain.command.commands;

import net.agiledeveloper.mobtime.domain.command.parameters.Parameter;
import net.agiledeveloper.mobtime.domain.command.parameters.ValueParameter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

public record OptionSet(Set<Parameter> parameters) implements Iterable<Parameter> {

    public Set<Parameter> parameters() {
        return new HashSet<>(parameters);
    }

    public boolean hasOption(Class<? extends Parameter> parameterName) {
        return parameters().stream()
                .anyMatch(parameter -> parameter.getClass().equals(parameterName));
    }

    public Optional<Parameter> getOption(Class<? extends Parameter> parameterName) {
        return parameters().stream()
                .filter(parameter -> parameter.getClass().equals(parameterName))
                .findFirst();
    }

    public <V, P extends ValueParameter<V>> V getValue(Class<P> parameterType, V defaultValue) {
        Optional<Parameter> parameter = getOption(parameterType);
        if (parameter.isPresent() && parameterType.isInstance(parameter.get())) {
            return parameterType.cast(parameter.get()).value();
        } else {
            return defaultValue;
        }
    }

    @Override
    public Iterator<Parameter> iterator() {
        return parameters().iterator();
    }

}
