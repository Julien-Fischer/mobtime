package net.agiledeveloper.mobtime.infra;

import net.agiledeveloper.mobtime.domain.command.commands.Command;
import net.agiledeveloper.mobtime.domain.command.commands.impl.StartCommand;
import net.agiledeveloper.mobtime.domain.command.parameters.Parameter;
import net.agiledeveloper.mobtime.domain.command.parameters.impl.DryRunParameter;
import net.agiledeveloper.mobtime.domain.command.parameters.impl.DurationParameter;
import net.agiledeveloper.mobtime.domain.session.Session;
import net.agiledeveloper.mobtime.domain.session.SessionService;
import net.agiledeveloper.mobtime.utils.AppLogger;

import java.time.Duration;
import java.util.HashSet;
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

        Set<Parameter> parameters = new HashSet<>();
        Command command = null;

        AppLogger.logSeparator();
        AppLogger.log("Starting MobTime with parameters:");
        for (var argument : commandLine) {
            AppLogger.log(" ", argument);

            if (argument.equals("--start")) {
                command = new StartCommand(parameters, sessionService);
            }

            else if (argument.equals("--dry-run")) {
                parameters.add(new DryRunParameter());
            }

            else if (argument.startsWith("--duration")) {
                parameters.add(new DurationParameter(readDuration(argument)));
            }

            else if (argument.startsWith("--invalid")) {
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


    private static Duration readDuration(String argument) {
        String[] split = argument.split("=");
        var seconds = Session.DEFAULT_DURATION_SECONDS;
        if (split.length == 2) {
            try {
                seconds = Integer.parseInt(split[1]);
                if (seconds < 0) {
                    throw new IllegalArgumentException("--duration can not be negative. Received: " + seconds);
                }
            } catch (Exception cause) {
                reject(argument, cause);
            }
        }
        return Duration.ofSeconds((long) minutesToSeconds(seconds));
    }

    private static void reject(String argument, Throwable cause) {
        throw new IllegalArgumentException("Invalid duration parameter: " + argument, cause);
    }

}
