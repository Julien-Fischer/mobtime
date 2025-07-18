package net.agiledeveloper.mobtime.domain.command.commands;

import net.agiledeveloper.mobtime.domain.command.commands.impl.StartCommand;
import net.agiledeveloper.mobtime.domain.command.parameters.Parameter;
import net.agiledeveloper.mobtime.domain.command.parameters.impl.*;
import net.agiledeveloper.mobtime.domain.session.Session;
import net.agiledeveloper.mobtime.infra.roaming.FileSessionStorage;
import net.agiledeveloper.mobtime.test.builders.DurationParameterBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.stream.Stream;

import static net.agiledeveloper.mobtime.domain.session.FocusMode.*;
import static net.agiledeveloper.mobtime.test.builders.DurationParameterBuilder.aDurationParameter;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class StartCommandTest {

    private static final Duration FIVE_MINUTES = Duration.ofMinutes(5);
    private static final Duration TEN_SECONDS = Duration.of(10, ChronoUnit.SECONDS);
    private static final Duration ONE_MILLISECOND = Duration.of(1, ChronoUnit.MILLIS);
    private static final Path ROAMING_FILE = Path.of("MOBTIME_ROAMING");

    private StartCommand command;

    @BeforeEach
    void setUp() {
        command = null;
    }

    @AfterEach
    void tearDown() throws IOException {
        if (Files.exists(ROAMING_FILE)) {
            Files.delete(ROAMING_FILE);
        }
    }

    @Test
    void isDryRunEnabled_when_enabled_returns_true() {
        havingParameters(new DryRunParameter(), aDurationParameter().build());

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

        assertThat(command.isAutoNextModeEnabled()).isFalse();
    }

    @Test
    void isAutoNextModeEnabled_when_enabled_returns_true() {
        havingParameters(aDurationParameter().build(), new AutoNextParameter());

        assertThat(command.isAutoNextModeEnabled()).isTrue();
    }

    @Test
    void isRelocateEnabled_when_enabled_returns_true() {
        havingParameters(aDurationParameter().build(), new RelocateParameter());

        assertThat(command.isRelocateEnabled()).isTrue();
    }

    @Test
    void hasFocus_when_not_specified_returns_normal_mode() {
        havingParameters(aDurationParameter());

        assertThat(command.hasFocus(ZEN)).isFalse();
        assertThat(command.hasFocus(CHILL)).isFalse();
        assertThat(command.hasFocus(NORMAL)).isTrue();
    }

    @Test
    void hasOptionFocus_determines_whether_specified_focus_is_set() {
        havingParameters(aDurationParameter().build(), new FocusModeParameter(ZEN));

        assertThat(command.hasFocus(ZEN)).isTrue();
        assertThat(command.hasFocus(CHILL)).isFalse();
        assertThat(command.hasFocus(NORMAL)).isFalse();
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
    void getOptionDuration_returns_duration_in_milliseconds() {
        havingParameters(new DurationParameter(ONE_MILLISECOND));

        var duration = command.getDuration();

        assertThat(duration.toMillis())
                .isEqualTo(1);
    }

    void havingParameters(Parameter... parameter) {
        command = new StartCommand(Set.of(parameter), new FileSessionStorage(ROAMING_FILE));
    }

    void havingParameters(DurationParameterBuilder... parameters) {
        havingParameters(Stream.of(parameters)
                .map(DurationParameterBuilder::build)
                .toArray(DurationParameter[]::new));
    }

}
