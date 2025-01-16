package net.agiledeveloper.mobtime.domain.session;

import net.agiledeveloper.mobtime.domain.notification.session.*;
import net.agiledeveloper.mobtime.domain.ports.spi.MobPort;
import net.agiledeveloper.mobtime.domain.ports.spi.NotificationPort;
import net.agiledeveloper.mobtime.domain.ports.spi.TimerPort;
import net.agiledeveloper.mobtime.utils.AppLogger;

import java.time.Duration;

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
        AppLogger.logSeparator();
        AppLogger.log("Opening mob session (duration = " + durationString + ")");
        notificationPort.send(new SessionOpenNotification("Starting driver session", "..."));
        timerPort.runFor(
                session,
                this::refresh,
                this::close
        );
    }

    public void close(Session session) {
        if (session.isAutoModeEnabled()) {
            mobNext();
        } else {
            suggestMobNext();
        }
    }


    private void refresh(Session session, Duration remainingTime) {
        if (isGracePeriodOver(session, remainingTime)) {
            handleGracePeriodOver(session, remainingTime);
        } else {
            AppLogger.log("  Waiting for driving session to start");
        }
    }

    private void startSession(Session ignored) {
        var notification = new SessionStartNotification("Driving", "");
        notificationPort.send(notification);
        AppLogger.log("  Driving ");
    }

    private void refreshSession(Session ignored, Duration remainingTime, boolean littleTimeLeft) {
        var durationString = formatDuration(remainingTime);
        var notification = new SessionRefreshNotification("Driving", durationString, littleTimeLeft);
        notificationPort.send(notification);
        AppLogger.log("  Session ending in " + durationString);
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
        return littleTimeLeft || !session.isZenModeEnabled();
    }

    private boolean isGracePeriodOver(Session session, Duration remainingTime) {
        Duration elapsed = remainingTime.minus(session.duration());
        return !elapsed.isPositive();
    }

    private boolean hasLittleTimeLeft(Session session, Duration remainingTime) {
        return ((float) remainingTime.toMillis() / session.duration().toMillis()) < LOW_TIME_THRESHOLD;
    }

    private void suggestMobNext() {
        var notification = new SessionCloseNotification("Time out!", "Click Next or Done to switch driver");
        notificationPort.send(notification);
        AppLogger.logSeparator();
        AppLogger.log("Time out! Use mob next or mob done to switch driver");
    }

    private void mobNext() {
        var notification = new SessionShutdownNotification("Time out!", "Next to drive.");
        notificationPort.send(notification);
        AppLogger.logSeparator();
        AppLogger.log("Time out! Next to drive.");
        mobPort.next();
    }

}
