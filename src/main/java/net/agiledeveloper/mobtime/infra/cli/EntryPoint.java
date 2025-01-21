package net.agiledeveloper.mobtime.infra.cli;

import net.agiledeveloper.mobtime.Application;
import net.agiledeveloper.mobtime.infra.shell.LinuxShell;
import net.agiledeveloper.mobtime.infra.shell.ShellAdapter;

public class EntryPoint {

    public static void main(String[] args) {
        var shellAdapter = new ShellAdapter(LinuxShell.BASH);
        new Application(shellAdapter)
                .process(args);
    }

}
