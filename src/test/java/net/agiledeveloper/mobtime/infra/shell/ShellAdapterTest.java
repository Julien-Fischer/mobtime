package net.agiledeveloper.mobtime.infra.shell;

import net.agiledeveloper.mobtime.infra.InfraException;
import org.assertj.core.api.AbstractStringAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShellAdapterTest {

    private final ShellAdapter shellAdapter = new ShellAdapter(new CommandFormatterMock());
    private ByteArrayOutputStream outputStream;
    private int statusCode;

    @TempDir
    private Path tempDir;


    @AfterEach
    void tearDown() {
        System.setOut(System.out);
    }


    @Test
    void execute_when_status_code_zero() throws IOException, InfraException {
        givenThatScriptReturns(0);

        whenAdapterExecutes("echo 'Hello, world!'");

        assertThatOutput()
                .hasStatusCode(0)
                .contains("Hello, world!");
    }

    @Test
    void execute_when_status_code_non_zero() throws IOException, InfraException {
        givenThatScriptReturns(1);

        whenAdapterExecutes("echo 'An error occurred!'");

        assertThatOutput()
                .hasStatusCode(1)
                .contains("An error occurred!");
    }


    private void whenAdapterExecutes(String command) throws IOException, InfraException {
        // Create dummy script
        Path scriptPath = tempDir.resolve("mock_script.sh");
        String scriptContent = "#!/bin/bash\n" + command + "\nexit " + statusCode;
        Files.write(scriptPath, scriptContent.getBytes());
        // Make the script executable
        assertTrue(scriptPath.toFile().setExecutable(true));
        // Redirect System.out to capture the output
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        shellAdapter.execute(scriptPath.toString());
    }

    private void givenThatScriptReturns(int statusCode) {
        this.statusCode = statusCode;
    }

    private ScriptAssertion assertThatOutput() {
        return new ScriptAssertion();
    }


    private interface StatusCodeAssertion {
        OutputStreamAssertion hasStatusCode(int statusCode);
    }

    private interface OutputStreamAssertion {
        AbstractStringAssert<?> contains(String expected);
    }

    private class ScriptAssertion implements StatusCodeAssertion, OutputStreamAssertion {

        @Override
        public OutputStreamAssertion hasStatusCode(int code) {
            assertThat(statusCode).isEqualTo(code);
            return this;
        }

        @Override
        public AbstractStringAssert<?> contains(String expected) {
            return assertThat(outputStream.toString().trim()).contains(expected);
        }

    }


    private static class CommandFormatterMock implements CommandFormatter {

        @Override
        public ShellCommand format(String commandName) {
            return new ShellCommand(LinuxShell.SH, commandName);
        }

    }

}
