package net.agiledeveloper.mobtime.infra.swing.gui;

import java.awt.*;
import java.util.function.BiFunction;

import static net.agiledeveloper.mobtime.utils.EnumUtils.normalize;

public enum Location {

    NORTH        (Location::getNorthLocation),
    NORTH_EAST   (Location::getNorthEastLocation),
    EAST         (Location::getEastLocation),
    SOUTH_EAST   (Location::getSouthEastLocation),
    SOUTH        (Location::getSouthLocation),
    SOUTH_WEST   (Location::getSouthWestLocation),
    WEST         (Location::getWestLocation),
    NORTH_WEST   (Location::getNorthWestLocation),
    CENTER       (Location::getCenterLocation);

    private final BiFunction<Dimension, Dimension, Point> locator;


    Location(BiFunction<Dimension, Dimension, Point> of) {
        this.locator = of;
    }

    public Point relativeTo(Dimension dimension, Dimension anchor) {
        return locator.apply(dimension, anchor);
    }


    public static Location of(String name) {
        try {
            return Location.valueOf(normalize(name));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid location name: " + name, exception);
        }
    }


    private static Point getNorthLocation(Dimension element, Dimension anchor) {
        var x = (int) (anchor.width - element.getWidth()) / 2;
        return new Point(x, 0);
    }

    private static Point getNorthEastLocation(Dimension element, Dimension anchor) {
        var x = (int) (anchor.width - element.getWidth());
        return new Point(x, 0);
    }

    private static Point getEastLocation(Dimension element, Dimension anchor) {
        var x = (int) (anchor.width - element.getWidth());
        var y = (int) (anchor.height - element.getHeight()) / 2;
        return new Point(x, y);
    }

    private static Point getSouthEastLocation(Dimension element, Dimension anchor) {
        var x = (int) (anchor.width - element.getWidth());
        var y = (int) (anchor.height - element.getHeight());
        return new Point(x, y);
    }

    private static Point getSouthLocation(Dimension element, Dimension anchor) {
        var x = (int) (anchor.width - element.getWidth()) / 2;
        var y = (int) (anchor.height - element.getHeight());
        return new Point(x, y);
    }

    private static Point getSouthWestLocation(Dimension element, Dimension anchor) {
        var y = (int) (anchor.height - element.getHeight());
        return new Point(0, y);
    }

    private static Point getWestLocation(Dimension element, Dimension anchor) {
        var y = (int) (anchor.height - element.getHeight()) / 2;
        return new Point(0, y);
    }

    private static Point getNorthWestLocation(Dimension element, Dimension anchor) {
        return new Point(0, 0);
    }

    private static Point getCenterLocation(Dimension element, Dimension anchor) {
        var x = (int) (anchor.width - element.getWidth()) / 2;
        var y = (int) (anchor.height - element.getHeight()) / 2;
        return new Point(x, y);
    }

}
