package net.agiledeveloper.mobtime.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.stream.Stream;

import static net.agiledeveloper.mobtime.utils.DurationFormatter.formatDuration;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class DurationFormatterTest {

    static Stream<Arguments> invalidDurations() {
        return Stream.of(
                Arguments.of(Duration.ofMinutes(0).plusSeconds(2),   "00:02"),
                Arguments.of(Duration.ofMinutes(0).plusSeconds(20),  "00:20"),
                Arguments.of(Duration.ofMinutes(2),                  "02:00"),
                Arguments.of(Duration.ofMinutes(2).plusSeconds(31),  "02:31"),
                Arguments.of(Duration.ofMinutes(22).plusSeconds(31), "22:31")
        );
    }


    @ParameterizedTest
    @MethodSource("invalidDurations")
    void app_with_invalid_duration_throws(Duration duration, String formattedTime) {
        assertThat(formatDuration(duration))
                .isEqualTo(formattedTime);
    }

}
