package net.agiledeveloper.mobtime.infra.shell;

public enum LinuxShell implements Shell {

    SH  ("sh"),
    BASH("/bin/bash");

    private final String shellName;

    LinuxShell(String shellName) {
        this.shellName = shellName;
    }

    public String getName() {
        return shellName;
    }

    @Override
    public String[] formatCommand(String commandName) {
        return new String[] {shellName, "-c", commandName};
    }

}
