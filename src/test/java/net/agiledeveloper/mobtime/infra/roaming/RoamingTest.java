package net.agiledeveloper.mobtime.infra.roaming;

import net.agiledeveloper.mobtime.infra.swing.gui.Coordinate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;

class RoamingTest {

    private static final Path ROAMING_FILE      = Path.of("COORDINATE_FILE_PATH");
    private static final Path NON_EXISTENT_FILE = Path.of("NON_EXISTENT_FILE");

    private final Roaming roaming = new Roaming(ROAMING_FILE);


    @AfterEach
    void tearDown() {
        List.of(ROAMING_FILE, NON_EXISTENT_FILE)
                .forEach(path -> {
                    if (Files.exists(path)) {
                        try {
                            Files.delete(path);
                        } catch (IOException exception) {
                            throw new RuntimeException(exception);
                        }
                    }
                });
    }


    @Test
    void saveCoordinate_writes_serialized_coordinate_to_roaming() {
        var lastLocation = new Coordinate(3, 5);

        roaming.saveCoordinate(lastLocation);

        Optional<Coordinate> location = roaming.readCoordinate();

        assertThat(location).isPresent();
        assertThat(location.get()).isEqualTo(lastLocation);
    }

    @Test
    void readCoordinate_returns_empty_optional_when_file_does_not_exist() {
        var nonExistentRoaming = new Roaming(NON_EXISTENT_FILE);

        Optional<Coordinate> location = nonExistentRoaming.readCoordinate();

        assertThat(location).isEmpty();
    }


    @Test
    void readCoordinate_throws_exception_when_coordinate_could_not_be_parsed() throws IOException {
        givenThatCoordinateIsMalformed();

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(roaming::readCoordinate);
    }


    @Test
    void saveDetached_writes_serialized_coordinate_to_roaming() {
        roaming.saveDetached(true);

        Boolean detached = roaming.readDetached();

        assertThat(detached).isTrue();
    }

    @Test
    void readDetached_returns_empty_optional_when_file_does_not_exist() {
        var nonExistentRoaming = new Roaming(NON_EXISTENT_FILE);

        Boolean detached = nonExistentRoaming.readDetached();

        assertThat(detached).isFalse();
    }


    @Test
    void readDetached_throws_exception_when_coordinate_could_not_be_parsed() throws IOException {
        givenThatDetachedIsMalformed();

        assertThatNoException()
                .isThrownBy(roaming::readDetached);
    }


    private void givenThatCoordinateIsMalformed() throws IOException {
        Files.writeString(ROAMING_FILE, "coordinate=malformed coordinate");
    }

    private void givenThatDetachedIsMalformed() throws IOException {
        Files.writeString(ROAMING_FILE, "coordinate=malformed coordinate");
    }

}
