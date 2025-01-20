package net.agiledeveloper.mobtime.utils;

import java.io.File;
import java.nio.file.Path;

public class PathUtils {

    private PathUtils() {}


    public static Path path(String pathName) {
        var systemDependant = pathName.replace("/", File.separator);
        return Path.of(systemDependant);
    }

}
