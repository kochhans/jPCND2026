package application.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

public class ToolsDialogHelper {

    /**
     * Zeigt einen allgemeinen Hinweis-, Warn- oder Fehlerdialog.
     *
     * @param title   Titel des Dialogfensters
     * @param header  Überschrift im Dialog (kann null sein)
     * @param content Hauptinhalt / Nachricht
     * @param type    Art des Dialogs (INFORMATION, WARNING, ERROR)
     */
    public static void show(String title, String header, String content, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        Window owner = getActiveWindow();
        if (owner != null) {
            alert.initOwner(owner);
        }

        setDialogIcon(alert, type);
        alert.showAndWait();
    }

    /**
     * Kürzere Helfermethoden für häufige Fälle
     */
    public static void showInfo(String title, String message) {
        show(title, null, message, AlertType.INFORMATION);
    }

    public static void showWarning(String title, String message) {
        show(title, null, message, AlertType.WARNING);
    }

    public static void showError(String title, String message) {
        show(title, null, message, AlertType.ERROR);
    }

    // Holt das aktuell sichtbare JavaFX-Fenster
	/*
	 * private static Window getActiveWindow() { return Window.getWindows().stream()
	 * .filter(Window::isShowing) .findFirst() .orElse(null); }
	 */
    private static Window getActiveWindow() {
        return Window.getWindows().stream()
            .filter(w -> w.isShowing() && w.isFocused())
            .findFirst()
            .orElseGet(() -> Window.getWindows().stream()
                .filter(Window::isShowing)
                .findFirst()
                .orElse(null));
    }

    // Setzt das Icon oben links im Dialogfenster je nach Dialogtyp
    private static void setDialogIcon(Alert alert, AlertType type) {
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        String iconPath = switch (type) {
            case ERROR -> "/icons/javafx/error.png";
            case WARNING -> "/icons/javafx/warnung.png";
            case INFORMATION -> "/icons/javafx/information.png";
            default -> "/icons/javafx/pcndicon.png";
        };

        try {
            //Image icon = new Image(DialogHelper.class.getResourceAsStream(iconPath));
        	Image icon = new Image(iconPath);
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Icon konnte nicht geladen werden: " + iconPath);
        }
    }
}

