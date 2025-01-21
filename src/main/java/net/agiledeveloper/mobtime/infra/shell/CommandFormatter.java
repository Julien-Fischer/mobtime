package net.agiledeveloper.mobtime.infra.shell;

public interface CommandFormatter {

    String HOME_DIR = System.getProperty("user.home");

    ShellCommand format(String commandName);

}
