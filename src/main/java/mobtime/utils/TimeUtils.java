package mobtime.utils;

public class TimeUtils {

    private TimeUtils() {}

    public static double now() {
        return System.currentTimeMillis();
    }

    public static double minutesToMilliseconds(double durationMinutes) {
        return durationMinutes * 1000 * 60;
    }

    public static double millisecondsToSeconds(double duration) {
        return duration / 1000;
    }

    public static double secondsToMinutes(double duration) {
        return duration / 60;
    }

}
