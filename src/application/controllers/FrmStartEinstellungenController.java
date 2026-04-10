
package application.controllers;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import application.ConfigManager;
import application.StartupManager;
import application.ValuesGlobals;
import application.uicomponents.Msgbox;
import application.utils.DownloadAndUnzipTask;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.stage.DirectoryChooser;

import javafx.stage.Stage;
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

		// Speichern
		btnZurueck.setOnAction(e -> {
			try
			{
				saveSettings();
			}
			catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

		// Sofort Backup anlegen
		btnSicherungskopieErstellen.setOnAction(e -> {
			try
			{
				String dbPath = ConfigManager.loadDBPath();
				if (dbPath == null)
				{
					Msgbox.warn( "Sicherungskopie...", "Keine Datenbank gefunden. Bitte zuerst einen Pfad setzen.");
					return;
				}
				String targetDir = txtSicherungsordner.getText();
				if (targetDir.isEmpty())
				{
					Msgbox.warn("Sicherungskopie...", "Bitte Backup-Ordner wählen.");
					return;
				}

				File dbFile = new File(dbPath);
				File backupFile = new File(targetDir, "backup_" + System.currentTimeMillis() + ".db3");

				Files.copy(dbFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

				Msgbox.show("Sicherungskopie...", "Datenbank gesichert in die Datei...\n" + backupFile.getAbsolutePath());
			}
			catch (Exception ex)
			{
				Msgbox.warn( "Sicherungskopie...", "Sicherungsvorgang fehlgeschlagen:\n" + ex.getMessage());
			}
		});

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

			ConfigManager.saveBackupDirectory(txtSicherungsordner.getText().trim());
			// Msgbox.show("Fertig", "Backup-Ordner gespeichert.");
		});
		Platform.runLater(() -> {
			btnZurueck.requestFocus();
		});

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

	// ---------------------------------------------------------
	// Speichern + Sofort-Download wenn Datei fehlt
	// ---------------------------------------------------------
	private void saveSettings() throws IOException
	{
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
	                        + dbdateipfad + " verwendet."
	        );
	        txtSicherungsordner.setText(dbdateipfad);
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
	                        "Soll die aktuelle Version jetzt heruntergeladen werden?"
	        );

	        if (!yes)
	        {
	            Msgbox.warn( "Datenbank fehlt", "Ohne Datenbankdatei kann das Programm nicht benutzt werden!");
	            Platform.exit();
	            return;
	        }

	        lblTabellenueberschrift.setText(
	                "Die Datenbank wird nun heruntergeladen und entpackt. Bitte warten..."
	        );

	        File zipTarget = new File(dbFolder, "jPCND_download.zip");

	        DownloadAndUnzipTask task = new DownloadAndUnzipTask(
	                ValuesGlobals.REMOTE_ZIP_URL,
	                zipTarget,
	                dbFolder
	        );

	        progressBar.progressProperty().bind(task.progressProperty());
	        lblStatus.textProperty().bind(task.messageProperty());

	        task.setOnSucceeded(e ->
	        {
	            lblStatus.textProperty().unbind();
	            lblStatus.setText("Datenbank wurde erfolgreich heruntergeladen und entpackt!");

	            // 🔒 Pfade SPEICHERN (kanonisch!)
	            ConfigManager.saveDBPath(dbdateipfad);
	            ConfigManager.saveBackupDirectory(backupPath);
	            ConfigManager.saveGrafikDirectory(ordnerPfad + "titelgrafik");

	            Msgbox.show(
	                    "Datenbankpfad geändert...",
	                    "Der Datenbankpfad bzw. der Backup-Pfad wurde verändert.\n" +
	                            "Die Anwendung muss manuell neu gestartet werden."
	            );

	            onSaveSettings();
	        });

	        task.setOnFailed(e ->
	        {
	            lblStatus.textProperty().unbind();
	            lblStatus.setText("Fehler beim Download oder Entpacken!");
	            Msgbox.error("Fehler", "Download fehlgeschlagen. \nDas Programm wird beendet. \nLaden Sie die Datenbank bitte manuell von der Webseite herunter.");
				Platform.exit(); // JavaFX sauber beenden
				System.exit(0);
	        });
	       

	        new Thread(task).start();
	        return;
	    }

	    // ---------------------------------------------------------
	    // 5️⃣ Datenbank EXISTIERT → Pfad vergleichen
	    // ---------------------------------------------------------
	    String oldPath = ValuesGlobals.databasePath; // oder aus ConfigManager laden

	    boolean pathChanged = true;
	    if (oldPath != null && !oldPath.isBlank())
	    {
	        String oldCanonical = new File(oldPath).getCanonicalPath();
	        pathChanged = !oldCanonical.equals(dbdateipfad);
	    }

	    if (pathChanged)
	    {
	        Msgbox.warn(
	                "Datenbankpfad geändert",
	                "Der Speicherpfad wurde geändert auf:\n"
	                        + dbdateipfad + "\n\n"
	                        + "Eine Datenbankdatei ist hier schon vorhanden.\n"
	                        + "Bitte prüfen Sie die Version Ihrer Daten.\n\n"
	                        + "Die Anwendung wird nun beendet und muss manuell neu gestartet werden!"
	        );

	        // 🔒 Pfade SPEICHERN (kanonisch!)
	        ConfigManager.saveDBPath(dbdateipfad);
	        ConfigManager.saveBackupDirectory(backupPath);
	        ConfigManager.saveGrafikDirectory(ordnerPfad + "titelgrafik");

	        onSaveSettings();
	    }
	}

	
	@FXML
	private void onSaveSettings()
	{
	    try
	    {
	        // 1️⃣ Alten DB-Pfad merken (kann null sein!)
	        String oldDbPath = ValuesGlobals.databasePath;
	        System.out.println("alterDB-Pfad: " + oldDbPath);

	        // 2️⃣ Neue Einstellungen speichern
	        String newDbPath = "jdbc:sqlite:" + ConfigManager.loadDBPath();
	        ValuesGlobals.databasePath = newDbPath;
	        System.out.println("neuerDB-Pfad: " + newDbPath);

	        // 3️⃣ Modales Fenster schließen
	        stage.close();

	        // 4️⃣ Vergleich NULL-sicher
	        if (!Objects.equals(oldDbPath, newDbPath))
	        {
	            // DB-Pfad geändert → kompletter Neustart
	        	StartupManager.restart();
	        }
	        else
	        {
	            // Nur UI neu laden
	        	StartupManager.restart();
	        }
	    }
	    catch (Exception e)
	    {
	        Msgbox.error( "Fehler beim Speichern der Einstellungen", e.getMessage());
	    }
	}




	

	// ---------------------------------------------------------
	// SYNCHRONER Download (ohne Task!)
	// ---------------------------------------------------------
