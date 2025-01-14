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
            AppLogger.logSeparator();
            AppLogger.log("Error parsing command: " + exception.getMessage());
            AppLogger.log("Closing mob session...");
            AppLogger.logSeparator();
            shellAdapter.execute("mob done");
            System.exit(0);
        }

        command.execute();

        AppLogger.log("Command processed");
    }

}
