package net.agiledeveloper.mobtime.orchestrator;

import net.agiledeveloper.App;
import net.agiledeveloper.mobtime.domain.command.CommandLineInterpreter;
import net.agiledeveloper.mobtime.domain.command.UIOptionSet;
import net.agiledeveloper.mobtime.domain.command.commands.Command;
import net.agiledeveloper.mobtime.domain.ports.spi.MobPort;
import net.agiledeveloper.mobtime.domain.ports.spi.RoamingPort;
import net.agiledeveloper.mobtime.domain.session.MobService;
import net.agiledeveloper.mobtime.domain.session.SessionService;
import net.agiledeveloper.mobtime.infra.cli.BashParameter;
import net.agiledeveloper.mobtime.infra.cli.CommandLineParser;
import net.agiledeveloper.mobtime.infra.roaming.FileRoaming;
import net.agiledeveloper.mobtime.infra.swing.SwingNotificationAdapter;
import net.agiledeveloper.mobtime.infra.swing.SwingTimerAdapter;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

public class Application {

    private static final Path ROAMING_FILE = getAppDirectory().resolve("roaming");

    private final MobPort mobPort;
    private final RoamingPort roaming;


    public Application(MobPort mobPort) {
        this.mobPort = mobPort;
        this.roaming = new FileRoaming(ROAMING_FILE);
    }


    public void process(String[] commandLine) {
        var commandLineParser = new CommandLineParser();
        List<BashParameter> bashParameters = getOrThrow(() -> commandLineParser.parse(commandLine));
        var options = new UIOptionSet(bashParameters);

        var mobService = new MobService(mobPort);
        var notificationAdapter = new SwingNotificationAdapter(mobService, roaming, options);
        var sessionService = new SessionService(new SwingTimerAdapter(), notificationAdapter, mobPort);

        var handler = new CommandLineInterpreter(roaming);
        Command command = getOrThrow(() -> handler.interpret(bashParameters));

        sessionService.execute(command);

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
