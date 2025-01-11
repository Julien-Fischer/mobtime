package mobtime;

import mobtime.domain.command.commands.Command;
import mobtime.domain.session.SessionService;
import mobtime.infra.CommandLineParser;
import mobtime.infra.NaiveTimeLoop;
import mobtime.infra.SwingNotificationAdapter;
import mobtime.utils.AppLogger;

public class Application {

    public Application(String[] commandLine) {
        var sessionService = new SessionService(new NaiveTimeLoop(), new SwingNotificationAdapter());
        var parser = new CommandLineParser(sessionService);

        Command command = parser.parse(commandLine);

        command.execute();

        AppLogger.log("Done");
    }

}
