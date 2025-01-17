package net.agiledeveloper.mobtime.infra.cli;

import net.agiledeveloper.mobtime.domain.command.parameters.Parameter;

public class BashParameter implements Parameter {

    private final String[] split;

    BashParameter(String argument) {
        split = argument.substring(2).split(SEPARATOR);
    }

    @Override
    public String name() {
        return split[0];
    }

    public String value() {
        return split[1];
    }

    public boolean hasName(String name) {
        return split[0].equals(name);
    }

    public boolean hasValue() {
        return split.length == 2;
    }

    @Override
    public String toString() {
        var subject = PREFIX + name();
        if (hasValue()) {
            subject += SEPARATOR + value();
        }
        return subject;
    }

}
