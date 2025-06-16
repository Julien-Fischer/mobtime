package net.agiledeveloper.mobtime.domain.session;

import net.agiledeveloper.mobtime.domain.ports.api.OnDone;
import net.agiledeveloper.mobtime.domain.ports.api.OnTick;
import net.agiledeveloper.mobtime.domain.ports.spi.MobPort;
import net.agiledeveloper.mobtime.domain.ports.spi.TimerPort;
import net.agiledeveloper.mobtime.infra.notification.NotificationMock;
import net.agiledeveloper.mobtime.test.lib.Mock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.agiledeveloper.mobtime.domain.session.EndMode.AUTOMATICALLY_PASS_KEYBOARD;
import static net.agiledeveloper.mobtime.domain.session.EndMode.WAIT_FOR_INSTRUCTION;
import static net.agiledeveloper.mobtime.test.builders.SessionBuilder.aSession;
import static net.agiledeveloper.mobtime.test.lib.MockAssertion.expectThat;

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
        var anySession = aSession().build();

        sessionService.open(anySession);

        expectThat(timerMock).wasCalledOnce();
    }

    @Test
    void close_dispatches_a_notification() {
        var anySession = aSession().build();

        sessionService.close(anySession);

        expectThat(notificationMock).wasCalledOnce();
    }

    @Test
    void close_when_auto_mode_enabled_calls_mob_port() {
        var session = aSession().thatWill(AUTOMATICALLY_PASS_KEYBOARD).build();

        sessionService.close(session);

        expectThat(shellMock).wasCalledOnce();
    }

    @Test
    void close_when_auto_mode_enabled_does_not_call_mob_port() {
        var session = aSession().thatWill(WAIT_FOR_INSTRUCTION).build();

        sessionService.close(session);

        expectThat(shellMock).wasNeverCalled();
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
