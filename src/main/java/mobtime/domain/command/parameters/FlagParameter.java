package mobtime.domain.command.parameters;

import mobtime.domain.command.commands.AbstractParameter;

public abstract class FlagParameter extends AbstractParameter {

    protected FlagParameter(String name) {
        super(name);
    }

    public String toString() {
        return "--" + name();
    }

}
