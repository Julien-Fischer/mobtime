package mobtime.infra;

import mobtime.MobTimeException;
import mobtime.domain.spi.TimerPort;
import mobtime.utils.AppLogger;
import mobtime.utils.DurationFormatter;

import java.time.Duration;

import static mobtime.utils.TimeUtils.now;

public class NaiveTimeLoop implements TimerPort {

    private static final int DEFAULT_FREQUENCY_MS = 1000;

    private final int sleepInterval;


    public NaiveTimeLoop() {
        this(DEFAULT_FREQUENCY_MS);
    }

    public NaiveTimeLoop(int tickFrequency) {
        this.sleepInterval = tickFrequency;
    }


    @Override
    public void runFor(Duration milliseconds) {
        try {
            runInBackground(milliseconds.getSeconds());
        } catch (InterruptedException cause) {
            Thread.currentThread().interrupt();
            throw new MobTimeException(cause);
        }
    }

    private void runInBackground(long timerDuration) throws InterruptedException {
        double startTime = now();
        double endTime = startTime + timerDuration;

        while (now() < endTime) {
            AppLogger.log("  Mob Next in " + DurationFormatter.formatRemainingTime(timerDuration, startTime));
            Thread.sleep(sleepInterval);
        }

        AppLogger.log(timerDuration + " seconds have passed. Exiting the program.");
    }

}
