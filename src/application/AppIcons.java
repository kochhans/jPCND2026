package application;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Zentrale Verwaltung der Anwendungs-Icons.   
 * Lädt 16x16, 32x32, 64x64 für Hauptfenster & Msgboxen.
 * Kompatibel mit IDE und JAR (Windows, Linux, Mac).
 */
public class AppIcons {

    private static final List<Image> icons = new ArrayList<>();

    static {
        loadIcons();
    }

    private static void loadIcons() {
        String[] paths = {
            "/icons/javafx/jpcndicon0016.png",
            "/icons/javafx/jpcndicon0032.png",
            "/icons/javafx/jpcndicon0064.png"
        };

        for (String path : paths) {
            try {
                // 1️⃣ Prüfen ob Resource vorhanden
                if (AppIcons.class.getResource(path) != null) {
                    // 2️⃣ Image laden via toExternalForm (funktioniert IDE + JAR)
                    Image img = new Image(AppIcons.class.getResource(path).toExternalForm());
                    if (!img.isError()) {
                        icons.add(img);
                    } else {
                        System.err.println("Icon konnte nicht geladen werden (Image error): " + path);
                    }
                } else {
                    System.err.println("Icon Resource nicht gefunden: " + path);
                }
            } catch (Exception e) {
                System.err.println("Fehler beim Laden des Icons: " + path);
                e.printStackTrace();
            }
        }
    }

    /**
     * Gibt die Liste der geladenen Icons zurück.
     * Kann direkt in Stage.getIcons().setAll(...) verwendet werden.
     */
    public static List<Image> getIcons() {
        return icons;
    }
}