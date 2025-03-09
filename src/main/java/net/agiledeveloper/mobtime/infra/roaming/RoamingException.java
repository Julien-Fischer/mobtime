package net.agiledeveloper.mobtime.infra.roaming;

public class RoamingException extends RuntimeException {

    public RoamingException(String message) {
        super(message);
    }

    public RoamingException(Throwable cause) {
        super(cause);
    }

}
