package net.agiledeveloper.mobtime.domain.ports.api;

import net.agiledeveloper.mobtime.domain.command.commands.Command;
import net.agiledeveloper.mobtime.domain.command.commands.impl.StartCommand;
import net.agiledeveloper.mobtime.domain.session.Session;

import static java.lang.String.format;

public interface SessionServicePort {

    default void execute(Command command) {
        if (command instanceof StartCommand startCommand) {
            if (!startCommand.isDryRunEnabled()) {
                open(startCommand.session());
            }
        } else {
            throw new UnsupportedOperationException(format("%s is not supported", command.getClass().getSimpleName()));
        }
    }

    void open(Session session);

    void close(Session session);

}
