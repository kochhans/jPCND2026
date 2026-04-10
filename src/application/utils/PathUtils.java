package application.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.IOException;

public class PathUtils {

    /* ===============================
     * Bestehende Methode – unverändert
     * =============================== */

    public static String getOrdnerPfadMitSeparator(String dateiPfad) {
        if (dateiPfad == null || dateiPfad.isEmpty()) {
            return null;
        }

        Path pfad = Paths.get(dateiPfad);
        Path parent = pfad.getParent();

        if (parent == null) {
            return null;
        }

        String sep = File.separator;
        String ordnerPfad = parent.toString();

        if (!ordnerPfad.endsWith(sep)) {
            ordnerPfad += sep;
        }

        return ordnerPfad;
    }

    /* ===============================
     * NEU: OS-Erkennung
     * =============================== */

    private static final String OS =
            System.getProperty("os.name").toLowerCase();

    private static boolean isWindows() {
        return OS.contains("win");
    }

    /* ===============================
     * NEU: Config-Verzeichnis
     * =============================== */

    public static Path getConfigDir() {
        Path home = Paths.get(System.getProperty("user.home"));

        if (isWindows()) {
            return home.resolve(".jpcnd");
        }

        // Linux / Unix (XDG)
        String xdgConfig = System.getenv("XDG_CONFIG_HOME");
        if (xdgConfig != null && !xdgConfig.isBlank()) {
            return Paths.get(xdgConfig, "jpcnd");
        }

        return home.resolve(".config").resolve("jpcnd");
    }

    /* ===============================
     * NEU: Data-Verzeichnis (License etc.)
     * =============================== */

    public static Path getDataDir() {
        if (isWindows()) {
            String localAppData = System.getenv("LOCALAPPDATA");
            if (localAppData != null) {
                return Paths.get(localAppData, "jpcnd");
            }
        }

        // Linux / Unix (XDG)
        Path home = Paths.get(System.getProperty("user.home"));

        String xdgData = System.getenv("XDG_DATA_HOME");
        if (xdgData != null && !xdgData.isBlank()) {
            return Paths.get(xdgData, "jpcnd");
        }

        return home.resolve(".local").resolve("share").resolve("jpcnd");
    }

    /* ===============================
     * NEU: Konkrete Dateien
     * =============================== */

    public static Path getConfigFile() {
        return getConfigDir().resolve("config.ini");
    }

    public static Path getLicenseFile() {
        return getDataDir().resolve("license.key");
    }

    /* ===============================
     * NEU: Sicherstellen, dass Ordner existieren
     * =============================== */

    public static void ensureUserDirsExist() throws IOException {
        Files.createDirectories(getConfigDir());
        Files.createDirectories(getDataDir());
    }
}
