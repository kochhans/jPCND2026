
package application;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import application.controllers.FrmStartController;
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
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class StartupManager
{
	private Stage primaryStage;
	private static StartupManager instance;

	public StartupManager(Stage primaryStage)
	{
		this.primaryStage = primaryStage;
		instance = this;
	}

	public void start()
	{
		// 🔹 Schritt für Schritt sicherstellen
		runStep(StartupStep.LICENSE_CHECK);
	}

	private void runStep(StartupStep step)
	{
		switch (step)
		{
		case LICENSE_CHECK:
			if (!ensureLicense())
			{
				Platform.exit();
				return;
			}
			runStep(StartupStep.DB_CHECK);
			break;

		case DB_CHECK:
			if (!ensureDatabase())
			{
				Platform.exit();
				return;
			}
			runStep(StartupStep.INIT_DB);
			break;


		case INIT_DB:
			// neueste lokale Datenbankstruktur-Version aus Tabelle tblZDatenbankstruktur
			// holen und prüfen, ob es Updates in der DB-Struktur gibt
			
			String localDbStructureVersion = DatabaseVersionUtil.getLocalDatabaseStructureVersion();
			System.out.println("Lokale DB-Struktur: " + localDbStructureVersion);
		
			if (DatabaseVersionUtil.isVersionLess(localDbStructureVersion, "1.26.0402"))
			{// in 1.26.0402 wurden die Felder user_modiefuied in alle Tabellen ergänzt
				Connection conn = DBManager.getConnection();
				try
				{
					DatabaseMergeService service = new DatabaseMergeService(ValuesGlobals.dbPfad);
					List<String> tables = service.getAllRelevantTables(conn);

					service.ensureUserModifiedColumnAllTables(conn, tables);
					service.migrateUserModifiedNotNull(conn, tables);
					
					DatabaseVersionUtil.setDatabaseStructureVersion("1.26.0402");
					Msgbox.show("Datenbankanpassung ...","Es wurde die Datenbank-Strukturänderung auf 1.26.0402 erfolgreich durchgeführt");

				}
				catch (SQLException e)
				{
					Msgbox.error("DB-Migration 1.26.0402 fehlgeschlagen", e.getMessage());
					Platform.exit();
					return;
				}
			}
			
//			if () { // hier kommt das nächste Update rein
//				System.out.println("vorbereitet für das nächste Struktur-Update");
//			}
			
			
			
			runStep(StartupStep.SHOW_MAIN_UI);
			break;

		case SHOW_MAIN_UI:
			showMainWindow(); // 🔹 erst jetzt Hauptfenster zeigen
			runStep(StartupStep.BACKGROUND_INIT);
			break;

		case BACKGROUND_INIT:
			startBackgroundTasks();
			runStep(StartupStep.DONE);
			break;

		case DONE:
			System.out.println("✅ (11) Startup complete");
			break;
		}
	}

	// =========================
	// Lizenz
	// =========================
	private boolean ensureLicense()
	{
		if (LicenseManager.getInstance().checkLicense() == LicenseCheckResult.VALID)
			return true;

		boolean ok = showLicenseDialog();
		return ok && LicenseManager.getInstance().checkLicense() == LicenseCheckResult.VALID;
	}

	// =========================
	// DB
	// =========================
	private boolean ensureDatabase()
	{
		String dbPath = ConfigManager.loadDBPath();

		if (dbPath == null || !new File(dbPath).exists())
		{
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
	// UI
	// =========================
	private void showMainWindow()
	{
		try
		{
			// primaryStage.setOpacity(0); // 🔹 unsichtbar starten

			System.out.println("(1a ShowMainWindow() Pfade für Grafiken festlegen");
			ValuesGlobals.progPfadGrafik = ConfigManager.loadGrafikDirectory() + "/";
			System.out.println(ValuesGlobals.progPfadGrafik);

			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getResource("/application/views/FrmStart.fxml"));

			Parent root = fxmlLoader.load();
			FrmStartController controller = fxmlLoader.getController();

			Scene scene = new Scene(root, 1200, 720);
			scene.getStylesheets().add(
					getClass().getResource("/application/styles/application.css").toExternalForm());

			primaryStage.setScene(scene);
			primaryStage.setTitle(ValuesGlobals.progTitel +
					" -- Programm-" + ValuesGlobals.progVersion + " "
					+ "-- Datenbank -Version " + DatabaseVersionUtil.getLocalDatabaseVersion() + " -- -Pfad  " + ValuesGlobals.dbPfad);

			Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
			primaryStage.setWidth(screenBounds.getWidth() * 0.9);
			primaryStage.setHeight(screenBounds.getHeight() * 0.9);
			primaryStage.centerOnScreen();
			primaryStage.setMinWidth(1200);
			primaryStage.setMinHeight(720);

			// Icons
//            primaryStage.getIcons().setAll(
//                new Image(getClass().getResource("/icons/javafx/jpcndicon0016.png").toExternalForm()),
//                new Image(getClass().getResource("/icons/javafx/jpcndicon0032.png").toExternalForm()),
//                new Image(getClass().getResource("/icons/javafx/jpcndicon0064.png").toExternalForm())
//            );

			primaryStage.setOnCloseRequest(e -> {
				Parent currentRoot = primaryStage.getScene().getRoot();
				if (!"rootStart".equals(currentRoot.getId()))
				{
					e.consume();
					try
					{
						SceneManager.showStart(primaryStage);
					}
					catch (IOException ex)
					{
						ex.printStackTrace();
					}
					return;
				}
				e.consume();
				ToolsWinHelper.closeApplication();
			});

			primaryStage.show();
			controller.initView();

			// 🔹 Fenster sichtbar machen, nach DB und Controller init
			// primaryStage.setOpacity(1);
			primaryStage.toFront();

		}
		catch (Exception e)
		{
			e.printStackTrace();
			Platform.exit();
		}
	}

	// =========================
	// Background
	// =========================
	private void startBackgroundTasks()
	{
		Task<Void> task = new Task<>()
		{
			@Override
			protected Void call() throws Exception
			{
				return null;
			}
		};
		new Thread(task, "background-init").start();
	}

	// =========================
	// Dialoge
	// =========================
	private boolean showLicenseDialog()
	{
		try
		{
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
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	private boolean showDatabaseDialog()
	{
		try
		{
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
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	// =========================
	// Neustart / Benutzer informieren
	// =========================
	public void restartApplication()
	{
		Platform.runLater(() -> {
			try
			{
				// 🔹 Nur Meldung anzeigen
				// Msgbox.show("Neustart erforderlich", "Das Update wurde eingespielt.\nBitte
				// starten Sie das Programm nun neu.");

				// 🔹 Aktuelle Anwendung sauber beenden
				Platform.exit();
				System.exit(0);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				Msgbox.error("Fehler beim Beenden", e.getMessage());
			}
		});
	}

	// 🔹 Statische Hilfsmethode, überall aufrufbar
	public static void restart()
	{
		if (instance != null)
		{
			instance.restartApplication();
		}
		else
		{
			System.err.println("[WARN] StartupManager-Instanz nicht initialisiert!");
			// Fallback: einfach exit
			Platform.exit();
			System.exit(0);
		}
	}
}
