package net.agiledeveloper.mobtime.domain;

public record Ratio(double value) {

    public static Ratio of(double numerator, double denominator) {
        return new Ratio(numerator / denominator);
    }

    public boolean greaterThan(Ratio ratio) {
        return value > ratio.value;
    }

    public boolean lessThan(Ratio ratio) {
        return value < ratio.value;
    }

}
