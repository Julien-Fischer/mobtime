package net.agiledeveloper.mobtime.utils;

import java.time.Instant;

import static net.agiledeveloper.mobtime.utils.TimeFormatter.formatInstant;

public class AppLogger {

    public static final String DEFAULT_SEPARATOR = "-".repeat(60);
    public static final String ERROR_PREFIX = "/!\\";

    private final Target target;
    private final TimeProvider timeProvider;


    public AppLogger(Target target, TimeProvider timeProvider) {
        this.target = target;
        this.timeProvider = timeProvider;
    }


    public void print(String message) {
        target.print(message);
    }

    public void logSeparator() {
        print(DEFAULT_SEPARATOR);
    }

    public void logSeparator(String separator) {
        print(separator);
    }

    public void log(String... elements) {
        print("[%s] %s".formatted(now(), join(elements)));
    }

    public void err(String... elements) {
        print("[%s] %s %s".formatted(now(), ERROR_PREFIX, join(elements)));
    }

    private String now() {
        return formatInstant(timeProvider.now());
    }

    private String join(String... elements) {
        return String.join(" ", elements);
    }

    public void err(Exception exception) {
        err(exception.getMessage());
    }


    public interface Target {
        void print(String message);
    }

    public interface TimeProvider {
        Instant now();
    }

}
