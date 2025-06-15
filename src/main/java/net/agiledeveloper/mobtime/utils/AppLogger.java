package net.agiledeveloper.mobtime.utils;

import java.time.Instant;

import static net.agiledeveloper.mobtime.utils.AppLogger.Level.*;
import static net.agiledeveloper.mobtime.utils.TimeFormatter.formatInstant;

public class AppLogger {

    public static final String DEFAULT_SEPARATOR = "-".repeat(60);
    public static final String ERROR_PREFIX = "/!\\";
    public static final Level DEFAULT_LEVEL = ERROR;

    private final Target target;
    private final TimeProvider timeProvider;
    private Level level = DEFAULT_LEVEL;


    AppLogger(Target target, TimeProvider timeProvider) {
        this.target = target;
        this.timeProvider = timeProvider;
    }


    public void setLevel(Level level) {
        this.level = level;
    }

    public void print(String message) {
        target.print(message);
    }

    public void print(Level messageLevel, String message) {
        if (this.level.lessOrEqual(messageLevel)) {
            print(message);
        }
    }

    public void logSeparator() {
        print(INFO, DEFAULT_SEPARATOR);
    }

    public void logSeparator(String separator) {
        print(INFO, separator);
    }

    public void log(String... elements) {
        log(INFO, elements);
    }

    public void log(Level level, String... elements) {
        print(level, "[%s] %s".formatted(now(), join(elements)));
    }

    public void debug(String... elements) {
        log(DEBUG, elements);
    }

    public void err(String... elements) {
        log(ERROR, "%s %s".formatted(ERROR_PREFIX, join(elements)));
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


    public enum Level {

        DEBUG (0),
        INFO  (1),
        ERROR (2);

        private final int severity;

        Level(int severity) {
            this.severity = severity;
        }

        public boolean lessOrEqual(Level level) {
            return severity <= level.severity;
        }

        public static Level of(String level) {
            return Level.valueOf(EnumUtils.normalize(level));
        }

    }

}
