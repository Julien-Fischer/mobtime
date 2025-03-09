package net.agiledeveloper.mobtime.infra.roaming;

import net.agiledeveloper.mobtime.infra.swing.gui.Coordinate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static net.agiledeveloper.mobtime.infra.roaming.Roaming.Key.*;
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
    void setCoordinate_writes_serialized_coordinate_to_roaming() {
        var lastLocation = new Coordinate(3, 5);

        roaming.setCoordinate(lastLocation);

        Optional<Coordinate> location = roaming.getCoordinate();

        assertThat(location).isPresent();
        assertThat(location.get()).isEqualTo(lastLocation);
    }

    @Test
    void getCoordinate_returns_empty_optional_when_file_does_not_exist() {
        var nonExistentRoaming = new Roaming(NON_EXISTENT_FILE);

        Optional<Coordinate> location = nonExistentRoaming.getCoordinate();

        assertThat(location).isEmpty();
    }


    @Test
    void getCoordinate_throws_exception_when_coordinate_could_not_be_parsed() throws IOException {
        givenThatCoordinateIsMalformed();

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(roaming::getCoordinate);
    }


    @Test
    void setDetached_writes_serialized_coordinate_to_roaming() {
        roaming.setDetached(true);

        Boolean detached = roaming.isDetached();

        assertThat(detached).isTrue();
    }

    @Test
    void isDetached_returns_empty_optional_when_file_does_not_exist() {
        var nonExistentRoaming = new Roaming(NON_EXISTENT_FILE);

        Boolean detached = nonExistentRoaming.isDetached();

        assertThat(detached).isFalse();
    }


    @Test
    void isDetached_throws_exception_when_coordinate_could_not_be_parsed() throws IOException {
        givenThatDetachedIsMalformed();

        assertThatNoException()
                .isThrownBy(roaming::isDetached);
    }


    @Test
    void setLastActivity_writes_serialized_coordinate_to_roaming() {
        roaming.setLastActivity(Instant.EPOCH);

        Optional<Instant> lastActivity = roaming.getLastActivity();

        assertThat(lastActivity).isPresent();
        assertThat(lastActivity.get()).isEqualTo(Instant.EPOCH);
    }

    @Test
    void getLastActivity_returns_empty_optional_when_file_does_not_exist() {
        var nonExistentRoaming = new Roaming(NON_EXISTENT_FILE);

        Optional<Instant> detached = nonExistentRoaming.getLastActivity();

        assertThat(detached).isEmpty();
    }


    @Test
    void getLastActivity_throws_exception_when_coordinate_could_not_be_parsed() throws IOException {
        givenThatLastActivityIsMalformed();

        assertThatExceptionOfType(RoamingException.class)
                .isThrownBy(roaming::getLastActivity);
    }


    private void givenThatCoordinateIsMalformed() throws IOException {
        setMalformedProperty(COORDINATE, "malformed coordinate");
    }

    private void givenThatDetachedIsMalformed() throws IOException {
        setMalformedProperty(DETACH, "malformed boolean");
    }

    private void givenThatLastActivityIsMalformed() throws IOException {
        setMalformedProperty(LAST_ACTIVITY, "malformed timestamp");
    }

    private void setMalformedProperty(Roaming.Key key, String value) throws IOException {
        Files.writeString(ROAMING_FILE, key + "=" + value);
    }

}
