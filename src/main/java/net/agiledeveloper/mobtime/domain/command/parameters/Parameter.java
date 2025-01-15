package net.agiledeveloper.mobtime.domain.command.parameters;

public interface Parameter {

    String PREFIX = "--";
    String SEPARATOR = "=";

    String name();

}
