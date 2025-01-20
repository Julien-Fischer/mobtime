package net.agiledeveloper.mobtime.utils;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;

import static net.agiledeveloper.mobtime.utils.PathUtils.path;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class PathUtilsTest {

    @Test
    void path_returns_path_with_system_dependant_separators() {
        Path path = path("/path/to/directory");
        var systemPath = File.separator + "path" + File.separator + "to" + File.separator + "directory";

        assertThat(path).hasToString(systemPath);
    }

    @Test
    void empty() {
        Path path = path("");

        assertThat(path).hasToString("");
    }

}
