package net.agiledeveloper.mobtime.domain.session;

import net.agiledeveloper.mobtime.domain.notification.session.SessionCloseNotification;
import net.agiledeveloper.mobtime.domain.notification.session.SessionOpenNotification;
import net.agiledeveloper.mobtime.domain.notification.session.SessionRefreshNotification;
import net.agiledeveloper.mobtime.domain.notification.session.SessionShutdownNotification;
import net.agiledeveloper.mobtime.domain.ports.spi.MobPort;
import net.agiledeveloper.mobtime.domain.ports.spi.NotificationPort;
import net.agiledeveloper.mobtime.domain.ports.spi.TimerPort;
import net.agiledeveloper.mobtime.utils.AppLogger;

import java.time.Duration;

import static net.agiledeveloper.mobtime.utils.TimeFormatter.formatDuration;

public class SessionService {

    private final TimerPort timerPort;
    private final NotificationPort notificationPort;
    private final MobPort mobPort;


    public SessionService(TimerPort timerPort, NotificationPort notificationPort, MobPort mobPort) {
        this.timerPort = timerPort;
        this.notificationPort = notificationPort;
        this.mobPort = mobPort;
    }


    public void open(Session session) {
        var durationString = formatDuration(session.duration());
        AppLogger.logSeparator();
        AppLogger.log("Opening mob session (duration = " + durationString + ")");
        notificationPort.send(new SessionOpenNotification("Starting...", durationString));
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
            var durationString = formatDuration(remainingTime);
            var notification = new SessionRefreshNotification("Driving", durationString);
            notificationPort.send(notification);
            AppLogger.log("  Session ending in " + durationString);
        } else {
            AppLogger.log("  Waiting for driving session to start");
        }
    }

    private boolean isGracePeriodOver(Session session, Duration remainingTime) {
        Duration elapsed = remainingTime.minus(session.duration());
        return !elapsed.isPositive();
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
