package net.agiledeveloper.mobtime.domain.command.parameters;

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
