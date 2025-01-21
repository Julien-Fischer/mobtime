package net.agiledeveloper.mobtime.infra.shell;

public record ShellCommand(Shell shell, String commandLine) {

    public String[] toArray() {
        return new String[] {shell.getName(), shell.getOption(), commandLine};
    }

}
