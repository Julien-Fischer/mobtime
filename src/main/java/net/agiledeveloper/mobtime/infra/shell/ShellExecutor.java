package net.agiledeveloper.mobtime.infra.shell;

import net.agiledeveloper.mobtime.utils.AppLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class ShellExecutor {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(60);


    private ShellExecutor() {}


    public static int execute(String command, Shell shell) throws ShellException {
        return execute(command, shell, DEFAULT_TIMEOUT);
    }

    public static int execute(String command, Shell shell, Duration timeout) throws ShellException {
        try {
            String[] commandLine = shell.formatCommand(command);
            Process process = Runtime.getRuntime().exec(commandLine);
            try (
                    var reader = buffer(process.getInputStream());
                    var errorReader = buffer(process.getErrorStream());
            ) {
                read(reader);
                read(errorReader);

                if (completesBefore(process, timeout)) {
                    int exitCode = process.exitValue();
                    AppLogger.log(String.join(" ", commandLine) + " - process exited with code " + exitCode);
                    return exitCode;
                } else {
                    process.destroyForcibly();
                    throw new ShellException("Process timed out after " + timeout.toSeconds() + " seconds");
                }
            }
        } catch (IOException e) {
            throw new ShellException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ShellException(e);
        }
    }

    private static boolean completesBefore(Process process, Duration timeout) throws InterruptedException {
        return process.waitFor(timeout.toSeconds(), TimeUnit.SECONDS);
    }

    private static BufferedReader buffer(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream));
    }

    private static void read(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            AppLogger.log(line);
        }
    }

}
