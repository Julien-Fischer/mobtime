package net.agiledeveloper.mobtime.domain.session;

import net.agiledeveloper.App;
import net.agiledeveloper.mobtime.domain.notification.session.*;
import net.agiledeveloper.mobtime.domain.ports.api.SessionServicePort;
import net.agiledeveloper.mobtime.domain.ports.spi.MobPort;
import net.agiledeveloper.mobtime.domain.ports.spi.NotificationPort;
import net.agiledeveloper.mobtime.domain.ports.spi.TimerPort;

import java.time.Duration;

import static net.agiledeveloper.mobtime.domain.session.FocusMode.ZEN;

public class SessionService implements SessionServicePort {

    private final TimerPort timerPort;
    private final NotificationPort notificationPort;
    private final MobPort mobPort;


    public SessionService(TimerPort timerPort, NotificationPort notificationPort, MobPort mobPort) {
        this.timerPort = timerPort;
        this.notificationPort = notificationPort;
        this.mobPort = mobPort;
    }


    @Override
    public void open(Session session) {
        notificationPort.send(new SessionOpenNotification(session, "Starting driver session...", ""));
        timerPort.runFor(session, this::onTick, this::onDone);
    }

    @Override
    public void close(Session session) {
        if (session.shouldAutomaticallyPassKeyboard()) {
            passKeyboardFrom(session);
        } else {
            suggestPassingKeyboardFrom(session);
        }
    }


    private void onTick(Session session, Duration remainingTime) {
        if (session.isGracePeriodOver()) {
            handleGracePeriodOver(session);
        } else {
            App.logger.log("  Waiting for driving session to start");
        }
    }

    private void onDone(Session session) {
        close(session);
    }

    private void startSession(Session session) {
        var notification = new SessionStartNotification(session, "Driving", "");
        notificationPort.send(notification);
    }

    private void sendRefreshNotification(Session session) {
        var notification = new SessionRefreshNotification(session);
        notificationPort.send(notification);
    }

    private void handleGracePeriodOver(Session session) {
        if (!session.hasStarted()) {
            startSession(session);
        } else if (session.isOverSoon() || !session.hasFocus(ZEN)) {
            sendRefreshNotification(session);
        }
    }

    private void passKeyboardFrom(Session session) {
        var notification = new SessionShutdownNotification(session, "Pass keyboard", "Next to drive.");
        notificationPort.send(notification);
        mobPort.next();
    }

    private void suggestPassingKeyboardFrom(Session session) {
        var notification = new SessionCloseNotification(session, "Pass keyboard", "");
        notificationPort.send(notification);
    }

}
