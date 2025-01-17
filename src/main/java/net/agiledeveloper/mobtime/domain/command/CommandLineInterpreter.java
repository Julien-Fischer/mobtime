package net.agiledeveloper.mobtime.domain.command;

import net.agiledeveloper.mobtime.domain.command.commands.Command;
import net.agiledeveloper.mobtime.domain.command.commands.impl.StartCommand;
import net.agiledeveloper.mobtime.domain.command.parameters.Parameter;
import net.agiledeveloper.mobtime.domain.command.parameters.impl.AutoModeParameter;
import net.agiledeveloper.mobtime.domain.command.parameters.impl.DryRunParameter;
import net.agiledeveloper.mobtime.domain.command.parameters.impl.DurationParameter;
import net.agiledeveloper.mobtime.domain.command.parameters.impl.ZenParameter;
import net.agiledeveloper.mobtime.domain.session.Session;
import net.agiledeveloper.mobtime.domain.session.SessionService;
import net.agiledeveloper.mobtime.infra.cli.BashParameter;
import net.agiledeveloper.mobtime.utils.AppLogger;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandLineInterpreter {

    private final SessionService sessionService;


    public CommandLineInterpreter(SessionService sessionService) {
        this.sessionService = sessionService;
    }


    public Command interpret(List<BashParameter> commandLine) {
        Set<Parameter> parameters = new HashSet<>();
        Command command = null;

        AppLogger.logSeparator();
        AppLogger.log("Starting MobTime with parameters:");
        for (var parameter : commandLine) {
            AppLogger.log(" ", parameter.toString());

            if (parameter.hasName("start")) {
                command = new StartCommand(parameters, sessionService);
            }

            else if (parameter.hasName("dry-run")) {
                parameters.add(new DryRunParameter());
            }

            else if (parameter.hasName("duration")) {
                parameters.add(new DurationParameter(readDuration(parameter)));
            }

            else if (parameter.hasName("auto")) {
                parameters.add(new AutoModeParameter());
            }

            else if (parameter.hasName("zen")) {
                parameters.add(new ZenParameter());
            }

            else if (parameter.hasName("invalid")) {
                var msg = "Error: --invalid is not a valid argument";
                AppLogger.log(msg);
                throw new IllegalArgumentException(msg);
            }
        }

        if (command == null) {
            throw new IllegalArgumentException("No command specified");
        }

        AppLogger.logSeparator();
        AppLogger.log("Command parameters:");
        for (var parameter : command.parameters()) {
            AppLogger.log(" ", parameter.toString());
        }
        return command;
    }


    private static Duration readDuration(BashParameter argument) {
        var minutes = Session.DEFAULT_DURATION.toMinutes();
        if (argument.hasValue()) {
            try {
                minutes = Integer.parseInt(argument.value());
                if (minutes < 0) {
                    throw new IllegalArgumentException("--duration can not be negative. Received: " + minutes);
                }
            } catch (Exception cause) {
                reject(argument, cause);
            }
        }
        return Duration.ofMinutes(minutes);
    }

    private static void reject(BashParameter argument, Throwable cause) {
        var message = "Invalid " + argument.name();
        if (argument.hasValue()) {
            message += " parameter: " + argument.value();
        }
        throw new IllegalArgumentException(message, cause);
    }

}
