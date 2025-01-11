package mobtime.domain.session;

import mobtime.domain.Notification;
import mobtime.domain.ports.spi.NotificationPort;
import mobtime.domain.ports.spi.TimerPort;
import mobtime.utils.AppLogger;

import static mobtime.utils.DurationFormatter.formatDuration;

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
