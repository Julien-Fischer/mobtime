package net.agiledeveloper.mobtime.infra.shell;

public interface Shell {

    String getName();

    String[] formatCommand(String commandName);

}
