package net.agiledeveloper.mobtime;

import net.agiledeveloper.mobtime.domain.command.CommandLineInterpreter;
import net.agiledeveloper.mobtime.domain.command.UIOptionSet;
import net.agiledeveloper.mobtime.domain.command.commands.Command;
import net.agiledeveloper.mobtime.domain.ports.spi.MobPort;
import net.agiledeveloper.mobtime.domain.session.MobService;
import net.agiledeveloper.mobtime.domain.session.SessionService;
import net.agiledeveloper.mobtime.infra.cli.BashParameter;
import net.agiledeveloper.mobtime.infra.cli.CommandLineParser;
import net.agiledeveloper.mobtime.infra.roaming.Roaming;
import net.agiledeveloper.mobtime.infra.swing.SwingNotificationAdapter;
import net.agiledeveloper.mobtime.infra.swing.SwingTimerAdapter;
import net.agiledeveloper.mobtime.utils.App;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

import static net.agiledeveloper.mobtime.utils.AppLogger.Level.DEBUG;

public class Application {

    private static final Path ROAMING_FILE = getAppDirectory().resolve("roaming");

    private final MobPort mobPort;
    private final Roaming roaming;


    public Application(MobPort mobPort) {
        this.mobPort = mobPort;
        this.roaming = new Roaming(ROAMING_FILE);
    }


    public void process(String[] commandLine) {
        var commandLineParser = new CommandLineParser();
        List<BashParameter> bashParameters = getOrThrow(() -> commandLineParser.parse(commandLine));
        var options = new UIOptionSet(bashParameters);

        if (options.isDebugModeEnabled()) {
            App.logger.setLevel(DEBUG);
        }

        var mobService = new MobService(mobPort);
        var notificationAdapter = new SwingNotificationAdapter(mobService, roaming, options);
        var sessionService = new SessionService(new SwingTimerAdapter(), notificationAdapter, mobPort);

        var handler = new CommandLineInterpreter(sessionService, roaming);
        Command command = getOrThrow(() -> handler.interpret(bashParameters));

        command.execute();

        App.logger.log("Command processed");
    }


    private <T> T getOrThrow(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception exception) {
            logError(exception.getMessage());
            mobPort.done();
            throw exception;
        }
    }

    private static void logError(String message) {
        var separator = "/!\\ ".repeat(20);
        App.logger.logSeparator(separator);
        App.logger.err("Error parsing command");
        App.logger.err("E: " + message);
        App.logger.err("Closing mob session...");
        App.logger.logSeparator(separator);
    }


    private static Path getAppDirectory() {
        String homeDirectory = System.getProperty("user.home");
        return Path.of(homeDirectory, "mobtime");
    }

}
