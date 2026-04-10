package application.utils;

import javafx.application.Platform;

import javafx.concurrent.Task;

import javafx.scene.control.*;

import javafx.scene.layout.Priority;

import javafx.scene.layout.VBox;

import java.awt.Desktop;

import java.net.URI;

import java.net.http.HttpClient;

import java.net.http.HttpRequest;

import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;

import java.time.Duration;

import application.ConfigLoader;

import application.ValuesGlobals;

import application.db.DatabaseVersionUtil;

/**
 * 
 * Vollständig moderne Update-Checker Klasse
 * 
 * ----------------------------------------- - lädt alle URLs/Timeouts aus
 * 
 * config.properties - moderne HTTP-API (HttpClient) - Versionsvergleich
 * 
 * (beliebige Revisionslänge) - "Was ist neu?" Changelog-Anzeige - "Über dieses
 * 
 * Programm"-Dialog - Update-Dialog mit Weiterleitung zur Downloadseite -
 * 
 * saubere Fehlerbehandlung - JavaFX-thread-safe (Tasks + runLater)
 * 
 */

public class ToolsUpdateChecker
{

	// -------------------------------

	// Werte aus config.properties

	// -------------------------------

	private static final String PROG_VERSION_URL = ConfigLoader.get("version.url",

			"https://www.pcnd.eu/jpcnd/version/jpcnd-prog-version.txt");

	private static final String DB_VERSION_URL = ConfigLoader.get("db.version.url",

			"https://www.pcnd.eu/jpcnd/version/jpcnd-db-version.txt");

	private static final String CHANGELOG_URL = ConfigLoader.get("changelog.url",

			"https://www.pcnd.eu/jpcnd/version/jpcnd-changelog.txt");

	private static final String DOWNLOAD_PAGE = ConfigLoader.get("download.url",

			"https://www.pcnd.eu/jpcnd/index.php?aw=000downloadseite.php");

	private static final String DB_DOWNLOAD_URL = ConfigLoader.get("db.download.url",

			"https://www.pcnd.eu/jpcnd/index.php?aw=000downloadseite.php");

	private static final int HTTP_TIMEOUT = ConfigLoader.getInt("http.timeout", 15);

	// HttpClient (wiederverwendbar, thread-safe)

