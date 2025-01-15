package net.agiledeveloper.mobtime.domain.session;

import net.agiledeveloper.mobtime.domain.notification.Notification;
import net.agiledeveloper.mobtime.domain.ports.api.OnDone;
import net.agiledeveloper.mobtime.domain.ports.api.OnTick;
import net.agiledeveloper.mobtime.domain.ports.spi.MobPort;
import net.agiledeveloper.mobtime.domain.ports.spi.NotificationPort;
import net.agiledeveloper.mobtime.domain.ports.spi.TimerPort;
import net.agiledeveloper.mobtime.test.Mock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.agiledeveloper.mobtime.test.Builders.aSession;
import static net.agiledeveloper.mobtime.test.MockAssertion.expectThat;

class SessionServiceTest {

    private TimerMock timerMock;
    private NotificationMock notificationMock;
    private ShellMock shellMock;

    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        timerMock = new TimerMock();
        notificationMock = new NotificationMock();
        shellMock = new ShellMock();
        sessionService = new SessionService(timerMock, notificationMock, shellMock);
    }


    @Test
    void open_triggers_a_timer() {
        sessionService.open(aSession());

        expectThat(timerMock).wasCalledOnce();
    }

    @Test
    void close_dispatches_a_notification() {
        sessionService.close(aSession());

        expectThat(notificationMock).wasCalledOnce();
    }

    @Test
    void close_when_auto_mode_enabled_calls_mob_port() {
        var session = aSession(true);

        sessionService.close(session);

        expectThat(shellMock).wasCalledOnce();
    }

    @Test
    void close_when_auto_mode_enabled_does_not_call_mob_port() {
        var session = aSession(false);

        sessionService.close(session);

        expectThat(shellMock).wasNeverCalled();
    }

}


class NotificationMock extends Mock implements NotificationPort {

    @Override
    public void send(Notification notification) {
        incrementCallCount();
    }

}


class TimerMock extends Mock implements TimerPort {

    @Override
    public void runFor(Session session, OnTick onTick, OnDone onDone) {
        incrementCallCount();
    }

}

class ShellMock extends Mock implements MobPort {

    @Override
    public void next() {
        incrementCallCount();
    }

    @Override
    public void done() {
        incrementCallCount();
    }

}
