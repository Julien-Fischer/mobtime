package net.agiledeveloper.mobtime.infra.git;

import net.agiledeveloper.mobtime.infra.InfraException;
import net.agiledeveloper.mobtime.utils.AppLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShellExecutor {

    private ShellExecutor() {}

    public static int execute(String command, Shell shell) throws InfraException {
        String[] commandLine = shell.formatCommand(command);
        try {
            Process process = Runtime.getRuntime().exec(commandLine);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                AppLogger.log(line);
            }
            int exitCode = process.waitFor();
            AppLogger.log(String.join(" ", commandLine) + " - process exited with code " + exitCode);
            return exitCode;
        } catch (IOException e) {
            throw new InfraException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InfraException(e);
        }
    }

}
