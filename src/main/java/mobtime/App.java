package mobtime;

import mobtime.domain.session.Session;
import mobtime.domain.session.SessionService;
import mobtime.domain.time.Duration;
import mobtime.infra.NaiveTimeLoop;
import mobtime.utils.AppLogger;

import java.time.temporal.ChronoUnit;

public class App {

    private static final double DEFAULT_DURATION_MINUTES = 15;

    private static boolean dryRun = false;
    private static boolean startSession = false;
    private static double sessionDuration = DEFAULT_DURATION_MINUTES;

    private static SessionService sessionService = new SessionService(new NaiveTimeLoop());


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
            var duration = new Duration(sessionDuration, ChronoUnit.MINUTES);
            var session = new Session(duration, App::notifyUser);
            sessionService.start(session);
        }
    }

    private static void notifyUser() {
        AppLogger.log("Mob session ended");
    }

    private static double readDuration(String argument) {
        String[] split = argument.split("=");
        return (split.length == 2) ? Integer.parseInt(split[1]) : DEFAULT_DURATION_MINUTES;
    }

}
