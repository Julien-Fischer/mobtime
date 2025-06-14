package net.agiledeveloper.mobtime.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RatioTest {

    @Test
    void ratioComparisonWorks() {
        Ratio low = new Ratio(0.1);
        Ratio high = new Ratio(0.3);

        assertThat(low.lessThan(high)).isTrue();
        assertThat(high.greaterThan(low)).isTrue();

        assertThat(high.lessThan(low)).isFalse();
        assertThat(low.greaterThan(high)).isFalse();
    }

    @Test
    void ratioOfCalculatesValue() {
        Ratio ratio = Ratio.of(1, 4);

        assertThat(ratio.value()).isEqualTo(0.25);
    }

}
