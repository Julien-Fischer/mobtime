package net.agiledeveloper.mobtime.domain.command.parameters;

public abstract class FlagParameter extends AbstractParameter {

    protected FlagParameter(String name) {
        super(name);
    }

    public String toString() {
        return PREFIX + name();
    }

}
