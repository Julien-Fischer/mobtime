package net.agiledeveloper.mobtime.utils;

import net.agiledeveloper.mobtime.test.lib.Mock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;

import static net.agiledeveloper.mobtime.test.lib.BuilderAssertion.expectThat;
import static net.agiledeveloper.mobtime.utils.TimeFormatter.formatInstant;

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
        logger.log("hello", "world", "!");

        expectThat(target.getMessage())
                .isEqualTo("[%s] hello world !".formatted(DATETIME));
    }

    @Test
    void log_when_no_message_logs_current_time() {
        logger.log();

        expectThat(target.getMessage())
                .isEqualTo("[%s] ".formatted(DATETIME));
    }

    @Test
    void err_logs_specified_messages() {
        logger.err("hello", "world", "!");

        expectThat(target.getMessage())
                .isEqualTo("[%s] /!\\ hello world !".formatted(DATETIME));
    }

    @Test
    void err_logs_specified_exception() {
        var exception = new IOException("hello world !");

        logger.err(exception);

        expectThat(target.getMessage())
                .isEqualTo("[%s] /!\\ %s".formatted(DATETIME, exception.getMessage()));
    }

    @Test
    void logSeparator_uses_default_separator() {
        logger.logSeparator();

        expectThat(target.getMessage())
                .isEqualTo(AppLogger.DEFAULT_SEPARATOR);
    }

    @Test
    void logSeparator_uses_provided_separator_when_specified() {
        logger.logSeparator("---");

        expectThat(target.getMessage())
                .isEqualTo("---");
    }



    private static class TargetMock extends Mock implements AppLogger.Target {

        private String message;

        @Override
        public void print(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

    }

}
