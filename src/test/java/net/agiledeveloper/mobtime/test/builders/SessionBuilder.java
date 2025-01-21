package net.agiledeveloper.mobtime.test.builders;

import net.agiledeveloper.mobtime.domain.session.FocusMode;
import net.agiledeveloper.mobtime.domain.session.Session;
import net.agiledeveloper.mobtime.test.lib.Builder;

import java.time.Duration;

public class SessionBuilder implements Builder<Session> {

    private Duration duration = Session.DEFAULT_DURATION;
    private FocusMode focusMode = FocusMode.NORMAL;
    private String username = Session.DEFAULT_USERNAME;
    private boolean auto = false;


    private SessionBuilder() {}


    public static SessionBuilder aSession() {
        return new SessionBuilder();
    }

    public SessionBuilder lasting(Duration duration) {
        this.duration = duration;
        return this;
    }

    public SessionBuilder withFocusMode(FocusMode focusMode) {
        this.focusMode = focusMode;
        return this;
    }

    public SessionBuilder withUserName(String username) {
        this.username = username;
        return this;
    }

    public SessionBuilder withAutoMode(boolean auto) {
        this.auto = auto;
        return this;
    }

    @Override
    public Session build() {
        return new Session(duration, auto, focusMode, username);
    }

}
