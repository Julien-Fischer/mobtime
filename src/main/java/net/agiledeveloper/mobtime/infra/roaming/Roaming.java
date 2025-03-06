package net.agiledeveloper.mobtime.infra.roaming;

import net.agiledeveloper.mobtime.infra.swing.gui.Coordinate;
import net.agiledeveloper.mobtime.utils.App;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

    public Coordinate read() throws IOException {
        try {
            var serialized = new String(Files.readAllBytes(roamingFile));
            return Coordinate.of(serialized);
        } catch (IOException ex) {
            App.logger.err(ex);
            throw ex;
        }
    }

}
