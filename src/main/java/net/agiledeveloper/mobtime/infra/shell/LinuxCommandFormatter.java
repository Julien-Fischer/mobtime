package net.agiledeveloper.mobtime.infra.shell;

import java.nio.file.Path;

import static net.agiledeveloper.mobtime.utils.PathUtils.path;

public class LinuxCommandFormatter implements CommandFormatter {

    private static final Path SHARED_LIB_PATH  = path(HOME_DIR + "/mobtime/src/mobtime_lib.sh");
    private static final Path COMMAND_ROOT_DIR = path("/usr/local/bin");

    private final LinuxShell shell;

    public LinuxCommandFormatter(LinuxShell shell) {
        this.shell = shell;
    }

    @Override
    public ShellCommand format(String commandName) {
        Path commandPath = COMMAND_ROOT_DIR.resolve(commandName);
        String commandLine = "source " + SHARED_LIB_PATH + " && " + commandPath;
        return new ShellCommand(shell, commandLine);
    }

}
