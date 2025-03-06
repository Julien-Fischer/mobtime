package net.agiledeveloper.mobtime.infra.shell;

import net.agiledeveloper.mobtime.domain.ports.spi.MobPort;
import net.agiledeveloper.mobtime.infra.InfraException;
import net.agiledeveloper.mobtime.utils.App;

public class ShellAdapter implements MobPort {

    private final CommandFormatter commandFormatter;


    public ShellAdapter(CommandFormatter commandFormatter) {
        this.commandFormatter = commandFormatter;
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
        ShellCommand formattedCommand = commandFormatter.format(command);
        return ShellExecutor.execute(formattedCommand);
    }

    private void tryExecuting(String command) {
        try {
            execute(command);
        } catch (InfraException cause) {
            App.logger.log("Infra - " + command);
            throw new InfraException(cause);
        }
    }

}
