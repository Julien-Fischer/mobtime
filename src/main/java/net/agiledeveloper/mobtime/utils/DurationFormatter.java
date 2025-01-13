package net.agiledeveloper.mobtime.utils;

import java.time.Duration;

public class DurationFormatter {

    private DurationFormatter() {}


    public static String formatDuration(Duration duration) {
        long totalSeconds = duration.getSeconds();
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

}
