package net.agiledeveloper.mobtime.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class EnumUtils {

    private static final String SEPARATOR = "_";

    private EnumUtils() {}


    public static <E extends Enum<E>> String printValues(Class<E> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }

    public static String normalize(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Input string cannot be null or empty");
        }
        return name.toUpperCase()
                .replace("-", SEPARATOR)
                .replace(".", SEPARATOR)
                .replaceAll("\\s+", SEPARATOR);
    }

}
