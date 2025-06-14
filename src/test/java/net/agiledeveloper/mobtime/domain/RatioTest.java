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

}
