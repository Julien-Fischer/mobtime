package net.agiledeveloper.mobtime.domain.command.parameters;

import net.agiledeveloper.mobtime.domain.command.commands.AbstractParameter;

public abstract class ValueParameter<V> extends AbstractParameter {

    private final V val;

    protected ValueParameter(String name, V val) {
        super(name);
        this.val = val;
    }

    public V value() {
        return val;
    }

    @Override
    public String toString() {
        return PREFIX + name() + SEPARATOR + value();
    }

}
