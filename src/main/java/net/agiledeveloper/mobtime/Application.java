package net.agiledeveloper.mobtime;

import net.agiledeveloper.mobtime.domain.command.CommandLineInterpreter;
import net.agiledeveloper.mobtime.domain.command.commands.Command;
import net.agiledeveloper.mobtime.domain.ports.api.SessionPort;
import net.agiledeveloper.mobtime.domain.ports.spi.MobPort;
import net.agiledeveloper.mobtime.domain.ports.spi.NotificationPort;
import net.agiledeveloper.mobtime.domain.session.MobService;
import net.agiledeveloper.mobtime.domain.session.SessionService;
import net.agiledeveloper.mobtime.infra.cli.BashParameter;
import net.agiledeveloper.mobtime.infra.cli.CommandLineParser;
import net.agiledeveloper.mobtime.infra.swing.SwingNotificationAdapter;
import net.agiledeveloper.mobtime.infra.swing.SwingWorkerTimeAdapter;
import net.agiledeveloper.mobtime.infra.swing.gui.Location;
import net.agiledeveloper.mobtime.infra.swing.gui.SwingPopup;
import net.agiledeveloper.mobtime.utils.App;

import java.util.List;
import java.util.function.Supplier;

public class Application {

    private final MobPort mobPort;

    public Application(MobPort mobPort) {
        this.mobPort = mobPort;
    }

    public void process(String[] commandLine) {
        var parser = new CommandLineParser();
        List<BashParameter> bashParameters = getOrThrow(() -> parser.parse(commandLine));

        var mobService = new MobService(mobPort);
        var notificationAdapter = createNotificationAdapter(mobService, bashParameters);
        var sessionService = new SessionService(new SwingWorkerTimeAdapter(), notificationAdapter, mobPort);

        var handler = new CommandLineInterpreter(sessionService);
        Command command = getOrThrow(() -> handler.interpret(bashParameters));

        command.execute();

        App.logger.log("Command processed");
    }


    private <T> T getOrThrow(Supplier<T> procedure) {
        try {
            return procedure.get();
        } catch (Exception exception) {
            logError(exception.getMessage());
            mobPort.done();
            throw exception;
        }
    }

    private static void logError(String message) {
        var errorSeparator = "/!\\ ".repeat(20);
        App.logger.logSeparator(errorSeparator);
        App.logger.err("Error parsing command");
        App.logger.err("E: " + message);
        App.logger.err("Closing mob session...");
        App.logger.logSeparator(errorSeparator);
    }

    private static boolean shouldMinimize(List<BashParameter> bashParameters) {
        return !bashParameters.stream()
                .filter(param -> param.hasName("mini"))
                .toList()
                .isEmpty();
    }

    private static Location getLocation(List<BashParameter> bashParameters) {
        for (BashParameter bashParameter : bashParameters) {
            if (bashParameter.hasName("location") && bashParameter.hasValue()) {
                return Location.of(bashParameter.value());
            }
        }
        return SwingPopup.DEFAULT_LOCATION;
    }

    private static NotificationPort createNotificationAdapter(SessionPort sessionPort, List<BashParameter> bashParameters) {
        return new SwingNotificationAdapter(sessionPort, shouldMinimize(bashParameters), getLocation(bashParameters));
    }

}
