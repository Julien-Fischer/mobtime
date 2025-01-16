package net.agiledeveloper.mobtime.domain.command.commands.impl;

import net.agiledeveloper.mobtime.domain.command.commands.AbstractCommand;
import net.agiledeveloper.mobtime.domain.command.parameters.Parameter;
import net.agiledeveloper.mobtime.domain.command.parameters.impl.AutoModeParameter;
import net.agiledeveloper.mobtime.domain.command.parameters.impl.DryRunParameter;
import net.agiledeveloper.mobtime.domain.command.parameters.impl.DurationParameter;
import net.agiledeveloper.mobtime.domain.command.parameters.impl.ZenParameter;
import net.agiledeveloper.mobtime.domain.session.Session;
import net.agiledeveloper.mobtime.domain.session.SessionService;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

public class StartCommand extends AbstractCommand {

    private final SessionService sessionService;

    private Duration durationCache = null;


    public StartCommand(Set<Parameter> parameters, SessionService sessionService) {
        super(parameters);
        this.sessionService = sessionService;
    }


    @Override
    public void execute() {
        mobStart();
    }

    public boolean isZenModeEnabled() {
        return has(ZenParameter.class);
    }

    public boolean isAutoModeEnabled() {
        return has(AutoModeParameter.class);
    }

    public boolean isDryRunEnabled() {
        return has(DryRunParameter.class);
    }

    public Duration getDuration() {
        if (durationCache == null) {
            durationCache = findDuration();
        }
        return durationCache;
    }


    private void mobStart() {
        if (!isDryRunEnabled()) {
            var session = new Session(getDuration(), isAutoModeEnabled(), isZenModeEnabled());
            sessionService.open(session);
        }
    }

    private Duration findDuration() {
        Optional<Parameter> duration = get(DurationParameter.class);
        if (duration.isPresent()) {
            var durationParameter = (DurationParameter) duration.get();
            return durationParameter.value();
        } else {
            return Session.DEFAULT_DURATION;
        }
    }

}
