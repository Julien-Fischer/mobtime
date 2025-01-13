package net.agiledeveloper.mobtime;

import net.agiledeveloper.mobtime.domain.command.commands.Command;
import net.agiledeveloper.mobtime.domain.session.MobService;
import net.agiledeveloper.mobtime.domain.session.SessionService;
import net.agiledeveloper.mobtime.infra.CommandLineParser;
import net.agiledeveloper.mobtime.infra.git.MobShAdapter;
import net.agiledeveloper.mobtime.infra.swing.SwingNotificationAdapter;
import net.agiledeveloper.mobtime.infra.swing.SwingWorkerTimeAdapter;
import net.agiledeveloper.mobtime.utils.AppLogger;

public class Application {

    public Application(String[] commandLine) {
        var mobService = new MobService(new MobShAdapter());
        var notificationAdapter = new SwingNotificationAdapter(mobService);
        var sessionService = new SessionService(new SwingWorkerTimeAdapter(), notificationAdapter);
        var parser = new CommandLineParser(sessionService);

        Command command = parser.parse(commandLine);

        command.execute();

        AppLogger.log("Command processed");
    }

}
