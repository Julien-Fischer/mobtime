package net.agiledeveloper.mobtime.infra.swing;

public enum Location {

    NORTH,
    NORTH_EAST,
    EAST,
    SOUTH_EAST,
    SOUTH,
    SOUTH_WEST,
    WEST,
    NORTH_WEST,
    CENTER;


    public static Location of(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Input string cannot be null or empty");
        }

        var normalizedName = name.toUpperCase()
                .replace("-", "_")
                .replace(".", "_")
                .replaceAll("\\s+", "_");

        try {
            return Location.valueOf(normalizedName);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid location name: " + name, exception);
        }
    }

}
