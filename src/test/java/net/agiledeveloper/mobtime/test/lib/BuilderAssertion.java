package net.agiledeveloper.mobtime.test.lib;

import org.assertj.core.api.AbstractAssert;

import static org.assertj.core.api.Assertions.assertThat;

public class BuilderAssertion {

    public static <T> AbstractAssert<?, T> expectThat(T element) {
        return assertThat(element);
    }

    public static <T> AbstractAssert<?, T> expectThat(Builder<T> builder) {
        return assertThat(builder.build());
    }

}
