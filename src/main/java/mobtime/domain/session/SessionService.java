package mobtime.domain.session;

import mobtime.domain.ports.spi.TimerPort;
import mobtime.utils.AppLogger;

import static mobtime.utils.DurationFormatter.formatDuration;

public class SessionService {

    private final TimerPort timerPort;

    public SessionService(TimerPort timerPort) {
        this.timerPort = timerPort;
    }

    public void start(Session session) {
        var duration = session.duration();
        var durationString = formatDuration(duration.getMillis());
        AppLogger.log("Mob session ending in " + durationString);
        timerPort.runFor(duration, session::callback);
    }

}
