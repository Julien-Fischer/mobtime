package mobtime.domain.command.commands;

import mobtime.domain.command.parameters.Parameter;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractCommand implements Command {

    private final Set<Parameter> parameters;

    protected AbstractCommand(Set<Parameter> parameters) {
        this.parameters = parameters;
    }

    @Override
    public Set<Parameter> parameters() {
        return new HashSet<>(parameters);
    }

}
