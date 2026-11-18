package net.grongubbe.tengoku.common.io;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;

public final class AppPaths {
    private static final String APP_DIRECTORY = ".tengoku";

    private AppPaths() {}

    public static Path getAppDirectory() {
        Path home = Path.of(Objects.requireNonNull(System.getProperty("user.home"), "user.home"));

        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);

        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");

            Path base = appData != null && !appData.isBlank() ? Path.of(appData) : home.resolve("AppData").resolve("Roaming");

            return base.resolve(APP_DIRECTORY);
        }

        if (os.contains("mac") || os.contains("darwin")) {
            return home.resolve("Library").resolve("Application Support").resolve("Tengoku");
        }

        String xdgDataHome = System.getenv("XDG_DATA_HOME");

        Path base = xdgDataHome != null && !xdgDataHome.isBlank() ? Path.of(xdgDataHome) : home.resolve(".local").resolve("share");

        return base.resolve("tengoku");
    }
}