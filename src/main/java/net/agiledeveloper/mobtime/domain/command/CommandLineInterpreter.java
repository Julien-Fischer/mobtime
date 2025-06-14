package net.agiledeveloper.mobtime.domain.command;

import net.agiledeveloper.mobtime.domain.command.commands.Command;
import net.agiledeveloper.mobtime.domain.command.commands.impl.StartCommand;
import net.agiledeveloper.mobtime.domain.command.parameters.Parameter;
import net.agiledeveloper.mobtime.domain.command.parameters.impl.*;
import net.agiledeveloper.mobtime.domain.session.FocusMode;
import net.agiledeveloper.mobtime.domain.session.Session;
import net.agiledeveloper.mobtime.domain.session.SessionService;
import net.agiledeveloper.mobtime.domain.session.Username;
import net.agiledeveloper.mobtime.infra.cli.BashParameter;
import net.agiledeveloper.mobtime.infra.roaming.Roaming;
import net.agiledeveloper.mobtime.utils.App;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static net.agiledeveloper.mobtime.utils.EnumUtils.printValues;

public class CommandLineInterpreter {

    private final SessionService sessionService;
    private final Roaming roaming;

    private boolean pausable = false;


    public CommandLineInterpreter(SessionService sessionService, Roaming roaming) {
        this.sessionService = sessionService;
        this.roaming = roaming;
    }


    public Command interpret(List<BashParameter> commandLine) {
        Set<Parameter> parameters = new HashSet<>();
        Command command = null;

        App.logger.logSeparator();
        App.logger.log("MobTime");
        App.logger.log("Input parameters:");
        for (var parameter : commandLine) {
            App.logger.log(" ", parameter.toString());

            if (parameter.hasName("start")) {
                command = new StartCommand(parameters, sessionService, roaming);
            }

            else if (parameter.hasName("dry-run")) {
                parameters.add(new DryRunParameter());
            }

            else if (parameter.hasName("duration")) {
                parameters.add(new DurationParameter(readDuration(parameter)));
            }

            else if (parameter.hasName("auto-next")) {
                parameters.add(new AutoNextParameter());
            }

            else if (parameter.hasName("focus")) {
                parameters.add(new FocusModeParameter(readFocus(parameter)));
            }

            else if (parameter.hasName("user-name")) {
                parameters.add(new UserNameParameter(readUserName(parameter)));
            }

            else if (parameter.hasName("pausable")) {
                pausable = true;
            }

            else if (parameter.hasName("reset")) {
                parameters.add(new ResetParameter());
            }

            else if (parameter.hasName("invalid")) {
                var msg = "Error: --invalid is not a valid argument";
                App.logger.log(msg);
                throw new IllegalArgumentException(msg);
            }
        }

        if (command == null) {
            throw new IllegalArgumentException("No command specified");
        }

        roaming.setPausable(pausable);

        App.logger.logSeparator();
        App.logger.log("Command parameters:");
        command.options()
                .forEach(option -> App.logger.log(" ", option.toString()));
        return command;
    }


    private static Username readUserName(BashParameter argument) {
        return argument.hasValue() ? new Username(argument.value()) : Session.DEFAULT_USERNAME;
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