//	private boolean downloadAndUnzipSynchron(String url, File zipFile, File targetDb)
//	{
//
//		try
//		{
//
//			HttpClient client = HttpClient.newHttpClient();
//			HttpRequest req = HttpRequest.newBuilder()
//					.uri(URI.create(url))
//					.GET()
//					.build();
//
//			HttpResponse<Path> resp = client.send(req,
//					HttpResponse.BodyHandlers.ofFile(zipFile.toPath()));
//
//			if (resp.statusCode() != 200)
//			{
//				Msgbox.error("Fehler", "Download fehlgeschlagen. HTTP-Code: " + resp.statusCode());
//				return false;
//			}
//
//			unzip(zipFile, targetDb);
//			zipFile.delete();
//
//			return true;
//
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			Msgbox.error("Fehler", e.getMessage());
//			return false;
//		}
//	}

	// ---------------------------------------------------------
	// Entpacken exakt EINER DB-Datei
	// ---------------------------------------------------------
//	private void unzip(File zipFile, File targetDb) throws IOException
//	{
//
//		try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile.toPath())))
//		{
//
//			ZipEntry entry;
//
//			while ((entry = zis.getNextEntry()) != null)
//			{
//
//				if (!entry.isDirectory())
//				{
//					Files.copy(zis, targetDb.toPath(), StandardCopyOption.REPLACE_EXISTING);
//				}
//
//				zis.closeEntry();
//			}
//		}
//	}
	// ####################################################
	// Buttons mit Aktion

	@FXML
	void btnZurueck_OnClick(ActionEvent event) throws Exception
	{
		this.stage.close();
	}

	@FXML
	void btnAbbruchZurueck_OnClick(ActionEvent event) throws Exception
	{
		this.stage.close();
	}

	@FXML
	void btnSicherungskopieErstellen_OnClick(ActionEvent event) throws Exception
	{
		// this.stage.close();
	}

}
