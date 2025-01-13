package net.agiledeveloper.mobtime.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class AppLogger {

    public static final String SEPARATOR = "-".repeat(60);


    private AppLogger() {}


    public static void logSeparator() {
        print(SEPARATOR);
    }

    public static void log(String... elements) {
        print("[" + now() + "]" + " " + String.join(" ", elements));
    }


    private static void print(String message) {
        System.out.println(message);
    }

    private static String now() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(Instant.now());
    }

}
