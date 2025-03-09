package net.agiledeveloper.mobtime.infra.roaming;

import net.agiledeveloper.mobtime.infra.swing.gui.Coordinate;
import net.agiledeveloper.mobtime.utils.App;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Properties;

import static net.agiledeveloper.mobtime.infra.roaming.Roaming.Key.*;

public class Roaming {

    private final Path roamingFile;
    private Properties properties;


    public Roaming(Path roamingFile) {
        this.roamingFile = roamingFile;
    }


    public void setCoordinate(Coordinate coordinate) {
        write(COORDINATE, coordinate);
    }

    public void setDetached(boolean detached) {
        write(DETACH, detached);
    }

    public void setActivityStart(Instant lastActivity) {
        write(ACTIVITY_START, lastActivity.toEpochMilli());
    }

    public void setActivityStop(Instant lastActivity) {
        write(ACTIVITY_STOP, lastActivity.toEpochMilli());
    }

    public Duration getLastActivityDuration() {
        var start = this.getActivityStart();
        var stop = this.getActivityStop();
        if (start.isPresent() && stop.isPresent()) {
            return Duration.between(start.get(), stop.get());
        } else {
            throw new UnsupportedOperationException("%s, %s".formatted(start, stop));
        }
    }

    public Optional<Coordinate> getCoordinate() {
        var serialized = read(COORDINATE);
        return (serialized == null) ?
                Optional.empty() :
                Optional.of(Coordinate.of(serialized));
    }

    public boolean isDetached() {
        var serialized = read(DETACH);
        return Boolean.parseBoolean(serialized);
    }

    public Optional<Instant> getActivityStart() {
        var serialized = read(ACTIVITY_START);
        return (serialized == null) ?
                Optional.empty() :
                ofEpochMilli(serialized);
    }

    public Optional<Instant> getActivityStop() {
        var serialized = read(ACTIVITY_STOP);
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

    private void write(Key key, Object value) {
        if (properties == null) {
            properties = new Properties();
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
    }

    private void createRoamingIfNotExists() throws IOException {
        if (!Files.exists(roamingFile)) {
            Files.createFile(roamingFile);
        }
    }


    public enum Key {

        COORDINATE     ("coordinate"),
        DETACH         ("detach"),
        ACTIVITY_START ("activity.start"),
        ACTIVITY_STOP  ("activity.stop");

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
