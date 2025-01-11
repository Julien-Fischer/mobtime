package net.agiledeveloper.mobtime.domain.session;

import net.agiledeveloper.mobtime.domain.Notification;
import net.agiledeveloper.mobtime.domain.ports.spi.NotificationPort;
import net.agiledeveloper.mobtime.domain.ports.spi.TimerPort;
import net.agiledeveloper.mobtime.utils.AppLogger;

import static net.agiledeveloper.mobtime.utils.DurationFormatter.formatDuration;

public class SessionService {

    private final TimerPort timerPort;
    private final NotificationPort notificationPort;

    public SessionService(TimerPort timerPort, NotificationPort notificationPort) {
        this.timerPort = timerPort;
        this.notificationPort = notificationPort;
    }

    public void start(Session session) {
        var duration = session.duration();
        var durationString = formatDuration(duration.getMillis());
        AppLogger.log("Mob session ending in " + durationString);
        timerPort.runFor(duration, () -> end(session));
    }

    public void end(Session session) {
        var durationString = formatDuration(session.actualDuration().getMillis());
        var message = "Mob session ended after " + durationString;
        var notification = new Notification(message);
        notificationPort.send(notification);
    }

}
