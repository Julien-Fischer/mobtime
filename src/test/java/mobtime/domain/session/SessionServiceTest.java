package mobtime.domain.session;

import mobtime.domain.Duration;
import mobtime.domain.Notification;
import mobtime.domain.ports.spi.NotificationPort;
import mobtime.domain.ports.spi.TimerPort;
import mobtime.test.BaseMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static mobtime.test.Builders.aSession;
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
    void start_triggers_a_timer() {
        var sessionService = new SessionService(timerMock, notificationMock);

        sessionService.start(aSession());

        assertThat(timerMock.wasCalledOnce()).isTrue();
    }

    @Test
    void end_dispatches_a_notification() {
        var sessionService = new SessionService(timerMock, notificationMock);

        sessionService.end(aSession());

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
    public void runFor(Duration duration, Runnable then) {
        incrementCallCount();
    }
}
