package net.agiledeveloper.mobtime.orchestrator;

import net.agiledeveloper.App;
import net.agiledeveloper.mobtime.domain.command.CommandLineInterpreter;
import net.agiledeveloper.mobtime.domain.command.UIOptionSet;
import net.agiledeveloper.mobtime.domain.command.commands.Command;
import net.agiledeveloper.mobtime.domain.ports.spi.MobPort;
import net.agiledeveloper.mobtime.domain.ports.spi.NotificationPort;
import net.agiledeveloper.mobtime.domain.ports.spi.SessionStorage;
import net.agiledeveloper.mobtime.domain.session.MobService;
import net.agiledeveloper.mobtime.domain.session.SessionService;
import net.agiledeveloper.mobtime.infra.cli.BashParameter;
import net.agiledeveloper.mobtime.infra.cli.CommandLineParser;
import net.agiledeveloper.mobtime.infra.notification.CompositeNotificationAdapter;
import net.agiledeveloper.mobtime.infra.notification.LoggerNotificationAdapter;
import net.agiledeveloper.mobtime.infra.notification.SwingNotificationAdapter;
import net.agiledeveloper.mobtime.infra.roaming.FileSessionStorage;
import net.agiledeveloper.mobtime.infra.swing.SwingTimerAdapter;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

public class Application {

    private static final Path ROAMING_FILE = getAppDirectory().resolve("roaming");

    private final MobPort mobPort;
    private final SessionStorage sessionRepository;


    public Application(MobPort mobPort) {
        this.mobPort = mobPort;
        this.sessionRepository = new FileSessionStorage(ROAMING_FILE);
    }


    public void process(String[] commandLine) {
        var commandLineParser = new CommandLineParser();
        List<BashParameter> bashParameters = getOrThrow(() -> commandLineParser.parse(commandLine));
        var options = new UIOptionSet(bashParameters);

        var mobService = new MobService(mobPort);
        var notificationAdapter = getNotificationAdapter(mobService, options);
        var sessionService = new SessionService(new SwingTimerAdapter(), notificationAdapter, mobPort);

        var handler = new CommandLineInterpreter(sessionRepository);
        Command command = getOrThrow(() -> handler.interpret(bashParameters));

        if (!command.isDryRunEnabled()) {
            sessionService.execute(command);
        }

        App.logger.log("Command processed");
    }

    private NotificationPort getNotificationAdapter(MobService mobService, UIOptionSet options) {
        return new CompositeNotificationAdapter(
                new SwingNotificationAdapter(mobService, sessionRepository, options),
                new LoggerNotificationAdapter(App.logger)
        );
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
