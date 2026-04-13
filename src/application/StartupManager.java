
package application;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import application.controllers.DatabaseControllerAktionen;
import application.controllers.FrmStartEinstellungenController;
import application.controllers.LicenseDialogController;
import application.db.DBManager;
import application.db.DatabaseVersionUtil;
import application.dbupdate.DatabaseMergeService;
import application.uicomponents.Msgbox;
import application.utils.ToolsWinHelper;
import application.utils.license.LicenseCheckResult;
import application.utils.license.LicenseManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class StartupManager {

    private Stage primaryStage;
    private static StartupManager instance;

    public StartupManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
        instance = this;
    }

    public void start() {
        runStep(StartupStep.LICENSE_CHECK);
    }

    private void runStep(StartupStep step) {
        switch (step) {

            case LICENSE_CHECK:
                if (!ensureLicense()) {
                    Platform.exit();
                    return;
                }
                runStep(StartupStep.DB_CHECK);
                break;

            case DB_CHECK:
                if (!ensureDatabase()) {
                    Platform.exit();
                    return;
                }
                runStep(StartupStep.INIT_DB);
                break;

            case INIT_DB:

                String localDbStructureVersion = DatabaseVersionUtil.getLocalDatabaseStructureVersion();
                System.out.println("Lokale DB-Struktur: " + localDbStructureVersion);

                if (DatabaseVersionUtil.isVersionLess(localDbStructureVersion, "1.26.0402")) {
                    Connection conn = DBManager.getConnection();
                    try {
                        DatabaseMergeService service = new DatabaseMergeService(ValuesGlobals.dbPfad);
                        List<String> tables = service.getAllRelevantTables(conn);

                        service.ensureUserModifiedColumnAllTables(conn, tables);
                        service.migrateUserModifiedNotNull(conn, tables);

                        DatabaseVersionUtil.setDatabaseStructureVersion("1.26.0402");
                        Msgbox.show("Datenbankanpassung ...",
                                "Es wurde die Datenbank-Strukturänderung auf 1.26.0402 erfolgreich durchgeführt");

                    } catch (SQLException e) {
                        Msgbox.error("DB-Migration 1.26.0402 fehlgeschlagen", e.getMessage());
                        Platform.exit();
                        return;
                    }
                }

                runStep(StartupStep.SHOW_MAIN_UI);
                break;

            case SHOW_MAIN_UI:
                showMainWindow();
                runStep(StartupStep.BACKGROUND_INIT);
                break;

            case BACKGROUND_INIT:
                startBackgroundTasks();
                runStep(StartupStep.DONE);
                break;

            case DONE:
                System.out.println("✅ Startup complete");
                break;
        }
    }

    // =========================
    // Lizenz
    // =========================
    private boolean ensureLicense() {
        if (LicenseManager.getInstance().checkLicense() == LicenseCheckResult.VALID)
            return true;

        boolean ok = showLicenseDialog();
        return ok && LicenseManager.getInstance().checkLicense() == LicenseCheckResult.VALID;
    }

    // =========================
    // DB
    // =========================
    private boolean ensureDatabase() {
        String dbPath = ConfigManager.loadDBPath();

        if (dbPath == null || !new File(dbPath).exists()) {
            boolean ok = showDatabaseDialog();
            if (!ok)
                return false;
            dbPath = ValuesGlobals.dbPfad;
        }

        ValuesGlobals.dbPfad = dbPath;
        ValuesGlobals.databasePath = dbPath;

        return true;
    }

    // =========================
    // UI (JETZT MIT SCENEMANAGER)
    // =========================
    private void showMainWindow() {
        try {

            System.out.println("(1a) ShowMainWindow() Pfade für Grafiken festlegen");
            ValuesGlobals.progPfadGrafik = ConfigManager.loadGrafikDirectory() + "/";
            System.out.println(ValuesGlobals.progPfadGrafik);

            // 🔹 Dummy Scene setzen (wichtig für SceneManager)
            Scene scene = new Scene(new StackPane(), 1240, 780);
            scene.getStylesheets().add(
                    getClass().getResource("/application/styles/application.css").toExternalForm());

            primaryStage.setScene(scene);

            // 🔹 Fenster konfigurieren
            primaryStage.setTitle(ValuesGlobals.progTitel +
                    " -- Programm-" + ValuesGlobals.progVersion + " " +
                    "-- Datenbank -Version " + DatabaseVersionUtil.getLocalDatabaseVersion() +
                    " -- -Pfad  " + ValuesGlobals.dbPfad);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            primaryStage.setWidth(screenBounds.getWidth() * 0.9);
            primaryStage.setHeight(screenBounds.getHeight() * 0.9);
            primaryStage.centerOnScreen();
            primaryStage.setMinWidth(1240);
            primaryStage.setMinHeight(780);

            // 🔹 SceneManager initialisieren (entscheidet selbst, welche Scene geladen wird)
            DatabaseControllerAktionen dbController = new DatabaseControllerAktionen();
            SceneManager.init(primaryStage, dbController);

            // 🔹 Close-Handling vereinfacht
            primaryStage.setOnCloseRequest(e -> {
                if (!Msgbox.yesno("Programm beenden", "Möchten Sie das Programm wirklich beenden?")) {
                    e.consume();
                    return;
                }
                SceneManager.exitApp();
            });
//            primaryStage.setOnCloseRequest(e -> {
//                if (SceneManager.getCurrentScene() != SceneManager.SceneType.START) {
//                    e.consume();
//                    try {
//                        SceneManager.showStart(primaryStage);
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                } else {
//                    SceneManager.exitApp();
//                }
//            });

            primaryStage.show();
            primaryStage.toFront();

        } catch (Exception e) {
            e.printStackTrace();
            Platform.exit();
        }
    }

    // =========================
    // Background
    // =========================
    private void startBackgroundTasks() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                return null;
            }
        };
        new Thread(task, "background-init").start();
    }

    // =========================
    // Dialoge
    // =========================
    private boolean showLicenseDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/views/LicenseDialog.fxml"));
            Parent root = loader.load();

            Stage dialog = new Stage();
            dialog.initOwner(primaryStage);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.getIcons().add(new Image(getClass().getResourceAsStream("/icons/javafx/jpcndicon0064.png")));
            dialog.setResizable(false);
            dialog.setWidth(550);
            dialog.setHeight(200);

            LicenseDialogController controller = loader.getController();
            controller.setStage(dialog);

            dialog.setScene(new Scene(root));
            dialog.showAndWait();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean showDatabaseDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/views/FrmStartEinstellungen.fxml"));
            Parent root = loader.load();

            Stage dialog = new Stage();
            dialog.initOwner(primaryStage);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setResizable(false);
            dialog.setWidth(720);
            dialog.setHeight(310);
            dialog.getIcons().add(new Image(getClass().getResourceAsStream("/icons/javafx/jpcndicon0064.png")));

            FrmStartEinstellungenController controller = loader.getController();
            controller.setStage(dialog);

            dialog.setScene(new Scene(root));
            dialog.showAndWait();
            return !controller.txtDatenbankpfad.getText().trim().isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================
    // Neustart
    // =========================
    public void restartApplication() {
        Platform.runLater(() -> {
            try {
                Platform.exit();
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
                Msgbox.error("Fehler beim Beenden", e.getMessage());
            }
        });
    }

    public static void restart() {
        if (instance != null) {
            instance.restartApplication();
        } else {
            System.err.println("[WARN] StartupManager-Instanz nicht initialisiert!");
            Platform.exit();
            System.exit(0);
        }
    }
}

