package mobtime.domain.time;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.List;

import static java.time.temporal.ChronoUnit.*;
import static mobtime.utils.TimeUtils.*;

public record Duration(double amount, ChronoUnit unit) implements TemporalAmount {

    public static final double DEFAULT_VALUE_MINUTES = 15;
    public static final List<TemporalUnit> SUPPORTED_UNITS = List.of(
            MILLIS, SECONDS, MINUTES
    );

    public Duration(double amount, ChronoUnit unit) {
        this.amount = requirePositive(amount);
        this.unit = unit;
    }


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
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Temporal subtractFrom(Temporal temporal) {
        throw new UnsupportedOperationException("Not implemented");
    }


    public static Duration fromMinutes(double minutes) {
        return new Duration(minutes, ChronoUnit.MINUTES);
    }


    private double fail(TemporalUnit unit) {
        throw new IllegalStateException("Unexpected value: " + unit);
    }

    private double requirePositive(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Invalid duration: value must be non-negative. Received: " + amount);
        }
        return amount;
    }

}
