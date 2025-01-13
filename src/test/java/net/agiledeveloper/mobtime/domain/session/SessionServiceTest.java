package net.agiledeveloper.mobtime.domain.session;

import net.agiledeveloper.mobtime.domain.notification.Notification;
import net.agiledeveloper.mobtime.domain.ports.api.OnDone;
import net.agiledeveloper.mobtime.domain.ports.api.OnTick;
import net.agiledeveloper.mobtime.domain.ports.spi.NotificationPort;
import net.agiledeveloper.mobtime.domain.ports.spi.TimerPort;
import net.agiledeveloper.mobtime.test.BaseMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.agiledeveloper.mobtime.test.Builders.aSession;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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

        assertThat(timerMock.wasCalledOnce()).isTrue();
    }

    @Test
    void close_dispatches_a_notification() {
        var sessionService = new SessionService(timerMock, notificationMock);

        sessionService.close(aSession());

        assertThat(notificationMock.wasCalledOnce()).isTrue();
    }

}


class NotificationMock extends BaseMock implements NotificationPort {

    @Override
    public void send(Notification notification) {
        incrementCallCount();
    }

}


class TimerMock extends BaseMock implements TimerPort {

    @Override
    public void runFor(Session session, OnTick onTick, OnDone onDone) {
        incrementCallCount();
    }

}
