package mobtime.infra;

import mobtime.domain.command.commands.Command;
import mobtime.domain.command.commands.impl.StartCommand;
import mobtime.domain.command.parameters.Parameter;
import mobtime.domain.command.parameters.impl.DryRunParameter;
import mobtime.domain.command.parameters.impl.DurationParameter;
import mobtime.domain.session.SessionService;
import mobtime.domain.time.Duration;
import mobtime.utils.AppLogger;

import java.util.HashSet;
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

        Set<Parameter> parameters = new HashSet<>();
        Command command = null;

        for (var argument : commandLine) {
            AppLogger.log(argument);

            if (argument.equals("--start")) {
                command = new StartCommand(parameters, sessionService);
            }

            else if (argument.equals("--dry-run")) {
                parameters.add(new DryRunParameter());
            }

            else if (argument.startsWith("--duration")) {
                parameters.add(new DurationParameter(of(argument)));
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


    public static Duration of(String argument) {
        String[] split = argument.split("=");
        var amount = Duration.DEFAULT_VALUE_MINUTES;
        if (split.length == 2) {
            try {
                amount = Integer.parseInt(split[1]);
            } catch (Exception cause) {
                throw new IllegalArgumentException("Invalid duration parameter: " + argument, cause);
            }
        }
        return Duration.fromMinutes(amount);
    }

}
