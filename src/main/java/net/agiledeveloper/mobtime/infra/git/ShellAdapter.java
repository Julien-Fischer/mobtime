package net.agiledeveloper.mobtime.infra.git;

import net.agiledeveloper.mobtime.domain.ports.spi.MobPort;
import net.agiledeveloper.mobtime.infra.InfraException;
import net.agiledeveloper.mobtime.utils.AppLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShellAdapter implements MobPort {

    @Override
    public void next() {
        try {
            execute("mob next");
        } catch (InfraException cause) {
            throw new InfraException(cause);
        }
        AppLogger.log("Infra - mob next");
    }

    @Override
    public void done() {
        try {
            execute("mob done");
        } catch (InfraException cause) {
            throw new InfraException(cause);
        }
        AppLogger.log("Infra - mob done");
    }


    public int execute(String command) throws InfraException {
        String[] commandLine = new String[] {"sh", "-c", command};
        try {
            Process process = Runtime.getRuntime().exec(commandLine);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                AppLogger.log(line);
            }
            int exitCode = process.waitFor();
            AppLogger.log("Infra - mob exited with code " + exitCode);
            return exitCode;
        } catch (IOException e) {
            throw new InfraException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InfraException(e);
        }
    }

}
