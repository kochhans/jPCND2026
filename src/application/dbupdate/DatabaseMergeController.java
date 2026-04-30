package application.dbupdate;

import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.lang.System.Logger;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import application.ConfigManager;
import application.ValuesGlobals;
import application.uicomponents.Msgbox;



public class DatabaseMergeController
{

	@FXML
	private Button btnMerge, btnSelectSource, btnDownloadDB;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private TextArea logArea;
	@FXML
	private Label lblDownload;
	@FXML
	private ListView<String> tableListView; // unsichtbar im FXML, nur intern

	private String sourceDbPath;
	private DatabaseMergeService service;
	private File updateFolder;
	// private File downloadedZip;
	private static final Logger LOGGER = Logger.getLogger("DatabaseMergeLogger");

	// Tabellen, die niemals gemerged werden sollen
//	private final List<String> blackList = List.of("tblNotenmappe", "tblNotenmappeEdition");
	private final List<String> blackList = List.of(
			"tblNotenmappe",
			"tblNotenmappeEdition",
			"tblChoraktionen",
			"tblChoraktionenPersonen",
			"tblChoraktionenPositionen",
			"tblChorPersonen",
			"tblZDatenbankstruktur");
			 //"tblAdbVersion");

	

	private void initLogger() {
	    try {
	        Path dbPath = Paths.get(ValuesGlobals.dbPfad);
	        Path logPath = dbPath.getParent().resolve("merge.log");

	        FileHandler fileHandler = new FileHandler(logPath.toString(), true);
	        fileHandler.setFormatter(new SimpleFormatter());

	        LOGGER.addHandler(fileHandler);
	        LOGGER.setUseParentHandlers(false); // verhindert Console-Spam

	    } catch (IOException e) {
	        e.printStackTrace(); // Fallback
	    }
	}
	
	
	@FXML
	public void initialize()
	{
		progressBar.setProgress(0);
		btnMerge.setDisable(true);
		lblDownload.setVisible(true);

		// ListView unsichtbar + Drag & Drop deaktiviert
		tableListView.setVisible(false);
		tableListView.setManaged(false);
		tableListView.setDisable(true);
		initLogger();

	}

