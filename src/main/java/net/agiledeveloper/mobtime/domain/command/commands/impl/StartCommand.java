package net.agiledeveloper.mobtime.domain.command.commands.impl;

import net.agiledeveloper.App;
import net.agiledeveloper.mobtime.domain.command.commands.AbstractCommand;
import net.agiledeveloper.mobtime.domain.command.parameters.Parameter;
import net.agiledeveloper.mobtime.domain.command.parameters.ValueParameter;
import net.agiledeveloper.mobtime.domain.command.parameters.impl.*;
import net.agiledeveloper.mobtime.domain.ports.spi.SessionStorage;
import net.agiledeveloper.mobtime.domain.session.EndMode;
import net.agiledeveloper.mobtime.domain.session.FocusMode;
import net.agiledeveloper.mobtime.domain.session.Session;
import net.agiledeveloper.mobtime.domain.session.Username;

import java.time.Clock;
import java.time.Duration;
import java.util.Set;

import static net.agiledeveloper.mobtime.domain.session.EndMode.AUTOMATICALLY_PASS_KEYBOARD;
import static net.agiledeveloper.mobtime.domain.session.EndMode.WAIT_FOR_INSTRUCTION;
import static net.agiledeveloper.mobtime.utils.TimeFormatter.formatDuration;

public class StartCommand extends AbstractCommand {

    private final SessionStorage roaming;

    private Duration durationCache = null;
    private FocusMode focusCache = null;


    public StartCommand(Set<Parameter> parameters, SessionStorage roaming) {
        super(parameters);
        this.roaming = roaming;
    }


    public EndMode getEndMode() {
        return isAutoNextModeEnabled() ? AUTOMATICALLY_PASS_KEYBOARD : WAIT_FOR_INSTRUCTION;
    }

    public boolean isAutoNextModeEnabled() {
        return hasOption(AutoNextParameter.class);
    }

    @Override
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
        App.logger.debug("[roaming] Duration cache: " + durationCache);
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

    public Session session() {
        return new Session(
                Clock.systemDefaultZone(),
                getDuration(),
                getEndMode(),
                findFocusMode(),
                findUserName()
        );
    }

    private Duration findDuration() {
        var specifiedDuration = getArgumentDuration();
        var reset = resetTimer();
        App.logger.debug("[roaming] Specified duration: " + formatDuration(specifiedDuration));
        App.logger.debug("[roaming] Force reset: " + reset);

        if (roaming.isPausable() && !reset) {
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

    private boolean resetTimer() {
        return hasOption(ResetParameter.class);
    }

    private Duration getArgumentDuration() {
        return valueOf(DurationParameter.class, Session.DEFAULT_DURATION);
    }

    private FocusMode findFocusMode() {
        return valueOf(FocusModeParameter.class, Session.DEFAULT_FOCUS_MODE);
    }

    private Username findUserName() {
        return valueOf(UserNameParameter.class, Session.DEFAULT_USERNAME);
    }

    private <V, P extends ValueParameter<V>> V valueOf(Class<P> parameterType, V defaultValue) {
        return options().getValue(parameterType, defaultValue);
    }

}
