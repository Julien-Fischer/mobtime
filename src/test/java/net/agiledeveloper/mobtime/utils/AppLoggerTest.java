package net.agiledeveloper.mobtime.utils;

import net.agiledeveloper.mobtime.test.lib.Mock;
import net.agiledeveloper.mobtime.utils.AppLogger.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.time.Instant;

import static net.agiledeveloper.mobtime.test.lib.BuilderAssertion.expectThat;
import static net.agiledeveloper.mobtime.utils.AppLogger.Level.*;
import static net.agiledeveloper.mobtime.utils.TimeFormatter.formatInstant;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AppLoggerTest {

    private static final Instant NOW = Instant.now();
    private static final String DATETIME = formatInstant(NOW);

    private AppLogger logger;
    private TargetMock target;


    @BeforeEach
    void setUp() {
        target = new TargetMock();
        logger = new AppLogger(target, () -> NOW);
    }


    @Test
    void log_logs_specified_messages() {
        logger.setLevel(INFO);

        logger.log("hello", "world", "!");

        expectThat(target.getMessage())
                .isEqualTo("[%s] hello world !".formatted(DATETIME));
    }

    @Test
    void log_when_no_message_logs_current_time() {
        logger.setLevel(INFO);

        logger.log();

        expectThat(target.getMessage())
                .isEqualTo("[%s] ".formatted(DATETIME));
    }

    @Test
    void err_logs_specified_messages() {
        logger.setLevel(INFO);

        logger.err("hello", "world", "!");

        expectThat(target.getMessage())
                .isEqualTo("[%s] /!\\ hello world !".formatted(DATETIME));
    }

    @Test
    void err_logs_specified_exception() {
        logger.setLevel(INFO);

        var exception = new IOException("hello world !");

        logger.err(exception);

        expectThat(target.getMessage())
                .isEqualTo("[%s] /!\\ %s".formatted(DATETIME, exception.getMessage()));
    }

    @Test
    void logSeparator_uses_default_separator() {
        logger.setLevel(INFO);

        logger.logSeparator();

        expectThat(target.getMessage())
                .isEqualTo(AppLogger.DEFAULT_SEPARATOR);
    }

    @Test
    void logSeparator_uses_provided_separator_when_specified() {
        logger.setLevel(INFO);

        logger.logSeparator("---");

        expectThat(target.getMessage())
                .isEqualTo("---");
    }

    @Test
    void logger_does_not_log_info_messages_when_set_to_error_level() {
        logger.setLevel(ERROR);

        logger.debug("this message should not be logged");
        logger.log("this message should not be logged");

        assertThat(target.wasNeverCalled())
                .isTrue();
    }

    @Test
    void logger_does_not_log_debug_messages_when_set_to_info_level() {
        logger.setLevel(INFO);

        logger.debug("this message should not be logged");

        assertThat(target.wasNeverCalled())
                .isTrue();
    }

    @Test
    void logger_logs_debug_messages_when_set_to_debug_level() {
        logger.setLevel(DEBUG);

        logger.debug("this message should not be logged");

        assertThat(target.wasNeverCalled())
                .isFalse();
    }

    @Nested
    class LevelTest {

        @Test
        void lessOrEqual() {
            assertThat(DEBUG.lessOrEqual(INFO)).isTrue();
            assertThat(DEBUG.lessOrEqual(ERROR)).isTrue();
            assertThat(INFO.lessOrEqual(INFO)).isTrue();

            assertThat(ERROR.lessOrEqual(INFO)).isFalse();
            assertThat(ERROR.lessOrEqual(DEBUG)).isFalse();
            assertThat(INFO.lessOrEqual(DEBUG)).isFalse();
        }

        @ParameterizedTest
        @ValueSource(strings = {"info", "Info", "INFO", "InFo"})
        void of_interpolates_enum_constant(String level) {
            assertThat(Level.of(level)).isEqualTo(INFO);
        }

    }


    private static class TargetMock extends Mock implements AppLogger.Target {

        private String message;

        @Override
        public void print(String message) {
            this.message = message;
            incrementCallCount();
        }

        public String getMessage() {
            return message;
        }

    }

}
