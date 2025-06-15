package net.agiledeveloper.mobtime.domain.ports.spi;

import net.agiledeveloper.mobtime.infra.swing.gui.Coordinate;

import java.time.Duration;
import java.util.Optional;

public interface SessionStorage {

    void setCoordinate(Coordinate coordinate);

    void setPausable(boolean pausable);

    void setActivityDuration(Duration duration);

    void setActivityRemaining(Duration remaining);

    Optional<Duration> getActivityDuration();

    Optional<Duration> getActivityRemaining();

    Optional<Coordinate> getCoordinate();

    boolean isPausable();

    default boolean hasOngoingActivity() {
        var remaining = getActivityRemaining();
        var duration = getActivityDuration();
        if (remaining.isEmpty() || duration.isEmpty()) {
            return false;
        }
        return duration.get()
                .minus(remaining.get())
                .isPositive();
    }

}
