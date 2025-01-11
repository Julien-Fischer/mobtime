package net.agiledeveloper.mobtime.utils;

public class TimeUtils {

    private TimeUtils() {}

    public static double now() {
        return System.currentTimeMillis();
    }

    public static double minutesToSeconds(double duration) {
        return duration * 60;
    }

    public static double minutesToMillis(double duration) {
        return duration * 1000 * 60;
    }

    public static double millisToSeconds(double duration) {
        return duration / 1000;
    }

    public static double secondsToMinutes(double duration) {
        return duration / 60;
    }

    public static double secondsToMillis(double duration) {
        return duration * 1000;
    }

    public static double millisToMinutes(double duration) {
        return duration / 1000 / 60;
    }

}
