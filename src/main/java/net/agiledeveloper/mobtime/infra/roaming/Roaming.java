package net.agiledeveloper.mobtime.infra.roaming;

import net.agiledeveloper.mobtime.infra.swing.gui.Coordinate;
import net.agiledeveloper.mobtime.utils.App;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import java.util.Properties;

public class Roaming {

    private final Path roamingFile;
    private Properties properties;


    public Roaming(Path roamingFile) {
        this.roamingFile = roamingFile;
    }


    public void setCoordinate(Coordinate coordinate) {
        write("coordinate", coordinate);
    }

    public void setDetached(boolean detached) {
        write("detach", detached);
    }

    public void setLastActivity(Instant lastActivity) {
        write("last.activity", lastActivity.toEpochMilli());
    }

    public Optional<Coordinate> getCoordinate() {
        var serialized = read("coordinate");
        return (serialized == null) ?
                Optional.empty() :
                Optional.of(Coordinate.of(serialized));
    }

    public boolean isDetached() {
        var serialized = read("detach");
        return Boolean.parseBoolean(serialized);
    }

    public Optional<Instant> getLastActivity() {
        var serialized = read("last.activity");
        return (serialized == null) ?
                Optional.empty() :
                ofEpochMilli(serialized);
    }

    private Optional<Instant> ofEpochMilli(String epochMilli) {
        try {
            long epochMillis = Long.parseLong(epochMilli);
            var instant = Instant.ofEpochMilli(epochMillis);
            return Optional.of(instant);
        } catch (NumberFormatException cause) {
            throw new RoamingException(cause);
        }
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
            loadProperties();
            return properties.getProperty(key);
        } catch (Exception cause) {
            App.logger.err(cause);
            throw new RoamingException(cause);
        }
    }

    private void loadProperties() throws IOException {
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
