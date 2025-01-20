package net.agiledeveloper.mobtime.infra.shell;

import java.nio.file.Path;

import static net.agiledeveloper.mobtime.utils.PathUtils.path;

public enum LinuxShell implements Shell {

    SH  ("sh"),
    BASH("/bin/bash");

    private static final String HOME_DIR = System.getProperty("user.home");
    private static final Path SHARED_LIB_PATH  = path(HOME_DIR + "/mobtime/src/mobtime_lib.sh");
    private static final Path COMMAND_ROOT_DIR = path("/usr/local/bin");

    private final String shellName;


    LinuxShell(String shellName) {
        this.shellName = shellName;
    }


    public String getName() {
        return shellName;
    }

    @Override
    public String[] formatCommand(String commandName) {
        String commandPath = COMMAND_ROOT_DIR.resolve(commandName).toString();
        String commandLine = "source " + SHARED_LIB_PATH + " && " + commandPath;
        return new String[] {shellName, "-c", commandLine};
    }

}