	private static final HttpClient httpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL)

			.connectTimeout(Duration.ofSeconds(HTTP_TIMEOUT)).build();

	private static final boolean AUTH_ENABLED = ConfigLoader.getBoolean("update.auth.enabled", false);

	private static final String AUTH_USER = ConfigLoader.get("update.auth.username", "");

	private static final String AUTH_PASS = ConfigLoader.get("update.auth.password", "");

	// =========================================================================

	// Updateprüfung – Hauptmethode

	// =========================================================================

	public static String checkForUpdatesUniversell(String typ) throws Exception
	{

		String onlineDbVersion;
		String localDbVersion;
		String onlineVersion = "";
		boolean neueVersion = false;
		if (typ.equals("db"))
		{
			localDbVersion = DatabaseVersionUtil.getLocalDatabaseVersion();
			String content = fetchTextFromWeb(PROG_VERSION_URL);
			if (content == null || content.isBlank())
			{
				// 🔹 kein Internet oder leer → einfach abbrechen
				return "xxx";
			}
			onlineDbVersion = fetchTextFromWeb(DB_VERSION_URL).split("\n")[0].trim();
			neueVersion = isNewerVersion(localDbVersion, onlineDbVersion);
			onlineVersion = onlineDbVersion;
		}
		else if (typ.equals("prog"))
		{
			String content = fetchTextFromWeb(PROG_VERSION_URL);
			if (content == null || content.isBlank())
			{
				return "xxx";
			}
			onlineVersion = content.split("\n")[0].trim(); // erste Zeile = Version
			neueVersion = isNewerVersion(ValuesGlobals.progVersion, onlineVersion);
		}
		else
		{
			return "";
		}
		if (neueVersion == true)
		{
			return onlineVersion;
		}
		else
		{
			return "";
		}
	}

	public static void checkForUpdates(javafx.stage.Stage owner)
	{

		Task<Void> task = new Task<>()
		{

			private String onlineVersion;

			@Override

			protected Void call() throws Exception
			{

				String content = fetchTextFromWeb(PROG_VERSION_URL);

				if (content == null || content.isBlank())
				{

					throw new RuntimeException("Die Versionsdatei ist leer oder ungültig.");

				}

				onlineVersion = content.split("\n")[0].trim(); // erste Zeile = Version

				boolean newer = isNewerVersion(ValuesGlobals.progVersion, onlineVersion);

				Platform.runLater(() -> {

					if (newer)
					{

						showRedirectDialog(owner, onlineVersion);

					}
					else
					{

						showInfo(owner, "Kein Update verfügbar",

								"Sie verwenden bereits die neueste Version:\n\n" + ValuesGlobals.progVersion);

					}

				});

				return null;

			}

			@Override

			protected void failed()
			{

				String msg = (getException() != null) ? getException().getMessage() : "Unbekannter Fehler";

				Platform.runLater(() -> showError(owner, "Fehler beim Updatecheck", msg));

			}

		};

		new Thread(task, "UpdateCheck-Thread").start();

	}

	public static void checkForDatabaseUpdate(javafx.stage.Stage owner)
	{

		Task<Void> task = new Task<>()
		{

			private String onlineDbVersion;

			private String localDbVersion;

			@Override

			protected Void call() throws Exception
			{

				localDbVersion = DatabaseVersionUtil.getLocalDatabaseVersion();

				onlineDbVersion = fetchTextFromWeb(DB_VERSION_URL).split("\n")[0].trim();

				boolean newer = isNewerVersion(localDbVersion, onlineDbVersion);

				Platform.runLater(() -> {

					if (newer)
					{

						showDatabaseUpdateDialog(owner, localDbVersion, onlineDbVersion);

					}
					else
					{

						showInfo(owner, "Datenbank aktuell", "Ihre Datenbank ist aktuell:\n\n" + localDbVersion);

					}

				});

				return null;

			}

			@Override

			protected void failed()
			{

				Platform.runLater(

						() -> showError(owner, "Datenbank-Updateprüfung fehlgeschlagen", getException().getMessage()));

			}

		};

		new Thread(task, "DB-UpdateCheck").start();

	}

	private static void showDatabaseUpdateDialog(javafx.stage.Stage owner, String localVersion, String onlineVersion)
	{

		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

		alert.initOwner(owner);

		alert.setTitle("Datenbank-Update verfügbar");

		alert.setHeaderText("Neue Datenbankversion verfügbar");

		alert.setContentText("Installierte Version: " + localVersion + "\n" + "Verfügbare Version: " + onlineVersion

				+ "\n\n" + "Möchten Sie die aktuelle Datenbank herunterladen?");

		ButtonType downloadBtn = new ButtonType("Datenbank herunterladen");

		ButtonType cancelBtn = new ButtonType("Später");

		alert.getButtonTypes().setAll(downloadBtn, cancelBtn);

		alert.showAndWait().ifPresent(bt -> {

			if (bt == downloadBtn)
			{

				openBrowser(DB_DOWNLOAD_URL);

			}

		});

	}

	// =========================================================================

	// Moderne HTTP-Textabfrage

	// =========================================================================

	private static String fetchTextFromWeb(String url) throws Exception
	{
		try
		{
			HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url))
					.timeout(Duration.ofSeconds(HTTP_TIMEOUT)).header("Accept", "text/plain");
			if (AUTH_ENABLED)
			{ // ist es htaccess gesichert?
				builder.header("Authorization", createBasicAuthHeader(AUTH_USER, AUTH_PASS));
			}
			HttpRequest request = builder.GET().build();
			HttpResponse<String> response = httpClient.send(request,
					HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
			if (response.statusCode() != 200)
			{
				throw new RuntimeException("HTTP Fehler " + response.statusCode() + " beim Laden: " + url);
			}
			return response.body();
		}
		catch (java.net.ConnectException e)
		{
			// 🔹 KEIN Internet / Server nicht erreichbar
			System.out.println("Keine Internetverbindung für Update-Check.");
			return null;
		}
		catch (java.net.http.HttpTimeoutException e)
		{
			// 🔹 Server antwortet nicht
			System.out.println("Timeout beim Update-Check.");
			return null;
		}
		catch (Exception e)
		{
			// 🔹 alles andere
			System.out.println("Fehler beim Laden von: " + url);
			return null;
		}
	}

	// =========================================================================

	// Versionsvergleich – absolut robust

	// =========================================================================

	private static boolean isNewerVersion(String current, String online)
	{

		int[] c = parseVersion(current);

		int[] o = parseVersion(online);

		int max = Math.max(c.length, o.length);

		for (int i = 0; i < max; i++)
		{

			int cv = (i < c.length) ? c[i] : 0;

			int ov = (i < o.length) ? o[i] : 0;

			if (ov > cv)

				return true;

			if (ov < cv)

				return false;

		}

		return false; // Gleich

	}

	private static int[] parseVersion(String version)
	{

		String cleaned = (version == null) ? "" : version.replaceAll("[^0-9.]", "");

		if (cleaned.isBlank())

			return new int[] { 0 };

		String[] parts = cleaned.split("\\.");

		int[] nums = new int[parts.length];

		for (int i = 0; i < parts.length; i++)
		{

			try
			{

				nums[i] = Integer.parseInt(parts[i]);

			}
			catch (Exception e)
			{

				nums[i] = 0;

			}

		}

		return nums;

	}

	// =========================================================================

	// Dialoge: Info / Fehler

	// =========================================================================

	private static void showInfo(javafx.stage.Stage owner, String title, String text)
	{

		Alert alert = new Alert(Alert.AlertType.INFORMATION);

		alert.initOwner(owner);

		alert.setTitle(title);

		alert.setHeaderText(null);

		alert.setContentText(text);

		alert.getDialogPane().setMinWidth(450);

		alert.showAndWait();

	}

	private static void showError(javafx.stage.Stage owner, String title, String text)
	{

		Alert alert = new Alert(Alert.AlertType.ERROR);

		alert.initOwner(owner);

		alert.setTitle(title);

		alert.setHeaderText(null);

		alert.setContentText(text);

		alert.getDialogPane().setMinWidth(450);

		alert.showAndWait();

	}

	// =========================================================================

	// Update-Dialog

	// =========================================================================

	private static void showRedirectDialog(javafx.stage.Stage owner, String onlineVersion)
	{

		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

		alert.initOwner(owner);

		alert.setTitle("Update verfügbar");

		alert.setHeaderText("Neue Version verfügbar: " + onlineVersion);

		alert.setContentText("Es ist eine neue Version verfügbar.\n\n"

				+ "Klicken Sie auf „Downloadseite öffnen“, um die offizielle Installationsseite aufzurufen.");

		ButtonType openBtn = new ButtonType("Downloadseite öffnen");

		ButtonType changelogBtn = new ButtonType("Was ist neu?");

		ButtonType cancelBtn = new ButtonType("Abbrechen", ButtonBar.ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(openBtn, changelogBtn, cancelBtn);

		alert.showAndWait().ifPresent(button -> {

			if (button == openBtn)
			{

				openBrowser(DOWNLOAD_PAGE);

			}

			else if (button == changelogBtn)
			{

				showChangelog(owner);

			}

		});

	}

	// =========================================================================

	// Changelog-Fenster

	// =========================================================================

	private static void showChangelog(javafx.stage.Stage owner)
	{

		Task<String> task = new Task<>()
		{

			@Override

			protected String call() throws Exception
			{

				return fetchTextFromWeb(CHANGELOG_URL);

			}

		};

		task.setOnSucceeded(ev -> {

			String text = task.getValue();

			if (text == null || text.isBlank())
			{

				showInfo(owner, "Änderungsprotokoll", "Kein Changelog vorhanden.");

				return;

			}

			// scrollbares TextArea

			TextArea ta = new TextArea(text);

			ta.setEditable(false);

			ta.setWrapText(true);

			ta.setPrefWidth(700);

			ta.setPrefHeight(450);

			VBox container = new VBox(ta);

			VBox.setVgrow(ta, Priority.ALWAYS);

			Dialog<Void> d = new Dialog<>();

			d.setTitle("Was ist neu?");

			d.initOwner(owner);

			d.getDialogPane().setContent(container);

			d.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

			d.getDialogPane().setMinWidth(720);

			d.showAndWait();

		});

		task.setOnFailed(

				ev -> showError(owner, "Fehler beim Laden", "Das Änderungsprotokoll konnte nicht geladen werden."));

		new Thread(task, "Changelog-Thread").start();

	}

	// =========================================================================

	// Browser öffnen – Cross-Platform & Robust

	// =========================================================================

	private static void openBrowser(String url)
	{

		try
		{

			URI uri = URI.create(url);

			// Desktop API?

			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
			{

				Desktop.getDesktop().browse(uri);

				return;

			}

			// OS-basierte Fallbacks

			String os = System.getProperty("os.name").toLowerCase();

			if (os.contains("win"))
			{

				new ProcessBuilder("cmd", "/c", "start", uri.toString()).start();

			}
			else if (os.contains("mac"))
			{

				new ProcessBuilder("open", uri.toString()).start();

			}
			else if (os.contains("nux") || os.contains("nix"))
			{

				new ProcessBuilder("xdg-open", uri.toString()).start();

			}
			else
			{

				throw new RuntimeException("Browser-Öffnen wird auf diesem OS nicht unterstützt.");

			}

		}
		catch (Exception e)
		{

			e.printStackTrace();

			Platform.runLater(

					() -> showError(null, "Fehler", "Der Browser konnte nicht geöffnet werden:\n" + e.getMessage()));

		}

	}

	private static String createBasicAuthHeader(String user, String pass)
	{

		String s = user + ":" + pass;

		return "Basic " + java.util.Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));

	}

	// =========================================================================

	// About-Dialog

	// =========================================================================

	public static void showAboutDialog(javafx.stage.Stage owner)
	{

		String javaVersion = System.getProperty("java.version");

		String os = System.getProperty("os.name") + " " + System.getProperty("os.version");

		Alert alert = new Alert(Alert.AlertType.INFORMATION);

		alert.initOwner(owner);

		alert.setTitle("Über dieses Programm");

		alert.setHeaderText("JPCND – PCND Software");

		String msg = String.format("Version: %s%nJava-Version: %s%nBetriebssystem: %s%n%n© %d PCND Software",

				ValuesGlobals.progVersion, javaVersion, os, java.time.Year.now().getValue());

		alert.setContentText(msg);

		alert.getDialogPane().setMinWidth(450);

		alert.showAndWait();

	}

}
