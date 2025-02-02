package net.agiledeveloper.mobtime.utils;

import java.time.Instant;

import static net.agiledeveloper.mobtime.utils.TimeFormatter.formatInstant;

public class AppLogger {

    public static final String DEFAULT_SEPARATOR = "-".repeat(60);
    public static final String ERROR_PREFIX = "/!\\";


    private AppLogger() {}


    public static void logSeparator() {
        print(DEFAULT_SEPARATOR);
    }

    public static void logSeparator(String separator) {
        print(separator);
    }

    public static void log(String... elements) {
        print("[" + now() + "]" + " " + String.join(" ", elements));
    }

    public static void err(String... elements) {
        print("[" + now() + "]" + " " + ERROR_PREFIX + " " + String.join(" ", elements));
    }


    private static void print(String message) {
        System.out.println(message);
    }

    private static String now() {
        return formatInstant(Instant.now());
    }

}
