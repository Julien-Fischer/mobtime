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

    private static final float LOW_TIME_THRESHOLD = 0.25f;

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
        var durationString = formatDuration(session.duration());
        App.logger.logSeparator();
        App.logger.log("Opening mob session (duration = " + durationString + ")");
        notificationPort.send(new SessionOpenNotification(session, "Starting driver session...", ""));
        timerPort.runFor(
                session,
                this::refresh,
                this::close
        );
    }

    public void close(Session session) {
        if (session.isAutoModeEnabled()) {
            mobNext(session);
        } else {
            suggestMobNext(session);
        }
    }


    private void refresh(Session session, Duration remainingTime) {
        if (isGracePeriodOver(session, remainingTime)) {
            handleGracePeriodOver(session, remainingTime);
        } else {
            App.logger.log("  Waiting for driving session to start");
        }
    }

    private void startSession(Session session) {
        var notification = new SessionStartNotification(session, "Driving", "");
        notificationPort.send(notification);
        App.logger.log("  Driving ");
    }

    private void refreshSession(Session session, Duration remainingTime, boolean littleTimeLeft) {
        var durationString = formatDuration(remainingTime);
        var notification = new SessionRefreshNotification(session, session.username(), durationString, littleTimeLeft, remainingTime);
        notificationPort.send(notification);
        App.logger.log("  Session ending in " + durationString);
    }

    private void handleGracePeriodOver(Session session, Duration remainingTime) {
        if (!sessionStarted) {
            startSession(session);
            sessionStarted = true;
        } else {
            boolean littleTimeLeft = hasLittleTimeLeft(session, remainingTime);
            if (shouldRefresh(session, littleTimeLeft)) {
                refreshSession(session, remainingTime, littleTimeLeft);
            }
        }
    }

    private boolean shouldRefresh(Session session, boolean littleTimeLeft) {
        return littleTimeLeft || !session.hasFocus(ZEN);
    }

    private boolean isGracePeriodOver(Session session, Duration remainingTime) {
        Duration elapsed = remainingTime.minus(session.duration());
        return !elapsed.isPositive();
    }

    private boolean hasLittleTimeLeft(Session session, Duration remainingTime) {
        return ((float) remainingTime.toMillis() / session.duration().toMillis()) < LOW_TIME_THRESHOLD;
    }

    private void suggestMobNext(Session session) {
        var notification = new SessionCloseNotification(session, "Pass keyboard", "");
        notificationPort.send(notification);
        App.logger.logSeparator();
        App.logger.log("Pass keyboard Use mob next to switch driver or mob done to end the mob session");
    }

    private void mobNext(Session session) {
        var notification = new SessionShutdownNotification(session, "Pass keyboard", "Next to drive.");
        notificationPort.send(notification);
        App.logger.logSeparator();
        App.logger.log("Pass keyboard! Next to drive.");
        mobPort.next();
    }

}
