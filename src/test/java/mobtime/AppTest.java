package mobtime;

import org.assertj.core.api.AbstractStringAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class AppTest {

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


    @Test
    void app_without_parameters_runs() {
        runApp();

        assertStandardOutput()
                .isEqualTo("Done");
    }

    @Test
    void app_with_parameters_runs() {
        withParameters("--key=value");

        runApp();

        assertStandardOutput()
                .contains("--key=value")
                .doesNotContain("Error:");
    }

    @Test
    void app_with_invalid_parameters_throws() {
        withParameters("--invalid=parameter");

        assertThatThrownBy(this::runApp)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not a valid argument");

        assertStandardOutput()
                .contains("Error:")
                .doesNotContain("Done");
    }


    private void runApp() {
        App.main(args);
    }

    private AbstractStringAssert<?> assertStandardOutput() {
        return assertThat(outContent.toString().trim());
    }

    private void withParameters(String parameters) {
        args = new String[] {parameters};
    }

}
