package net.agiledeveloper.mobtime.domain.command.commands;

import net.agiledeveloper.mobtime.domain.command.commands.impl.StartCommand;
import net.agiledeveloper.mobtime.domain.command.parameters.Parameter;
import net.agiledeveloper.mobtime.domain.command.parameters.impl.DryRunParameter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static net.agiledeveloper.mobtime.test.builders.DurationParameterBuilder.aDurationParameter;
import static net.agiledeveloper.mobtime.test.builders.StartCommandBuilder.aStartCommand;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CommandTest {

    private Command command;

    @BeforeEach
    void setUp() {
        command = null;
    }

    @Test
    void is_when_same_class_returns_true() {
        var startCommand = aStartCommand().build();

        assertThat(startCommand.is(StartCommand.class)).isTrue();
    }

    @Test
    void is_when_different_class_returns_false() {
        var startCommand = aStartCommand().build();

        assertThat(startCommand.is(Command.class)).isFalse();
        assertThat(startCommand.is(MockCommand.class)).isFalse();
    }

    @Test
    void has_when_parameter_present_returns_true() {
        havingParameters(new DryRunParameter());

        var hasParameter = command.hasOption(DryRunParameter.class);

        assertThat(hasParameter).isTrue();
    }

    @Test
    void has_when_parameter_absent_returns_true() {
        havingNoOptions();

        var hasParameter = command.hasOption(DryRunParameter.class);

        assertThat(hasParameter).isFalse();
    }

    @Test
    void get_when_parameter_present_returns_optional() {
        havingParameters(aDurationParameter().build(), new DryRunParameter());

        var optionalParameter = command.getOption(DryRunParameter.class);

        assertThat(optionalParameter).isPresent();
    }

    @Test
    void get_when_parameter_absent_returns_empty_optional() {
        havingNoOptions();

        var optionalParameter = command.getOption(DryRunParameter.class);

        assertThat(optionalParameter).isEmpty();
    }


    void havingParameters(Parameter... parameter) {
        command = new StartCommand(Set.of(parameter), null);
    }

    void havingNoOptions() {
        command = new StartCommand(Collections.emptySet(), null);
    }


    private static class MockCommand implements Command {

        @Override
        public boolean isDryRunEnabled() {
            return true;
        }

        @Override
        public OptionSet options() {
            return OptionSet.empty();
        }

    }

}
