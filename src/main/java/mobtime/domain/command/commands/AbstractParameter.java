package mobtime.domain.command.commands;

import mobtime.domain.command.parameters.Parameter;

public class AbstractParameter implements Parameter {

    private final String name;

    public AbstractParameter(String name) {
        this.name = name;
    }

    @Override
    public final String name() {
        return name;
    }

}
