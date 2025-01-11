package net.agiledeveloper.mobtime.utils;

import java.text.DecimalFormat;

import static net.agiledeveloper.mobtime.utils.TimeUtils.*;

public class DurationFormatter {

    private static final String SECONDS_FORMAT = "0";

    private DurationFormatter() {}

    public static String formatDuration(double durationMs) {
        double remainingSeconds = millisToSeconds(durationMs);
        return formatTime(durationMs - remainingSeconds);
    }

    public static String formatRemainingTime(double durationMs, double fromMs) {
        double elapsed = now() - fromMs;
        return formatTime(durationMs - elapsed);
    }

    private static String formatTime(double remainingMilliseconds) {
        double remainingSeconds = millisToSeconds(remainingMilliseconds);
        double remainingMinutes = secondsToMinutes(remainingSeconds);
        if (remainingSeconds <= 60) {
            DecimalFormat df = new DecimalFormat(SECONDS_FORMAT);
            return df.format(remainingSeconds) + " sec";
        } else {
            return Math.round(remainingMinutes) + " min";
        }
    }

}
