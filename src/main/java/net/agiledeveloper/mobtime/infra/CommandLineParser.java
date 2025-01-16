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

import static net.agiledeveloper.mobtime.utils.TimeConverter.minutesToSeconds;

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
        for (var argument : bashParameters) {
            AppLogger.log(" ", argument.toString());

            if (argument.hasName("start")) {
                command = new StartCommand(parameters, sessionService);
            }

            else if (argument.hasName("dry-run")) {
                parameters.add(new DryRunParameter());
            }

            else if (argument.hasName("duration")) {
                parameters.add(new DurationParameter(readDuration(argument)));
            }

            else if (argument.hasName("auto")) {
                parameters.add(new AutoModeParameter());
            }

            else if (argument.hasName("zen")) {
                parameters.add(new ZenParameter());
            }

            else if (argument.hasName("invalid")) {
                var msg = "Error: --invalid is not a valid argument";
                AppLogger.log(msg);
                throw new IllegalArgumentException(msg);
            }
        }

        if (command == null) {
            throw new IllegalArgumentException("No command specified");
        }

        return command;
    }


    private static Duration readDuration(BashParameter argument) {
        var seconds = Session.DEFAULT_DURATION_SECONDS;
        if (argument.hasValue()) {
            try {
                seconds = Integer.parseInt(argument.value());
                if (seconds < 0) {
                    throw new IllegalArgumentException("--duration can not be negative. Received: " + seconds);
                }
            } catch (Exception cause) {
                reject(argument, cause);
            }
        }
        return Duration.ofSeconds((long) minutesToSeconds(seconds));
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
