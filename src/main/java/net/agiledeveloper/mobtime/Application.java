package net.agiledeveloper.mobtime;

import net.agiledeveloper.mobtime.domain.command.commands.Command;
import net.agiledeveloper.mobtime.domain.session.MobService;
import net.agiledeveloper.mobtime.domain.session.SessionService;
import net.agiledeveloper.mobtime.infra.CommandLineParser;
import net.agiledeveloper.mobtime.infra.git.ShellAdapter;
import net.agiledeveloper.mobtime.infra.swing.SwingNotificationAdapter;
import net.agiledeveloper.mobtime.infra.swing.SwingWorkerTimeAdapter;
import net.agiledeveloper.mobtime.utils.AppLogger;

public class Application {

    public Application(String[] commandLine) {
        var shellAdapter = new ShellAdapter();
        var mobService = new MobService(shellAdapter);
        var notificationAdapter = new SwingNotificationAdapter(mobService);
        var sessionService = new SessionService(new SwingWorkerTimeAdapter(), notificationAdapter);
        var parser = new CommandLineParser(sessionService);

        Command command = null;
        try {
            command = parser.parse(commandLine);
        } catch (Exception exception) {
            terminate(exception, shellAdapter);
        }

        command.execute();

        AppLogger.log("Command processed");
    }


    private static void terminate(Exception exception, ShellAdapter shellAdapter) {
        var SEPARATOR = "/!\\ ".repeat(20);
        AppLogger.logSeparator(SEPARATOR);
        AppLogger.err("Error parsing command");
        AppLogger.err("E: " + exception.getMessage());
        AppLogger.err("Closing mob session...");
        AppLogger.logSeparator(SEPARATOR);
        shellAdapter.execute("mob done");
        System.exit(0);
    }

}
