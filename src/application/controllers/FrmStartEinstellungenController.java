
package application.controllers;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import application.AppInitializer;
import application.ConfigManager;
import application.StartSettingsResult;
import application.StartupManager;
import application.ValuesGlobals;
import application.uicomponents.Msgbox;
import application.utils.DownloadAndUnzipTask;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.DirectoryChooser;

import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

public class FrmStartEinstellungenController
{

	// --- Steuerelemente definieren
	// Buttons
	@FXML
	private Button btnDateiauswahl, btnDbpfadAuswaehlen, btnDbAnlegen, btnBackuppfadWaehlen,
			btnSicherungskopieErstellen, btnZurueck, btnDatenimport, btnAbbruchZurueck_OnClick;
	// Labels
	@FXML
	private Label lblHaupttitel, lblTabellenueberschrift, lblStatus;
	// Textfelder
	@FXML
	public TextField txtDatenbankpfad;
	@FXML
	private TextField txtSicherungsordner;
	@FXML
	private ProgressBar progressBar;

	private String selectedPath;

	private boolean startupMode = false;
	private StartSettingsResult result;

	// #####################################################################################################################
	// Stage aufbauen
	private Stage stage;

	public void setStage(Stage stage)
	{
		this.stage = stage;
	}

	@FXML
	private void initialize()
	{
		result = null;
		lblTabellenueberschrift.setVisible(false);
		// vorhandenen Pfad laden
		String cfg = ConfigManager.loadDBPath();
		if (cfg != null)
		{
			txtDatenbankpfad.setText(cfg);
		}
		String cfg2 = ConfigManager.loadBackupDirectory();
		if (cfg2 != null)
		{
			txtSicherungsordner.setText(cfg2);
		}

		// Ordner wählen
		btnDbpfadAuswaehlen.setOnAction(e -> chooseFolder());

//		// Sofort Backup anlegen
//		btnSicherungskopieErstellen.setOnAction(e -> {
//			try
//			{
//				String dbPath = ConfigManager.loadDBPath();
//				if (dbPath == null)
//				{
//					Msgbox.warn("Sicherungskopie...", "Keine Datenbank gefunden. Bitte zuerst einen Pfad setzen.");
//					return;
//				}
//				String targetDir = txtSicherungsordner.getText();
//				if (targetDir.isEmpty())
//				{
//					Msgbox.warn("Sicherungskopie...", "Bitte Backup-Ordner wählen.");
//					return;
//				}
//
//				File dbFile = new File(dbPath);
//				File backupFile = new File(targetDir, "backup_" + System.currentTimeMillis() + ".db3");
//
//				Files.copy(dbFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//
//				Msgbox.show("Sicherungskopie...", "Datenbank gesichert in die Datei...\n" + backupFile.getAbsolutePath());
//			}
//			catch (Exception ex)
//			{
//				Msgbox.warn("Sicherungskopie...", "Sicherungsvorgang fehlgeschlagen:\n" + ex.getMessage());
//			}
//		});

		// Backup-Ordner speichern
		btnBackuppfadWaehlen.setOnAction(e -> {
			DirectoryChooser chooser = new DirectoryChooser();
			chooser.setTitle("Backup-Ordner wählen");
			File dir = chooser.showDialog(stage);
			if (dir != null)
			{
				txtSicherungsordner.setText(dir.getAbsolutePath());

			}
			else
			{
				txtSicherungsordner.setText(txtDatenbankpfad.getText().trim());
			}

			// ConfigManager.saveBackupDirectory(txtSicherungsordner.getText().trim());
			// Msgbox.show("Fertig", "Backup-Ordner gespeichert.");
		});
		Platform.runLater(() -> {
			btnZurueck.requestFocus();
		});

	}

	public StartSettingsResult getResult()
	{
		return result;
	}

	// ---------------------------------------------------------
	// Ordner auswählen
	// ---------------------------------------------------------
	private void chooseFolder()
	{
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Ordner für Datenbank auswählen");

		File f = chooser.showDialog(stage);
		if (f != null)
		{
			String newPath = f.getAbsolutePath() + File.separator + "jpcnd.db3";
			txtDatenbankpfad.setText(newPath);
		}
	}

