package net.agiledeveloper.mobtime;

import net.agiledeveloper.mobtime.domain.command.commands.Command;
import net.agiledeveloper.mobtime.domain.session.SessionService;
import net.agiledeveloper.mobtime.infra.CommandLineParser;
import net.agiledeveloper.mobtime.infra.NaiveTimeLoop;
import net.agiledeveloper.mobtime.infra.SwingNotificationAdapter;
import net.agiledeveloper.mobtime.utils.AppLogger;

public class Application {

    public Application(String[] commandLine) {
        var sessionService = new SessionService(new NaiveTimeLoop(), new SwingNotificationAdapter());
        var parser = new CommandLineParser(sessionService);

        Command command = parser.parse(commandLine);

        command.execute();

        AppLogger.log("Done");
    }

}
