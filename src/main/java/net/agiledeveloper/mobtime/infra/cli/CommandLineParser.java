package net.agiledeveloper.mobtime.infra.cli;

import java.util.Arrays;
import java.util.List;

public class CommandLineParser {

    public List<BashParameter> parse(String[] commandLine) {
        if (commandLine == null) {
            throw new IllegalArgumentException("No command specified");
        }

        return Arrays.stream(commandLine)
                .map(BashParameter::new)
                .toList();
    }

}