	/** Quelldatenbank manuell auswählen */
	@FXML
	public void onSelectSource()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Quelldatenbank auswählen");
		fileChooser.getExtensionFilters().add(
				new FileChooser.ExtensionFilter("SQLite Dateien", "*.db*", "*.sqlite"));
		File file = fileChooser.showOpenDialog(btnSelectSource.getScene().getWindow());
		if (file != null)
		{
			sourceDbPath = file.getAbsolutePath();
			loadTables("📂 Quelldatei ausgewählt\n");
		}
	}

	/** Neue zentrale Datenbank herunterladen */
	// muss als ZIP vorliegen
	// in der ZIP muss folgenden Struktur sein:
	// up

	@FXML
	public void onDownloadDatabase()
	{

		Task<File> downloadTask = new Task<>()
		{
			@Override
			protected File call() throws Exception
			{

				String url = ValuesGlobals.REMOTE_DBUPDATE_URL;

				// Arbeitsordner (da liegt deine aktive DB)
				File activeDb = new File(ValuesGlobals.dbPfad);
				File dbFolder = activeDb.getParentFile();

				// Download-Zielordner (temporär)
				File targetFolder = new File(dbFolder, "db_update");
				if (!targetFolder.exists())
					targetFolder.mkdirs();
				updateFolder = targetFolder;

				File zipFile = new File(targetFolder, "updatepaket.zip");
				// downloadedZip = zipFile;

				updateMessage("⬇️ ZIP Download gestartet...");
				URI uri = URI.create(url);
				URL urlObj = uri.toURL();
				HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
				conn.setConnectTimeout(5000);
				conn.setReadTimeout(10000);

				long contentLength = conn.getContentLengthLong(); // -1 falls unbekannt

				try (InputStream in = conn.getInputStream();
						FileOutputStream out = new FileOutputStream(zipFile))
				{

					byte[] buffer = new byte[8192];
					long totalRead = 0;
					int len;

					while ((len = in.read(buffer)) != -1)
					{
						out.write(buffer, 0, len);
						totalRead += len;

						if (contentLength > 0)
						{
							updateProgress(totalRead, contentLength);
						}
						else
						{
							updateProgress(ProgressIndicator.INDETERMINATE_PROGRESS, 1);
						}
					}
				}

				updateMessage("✅ Download fertig: " + zipFile.getAbsolutePath());
				updateMessage("📦 Entpacken startet...");

				// Entpacken + aktive DB nicht überschreiben
				File extractedDb = unzipToFolderAvoidActiveDb(zipFile, dbFolder, activeDb.getName());

				if (extractedDb != null)
				{
					updateMessage("✅ Neue DB entpackt: " + extractedDb.getName());
				}
				else
				{
					updateMessage("⚠️ Keine neue DB-Datei im ZIP gefunden.");
				}

				return extractedDb;
			}
		};

		// ProgressBar binden
		progressBar.progressProperty().unbind();
		progressBar.progressProperty().bind(downloadTask.progressProperty());

		// TextArea Log aktualisieren
		downloadTask.messageProperty().addListener((obs, oldMsg, newMsg) -> {
			if (newMsg != null && !newMsg.isBlank())
			{
				Platform.runLater(() -> logArea.appendText(newMsg + "\n"));
			}
		});

		downloadTask.setOnSucceeded(e -> {
			File newDb = downloadTask.getValue();
			if (newDb != null && newDb.exists())
			{
				sourceDbPath = newDb.getAbsolutePath();
				loadTables("📂 Neue DB bereit für Merge:\n");
			}
			else
			{
				logArea.appendText("❌ Download/Entpacken abgeschlossen, aber keine DB zum Merge gefunden.\n");
			}
		});

		downloadTask.setOnFailed(e -> {
			Throwable ex = downloadTask.getException();
			logArea.appendText("❌ Fehler beim Download/Entpacken: " + ex.getMessage() + "\n");
			ex.printStackTrace();
		});

		Thread t = new Thread(downloadTask, "Download-DB-ZIP-Thread");
		t.setDaemon(true);
		t.start();
	}

	private File unzipToFolderAvoidActiveDb(File zipFile, File destDir, String activeDbName) throws IOException
	{

		if (!destDir.exists())
			destDir.mkdirs();

		File extractedDb = null;

		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile)))
		{
			ZipEntry entry;

			while ((entry = zis.getNextEntry()) != null)
			{

				File outFile = new File(destDir, entry.getName());

				// ZipSlip-Schutz
				String destDirPath = destDir.getCanonicalPath();
				String outFilePath = outFile.getCanonicalPath();
				if (!outFilePath.startsWith(destDirPath + File.separator))
				{
					throw new IOException("ZIP Eintrag außerhalb Zielordner blockiert: " + entry.getName());
				}

				// aktive DB nicht überschreiben
				if (outFile.getName().equals(activeDbName))
				{
					Platform.runLater(() -> logArea.appendText("⚠️ Datei " + activeDbName + " wird nicht überschrieben.\n"));
					zis.closeEntry();
					continue;
				}

				if (entry.isDirectory())
				{
					outFile.mkdirs();
				}
				else
				{
					File parent = outFile.getParentFile();
					if (!parent.exists())
						parent.mkdirs();

					try (FileOutputStream fos = new FileOutputStream(outFile))
					{
						byte[] buffer = new byte[8192];
						int len;
						while ((len = zis.read(buffer)) > 0)
						{
							fos.write(buffer, 0, len);
						}
					}

					// optional: gefundene DB speichern
					String name = outFile.getName().toLowerCase();
					if (name.endsWith(".db3") || name.endsWith(".db") || name.endsWith(".sqlite"))
					{
						extractedDb = outFile;
					}
				}

				zis.closeEntry();
			}
		}

		return extractedDb;
	}

	/** Tabellen laden (Blacklist wird ausgeschlossen) */
	private void loadTables(String logHeader)
	{
		if (sourceDbPath == null)
			return;

		logArea.appendText(logHeader + "" + sourceDbPath + "\n\n");

		try
		{
			service = new DatabaseMergeService(sourceDbPath, ValuesGlobals.dbPfad);
			service.excludeTables(blackList);

			List<String> tables = service.getTableNames();
			List<String> mergeTables = tables.stream()
					.filter(t -> !blackList.contains(t))
					.toList();

			tableListView.setItems(FXCollections.observableArrayList(mergeTables));
			btnMerge.setDisable(false);

			logArea.appendText("✅ Tabellen zum Übertragen stehen bereit:\n " + mergeTables + "\n");
			// // Buttons unsichtbar setzen im UI-Thread
			Platform.runLater(() -> {
				btnDownloadDB.setVisible(false);
				// btnDownloadDB.setManaged(false); // Layout-Manager ignoriert Button
				btnSelectSource.setVisible(false);
				// btnSelectSource.setManaged(false);
				lblDownload.setVisible(false);
			});

		}
		catch (Exception e)
		{
			logArea.appendText("❌ Fehler beim Laden der Tabellen: " + e.getMessage() + "\n");
			e.printStackTrace();
		}

	}

	/** Merge starten */
