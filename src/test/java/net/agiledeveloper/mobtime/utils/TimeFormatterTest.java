package net.agiledeveloper.mobtime.utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Stream;

import static java.time.LocalDateTime.parse;
import static java.time.ZonedDateTime.of;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static net.agiledeveloper.mobtime.utils.TimeFormatter.formatDuration;
import static net.agiledeveloper.mobtime.utils.TimeFormatter.formatInstant;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TimeFormatterTest {

    private static final ZoneId PARIS = ZoneId.of("Europe/Paris");

    @BeforeAll
    static void beforeAll() {
        TimeFormatter.setTimeZone(PARIS);
    }

    static Stream<Arguments> durations() {
        return Stream.of(
                Arguments.of(Duration.ofMinutes(0).plusSeconds(2),   "00:02"),
                Arguments.of(Duration.ofMinutes(0).plusSeconds(20),  "00:20"),
                Arguments.of(Duration.ofMinutes(2),                  "02:00"),
                Arguments.of(Duration.ofMinutes(2).plusSeconds(31),  "02:31"),
                Arguments.of(Duration.ofMinutes(22).plusSeconds(31), "22:31")
        );
    }

    static Stream<Arguments> instants() {
        return Stream.of(
                Arguments.of(at("2025-01-25T11:00:00"), "2025-01-25 11:00:00"),
                Arguments.of(at("2025-01-25T00:59:00"), "2025-01-25 00:59:00"),
                Arguments.of(at("2025-01-25T00:00:59"), "2025-01-25 00:00:59"),
                Arguments.of(at("2025-01-25T23:59:31"), "2025-01-25 23:59:31")
        );
    }


    @ParameterizedTest
    @MethodSource("durations")
    void formatDuration_returns_formatted_duration_as_string(Duration duration, String formattedTime) {
        assertThat(formatDuration(duration))
                .isEqualTo(formattedTime);
    }

    @ParameterizedTest
    @MethodSource("instants")
    void formatInstant_returns_formatted_instant_as_string(Instant instant, String formattedTime) {
        assertThat(formatInstant(instant))
                .isEqualTo(formattedTime);
    }


    private static Instant at(String dateString) {
        LocalDateTime datetime = parse(dateString, ISO_LOCAL_DATE_TIME);
        return of(datetime, PARIS).toInstant();
    }

}
