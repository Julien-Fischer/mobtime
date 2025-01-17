package net.agiledeveloper.mobtime.infra.swing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class LocationTest {

    static Stream<Arguments> locations() {
        return Stream.of(
                Arguments.of("north",      Location.NORTH),
                Arguments.of("north-east", Location.NORTH_EAST),
                Arguments.of("north_east", Location.NORTH_EAST),
                Arguments.of("north.east", Location.NORTH_EAST),
                Arguments.of("north east", Location.NORTH_EAST),
                Arguments.of("NorTh EaST", Location.NORTH_EAST),
                Arguments.of("south",      Location.SOUTH),
                Arguments.of("south-east", Location.SOUTH_EAST)
        );
    }


    @ParameterizedTest
    @MethodSource("locations")
    void of_returns_compass_instance(String subject, Location location) {
        assertThat(Location.of(subject))
                .isEqualTo(location);
    }

    @Test
    void of_when_null_throws_illegal_argument_exception() {
        assertThatThrownBy(() -> Location.of(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void of_when_empty_throws_illegal_argument_exception() {
        assertThatThrownBy(() -> Location.of(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void of_when_not_found_throws_illegal_argument_exception() {
        assertThatThrownBy(() -> Location.of("invalid location"))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
