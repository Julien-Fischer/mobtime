package net.agiledeveloper.mobtime.domain;

public record Ratio(double value) {

    public static final Ratio ZERO          = new Ratio(0);
    public static final Ratio ONE_QUARTER   = new Ratio(0.25);
    public static final Ratio HALF          = new Ratio(0.5);
    public static final Ratio THREE_QUARTER = new Ratio(0.75);

    public Ratio {
        if (value < 0 || value > 1) {
            throw new IllegalArgumentException("Ratio must be between 0 and 1");
        }
    }

    public static Ratio of(double numerator, double denominator) {
        return new Ratio(numerator / denominator);
    }

    public boolean greaterThan(Ratio ratio) {
        return value > ratio.value;
    }

    public boolean lessThan(Ratio ratio) {
        return value < ratio.value;
    }

    public boolean lessOrEqualTo(Ratio ratio) {
        return lessThan(ratio) || equalTo(ratio);
    }

    public boolean equalTo(Ratio ratio) {
        return value == ratio.value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
