package net.agiledeveloper.mobtime.infra.roaming;

import net.agiledeveloper.App;
import net.agiledeveloper.mobtime.domain.ports.spi.SessionStorage;
import net.agiledeveloper.mobtime.infra.swing.gui.Coordinate;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;
import java.util.Properties;

import static net.agiledeveloper.mobtime.infra.roaming.FileSessionStorage.Key.*;
import static net.agiledeveloper.mobtime.utils.TimeFormatter.toDuration;

public class FileSessionStorage implements SessionStorage {

    private final Path roamingFile;
    private Properties properties;


    public FileSessionStorage(Path roamingFile) {
        this.roamingFile = roamingFile;
    }


    @Override
    public void setCoordinate(Coordinate coordinate) {
        write(COORDINATE, coordinate);
    }

    @Override
    public void setPausable(boolean pausable) {
        write(PAUSABLE, pausable);
    }

    @Override
    public void setActivityDuration(Duration duration) {
        var millis = duration.toMillis();
        App.logger.debug("[roaming] Set activity duration: " + millis);
        write(ACTIVITY_DURATION, duration.toMillis());
    }

    @Override
    public void setActivityRemaining(Duration remaining) {
        var millis = remaining.toMillis();
        App.logger.debug("[roaming] Set activity remaining" + millis);
        write(ACTIVITY_REMAINING, remaining.toMillis());
    }

    @Override
    public Optional<Duration> getActivityDuration() {
        return getOptionalActivityDuration(ACTIVITY_DURATION);
    }

    @Override
    public Optional<Duration> getActivityRemaining() {
        return getOptionalActivityDuration(ACTIVITY_REMAINING);
    }

    @Override
    public Optional<Coordinate> getCoordinate() {
        var serialized = read(COORDINATE);
        return (serialized == null) ?
                Optional.empty() :
                Optional.of(Coordinate.of(serialized));
    }

    @Override
    public boolean isPausable() {
        var serialized = read(PAUSABLE);
        return Boolean.parseBoolean(serialized);
    }

    private Optional<Duration> getOptionalActivityDuration(Key key) {
        var serialized = read(key);
        try {
            return (serialized == null) ?
                    Optional.empty() :
                    Optional.of(toDuration(serialized));
        } catch (NumberFormatException cause) {
            throw new RoamingException(cause);
        }
    }

    private void write(Key key, Object value) {
        App.logger.debug("[roaming] Writing property: %s=%s".formatted(key, value));
        if (properties == null) {
            try {
                loadProperties();
            } catch (IOException cause) {
                properties = new Properties();
                App.logger.debug("[roaming] Could not load roaming properties. Created a new one.");
            }
        }
        properties.put(key.toString(), value.toString());
        try (var output = new FileOutputStream(roamingFile.toFile())) {
            properties.store(output, null);
        } catch (IOException cause) {
            throw new RoamingException(cause);
        }
    }

    private String read(Key key) {
        try {
            createRoamingIfNotExists();
            loadProperties();
            return properties.getProperty(key.toString());
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
        App.logger.debug("[roaming] Loading properties:", properties.toString());
    }

    private void createRoamingIfNotExists() throws IOException {
        if (!Files.exists(roamingFile)) {
            Files.createFile(roamingFile);
        }
    }

    public enum Key {

        COORDINATE         ("coordinate"),
        PAUSABLE           ("pausable"),
        ACTIVITY_REMAINING ("activity.remaining"),
        ACTIVITY_DURATION  ("activity.duration");

        private final String nameString;

        Key(String nameString) {
            this.nameString = nameString;
        }

        @Override
        public String toString() {
            return nameString;
        }

    }

}
