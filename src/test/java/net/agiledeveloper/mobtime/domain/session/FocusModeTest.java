package net.agiledeveloper.mobtime.domain.session;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class FocusModeTest {

    static Stream<Arguments> focusModes() {
        return Stream.of(
                Arguments.of("normal",  FocusMode.NORMAL),
                Arguments.of("NORMAL",  FocusMode.NORMAL),
                Arguments.of("chill",   FocusMode.CHILL),
                Arguments.of("CHILL",   FocusMode.CHILL),
                Arguments.of("zen",     FocusMode.ZEN),
                Arguments.of("ZEN",     FocusMode.ZEN)
        );
    }


    @ParameterizedTest
    @MethodSource("focusModes")
    void of_returns_compass_instance(String subject, FocusMode focus) {
        assertThat(FocusMode.of(subject))
                .isEqualTo(focus);
    }

    @Test
    void of_when_null_throws_illegal_argument_exception() {
        assertThatThrownBy(() -> FocusMode.of(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void of_when_empty_throws_illegal_argument_exception() {
        assertThatThrownBy(() -> FocusMode.of(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void of_when_not_found_throws_illegal_argument_exception() {
        assertThatThrownBy(() -> FocusMode.of("invalid focus"))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
