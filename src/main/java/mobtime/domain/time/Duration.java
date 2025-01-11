package mobtime.domain.time;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.List;

import static java.time.temporal.ChronoUnit.*;
import static mobtime.utils.TimeUtils.*;

public record Duration(double amount, ChronoUnit unit) implements TemporalAmount {

    private static final List<TemporalUnit> SUPPORTED_UNITS = List.of(
            MILLIS, SECONDS, MINUTES
    );

    public double getMinutes() {
        return switch (unit) {
            case MILLIS  -> millisToMinutes(amount);
            case SECONDS -> secondsToMinutes(amount);
            case MINUTES -> amount;
            default -> fail(unit);
        };
    }

    public double getSeconds() {
        return switch (unit) {
            case MILLIS  -> millisToSeconds(amount);
            case SECONDS -> amount;
            case MINUTES -> minutesToSeconds(amount);
            default -> fail(unit);
        };
    }

    public double getMillis() {
        return switch (unit) {
            case MILLIS  -> amount;
            case SECONDS -> secondsToMillis(amount);
            case MINUTES -> minutesToMillis(amount);
            default -> fail(unit);
        };
    }

    @Override
    public long get(TemporalUnit unit) {
        return switch (unit) {
            case MILLIS  -> (long) getMillis();
            case SECONDS -> (long) getSeconds();
            case MINUTES -> (long) getMinutes();
            default -> (long) fail(unit);
        };
    }

    @Override
    public List<TemporalUnit> getUnits() {
        return SUPPORTED_UNITS;
    }

    @Override
    public Temporal addTo(Temporal temporal) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Temporal subtractFrom(Temporal temporal) {
        throw new UnsupportedOperationException("Not implemented yet");
    }


    private double fail(TemporalUnit unit) {
        throw new IllegalStateException("Unexpected value: " + unit);
    }

}
