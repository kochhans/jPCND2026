package application.utils.license;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LicensePathUtil {

    /**
     * Liefert den Ordner, in dem die Lizenzdatei gespeichert werden soll.
     * Erstellt den Ordner bei Bedarf automatisch.
     */
    public static Path getLicenseFolder() {
        String os = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");
        Path folder;

        if (os.contains("win")) {
            folder = Paths.get(System.getenv("LOCALAPPDATA"), "jpcnd");
        } else if (os.contains("mac")) {
            folder = Paths.get(userHome, "Library", "Application Support", "jpcnd");
        } else { // Linux / Unix
            folder = Paths.get(userHome, ".config", "jpcnd");
        }

        try {
            Files.createDirectories(folder);
        } catch (Exception e) {
            throw new RuntimeException("Konnte Lizenzordner nicht erstellen: " + folder, e);
        }

        return folder;
    }

    /**
     * Voller Pfad zur Lizenzdatei
     */
    public static Path getLicenseFile() {
        return getLicenseFolder().resolve("license.key");
    }
}
