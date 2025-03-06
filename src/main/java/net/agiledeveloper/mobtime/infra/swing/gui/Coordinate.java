package net.agiledeveloper.mobtime.infra.swing.gui;

import static java.lang.Integer.parseInt;

public record Coordinate(Integer x, Integer y) {

    private static final String SEPARATOR = ", ";

    public static Coordinate of(String serialized) {
        var split = serialized
                .substring(1, serialized.length() - 1)
                .split(SEPARATOR);
        var x = parseInt(split[0]);
        var y = parseInt(split[1]);
        return new Coordinate(x, y);
    }

    @Override
    public String toString() {
        return "(%s%s%s)".formatted(x, SEPARATOR, y);
    }

}
