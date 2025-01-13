package net.agiledeveloper.mobtime.utils;

import org.junit.jupiter.api.Test;

import static net.agiledeveloper.mobtime.utils.TimeConverter.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TimeConverterTest {

    @Test
    void minutesToSeconds_returns_seconds() {
        var minutes = 1;

        assertThat(minutesToSeconds(minutes))
                .isEqualTo(60);
    }

    @Test
    void minutesToMillis_returns_millis() {
        var minutes = 1;

        assertThat(minutesToMillis(minutes))
                .isEqualTo(60000);
    }

    @Test
    void secondsToMinutes_returns_minutes() {
        var seconds = 60;

        assertThat(secondsToMinutes(seconds))
                .isEqualTo(1);
    }

    @Test
    void secondsToMillis_returns_millis() {
        var seconds = 1;

        assertThat(secondsToMillis(seconds))
                .isEqualTo(1000);
    }

    @Test
    void millisToMinutes_returns_minutes() {
        var millis = 60000;

        assertThat(millisToMinutes(millis))
                .isEqualTo(1);
    }

    @Test
    void millisToSeconds_returns_seconds() {
        var millis = 2000;

        assertThat(millisToSeconds(millis))
                .isEqualTo(2);
    }

}
