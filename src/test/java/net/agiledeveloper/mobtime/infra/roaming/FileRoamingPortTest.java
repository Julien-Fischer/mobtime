package net.agiledeveloper.mobtime.infra.roaming;

import net.agiledeveloper.mobtime.infra.swing.gui.Coordinate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static net.agiledeveloper.mobtime.infra.roaming.FileRoaming.Key.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;

class FileRoamingPortTest {

    private static final Duration FIVE_MINUTES = Duration.ofMinutes(5);
    private static final Duration TWO_MINUTES = Duration.ofMinutes(2);

    private static final Path ROAMING_FILE      = Path.of("COORDINATE_FILE_PATH");
    private static final Path NON_EXISTENT_FILE = Path.of("NON_EXISTENT_FILE");

    private final FileRoaming roaming = new FileRoaming(ROAMING_FILE);

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

        assertThat(roaming.getCoordinate())
                .contains(lastLocation);
    }

    @Test
    void getCoordinate_returns_empty_optional_when_file_does_not_exist() {
        var nonExistentRoaming = new FileRoaming(NON_EXISTENT_FILE);

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
    void setPausable_writes_serialized_coordinate_to_roaming() {
        roaming.setPausable(true);

        Boolean pausable = roaming.isPausable();

        assertThat(pausable).isTrue();
    }

    @Test
    void isPausable_returns_empty_optional_when_file_does_not_exist() {
        var nonExistentRoaming = new FileRoaming(NON_EXISTENT_FILE);

        Boolean pausable = nonExistentRoaming.isPausable();

        assertThat(pausable).isFalse();
    }

    @Test
    void isPausable_throws_exception_when_coordinate_could_not_be_parsed() throws IOException {
        givenThatpausableIsMalformed();

        assertThatNoException()
                .isThrownBy(roaming::isPausable);
    }


    @Test
    void getActivityDuration_writes_serialized_timestamp_to_roaming() {
        roaming.setActivityDuration(TWO_MINUTES);

        assertThat(roaming.getActivityDuration())
                .contains(TWO_MINUTES);
    }


    @Test
    void getActivityRemaining_writes_serialized_timestamp_to_roaming() {
        roaming.setActivityRemaining(FIVE_MINUTES);

        assertThat(roaming.getActivityRemaining())
                .contains(FIVE_MINUTES);
    }

    @Test
    void getActivityStart_throws_exception_when_coordinate_could_not_be_parsed() throws IOException {
        givenThatRemainingTimeIsMalformed();

        assertThatExceptionOfType(RoamingException.class)
                .isThrownBy(roaming::getActivityRemaining);
    }

    @Test
    void getActivityStop_throws_exception_when_coordinate_could_not_be_parsed() throws IOException {
        givenThatActivityDurationIsMalformed();

        assertThatExceptionOfType(RoamingException.class)
                .isThrownBy(roaming::getActivityDuration);
    }

    @Test
    void getActivityStop_returns_empty_optional_when_file_does_not_exist() {
        var nonExistentRoaming = new FileRoaming(NON_EXISTENT_FILE);

        Optional<Duration> duration = nonExistentRoaming.getActivityDuration();

        assertThat(duration).isEmpty();
    }

    @Test
    void getActivityRemaining_returns_empty_optional_when_file_does_not_exist() {
        var nonExistentRoaming = new FileRoaming(NON_EXISTENT_FILE);

        Optional<Duration> remaining = nonExistentRoaming.getActivityRemaining();

        assertThat(remaining).isEmpty();
    }

    @Test
    void roaming_stores_and_retrieves_any_value() {
        var expectedpausable = true;
        var expectedCoordinate = new Coordinate(3, 5);
        var expectedDuration = Duration.ofMinutes(5);
        var expectedRemaining = Duration.ofMinutes(4);
        roaming.setActivityDuration(expectedDuration);
        roaming.setActivityRemaining(expectedRemaining);
        roaming.setCoordinate(expectedCoordinate);
        roaming.setPausable(expectedpausable);

        Coordinate coordinate = roaming.getCoordinate().orElseThrow();
        boolean pausable = roaming.isPausable();
        Duration remaining = roaming.getActivityRemaining().orElseThrow();
        Duration duration = roaming.getActivityDuration().orElseThrow();

        assertThat(remaining).isEqualTo(expectedRemaining);
        assertThat(duration).isEqualTo(expectedDuration);
        assertThat(coordinate).isEqualTo(expectedCoordinate);
        assertThat(pausable).isEqualTo(expectedpausable);
        assertThat(duration).isEqualTo(expectedDuration);
    }

    @Test
    void getDuration_returns_activity_duration() throws IOException {
        givenThatActivityLastsFor(FIVE_MINUTES);

        assertThat(roaming.getActivityDuration())
                .contains(FIVE_MINUTES);
    }

    @Test
    void an_activity_is_not_ongoing_when_roaming_is_empty() {
        assertThat(roaming.hasOngoingActivity())
                .isFalse();
    }

    @Test
    void an_activity_is_not_ongoing_when_measured_duration_is_less_than_expected_duration() throws IOException {
        givenThatActivityLastsFor(TWO_MINUTES);
        givenThatRemainingTimeIs(FIVE_MINUTES);

        assertThat(roaming.hasOngoingActivity())
                .isFalse();
    }

    @Test
    void an_activity_is_ongoing_when_measured_duration_is_equal_or_greater_than_expected_duration() throws IOException {
        givenThatActivityLastsFor(FIVE_MINUTES);
        givenThatRemainingTimeIs(TWO_MINUTES);

        assertThat(roaming.hasOngoingActivity())
                .isTrue();
    }

    private void givenThatActivityLastsFor(Duration duration) throws IOException {
        setProperty(ACTIVITY_DURATION,  duration.toMillis());
    }

    private void givenThatRemainingTimeIs(Duration duration) throws IOException {
        setProperty(ACTIVITY_REMAINING, duration.toMillis());
    }

    private void givenThatCoordinateIsMalformed() throws IOException {
        setProperty(COORDINATE, "malformed coordinate");
    }

    private void givenThatpausableIsMalformed() throws IOException {
        setProperty(PAUSABLE, "malformed boolean");
    }

    private void givenThatRemainingTimeIsMalformed() throws IOException {
        setProperty(ACTIVITY_REMAINING, "malformed timestamp");
    }

    private void givenThatActivityDurationIsMalformed() throws IOException {
        setProperty(ACTIVITY_DURATION, "malformed timestamp");
    }

    private void setProperty(FileRoaming.Key key, Object value) throws IOException {
        Files.writeString(ROAMING_FILE, System.lineSeparator() + key + "=" + value.toString(), CREATE, APPEND);
    }

}
