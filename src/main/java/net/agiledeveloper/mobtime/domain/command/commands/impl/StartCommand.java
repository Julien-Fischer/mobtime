package net.agiledeveloper.mobtime.domain.command.commands.impl;

import net.agiledeveloper.mobtime.domain.command.commands.AbstractCommand;
import net.agiledeveloper.mobtime.domain.command.parameters.Parameter;
import net.agiledeveloper.mobtime.domain.command.parameters.ValueParameter;
import net.agiledeveloper.mobtime.domain.command.parameters.impl.*;
import net.agiledeveloper.mobtime.domain.session.FocusMode;
import net.agiledeveloper.mobtime.domain.session.Session;
import net.agiledeveloper.mobtime.domain.session.SessionService;
import net.agiledeveloper.mobtime.infra.roaming.Roaming;
import net.agiledeveloper.mobtime.utils.App;

import java.time.Duration;
import java.util.Set;

import static net.agiledeveloper.mobtime.utils.TimeFormatter.formatDuration;

public class StartCommand extends AbstractCommand {

    private final SessionService sessionService;
    private final Roaming roaming;

    private Duration durationCache = null;
    private FocusMode focusCache = null;


    public StartCommand(Set<Parameter> parameters, SessionService sessionService, Roaming roaming) {
        super(parameters);
        this.sessionService = sessionService;
        this.roaming = roaming;
    }


    @Override
    public void execute() {
        mobStart();
    }

    public boolean isAutoNextModeEnabled() {
        return hasOption(AutoNextParameter.class);
    }

    public boolean isDryRunEnabled() {
        return hasOption(DryRunParameter.class);
    }

    public boolean hasFocus(FocusMode mode) {
        return getFocusMode() == mode;
    }

    public boolean isRelocateEnabled() {
        return hasOption(RelocateParameter.class);
    }

    public Duration getDuration() {
        if (durationCache == null) {
            durationCache = findDuration();
            startRoaming(durationCache);
        }
        return durationCache;
    }

    public FocusMode getFocusMode() {
        if (focusCache == null) {
            focusCache = findFocusMode();
        }
        return focusCache;
    }


    private void mobStart() {
        if (!isDryRunEnabled()) {
            var session = new Session(getDuration(), isAutoNextModeEnabled(), findFocusMode(), findUserName());
            sessionService.open(session);
        }
    }

    private Duration findDuration() {
        var specifiedDuration = getArgumentDuration();

        if (roaming.isPausable()) {
            if (roaming.hasOngoingActivity()) {
                Duration duration = roaming.getActivityDuration().orElseThrow();
                Duration remaining = roaming.getActivityRemaining().orElseThrow();
                App.logger.debug("[roaming] Activity duration: " + formatDuration(duration));
                App.logger.debug("[roaming] Remaining activity duration: " + formatDuration(remaining));
                return remaining;
            } else {
                App.logger.debug("[roaming] Starting new activity with duration: " + formatDuration(specifiedDuration));
                return specifiedDuration;
            }
        }

        return specifiedDuration;
    }

    private void startRoaming(Duration duration) {
        if (roaming.isPausable() && !roaming.hasOngoingActivity()) {
            App.logger.debug("[roaming] Starting new activity with duration: " + formatDuration(duration));
            doStartRoaming(duration);
        }
    }

    private void doStartRoaming(Duration duration) {
        roaming.setActivityDuration(duration);
    }

    private Duration getArgumentDuration() {
        return valueOf(DurationParameter.class, Session.DEFAULT_DURATION);
    }

    private FocusMode findFocusMode() {
        return valueOf(FocusModeParameter.class, Session.DEFAULT_FOCUS_MODE);
    }

    private String findUserName() {
        return valueOf(UserNameParameter.class, Session.DEFAULT_USERNAME);
    }

    private <V, P extends ValueParameter<V>> V valueOf(Class<P> parameterType, V defaultValue) {
        return options().getValue(parameterType, defaultValue);
    }

}
