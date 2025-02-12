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

    public String getOption() {
        return "-c";
    }

}
