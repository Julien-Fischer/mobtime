package net.agiledeveloper.mobtime.infra.roaming;

import net.agiledeveloper.mobtime.infra.swing.gui.Coordinate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class RoamingTest {

    private static final Path ROAMING_FILE = Path.of("COORDINATE_FILE_PATH");

    private final Roaming roaming = new Roaming(ROAMING_FILE);


    @AfterEach
    void tearDown() throws IOException {
        Files.delete(ROAMING_FILE);
    }


    @Test
    void save_writes_serialized_coordinate_to_roaming() throws IOException {
        var lastLocation = new Coordinate(3, 5);

        roaming.save(lastLocation);

        Coordinate location = roaming.read();

        assertThat(location)
                .isEqualTo(lastLocation);
    }

}
