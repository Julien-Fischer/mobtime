package net.agiledeveloper.mobtime.infra;

import net.agiledeveloper.mobtime.MobTimeException;
import net.agiledeveloper.mobtime.domain.Duration;
import net.agiledeveloper.mobtime.domain.ports.spi.TimerPort;
import net.agiledeveloper.mobtime.utils.AppLogger;
import net.agiledeveloper.mobtime.utils.DurationFormatter;

import static net.agiledeveloper.mobtime.utils.TimeUtils.now;

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
    public void runFor(Duration duration, Runnable then) {
        timerDurationMilliseconds = duration.getMillis();
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
    }

}
