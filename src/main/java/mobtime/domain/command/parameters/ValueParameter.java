package mobtime.domain.command.parameters;

import mobtime.domain.command.commands.AbstractParameter;

public abstract class ValueParameter<V> extends AbstractParameter {

    protected ValueParameter(String name) {
        super(name);
    }

    public abstract V value();

    public String toString() {
        return "--" + name() + "=" + value();
    }

}
