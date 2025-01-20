package net.agiledeveloper.mobtime.infra.shell;

import net.agiledeveloper.mobtime.infra.InfraException;

public class ShellException extends InfraException {

    public ShellException(String message) {
        super(message);
    }

    public ShellException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShellException(Throwable cause) {
        super(cause);
    }

}