//	@FXML
//	public void onStartMerge()
//	{
//	    if (sourceDbPath == null)
//	    {
//	        logArea.appendText("❌ Keine Quelldatei ausgewählt!\n");
//	        return;
//	    }
//
//	    if (!Msgbox.yesno("Datenbank-Update ...", "Soll das Datenbank-Update nun gestartet werden?"))
//	    {
//	        return;
//	    }
//
//	    createDatabaseBackup();
//
//	    progressBar.progressProperty().unbind();
//	    progressBar.setProgress(0);
//	    btnMerge.setDisable(true);
//
//	    Task<Void> mergeTask = new Task<>()
//	    {
//	        @Override
//	        protected Void call() throws Exception
//	        {
//	            try (Connection sourceConn = DriverManager.getConnection("jdbc:sqlite:" + sourceDbPath);
//	                 Connection targetConn = DriverManager.getConnection("jdbc:sqlite:" + ValuesGlobals.dbPfad))
//	            {
//	                DatabaseMergeService service = new DatabaseMergeService(sourceDbPath, ValuesGlobals.dbPfad);
//	                service.excludeTables(blackList);
//
//	                List<String> tables = service.getTableNames().stream()
//	                        .filter(t -> !blackList.contains(t))
//	                        .toList();
//
//	                service.mergeAllTables(sourceConn, targetConn, tables);
//	            }
//
//	            return null;
//	        }
//	    };
//
//	    progressBar.progressProperty().bind(mergeTask.progressProperty());
//
//	    // ✅ Erfolg bleibt im UI
//	    mergeTask.setOnSucceeded(e -> {
//	        deleteUpdateFiles();
//	        btnMerge.setDisable(false);
//
//	        logArea.appendText("✅ Daten-Übertragung abgeschlossen!\n");
//
//	        Msgbox.show("Datenbank-Update ...",
//	                "Das Update wurde erfolgreich eingespielt.\nNun ist ein Neustart des Programms erforderlich.");
//
//	        System.exit(0);
//	    });
//
//	    // ❌ Fehler nur noch ins Logfile
//	    mergeTask.setOnFailed(e -> {
//	        btnMerge.setDisable(false);
//	        Throwable ex = mergeTask.getException();
//
//	        LOGGER.log(Level.SEVERE, "Fehler beim Datenbank-Merge", ex);
//	    });
//
//	    Thread mergeThread = new Thread(mergeTask, "Database-Merge-Thread");
//	    mergeThread.setDaemon(true);
//	    mergeThread.start();
//	}
	@FXML
	public void onStartMerge()
	{
		if (sourceDbPath == null)
		{
			logArea.appendText("❌ Keine Quelldatei ausgewählt!\n");
			return;
		}

		if (!Msgbox.yesno("Datenbank-Update ...", "Soll das Datenbank-Update nun gestartet werden?"))
		{
			return;
		}

		createDatabaseBackup(); // 💾 Backup erstellen

		// ProgressBar zurücksetzen und Task-Bindung vorbereiten
		progressBar.progressProperty().unbind();
		progressBar.setProgress(0);
		btnMerge.setDisable(true);

		Task<Void> mergeTask = new Task<>()
		{
			@Override
			protected Void call() throws Exception
			{
				try (Connection sourceConn = DriverManager.getConnection("jdbc:sqlite:" + sourceDbPath);
						Connection targetConn = DriverManager.getConnection("jdbc:sqlite:" + ValuesGlobals.dbPfad))
				{

					// Service initialisieren
					DatabaseMergeService service = new DatabaseMergeService(sourceDbPath, ValuesGlobals.dbPfad);

					// Blacklist berücksichtigen, falls nötig
					service.excludeTables(blackList);

					List<String> tables = service.getTableNames().stream()
							.filter(t -> !blackList.contains(t))
							.toList();

					// Tabellen zusammenführen
					service.mergeAllTables(sourceConn, targetConn, tables);

				}
				catch (SQLException e)
				{
					Platform.runLater(() -> logArea.appendText("❌ Fehler beim Merge: " + e.getMessage() + "\n"));
					e.printStackTrace();
				}

				return null;
			}
		};

		// ProgressBar an Task binden
		progressBar.progressProperty().bind(mergeTask.progressProperty());

		// Erfolgreiches Ende
		mergeTask.setOnSucceeded(e -> {
			deleteUpdateFiles(); // 👈 ZIP + entpackte Dateien löschen
			btnMerge.setDisable(false);

			logArea.appendText("✅ Daten-Übertragung abgeschlossen!\n");
			Msgbox.show("Datenbank-Update ...",
					"Das Update wurde erfolgreich eingespielt.\nNun ist ein Neustart des Programms erforderlich.");
			//StartupManager.restart();
			System.exit(0);
		});

		// Fehlerfall
		mergeTask.setOnFailed(e -> {
			btnMerge.setDisable(false);
			Throwable ex = mergeTask.getException();
			logArea.appendText("❌ Fehler beim Übertragen: " + ex.getMessage() + "\n");
			ex.printStackTrace();
		});

		// Task starten
		Thread mergeThread = new Thread(mergeTask, "Database-Merge-Thread");
		mergeThread.setDaemon(true);
		mergeThread.start();
	}
