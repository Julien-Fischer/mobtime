package net.agiledeveloper.mobtime.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static net.agiledeveloper.mobtime.utils.EnumUtils.normalize;
import static net.agiledeveloper.mobtime.utils.EnumUtils.printValues;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class EnumUtilsTest {

    enum Shape {
        TRIANGLE,
        CIRCLE,
        SQUARE_PROPERTY;
    }

    static Stream<Arguments> normalizedNames() {
        return Stream.of(
                Arguments.of("TRIANGLE",        Shape.TRIANGLE.name()),
                Arguments.of("SQUARE PROPERTY", Shape.SQUARE_PROPERTY.name()),
                Arguments.of("SQUARE_PROPERTY", Shape.SQUARE_PROPERTY.name()),
                Arguments.of("SQUARE-PROPERTY", Shape.SQUARE_PROPERTY.name()),
                Arguments.of("square property", Shape.SQUARE_PROPERTY.name()),
                Arguments.of("square_property", Shape.SQUARE_PROPERTY.name()),
                Arguments.of("square-property", Shape.SQUARE_PROPERTY.name())
        );
    }


    @ParameterizedTest
    @MethodSource("normalizedNames")
    void normalize_returns_normalized_string(String input, String shapeName) {
        assertThat(normalize(input))
                .isEqualTo(shapeName);
    }

    @Test
    void printValues_returns_enum_values_as_string() {
        var shapes = "TRIANGLE, CIRCLE, SQUARE_PROPERTY";

        assertThat(printValues(Shape.class))
                .isEqualTo(shapes);
    }

}