	public void setStartupMode(boolean startupMode)
	{
		this.startupMode = startupMode;
	}

	// ####################################################
	// Buttons mit Aktion

	@FXML
	private void btnZurueck_OnClick() // Speichern und Zurück
	{
		try
		{
			saveSettings(); // ⭐ HIER passiert alles

		}
		catch (IOException e)
		{
			e.printStackTrace();
			Msgbox.error("Fehler", e.getMessage());
		}
	}

	@FXML
	private void btnAbbruchZurueck_OnClick()
	{
		System.out.println("Abbruch");

		result = new StartSettingsResult(false, null);
		stage.close();
	}

	// ---------------------------------------------------------
	// Speichern + Sofort-Download wenn Datei fehlt
	// ---------------------------------------------------------
	private void saveSettings() throws IOException
	{
		String alterDbPfad = ConfigManager.loadDBPath();
		String alterBuPfad = ConfigManager.loadBackupDirectory();
		// ---------------------------------------------------------
		// 1️⃣ Datenbankpfad lesen & validieren
		// ---------------------------------------------------------
		String dbdateipfadInput = txtDatenbankpfad.getText().trim();
		if (dbdateipfadInput.isBlank())
		{
			Msgbox.warn("Fehler", "Bitte einen gültigen Datenbankpfad wählen.");
			return;
		}

		// 👉 IMMER kanonischen (absoluten) Pfad verwenden
		File dbFile = new File(dbdateipfadInput);
		String dbdateipfad = dbFile.getCanonicalPath();

		// Ordnerpfad für Grafik etc.
		String ordnerPfad = application.utils.PathUtils.getOrdnerPfadMitSeparator(dbdateipfad);

		// ---------------------------------------------------------
		// 2️⃣ Sicherungsordner prüfen
		// ---------------------------------------------------------
		String path2Input = txtSicherungsordner.getText().trim();
		if (path2Input.isBlank())
		{
			Msgbox.show(
					"Sicherungsordner...",
					"Der Sicherungspfad wurde nicht angegeben.\nAls Verzeichnis wird "
							+ ordnerPfad + " verwendet.");
			txtSicherungsordner.setText(ordnerPfad);
			path2Input = dbdateipfad;
		}

		final String backupPath = new File(path2Input).getCanonicalPath();

		// ---------------------------------------------------------
		// 3️⃣ Datenbankordner sicherstellen
		// ---------------------------------------------------------
		File dbFolder = dbFile.getParentFile();
		if (dbFolder != null && !dbFolder.exists())
		{
			dbFolder.mkdirs();
		}

		// ---------------------------------------------------------
		// 4️⃣ Datenbank existiert NICHT → Download
		// ---------------------------------------------------------
		if (!dbFile.exists())
		{

			boolean yes = Msgbox.yesno(
					"Datenbank fehlt",
					"In diesem Ordner befindet sich noch keine Datenbank.\n" +
							"Soll die aktuelle Version jetzt heruntergeladen werden?");

			if (!yes)
			{
				Msgbox.warn(
						"Datenbank fehlt",
						"Ohne Datenbankdatei kann das Programm nicht benutzt werden!");
				Platform.exit();
				return;
			}

			lblTabellenueberschrift.setVisible(true);
			lblTabellenueberschrift.setText(
					"Die Datenbank wird nun heruntergeladen und entpackt. Bitte warten...");

			File zipTarget = new File(dbFolder, "jPCND_download.zip");

			DownloadAndUnzipTask task = new DownloadAndUnzipTask(
					ValuesGlobals.REMOTE_ZIP_URL,
					zipTarget,
					dbFolder);

			// UI Binding
			progressBar.setVisible(true);
			lblStatus.setVisible(true);

			progressBar.progressProperty().bind(task.progressProperty());
			lblStatus.textProperty().bind(task.messageProperty());

			// -----------------------------------------------------
			// SUCCESS
			// -----------------------------------------------------
			task.setOnSucceeded(e -> {

				progressBar.progressProperty().unbind();
				lblStatus.textProperty().unbind();

				lblStatus.setText("Download abgeschlossen!");

				// Config speichern
				ConfigManager.saveDBPath(dbdateipfad);
				ConfigManager.saveBackupDirectory(backupPath);
				ConfigManager.saveGrafikDirectory(ordnerPfad + "titelgrafik");

				result = new StartSettingsResult(true, dbdateipfad);

				Msgbox.show(
						"Datenbankdownload ...",
						"Die Datenbank wurde erfolgreich geladen. Der Speicherpfad wurde festgelegt auf:\n"
								+ dbdateipfad + "\n\n"
								+ "ACHTUNG: Das Programm wird nun zur Neuinstellung der Daten beendet.\n"
								+ "Starten Sie bitte anschließend neu.");

				// 🔥 sauberer Exit (verzögert!)
				Platform.runLater(() -> {
					if (stage != null)
					{
						stage.close();
						Platform.exit();
					}

				});
			});

			// -----------------------------------------------------
			// FAILURE
			// -----------------------------------------------------
			task.setOnFailed(e -> {

				Throwable ex = task.getException();

				ex.printStackTrace(); // ⭐ WICHTIGSTER DEBUG

				progressBar.progressProperty().unbind();
				lblStatus.textProperty().unbind();

				lblStatus.setText("Fehler beim Download!");

				Msgbox.error(
						"Fehler",
						"Download fehlgeschlagen:\n" +
								(ex != null ? ex.getMessage() : "Unbekannter Fehler"));

				Platform.exit();
			});

			// -----------------------------------------------------
			// START THREAD (WICHTIG!)
			// -----------------------------------------------------
			Thread t = new Thread(task);
			t.setDaemon(true);
			t.setName("DB-Download-Thread");
			t.start();

			return;
		}

//		if(alterDbPfad.equals(txtDatenbankpfad.getText()))
//		{
//			result = new StartSettingsResult(false, null);
//			stage.close();
//			return;
//		}

		// ---------------------------------------------------------
		// 5️⃣ Datenbank EXISTIERT → Pfad vergleichen
		// ---------------------------------------------------------
		String oldPath = ValuesGlobals.databasePath; // oder aus ConfigManager laden

		boolean pathChanged = true;
		if (oldPath != null && !oldPath.isBlank())
		{
			String oldCanonical = new File(oldPath).getCanonicalPath();
			pathChanged = !oldCanonical.equals(dbdateipfad)||!alterBuPfad.equals(backupPath);
		}

		if (pathChanged)
		{

			Msgbox.warn(
					"Dateipfade wurden geändert ...",
					"Der Datenbank-Speicherplatz wurde festgelegt auf:\n"
							+ dbdateipfad + ",\n"
									+ "der Backup-Pfad wurde festgelegt auf: \n" 
							+ backupPath + "\n"
							+ "Eine Datenbankdatei ist unter " + dbdateipfad + "schon vorhanden.\n"
							+ "Bitte prüfen Sie die Version Ihrer Daten.\n\n"
							+ "ACHTUNG: Aufgrund der geänderten Speicherpfade wird das Programm beendet\n"
							+ "Starten Sie bitte anschließend neu.");
			finishAndSave(dbdateipfad, backupPath);
			// 🔒 Pfade SPEICHERN (kanonisch!)
//			ConfigManager.saveDBPath(dbdateipfad);
//			ConfigManager.saveBackupDirectory(backupPath);
//			ConfigManager.saveGrafikDirectory(ordnerPfad + "titelgrafik");
			// ConfigManager.saveBackupDirectory(txtSicherungsordner.getText().trim());

			Platform.exit();
			// System.exit(0);
		}
		this.stage.close();
	}

	private void finishAndSave(String dbPath, String backupPath)
	{
		result = new StartSettingsResult(true, dbPath);

		ConfigManager.saveDBPath(dbPath);
		ConfigManager.saveBackupDirectory(backupPath);
		ConfigManager.saveGrafikDirectory(
				application.utils.PathUtils.getOrdnerPfadMitSeparator(dbPath) + "titelgrafik");

		stage.close();
	}

}
