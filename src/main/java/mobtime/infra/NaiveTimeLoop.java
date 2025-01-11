package mobtime.infra;

import mobtime.MobTimeException;
import mobtime.domain.spi.TimerPort;
import mobtime.utils.AppLogger;
import mobtime.utils.DurationFormatter;

import java.time.Duration;

import static mobtime.utils.TimeUtils.now;

public class NaiveTimeLoop implements TimerPort {

    private static final int DEFAULT_FREQUENCY_MS = 1000;

    private final int tickFrequencyMilliseconds;

    private Runnable callback;
    private double timerDurationMilliseconds;


    public NaiveTimeLoop() {
        this(DEFAULT_FREQUENCY_MS);
    }

    public NaiveTimeLoop(int tickFrequencyMilliseconds) {
        this.tickFrequencyMilliseconds = tickFrequencyMilliseconds;
    }


    @Override
    public void runFor(Duration milliseconds, Runnable then) {
        timerDurationMilliseconds = milliseconds.getSeconds();
        callback = then;
        try {
            runInBackground();
        } catch (InterruptedException cause) {
            Thread.currentThread().interrupt();
            throw new MobTimeException(cause);
        }
    }

    private void runInBackground() throws InterruptedException {
        double startTime = now();
        double endTime = startTime + timerDurationMilliseconds;

        while (now() < endTime) {
            var durationString = DurationFormatter.formatRemainingTime(timerDurationMilliseconds, startTime);
            AppLogger.log("  Mob Next in " + durationString);
            Thread.sleep(tickFrequencyMilliseconds);
        }

        onTimerStop();
    }

    private void onTimerStop() {
        callback.run();
        var durationString = DurationFormatter.formatDuration(timerDurationMilliseconds);
        AppLogger.log(durationString + " seconds have passed. Exiting the program.");
    }

}
