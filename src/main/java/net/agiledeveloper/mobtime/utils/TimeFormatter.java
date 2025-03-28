package net.agiledeveloper.mobtime.utils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static java.lang.Long.parseLong;

public class TimeFormatter {

    private TimeFormatter() {}


    public static String formatDuration(Duration duration) {
        long totalSeconds = duration.getSeconds();
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public static String formatInstant(Instant instant) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(instant);
    }

    public static Instant toInstant(String dateTimeString) {
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        var localDateTime = LocalDateTime.parse(dateTimeString, formatter);
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    public static Duration toDuration(String epochMilli) {
        return Duration.ofMillis(parseLong(epochMilli));
    }

}
