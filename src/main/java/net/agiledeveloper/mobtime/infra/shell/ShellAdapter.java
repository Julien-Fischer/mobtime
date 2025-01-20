package net.agiledeveloper.mobtime.infra.shell;

import net.agiledeveloper.mobtime.domain.ports.spi.MobPort;
import net.agiledeveloper.mobtime.infra.InfraException;
import net.agiledeveloper.mobtime.utils.AppLogger;

public class ShellAdapter implements MobPort {

    private final Shell preferredShell;


    public ShellAdapter(Shell preferredShell) {
        this.preferredShell = preferredShell;
    }


    @Override
    public void next() {
        tryExecuting("mobnext");
    }

    @Override
    public void done() {
        tryExecuting("mobdone");
    }


    public int execute(String command) throws InfraException {
        return ShellExecutor.execute(command, preferredShell);
    }

    private void tryExecuting(String command) {
        try {
            execute(command);
        } catch (InfraException cause) {
            AppLogger.log("Infra - " + command);
            throw new InfraException(cause);
        }
    }

}
