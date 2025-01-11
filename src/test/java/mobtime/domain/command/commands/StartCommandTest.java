package mobtime.domain.command.commands;

import mobtime.domain.command.commands.impl.StartCommand;
import mobtime.domain.command.parameters.Parameter;
import mobtime.domain.command.parameters.impl.DryRunParameter;
import mobtime.domain.command.parameters.impl.DurationParameter;
import mobtime.domain.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.temporal.ChronoUnit;
import java.util.Set;

import static mobtime.test.Builders.aDurationParameter;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class StartCommandTest {

    private static final Duration FIVE_MINUTES = Duration.fromMinutes(5);
    private static final Duration TEN_SECONDS = new Duration(10, ChronoUnit.SECONDS);
    private static final Duration ONE_MILLISECOND = new Duration(1, ChronoUnit.MILLIS);

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
    void getDuration_when_present_returns_true() {
        havingParameters(
                new DryRunParameter(),
                new DurationParameter(FIVE_MINUTES)
        );

        var duration = command.getDuration();

        assertThat(duration)
                .isEqualTo(FIVE_MINUTES);
    }

    @Test
    void getDuration_when_absent_returns_false() {
        havingParameters(new DryRunParameter());

        var duration = command.getDuration();

        assertThat(duration.getMinutes())
                .isEqualTo(Duration.DEFAULT_VALUE_MINUTES);
    }

    @Test
    void getMinutes_returns_duration_in_minutes() {
        havingParameters(new DurationParameter(FIVE_MINUTES));

        var duration = command.getDuration();

        assertThat(duration.unit())
                .isEqualTo(ChronoUnit.MINUTES);
    }

    @Test
    void getSeconds_returns_duration_in_seconds() {
        havingParameters(new DurationParameter(TEN_SECONDS));

        var duration = command.getDuration();

        assertThat(duration.unit())
                .isEqualTo(ChronoUnit.SECONDS);
    }

    @Test
    void getMillis_returns_duration_in_milliseconds() {
        havingParameters(new DurationParameter(ONE_MILLISECOND));

        var duration = command.getDuration();

        assertThat(duration.unit())
                .isEqualTo(ChronoUnit.MILLIS);
    }


    void havingParameters(Parameter... parameter) {
        command = new StartCommand(Set.of(parameter), null);
    }

}
