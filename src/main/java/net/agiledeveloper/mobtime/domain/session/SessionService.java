package net.agiledeveloper.mobtime.domain.session;

import net.agiledeveloper.mobtime.domain.notification.session.*;
import net.agiledeveloper.mobtime.domain.ports.spi.MobPort;
import net.agiledeveloper.mobtime.domain.ports.spi.NotificationPort;
import net.agiledeveloper.mobtime.domain.ports.spi.TimerPort;
import net.agiledeveloper.mobtime.utils.App;

import java.time.Duration;

import static net.agiledeveloper.mobtime.domain.session.FocusMode.ZEN;
import static net.agiledeveloper.mobtime.utils.TimeFormatter.formatDuration;

public class SessionService {

    private final TimerPort timerPort;
    private final NotificationPort notificationPort;
    private final MobPort mobPort;
    private boolean sessionStarted = false;


    public SessionService(TimerPort timerPort, NotificationPort notificationPort, MobPort mobPort) {
        this.timerPort = timerPort;
        this.notificationPort = notificationPort;
        this.mobPort = mobPort;
    }


    public void open(Session session) {
        var durationString = formatDuration(session.initialDuration());
        App.logger.logSeparator();
        App.logger.log("Opening mob session (duration = %s, id = %s)".formatted(durationString, session.id()));
        notificationPort.send(new SessionOpenNotification(session, "Starting driver session...", ""));
        timerPort.runFor(
                session,
                this::refresh,
                this::close
        );
    }

    public void close(Session session) {
        if (session.shouldAutomaticallyPassKeyboard()) {
            mobNext(session);
        } else {
            suggestMobNext(session);
        }
    }


    private void refresh(Session session, Duration remainingTime) {
        if (session.isGracePeriodOver()) {
            handleGracePeriodOver(session);
        } else {
            App.logger.log("  Waiting for driving session to start");
        }
    }

    private void startSession(Session session) {
        var notification = new SessionStartNotification(session, "Driving", "");
        notificationPort.send(notification);
        App.logger.log("  Now driving ");
    }

    private void refreshSession(Session session) {
        var durationString = formatDuration(session.remainingTime());
        var notification = new SessionRefreshNotification(session, session.username(), durationString, session.remainingTime());
        notificationPort.send(notification);
        App.logger.log("  Session ending in " + durationString);
    }

    private void handleGracePeriodOver(Session session) {
        if (!sessionStarted) {
            startSession(session);
            sessionStarted = true;
        } else if (shouldRefresh(session)) {
            refreshSession(session);
        }
    }

    private static boolean shouldRefresh(Session session) {
        return session.isOverSoon() || !session.hasFocus(ZEN);
    }

    private void suggestMobNext(Session session) {
        var notification = new SessionCloseNotification(session, "Pass keyboard", "");
        notificationPort.send(notification);
        App.logger.logSeparator();
        App.logger.log("Pass keyboard - Use mob next to switch driver or mob done to end the mob session");
    }

    private void mobNext(Session session) {
        var notification = new SessionShutdownNotification(session, "Pass keyboard", "Next to drive.");
        notificationPort.send(notification);
        App.logger.logSeparator();
        App.logger.log("Pass keyboard! Next to drive.");
        mobPort.next();
    }

}
