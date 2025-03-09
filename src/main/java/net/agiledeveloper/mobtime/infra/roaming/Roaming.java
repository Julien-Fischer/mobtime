package net.agiledeveloper.mobtime.infra.roaming;

import net.agiledeveloper.mobtime.infra.swing.gui.Coordinate;
import net.agiledeveloper.mobtime.utils.App;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

public class Roaming {

    private final Path roamingFile;
    private Properties properties;


    public Roaming(Path roamingFile) {
        this.roamingFile = roamingFile;
    }


    public void saveCoordinate(Coordinate coordinate) {
        write("coordinate", coordinate);
    }

    public void saveDetached(boolean detached) {
        write("detach", detached);
    }

    public Optional<Coordinate> readCoordinate() {
        var serialized = read("coordinate");
        return (serialized == null) ?
                Optional.empty() :
                Optional.of(Coordinate.of(serialized));
    }

    public boolean readDetached() {
        var serialized = read("detach");
        return Boolean.parseBoolean(serialized);
    }


    private void write(String property, Object value) {
        if (properties == null) {
            properties = new Properties();
        }
        properties.put(property, value.toString());
        try (var output = new FileOutputStream(roamingFile.toFile())) {
            properties.store(output, null);
        } catch (IOException cause) {
            throw new RoamingException(cause);
        }
    }

    private String read(String key) {
        try {
            createRoamingIfNotExists();
            readProperties();
            return properties.getProperty(key);
        } catch (Exception cause) {
            App.logger.err(cause);
            throw new RoamingException(cause);
        }
    }

    private void readProperties() throws IOException {
        if (properties != null) {
            return;
        }

        properties = new Properties();
        try (var inputStream = new FileInputStream(roamingFile.toFile())) {
            properties.load(inputStream);
        }
    }

    private void createRoamingIfNotExists() throws IOException {
        if (!Files.exists(roamingFile)) {
            Files.createFile(roamingFile);
        }
    }

}
