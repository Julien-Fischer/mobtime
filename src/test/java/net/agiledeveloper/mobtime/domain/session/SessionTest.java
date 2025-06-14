package net.agiledeveloper.mobtime.domain.session;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;
import static net.agiledeveloper.mobtime.domain.session.FocusMode.NORMAL;
import static net.agiledeveloper.mobtime.domain.session.FocusMode.ZEN;
import static net.agiledeveloper.mobtime.test.builders.SessionBuilder.aSession;
import static org.assertj.core.api.Assertions.assertThat;

class SessionTest {

    @Test
    void grace_curation_defaults_to_two_seconds_when_not_set() {
        var session = aSession()
                .lasting(ofMinutes(20))
                .build();

        assertThat(session.graceDuration())
                .isEqualTo(Session.DEFAULT_GRACE_DURATION);
    }

    @Test
    void has_focus_returns_true_for_matching_mode() {
        var a = aSession()
                .withFocusMode(NORMAL)
                .build();

        assertThat(a.hasFocus(NORMAL)).isTrue();
        assertThat(a.hasFocus(ZEN)).isFalse();

        var b = aSession()
                .withFocusMode(ZEN)
                .build();

        assertThat(b.hasFocus(ZEN)).isTrue();
        assertThat(b.hasFocus(FocusMode.NORMAL)).isFalse();
    }

    @Test
    void grace_period_is_over_when_remaining_time_is_less_than_grace_duration() {
        var session = aSession()
                .lasting(ofSeconds(10))
                .build();

        Duration remaining = ofSeconds(9);
        assertThat(session.isGracePeriodOver(remaining)).isFalse();

        remaining = ofSeconds(8);
        assertThat(session.isGracePeriodOver(remaining)).isTrue();
    }

    @Test
    void a_session_is_over_soon_when_its_remaining_time_falls_below_threshold() {
        var session = aSession()
                .lasting(ofMinutes(1))
                .build();

        Duration remaining = ofSeconds(20);
        assertThat(session.isOverSoon(remaining)).isFalse();

        remaining = ofSeconds(10);
        assertThat(session.isOverSoon(remaining)).isTrue();
    }

}

