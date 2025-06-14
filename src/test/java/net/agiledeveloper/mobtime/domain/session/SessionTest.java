package net.agiledeveloper.mobtime.domain.session;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;
import static net.agiledeveloper.mobtime.domain.Ratio.*;
import static net.agiledeveloper.mobtime.domain.session.FocusMode.NORMAL;
import static net.agiledeveloper.mobtime.domain.session.FocusMode.ZEN;
import static net.agiledeveloper.mobtime.test.builders.SessionBuilder.aSession;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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
        a.start();

        assertThat(a.hasFocus(NORMAL)).isTrue();
        assertThat(a.hasFocus(ZEN)).isFalse();

        var b = aSession()
                .withFocusMode(ZEN)
                .build();
        b.start();

        assertThat(b.hasFocus(ZEN)).isTrue();
        assertThat(b.hasFocus(FocusMode.NORMAL)).isFalse();
    }

    @Test
    void grace_period_is_over_when_remaining_time_is_less_than_grace_duration() {
        var clock = new StubClock();
        var session = aSession()
                .withClock(clock)
                .lasting(ofSeconds(30))
                .build();
        session.start();

        clock.forward(ofSeconds(1));
        assertThat(session.isGracePeriodOver()).isFalse();

        clock.forward(ofSeconds(1));
        assertThat(session.isGracePeriodOver()).isTrue();
    }

    @Test
    void a_session_is_over_soon_when_its_remaining_time_falls_below_threshold() {
        var clock = new StubClock();
        var session = aSession()
                .withClock(clock)
                .lasting(ofSeconds(30))
                .build();
        session.start();

        clock.forward(ofSeconds(10));
        assertThat(session.isOverSoon()).isFalse();

        clock.forward(ofSeconds(10));
        assertThat(session.isOverSoon()).isFalse();

        clock.forward(ofSeconds(8));
        assertThat(session.isOverSoon()).isTrue();

        clock.forward(ofSeconds(1));
        assertThat(session.isOverSoon()).isTrue();

        clock.forward(ofSeconds(1));
        assertThat(session.isOverSoon()).isTrue();
    }

    @Test
    void progress_returns_correct_ratio() {
        var clock = new StubClock();
        var session = aSession()
                .withClock(clock)
                .lasting(ofSeconds(60))
                .build();
        session.start();

        clock.forward(ofSeconds(15));
        assertThat(session.progress()).isEqualTo(THREE_QUARTER);

        clock.forward(ofSeconds(15));
        assertThat(session.progress()).isEqualTo(HALF);

        clock.forward(ofSeconds(15));
        assertThat(session.progress()).isEqualTo(ONE_QUARTER);
    }

    @Test
    void a_session_is_over_when_its_remaining_time_drops_to_zero() {
        var clock = new StubClock();
        var session = aSession()
                .withClock(clock)
                .lasting(ofSeconds(30))
                .build();
        session.start();

        clock.forward(ofSeconds(10));
        assertThat(session.isOver()).isFalse();

        clock.forward(ofSeconds(10));
        assertThat(session.isOver()).isFalse();

        clock.forward(ofSeconds(10));
        assertThat(session.isOver()).isTrue();
    }

    @Test
    void a_session_must_be_started_to_compute_its_remaining_time() {
        var pendingSession = aSession().build();

        assertThatExceptionOfType(Session.SessionNotStartedException.class)
                .isThrownBy(pendingSession::remainingTime);
    }

    private static class StubClock extends Clock {

        private Instant currentTime;

        public StubClock() {
            this.currentTime = Instant.now();
        }

        public StubClock(Instant currentTime) {
            this.currentTime = currentTime;
        }

        public void setCurrentTime(Instant time) {
            currentTime = time;
        }

        public void forward(Duration duration) {
            setCurrentTime(currentTime.plus(duration));
        }

        public void backward(Duration duration) {
            setCurrentTime(currentTime.minus(duration));
        }

        @Override
        public Instant instant() {
            return currentTime;
        }

        @Override
        public ZoneId getZone() {
            return ZoneId.systemDefault();
        }

        @Override
        public Clock withZone(ZoneId zone) {
            throw new UnsupportedOperationException("Timezones are not supported by this stub");
        }

    }

}

