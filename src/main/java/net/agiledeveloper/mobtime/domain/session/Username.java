package net.agiledeveloper.mobtime.domain.session;

public record Username(String value) {

    public Username {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
    }

    @Override
    public String toString() {
        return value;
    }

}
