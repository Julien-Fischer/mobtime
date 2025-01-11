package mobtime;

import mobtime.domain.command.commands.Command;
import mobtime.domain.session.SessionService;
import mobtime.infra.CommandLineParser;
import mobtime.infra.NaiveTimeLoop;
import mobtime.utils.AppLogger;

public class Application {

    public Application(String[] commandLine) {
        var parser = new CommandLineParser(new SessionService(new NaiveTimeLoop()));
        Command command = parser.parse(commandLine);

        command.execute();

        AppLogger.log("Done");
    }

}
