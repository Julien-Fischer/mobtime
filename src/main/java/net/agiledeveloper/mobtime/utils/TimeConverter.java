package net.agiledeveloper.mobtime.utils;

public class TimeConverter {

    private TimeConverter() {}


    public static double minutesToSeconds(double duration) {
        return duration * 60;
    }

    public static double minutesToMillis(double duration) {
        return duration * 1000 * 60;
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

    public static double millisToSeconds(double duration) {
        return duration / 1000;
    }

}
