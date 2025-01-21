package net.agiledeveloper.mobtime.test.builders;

import net.agiledeveloper.mobtime.domain.command.commands.impl.StartCommand;
import net.agiledeveloper.mobtime.domain.command.parameters.Parameter;
import net.agiledeveloper.mobtime.domain.session.SessionService;
import net.agiledeveloper.mobtime.test.lib.Builder;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StartCommandBuilder implements Builder<StartCommand> {

    private SessionService sessionService;
    private Set<Parameter> parameters = new HashSet<>();


    public static StartCommandBuilder aStartCommand() {
        return new StartCommandBuilder();
    }

    public StartCommandBuilder usingService(final SessionService sessionService) {
        this.sessionService = sessionService;
        return this;
    }

    public StartCommandBuilder withParameters(Parameter... parameters) {
        this.parameters.addAll(Stream.of(parameters).collect(Collectors.toSet()));
        return this;
    }

    @Override
    public StartCommand build() {
        return new StartCommand(parameters, sessionService);
    }

}