//	@FXML
//	public void onStartMerge()
//	{
//
//
//		if (sourceDbPath == null)
//		{
//			logArea.appendText("❌ Keine Quelldatei ausgewählt!\n");
//			return;
//		}
//
//		if (!Msgbox.yesno("Datenbank-Update ...", "Soll das Datenbank-Update nun gestartet werden?"))
//		{
//			return;
//		}
//		createDatabaseBackup(); // 💾 Backup erstellen
//
//		// ProgressBar zurücksetzen und Task-Bindung vorbereiten
//		progressBar.progressProperty().unbind();
//		progressBar.setProgress(0);
//
//		btnMerge.setDisable(true);
//
//		Task<Void> mergeTask = new Task<>()
//		{
//			@Override
//			protected Void call() throws Exception
//			{
//
//		        //  Merge starten
//		        service.mergeTables(
//		            tableListView.getItems(),
//		            (cur, total) -> updateProgress(cur, total),
//		            msg -> Platform.runLater(() -> logArea.appendText(msg + "\n"))
//		        );
//
//
//				// optional kleine Pause
//				Thread.sleep(200);
//
//				return null;
//			}
//		};
//
//		// ProgressBar an Task binden
//		progressBar.progressProperty().bind(mergeTask.progressProperty());
//
//		// Erfolgreiches Ende
//		mergeTask.setOnSucceeded(e -> {
//			deleteUpdateFiles(); // 👈 löscht ZIP + entpackte Dateien
//			btnMerge.setDisable(false);
//
//			logArea.appendText("✅ Daten-Übertragung abgeschlossen!\n");
//			Msgbox.show("Datenbank-Update ...",
//					"Das Update wurde erfolgreich eingespielt.\nNun ist ein Neustart des Programms erforderlich.");
//			StartupManager.restart();
//		});
//
//		// Fehlerfall
//		mergeTask.setOnFailed(e -> {
//			btnMerge.setDisable(false);
//			Throwable ex = mergeTask.getException();
//			logArea.appendText("❌ Fehler beim Übertragen: " + ex.getMessage() + "\n");
//			ex.printStackTrace();
//		});
//
//		// Task starten
//		Thread mergeThread = new Thread(mergeTask, "Database-Merge-Thread");
//		mergeThread.setDaemon(true);
//		mergeThread.start();
//	}

	private void deleteUpdateFiles()
	{

		if (updateFolder == null || !updateFolder.exists())
			return;

		deleteDirectory(updateFolder);
		// entpackte Update-DB im Datenbankverzeichnis löschen
		try
		{

			File activeDb = new File(ValuesGlobals.dbPfad);
			File dbFolder = activeDb.getParentFile();

			File extractedDb = new File(dbFolder, "jpcnd-db-update-voll.db3");

			if (extractedDb.exists())
			{
				if (!extractedDb.getAbsolutePath().equals(ValuesGlobals.dbPfad))
				{
					extractedDb.delete();
				}
				// extractedDb.delete();
				Platform.runLater(() -> logArea.appendText("🧹 Update-DB bereinigt: " + extractedDb.getName() + "\n"));
			}

		}
		catch (Exception e)
		{
			Platform.runLater(() -> logArea.appendText("⚠️ Fehler beim Löschen der Update-DB: " + e.getMessage() + "\n"));
		}

		// jpcnd-db-update-voll.db3

		Platform.runLater(() -> logArea.appendText("🧹 Update-ZIP-Dateien bereinigt\n"));
	}

	private void deleteDirectory(File dir)
	{

		File[] files = dir.listFiles();

		if (files != null)
		{
			for (File f : files)
			{

				if (f.isDirectory())
				{
					deleteDirectory(f);
				}
				else
				{
					f.delete();
				}
			}
		}

		dir.delete();

	}

	private void createDatabaseBackup()
	{
		try
		{
			String backupPath = ConfigManager.loadBackupDirectory();
			File activeDb = new File(ValuesGlobals.dbPfad);
			File backupDir = new File(backupPath);

			if (!backupDir.exists())
				backupDir.mkdirs();

			String timestamp = java.time.LocalDateTime.now()
					.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

			String baseName = activeDb.getName().replaceFirst("[.][^.]+$", "");

			File backupFile = new File(backupDir, baseName + "_" + timestamp + ".db3");

			java.nio.file.Files.copy(
					activeDb.toPath(),
					backupFile.toPath(),
					java.nio.file.StandardCopyOption.REPLACE_EXISTING);

			Platform.runLater(() -> logArea.appendText("💾 Backup erstellt: " + backupFile.getAbsolutePath() + "\n"));

			// 🧹 alte Backups aufräumen
			cleanupOldBackups(backupDir, baseName, 20);
		}
		catch (Exception e)
		{
			Platform.runLater(() -> logArea.appendText("❌ Backup fehlgeschlagen: " + e.getMessage() + "\n"));
		}
	}

	private void cleanupOldBackups(File backupDir, String baseName, int keep)
	{
		File[] backups = backupDir.listFiles((dir, name) -> name.startsWith(baseName) && name.endsWith(".db3"));

		if (backups == null || backups.length <= keep)
			return;

		Arrays.sort(backups, Comparator.comparingLong(File::lastModified).reversed());

		for (int i = keep; i < backups.length; i++)
		{
			backups[i].delete();
		}

		Platform.runLater(() -> logArea.appendText("🧹 Alte Backups aufgeräumt\n"));
	}

	public void setStage(Stage stModalwindow)
	{
		// TODO Auto-generated method stub

	}
}
