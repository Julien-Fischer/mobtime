package net.agiledeveloper.mobtime.infra.roaming;

import net.agiledeveloper.mobtime.infra.swing.gui.Coordinate;
import net.agiledeveloper.mobtime.utils.App;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;
import java.util.Properties;

import static net.agiledeveloper.mobtime.infra.roaming.Roaming.Key.*;
import static net.agiledeveloper.mobtime.utils.TimeFormatter.toDuration;

public class Roaming {

    private final Path roamingFile;
    private Properties properties;


    public Roaming(Path roamingFile) {
        this.roamingFile = roamingFile;
    }


    public void setCoordinate(Coordinate coordinate) {
        write(COORDINATE, coordinate);
    }

    public void setPausable(boolean pausable) {
        write(PAUSABLE, pausable);
    }

    public void setActivityDuration(Duration duration) {
        var millis = duration.toMillis();
        App.logger.debug("[roaming] Set activity duration: " + millis);
        write(ACTIVITY_DURATION, duration.toMillis());
    }

    public void setActivityRemaining(Duration duration) {
        var millis = duration.toMillis();
        App.logger.debug("[roaming] Set activity remaining" + millis);
        write(ACTIVITY_REMAINING, duration.toMillis());
    }

    public Optional<Duration> getActivityDuration() {
        return getOptionalActivityDuration(ACTIVITY_DURATION);
    }

    public Optional<Duration> getActivityRemaining() {
        return getOptionalActivityDuration(ACTIVITY_REMAINING);
    }

    public Optional<Coordinate> getCoordinate() {
        var serialized = read(COORDINATE);
        return (serialized == null) ?
                Optional.empty() :
                Optional.of(Coordinate.of(serialized));
    }

    public boolean isPausable() {
        var serialized = read(PAUSABLE);
        return Boolean.parseBoolean(serialized);
    }

    public boolean hasOngoingActivity() {
        var remaining = getActivityRemaining();
        var duration = getActivityDuration();
        if (remaining.isEmpty() || duration.isEmpty()) {
            return false;
        }
        return duration.get()
                .minus(remaining.get())
                .isPositive();
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
