package net.agiledeveloper.mobtime.infra;

import net.agiledeveloper.mobtime.domain.command.commands.Command;
import net.agiledeveloper.mobtime.domain.command.commands.impl.StartCommand;
import net.agiledeveloper.mobtime.domain.command.parameters.Parameter;
import net.agiledeveloper.mobtime.domain.command.parameters.impl.AutoModeParameter;
import net.agiledeveloper.mobtime.domain.command.parameters.impl.DryRunParameter;
import net.agiledeveloper.mobtime.domain.command.parameters.impl.DurationParameter;
import net.agiledeveloper.mobtime.domain.command.parameters.impl.ZenParameter;
import net.agiledeveloper.mobtime.domain.session.Session;
import net.agiledeveloper.mobtime.domain.session.SessionService;
import net.agiledeveloper.mobtime.utils.AppLogger;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandLineParser {

    private final SessionService sessionService;


    public CommandLineParser(SessionService sessionService) {
        this.sessionService = sessionService;
    }


    public Command parse(String[] commandLine) {
        if (commandLine == null) {
            throw new IllegalArgumentException("No command specified");
        }

        var parameters = Arrays.stream(commandLine)
                .map(BashParameter::new)
                .toList();

        return parseParameters(parameters);
    }


    private Command parseParameters(List<BashParameter> bashParameters) {
        Set<Parameter> parameters = new HashSet<>();
        Command command = null;

        AppLogger.logSeparator();
        AppLogger.log("Starting MobTime with parameters:");
        for (var parameter : bashParameters) {
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


    private static final class BashParameter implements Parameter {

        private final String[] split;

        BashParameter(String argument) {
            split = argument.substring(2).split(SEPARATOR);
            System.out.println("debug");
        }

        @Override
        public String name() {
            return split[0];
        }

        public String value() {
            return split[1];
        }

        public boolean hasName(String name) {
            return split[0].equals(name);
        }

        public boolean hasValue() {
            return split.length == 2;
        }

        @Override
        public String toString() {
            var subject = PREFIX + name();
            if (hasValue()) {
                subject += SEPARATOR + value();
            }
            return subject;
        }
    }

}
