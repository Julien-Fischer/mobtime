package net.agiledeveloper.mobtime.domain.session;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;

class UsernameTest {

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  \r\n\t  "})
    void blank_usernames_can_not_be_instantiated(String invalidUsername) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Username(invalidUsername));
    }

    @Test
    void null_usernames_can_not_be_instantiated() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Username(null));
    }

    @Test
    void valid_usernames_can_be_instantiated() {
        assertThatNoException()
                .isThrownBy(() -> new Username("John Doe"));
    }

}
