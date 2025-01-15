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

    @BeforeEach
    void setUp() {
        timerMock = new TimerMock();
        notificationMock = new NotificationMock();
    }


    @Test
    void open_triggers_a_timer() {
        var sessionService = new SessionService(timerMock, notificationMock);

        sessionService.open(aSession());

        expectThat(timerMock).wasCalledOnce();
    }

    @Test
    void close_dispatches_a_notification() {
        var sessionService = new SessionService(timerMock, notificationMock);

        sessionService.close(aSession());

        expectThat(notificationMock).wasCalledOnce();
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
