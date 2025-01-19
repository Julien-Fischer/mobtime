package net.agiledeveloper.mobtime.infra.git;

public interface Shell {

    String getName();

    String[] formatCommand(String commandName);

}
