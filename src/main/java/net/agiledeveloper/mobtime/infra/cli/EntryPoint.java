package net.agiledeveloper.mobtime.infra.cli;

import net.agiledeveloper.mobtime.infra.shell.LinuxCommandFormatter;
import net.agiledeveloper.mobtime.infra.shell.LinuxShell;
import net.agiledeveloper.mobtime.infra.shell.ShellAdapter;
import net.agiledeveloper.mobtime.orchestrator.Application;

public class EntryPoint {

    public static void main(String[] args) {
        var commandFormatter = new LinuxCommandFormatter(LinuxShell.BASH);
        var shellAdapter = new ShellAdapter(commandFormatter);
        new Application(shellAdapter)
                .process(args);
    }

}
