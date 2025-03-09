package net.agiledeveloper.mobtime.domain.command.commands.impl;

import net.agiledeveloper.mobtime.domain.command.commands.AbstractCommand;
import net.agiledeveloper.mobtime.domain.command.parameters.Parameter;
import net.agiledeveloper.mobtime.domain.command.parameters.impl.*;
import net.agiledeveloper.mobtime.domain.session.FocusMode;
import net.agiledeveloper.mobtime.domain.session.Session;
import net.agiledeveloper.mobtime.domain.session.SessionService;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

public class StartCommand extends AbstractCommand {

    private final SessionService sessionService;

    private Duration durationCache = null;
    private FocusMode focusCache = null;


    public StartCommand(Set<Parameter> parameters, SessionService sessionService) {
        super(parameters);
        this.sessionService = sessionService;
    }


    @Override
    public void execute() {
        mobStart();
    }

    public boolean isAutoModeEnabled() {
        return has(AutoModeParameter.class);
    }

    public boolean isDryRunEnabled() {
        return has(DryRunParameter.class);
    }

    public boolean hasFocus(FocusMode mode) {
        return getFocusMode() == mode;
    }

    public boolean isAutoSaveEnabled() {
        return has(AutoSaveParameter.class);
    }

    public Duration getDuration() {
        if (durationCache == null) {
            durationCache = findDuration();
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
            var session = new Session(getDuration(), isAutoModeEnabled(), findFocusMode(), findUserName());
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

    private FocusMode findFocusMode() {
        Optional<Parameter> focusMode = get(FocusModeParameter.class);
        if (focusMode.isPresent()) {
            var focusParameter = (FocusModeParameter) focusMode.get();
            return focusParameter.value();
        } else {
            return Session.DEFAULT_FOCUS_MODE;
        }
    }

    private String findUserName() {
        Optional<Parameter> userName = get(UserNameParameter.class);
        if (userName.isPresent()) {
            var parameter = (UserNameParameter) userName.get();
            return parameter.value();
        } else {
            return Session.DEFAULT_USERNAME;
        }
    }

}
