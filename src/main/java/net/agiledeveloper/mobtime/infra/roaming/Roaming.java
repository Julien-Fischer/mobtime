package net.agiledeveloper.mobtime.infra.roaming;

import net.agiledeveloper.mobtime.infra.swing.gui.Coordinate;
import net.agiledeveloper.mobtime.utils.App;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Optional;

public class Roaming {

    private final Path roamingFile;


    public Roaming(Path roamingFile) {
        this.roamingFile = roamingFile;
    }


    public void save(Coordinate coordinate) {
        try {
            Files.writeString(roamingFile, coordinate.toString());
        } catch (IOException ex) {
            App.logger.err(ex);
        }
    }

    public Optional<Coordinate> read() {
        try {
            var serialized = new String(Files.readAllBytes(roamingFile));
            return Optional.of(Coordinate.of(serialized));
        } catch (NoSuchFileException cause1) {
            return Optional.empty();
        } catch (IOException cause) {
            App.logger.err(cause);
            throw new RoamingException(cause);
        }
    }

}
