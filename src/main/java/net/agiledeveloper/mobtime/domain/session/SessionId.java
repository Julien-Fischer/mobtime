package net.agiledeveloper.mobtime.domain.session;

import java.util.UUID;

import static java.util.UUID.randomUUID;

public record SessionId(UUID value) {

    public static SessionId random() {
        return new SessionId(randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
