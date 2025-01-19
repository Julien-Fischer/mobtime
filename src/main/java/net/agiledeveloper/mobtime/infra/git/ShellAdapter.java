package net.agiledeveloper.mobtime.infra.git;

import net.agiledeveloper.mobtime.domain.ports.spi.MobPort;
import net.agiledeveloper.mobtime.infra.InfraException;
import net.agiledeveloper.mobtime.utils.AppLogger;

public class ShellAdapter implements MobPort {

    private static final String COMMAND_ROOT_DIR = "/usr/local/bin";
    private static final Shell PREFERRED_SHELL = LinuxShell.SH;


    @Override
    public void next() {
        tryExecuting("mobnext");
    }

    @Override
    public void done() {
        tryExecuting("mobdone");
    }


    public int execute(String command) throws InfraException {
        return ShellExecutor.execute(command, PREFERRED_SHELL);
    }

    private void tryExecuting(String command) {
        try {
            execute(COMMAND_ROOT_DIR + "/" + command);
        } catch (InfraException cause) {
            AppLogger.log("Infra - " + command);
            throw new InfraException(cause);
        }
    }

}
