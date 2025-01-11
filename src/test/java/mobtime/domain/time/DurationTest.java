package mobtime.domain.time;

import org.assertj.core.data.Offset;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class DurationTest {

    static final Duration SIXTY_MINUTES   = new Duration(   60, ChronoUnit.MINUTES);
    static final Duration ONE_MINUTE      = new Duration(    1, ChronoUnit.MINUTES);
    static final Duration SIXTY_SECONDS   = new Duration(   60, ChronoUnit.SECONDS);
    static final Duration THIRTY_SECONDS  = new Duration(   30, ChronoUnit.SECONDS);
    static final Duration THOUSAND_MILLIS = new Duration( 1000, ChronoUnit.MILLIS);

    static Stream<Arguments> millisConversionTable() {
        return Stream.of(
                Arguments.of(SIXTY_MINUTES,   3600000),
                Arguments.of(ONE_MINUTE,       60000),
                Arguments.of(THIRTY_SECONDS,   30000),
                Arguments.of(SIXTY_SECONDS,    60000),
                Arguments.of(THOUSAND_MILLIS,   1000)
        );
    }

    static Stream<Arguments> secondConversionTable() {
        return Stream.of(
                Arguments.of(SIXTY_MINUTES,  3600),
                Arguments.of(ONE_MINUTE,       60),
                Arguments.of(THIRTY_SECONDS,   30),
                Arguments.of(SIXTY_SECONDS,    60),
                Arguments.of(THOUSAND_MILLIS,   1)
        );
    }

    static Stream<Arguments> minuteConversionTable() {
        return Stream.of(
                Arguments.of(SIXTY_MINUTES,    60),
                Arguments.of(ONE_MINUTE,        1),
                Arguments.of(THIRTY_SECONDS,    0.5),
                Arguments.of(SIXTY_SECONDS,     1),
                Arguments.of(THOUSAND_MILLIS,   0.016666)
        );
    }

    @ParameterizedTest
    @MethodSource("minuteConversionTable")
    void get_minutes_returns_duration_in_minutes(Duration duration, double expected) {
        assertThat(duration.getMinutes())
                .isCloseTo(expected, Offset.offset(0.01));
    }

    @ParameterizedTest
    @MethodSource("secondConversionTable")
    void get_seconds_returns_duration_in_seconds(Duration duration, int expected) {
        assertThat(duration.getSeconds())
                .isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("millisConversionTable")
    void get_millis_returns_duration_in_milliseconds(Duration duration, int expected) {
        assertThat(duration.getMillis())
                .isEqualTo(expected);
    }

}
