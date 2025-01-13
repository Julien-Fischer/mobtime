package net.agiledeveloper.mobtime.infra;

public class InfraException extends RuntimeException {

    public InfraException(String message) {
        super(message);
    }

    public InfraException(String message, Throwable cause) {
        super(message, cause);
    }

    public InfraException(Throwable cause) {
        super(cause);
    }

}
