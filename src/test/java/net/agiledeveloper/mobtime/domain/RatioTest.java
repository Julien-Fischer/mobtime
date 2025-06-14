package net.agiledeveloper.mobtime.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class RatioTest {

    @Test
    void ratios_can_be_compared() {
        Ratio low = new Ratio(0.1);
        Ratio high = new Ratio(0.3);

        assertThat(low.lessThan(high)).isTrue();
        assertThat(high.greaterThan(low)).isTrue();

        assertThat(high.lessThan(low)).isFalse();
        assertThat(low.greaterThan(high)).isFalse();
    }

    @Test
    void ratios_can_be_instantiated_from_a_fraction() {
        Ratio ratio = Ratio.of(1, 4);

        assertThat(ratio.value()).isEqualTo(0.25);
    }

    @Test
    void a_ratio_is_between_zero_and_one() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Ratio(1.001));
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Ratio(-0.001));

        assertThatNoException()
                .isThrownBy(() -> new Ratio(0));
        assertThatNoException()
                .isThrownBy(() -> new Ratio(1));
    }

    @Test
    void two_ratios_with_same_values_are_equal() {
        Ratio r1 = new Ratio(0.5);
        Ratio r2 = new Ratio(0.5);

        assertThat(r1.equalTo(r2)).isTrue();
    }

    @Test
    void two_ratios_with_different_values_are_not_equal() {
        Ratio r1 = new Ratio(0.5);
        Ratio r2 = new Ratio(0.6);

        assertThat(r1.equalTo(r2)).isFalse();
    }

    @Test
    void less_or_equal_returns_true_when_less() {
        Ratio r1 = new Ratio(0.5);
        Ratio r2 = new Ratio(0.6);

        assertThat(r1.lessOrEqualTo(r2)).isTrue();
        assertThat(r2.lessOrEqualTo(r1)).isFalse();
    }

    @Test
    void less_or_equal_returns_true_when_equal() {
        Ratio r1 = new Ratio(0.5);
        Ratio r2 = new Ratio(0.5);

        assertThat(r1.lessOrEqualTo(r2)).isTrue();
    }

}
