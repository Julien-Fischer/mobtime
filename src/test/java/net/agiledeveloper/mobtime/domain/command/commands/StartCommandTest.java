package net.agiledeveloper.mobtime.domain.command.commands;

import net.agiledeveloper.mobtime.domain.command.commands.impl.StartCommand;
import net.agiledeveloper.mobtime.domain.command.parameters.Parameter;
import net.agiledeveloper.mobtime.domain.command.parameters.impl.AutoModeParameter;
import net.agiledeveloper.mobtime.domain.command.parameters.impl.DryRunParameter;
import net.agiledeveloper.mobtime.domain.command.parameters.impl.DurationParameter;
import net.agiledeveloper.mobtime.domain.command.parameters.impl.ZenParameter;
import net.agiledeveloper.mobtime.domain.session.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import static net.agiledeveloper.mobtime.test.Builders.aDurationParameter;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class StartCommandTest {

    private static final Duration FIVE_MINUTES = Duration.ofMinutes(5);
    private static final Duration TEN_SECONDS = Duration.of(10, ChronoUnit.SECONDS);
    private static final Duration ONE_MILLISECOND = Duration.of(1, ChronoUnit.MILLIS);

    private StartCommand command;

    @BeforeEach
    void setUp() {
        command = null;
    }


    @Test
    void isDryRunEnabled_when_enabled_returns_true() {
        havingParameters(new DryRunParameter(), aDurationParameter());

        assertThat(command.isDryRunEnabled()).isTrue();
    }

    @Test
    void isDryRunEnabled_when_not_enabled_returns_false() {
        havingParameters(aDurationParameter());

        assertThat(command.isDryRunEnabled()).isFalse();
    }

    @Test
    void isAutoModeEnabled_when_not_enabled_returns_false() {
        havingParameters(aDurationParameter());

        assertThat(command.isAutoModeEnabled()).isFalse();
    }

    @Test
    void isAutoModeEnabled_when_enabled_returns_true() {
        havingParameters(aDurationParameter(), new AutoModeParameter());

        assertThat(command.isAutoModeEnabled()).isTrue();
    }

    @Test
    void isZenModeEnabled_when_not_enabled_returns_false() {
        havingParameters(aDurationParameter());

        assertThat(command.isZenModeEnabled()).isFalse();
    }

    @Test
    void isZenModeEnabled_when_enabled_returns_true() {
        havingParameters(aDurationParameter(), new ZenParameter());

        assertThat(command.isZenModeEnabled()).isTrue();
    }

    @Test
    void getDuration_when_present_returns_specified_duration() {
        havingParameters(
                new DryRunParameter(),
                new DurationParameter(FIVE_MINUTES)
        );

        var duration = command.getDuration();

        assertThat(duration)
                .isEqualTo(FIVE_MINUTES);
    }

    @Test
    void getDuration_when_absent_returns_default_duration() {
        havingParameters(new DryRunParameter());

        var duration = command.getDuration();

        assertThat(duration)
                .isEqualTo(Session.DEFAULT_DURATION);
    }

    @Test
    void getDuration_returns_duration_in_minutes() {
        havingParameters(new DurationParameter(FIVE_MINUTES));

        var duration = command.getDuration();

        assertThat(duration.toMinutes())
                .isEqualTo(5);
    }

    @Test
    void getDuration_returns_duration_in_seconds() {
        havingParameters(new DurationParameter(TEN_SECONDS));

        var duration = command.getDuration();

        assertThat(duration.getSeconds())
                .isEqualTo(10);
    }

    @Test
    void getDuration_returns_duration_in_milliseconds() {
        havingParameters(new DurationParameter(ONE_MILLISECOND));

        var duration = command.getDuration();

        assertThat(duration.toMillis())
                .isEqualTo(1);
    }

    void havingParameters(Parameter... parameter) {
        command = new StartCommand(Set.of(parameter), null);
    }

}
