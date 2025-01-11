package mobtime;

import mobtime.infra.NaiveTimeLoop;
import mobtime.utils.AppLogger;

import java.time.Duration;

import static mobtime.utils.DurationFormatter.formatDuration;
import static mobtime.utils.TimeUtils.minutesToMilliseconds;

public class App {

    private static final double DEFAULT_DURATION_MINUTES = 15;

    private static boolean dryRun = false;
    private static boolean startSession = false;
    private static double sessionDuration = DEFAULT_DURATION_MINUTES;


    public static void main(String[] args) {
        if (args != null) {
            parseArguments(args);
        }

        executeCommandLine();

        AppLogger.log("Done");
    }

    private static void parseArguments(String[] arguments) {
        for (var argument : arguments) {
            AppLogger.log(argument);

            if (argument.equals("--start")) {
                startSession = true;
            }

            if (argument.equals("--dry-run")) {
                dryRun = true;
            }

            if (argument.startsWith("--duration")) {
                sessionDuration = readDuration(argument);
            }

            if (argument.startsWith("--invalid")) {
                var msg = "Error: --invalid is not a valid argument";
                AppLogger.log(msg);
                throw new IllegalArgumentException(msg);
            }
        }
    }

    private static void executeCommandLine() {
        if (startSession) {
            mobStart();
        }
    }

    private static void mobStart() {
        if (!dryRun) {
            var durationMs = milliseconds(sessionDuration);
            var durationString = formatDuration(minutesToMilliseconds(sessionDuration));
            AppLogger.log("Mob session ending in " + durationString);
            var timer = new NaiveTimeLoop();
            timer.runFor(durationMs, App::notifyUser);
        }
    }

    private static void notifyUser() {
        AppLogger.log("Mob session ended");
    }

    private static double readDuration(String argument) {
        String[] split = argument.split("=");
        return (split.length == 2) ? Integer.parseInt(split[1]) : DEFAULT_DURATION_MINUTES;
    }

    private static Duration milliseconds(double durationMinutes) {
        return Duration.ofSeconds((int) minutesToMilliseconds(durationMinutes));
    }

}
