package net.agiledeveloper.mobtime.test.builders;

import net.agiledeveloper.mobtime.domain.session.*;
import net.agiledeveloper.mobtime.test.lib.Builder;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

public class SessionBuilder implements Builder<Session> {

    private Clock clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
    private SessionId id = SessionId.random();
    private Duration duration = Session.DEFAULT_DURATION;
    private FocusMode focusMode = FocusMode.NORMAL;
    private Username username = Session.DEFAULT_USERNAME;
    private EndMode endMode = EndMode.WAIT_FOR_INSTRUCTION;
    private boolean started;


    private SessionBuilder() {}


    public static SessionBuilder aSession() {
        return new SessionBuilder();
    }

    public SessionBuilder started() {
        this.started = true;
        return this;
    }


    public SessionBuilder withId(SessionId id) {
        this.id = id;
        return this;
    }

    public SessionBuilder lasting(Duration duration) {
        this.duration = duration;
        return this;
    }

    public SessionBuilder withFocusMode(FocusMode focusMode) {
        this.focusMode = focusMode;
        return this;
    }

    public SessionBuilder withUserName(Username username) {
        this.username = username;
        return this;
    }

    public SessionBuilder thatWill(EndMode endMode) {
        this.endMode = endMode;
        return this;
    }

    public SessionBuilder withClock(Clock clock) {
        this.clock = clock;
        return this;
    }

    @Override
    public Session build() {
        var session = new Session(id, clock, duration, endMode, focusMode, username);
        if (started) {
            session.start();
        }
        return session;
    }

}
