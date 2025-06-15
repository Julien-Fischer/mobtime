package net.agiledeveloper.mobtime.domain.command;

import net.agiledeveloper.App;
import net.agiledeveloper.mobtime.domain.command.commands.Command;
import net.agiledeveloper.mobtime.domain.command.commands.impl.StartCommand;
import net.agiledeveloper.mobtime.domain.command.parameters.Parameter;
import net.agiledeveloper.mobtime.domain.command.parameters.impl.*;
import net.agiledeveloper.mobtime.domain.ports.spi.SessionStorage;
import net.agiledeveloper.mobtime.domain.session.FocusMode;
import net.agiledeveloper.mobtime.domain.session.Session;
import net.agiledeveloper.mobtime.domain.session.Username;
import net.agiledeveloper.mobtime.infra.cli.BashParameter;
import net.agiledeveloper.mobtime.infra.swing.gui.Location;
import net.agiledeveloper.mobtime.utils.AppLogger.Level;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static net.agiledeveloper.mobtime.utils.EnumUtils.printValues;

public class CommandLineInterpreter {

    private final SessionStorage sessionStorage;

    private final Set<Parameter> parameters = new HashSet<>();

    public CommandLineInterpreter(SessionStorage sessionStorage) {
        this.sessionStorage = sessionStorage;
    }


    public Command interpret(List<BashParameter> commandLine) {
        App.logger.logSeparator();
        App.logger.log("MobTime");
        App.logger.log("Input parameters:");

        Command command = parse(commandLine);

        App.logger.logSeparator();
        printParameters(command);

        apply(command);
        return command;
    }

    private Command parse(List<BashParameter> commandLine) {
        var command = readCommand(commandLine);

        for (int i = 1; i < commandLine.size(); i++) {
            var parameter = commandLine.get(i);
            App.logger.log(" ", parameter.toString());

            parameters.add(readParameter(parameter));
        }
        return command;
    }

    private Command readCommand(List<BashParameter> commandLine) {
        var first = requireNonNull(commandLine).getFirst();
        if (first == null || !first.hasName("start")) {
            throw new IllegalArgumentException("No command specified");
        }
        App.logger.log(" ", "--start");
        return new StartCommand(parameters, sessionStorage);
    }

    private Parameter readParameter(BashParameter parameter) {
        return switch (parameter.name()) {
            case "dry-run"      -> new DryRunParameter();
            case "duration"     -> new DurationParameter(readDuration(parameter));
            case "auto-next"    -> new AutoNextParameter();
            case "mode"         -> new FocusModeParameter(readFocus(parameter));
            case "user-name"    -> new UserNameParameter(readUserName(parameter));
            case "pausable"     -> new PausableParameter();
            case "reset"        -> new ResetParameter();
            case "log-level"    -> new LogLevelParameter(readLogLevel(parameter));
            case "mini"         -> new MinimizeParameter();
            case "relocate"     -> new RelocateParameter();
            case "location"     -> new LocationParameter(readLocation(parameter));
            default             -> throw new IllegalArgumentException(format(
                    "Error: --%s is not a valid argument",
                    parameter.name()
            ));
        };
    }

    private void apply(Command command) {
        if (command.hasOption(PausableParameter.class)) {
            sessionStorage.setPausable(true);
        }
        var option = command.getOption(LogLevelParameter.class);
        if (option.isPresent() && option.get() instanceof LogLevelParameter level) {
            App.logger.setLevel(level.value());
        }
    }

    private static void printParameters(Command command) {
        App.logger.log("Command parameters:");
        command.options()
                .forEach(option -> App.logger.log(" ", option.toString()));
    }


    private static Username readUserName(BashParameter argument) {
        return argument.hasValue() ? new Username(argument.value()) : Session.DEFAULT_USERNAME;
    }

    private static Location readLocation(BashParameter argument) {
        var location = Location.NORTH_EAST;
        if (argument.hasValue()) {
            try {
                location = Location.of(argument.value());
                if (location == null) {
                    var message = format(
                            "--location must be one of (%s). Received: %s",
                            printValues(Location.class), location
                    );
                    throw new IllegalArgumentException(message);
                }
            } catch (Exception cause) {
                reject(argument, cause);
            }
        }
        return location;
    }

    private static Level readLogLevel(BashParameter argument) {
        var level = Level.ERROR;
        if (argument.hasValue()) {
            try {
                level = Level.of(argument.value());
                if (level == null) {
                    var message = format(
                            "--log-level must be one of (%s). Received: %s",
                            printValues(Level.class), level
                    );
                    throw new IllegalArgumentException(message);
                }
            } catch (Exception cause) {
                reject(argument, cause);
            }
        }
        return level;
    }

    private static FocusMode readFocus(BashParameter argument) {
        var mode = Session.DEFAULT_FOCUS_MODE;
        if (argument.hasValue()) {
            try {
                mode = FocusMode.of(argument.value());
                if (mode == null) {
                    var message = format(
                            "--focus must be one of (%s). Received: %s",
                            printValues(FocusMode.class), mode
                    );
                    throw new IllegalArgumentException(message);
                }
            } catch (Exception cause) {
                reject(argument, cause);
            }
        }
        return mode;
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
