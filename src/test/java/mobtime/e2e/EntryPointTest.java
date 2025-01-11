package mobtime.e2e;

import mobtime.EntryPoint;
import org.assertj.core.api.AbstractStringAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class EntryPointTest {

    private ByteArrayOutputStream outContent;
    private String[] args;


    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(System.out);
    }


    static Stream<Arguments> invalidDurations() {
        return Stream.of(
                Arguments.of("non-digits"),
                Arguments.of("0.00f"),
                Arguments.of("-1")
        );
    }


    @Test
    void app_without_command_throws() {
        assertThatThrownBy(this::runApp)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No command specified");

        assertStandardOutput()
                .doesNotContain("Done");
    }

    @Test
    void app_with_parameters_runs() {
        withParameters("--start", "--dry-run");

        runApp();

        assertStandardOutput()
                .doesNotContain("Error:");
    }

    @Test
    void app_with_duration_parameter_prints_duration() {
        withParameters("--start", "--duration=42", "--dry-run");

        runApp();

        assertStandardOutput()
                .contains("--start")
                .contains("--duration=42");
    }

    @Test
    void app_with_invalid_parameters_throws() {
        withParameters("--invalid=name");

        assertThatThrownBy(this::runApp)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not a valid argument");

        assertStandardOutput()
                .contains("Error:")
                .doesNotContain("Done");
    }

    @ParameterizedTest
    @MethodSource("invalidDurations")
    void app_with_invalid_duration_throws(String invalidDuration) {
        withParameters("--start", "--dry-run", "--duration=" + invalidDuration);

        assertThatThrownBy(this::runApp)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid duration");
    }


    private void runApp() {
        EntryPoint.main(args);
    }

    private AbstractStringAssert<?> assertStandardOutput() {
        return assertThat(outContent.toString().trim());
    }

    private void withParameters(String... parameters) {
        args = parameters;
    }

}
