package net.agiledeveloper.mobtime;

import net.agiledeveloper.mobtime.domain.command.CommandLineInterpreter;
import net.agiledeveloper.mobtime.domain.command.commands.Command;
import net.agiledeveloper.mobtime.domain.session.MobService;
import net.agiledeveloper.mobtime.domain.session.SessionService;
import net.agiledeveloper.mobtime.infra.cli.BashParameter;
import net.agiledeveloper.mobtime.infra.cli.CommandLineParser;
import net.agiledeveloper.mobtime.infra.git.ShellAdapter;
import net.agiledeveloper.mobtime.infra.swing.SwingNotificationAdapter;
import net.agiledeveloper.mobtime.infra.swing.SwingWorkerTimeAdapter;
import net.agiledeveloper.mobtime.utils.AppLogger;

import java.util.List;
import java.util.function.Supplier;

public class Application {

    private static final ShellAdapter shellAdapter = new ShellAdapter();

    public Application(String[] commandLine) {
        var parser = new CommandLineParser();
        List<BashParameter> bashParameters = tryGetting(() -> parser.parse(commandLine));

        var mobService = new MobService(shellAdapter);
        var notificationAdapter = new SwingNotificationAdapter(mobService, shouldMinimize(bashParameters));
        var sessionService = new SessionService(new SwingWorkerTimeAdapter(), notificationAdapter, shellAdapter);

        var handler = new CommandLineInterpreter(sessionService);
        Command command = tryGetting(() -> handler.interpret(bashParameters));

        command.execute();

        AppLogger.log("Command processed");
    }


    private static <T> T tryGetting(Supplier<T> procedure) {
        try {
            return procedure.get();
        } catch (Exception exception) {
            logError(exception.getMessage());
            shellAdapter.execute("mob done");
            throw exception;
        }
    }

    private static void logError(String message) {
        var SEPARATOR = "/!\\ ".repeat(20);
        AppLogger.logSeparator(SEPARATOR);
        AppLogger.err("Error parsing command");
        AppLogger.err("E: " + message);
        AppLogger.err("Closing mob session...");
        AppLogger.logSeparator(SEPARATOR);
    }

    private static boolean shouldMinimize(List<BashParameter> bashParameters) {
        return !bashParameters.stream()
                .filter(param -> param.hasName("mini"))
                .toList()
                .isEmpty();
    }

}
