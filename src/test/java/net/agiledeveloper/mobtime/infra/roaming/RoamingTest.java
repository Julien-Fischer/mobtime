package net.agiledeveloper.mobtime.infra.roaming;

import net.agiledeveloper.mobtime.infra.swing.gui.Coordinate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.time.temporal.ChronoUnit.MINUTES;
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
    void setActivityStart_writes_serialized_coordinate_to_roaming() {
        roaming.setActivityStart(Instant.EPOCH);

        Optional<Instant> lastActivity = roaming.getActivityStart();

        assertThat(lastActivity).isPresent();
        assertThat(lastActivity.get()).isEqualTo(Instant.EPOCH);
    }

    @Test
    void getActivityStart_returns_empty_optional_when_file_does_not_exist() {
        var nonExistentRoaming = new Roaming(NON_EXISTENT_FILE);

        Optional<Instant> startInstant = nonExistentRoaming.getActivityStart();

        assertThat(startInstant).isEmpty();
    }

    @Test
    void getActivityStart_throws_exception_when_coordinate_could_not_be_parsed() throws IOException {
        givenThatActivityStartIsMalformed();

        assertThatExceptionOfType(RoamingException.class)
                .isThrownBy(roaming::getActivityStart);
    }


    @Test
    void setActivityStop_writes_serialized_coordinate_to_roaming() {
        roaming.setActivityStop(Instant.EPOCH);

        Optional<Instant> lastActivity = roaming.getActivityStop();

        assertThat(lastActivity).isPresent();
        assertThat(lastActivity.get()).isEqualTo(Instant.EPOCH);
    }

    @Test
    void getActivityStop_returns_empty_optional_when_file_does_not_exist() {
        var nonExistentRoaming = new Roaming(NON_EXISTENT_FILE);

        Optional<Instant> stopInstant = nonExistentRoaming.getActivityStop();

        assertThat(stopInstant).isEmpty();
    }

    @Test
    void getActivityStop_throws_exception_when_coordinate_could_not_be_parsed() throws IOException {
        givenThatActivityStopIsMalformed();

        assertThatExceptionOfType(RoamingException.class)
                .isThrownBy(roaming::getActivityStop);
    }

    @Test
    void getLastActivityDuration_returns_duration() throws IOException {
        var start = Instant.EPOCH;
        var stop = start.plus(1, MINUTES);
        givenThatSessionLasted(start, stop);

        Duration duration = roaming.getLastActivityDuration();

        assertThat(duration)
                .isEqualTo(Duration.between(start, stop));
    }

    private void givenThatSessionLasted(Instant start, Instant end) throws IOException {
        setProperty(ACTIVITY_START, start.toEpochMilli());
        setProperty(ACTIVITY_STOP,  end.toEpochMilli());
    }

    private void givenThatCoordinateIsMalformed() throws IOException {
        setProperty(COORDINATE, "malformed coordinate");
    }

    private void givenThatDetachedIsMalformed() throws IOException {
        setProperty(DETACH, "malformed boolean");
    }

    private void givenThatActivityStartIsMalformed() throws IOException {
        setProperty(ACTIVITY_START, "malformed timestamp");
    }

    private void givenThatActivityStopIsMalformed() throws IOException {
        setProperty(ACTIVITY_STOP, "malformed timestamp");
    }

    private void setProperty(Roaming.Key key, Object value) throws IOException {
        Files.writeString(ROAMING_FILE, System.lineSeparator() + key + "=" + value.toString(), CREATE, APPEND);
    }

}
