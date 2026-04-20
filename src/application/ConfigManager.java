package application;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public class ConfigManager
{

	// ----------------------------------------
	// Wo die Konfigurationsdatei gespeichert wird
	// Beispiel: C:\Users\<User>\.jpcnd\config.ini
	// ----------------------------------------
	private static final Path CONFIG_DIR = Paths.get(
			System.getProperty("user.home"), ".jpcnd");
	private static final String APP_FOLDER = "jpcnd"; // ← deinen App-Namen eintragen
	private static final Path CONFIG_FILE = CONFIG_DIR.resolve("config.ini");

	// private static final String LICENSE_VALIDATE_URL =
	// "https://www.pcnd.eu/jpcnd/license-api/validate.php";

	private static final Properties props = new Properties();

	// ----------------------------------------
	// Statischer Initialisierer (nur einmal)
	// ----------------------------------------
	static
	{
		ensureConfigExists();
		load();
	}

	// ----------------------------------------
	// Sicherstellen, dass Verzeichnis & Datei existieren
	// ----------------------------------------
	private static void ensureConfigExists()
	{
		try
		{
			// Ordner erzeugen, falls nötig
			if (!Files.exists(CONFIG_DIR))
			{
				Files.createDirectories(CONFIG_DIR);
			}

			// Datei anlegen, falls nötig
			if (!Files.exists(CONFIG_FILE))
			{
				Files.createFile(CONFIG_FILE);

				// Defaultwerte speichern
				Properties def = new Properties();
				def.setProperty("db.path", "");
				try (OutputStream out = Files.newOutputStream(CONFIG_FILE))
				{
					def.store(out, "jPCND - Initiale Konfiguration");
				}
			}

		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.err.println("FEHLER: config.ini konnte nicht angelegt werden!");
		}
	}

	// ----------------------------------------
	// Laden der Config
	// ----------------------------------------
	private static void load()
	{
		try (InputStream in = Files.newInputStream(CONFIG_FILE))
		{
			props.load(in);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.err.println("FEHLER: config.ini konnte nicht gelesen werden!");
		}
	}

	// ----------------------------------------
	// Speichern der Config
	// ----------------------------------------
	private static synchronized void save()
	{
		try (OutputStream out = Files.newOutputStream(CONFIG_FILE))
		{
			props.store(out, "jPCND Konfiguration");
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.err.println("FEHLER: config.ini konnte nicht gespeichert werden!");
		}
	}

	// ----------------------------------------
	// Öffentliche API
	// ----------------------------------------

	/**
	 * Liefert die URL zur Online-Lizenzprüfung.
	 */
//    public static String getLicenseValidateUrl() {
//        return LICENSE_VALIDATE_URL;
//    }

	// Optional: Setter, wenn du die URL dynamisch ändern willst
	public static void setLicenseValidateUrl(String url)
	{
		// z.B. Validierung hinzufügen
		if (url != null && !url.isBlank())
		{
			// LICENSE_VALIDATE_URL = url; // falls nicht final
		}
	}

	public static String loadDBPath()
	{
		ValuesGlobals.dbPfad = props.getProperty("db.path", "");
		return props.getProperty("db.path", "");
	}

	public static void saveDBPath(String path)
	{
		props.setProperty("db.path", path != null ? path : "");
		save();
		ValuesGlobals.dbPfad = path;
	}

	public static Path getConfigFile()
	{
		return CONFIG_FILE;
	}

	public static Path getConfigDir()
	{
		return CONFIG_DIR;
	}

	// Backup-Verzeichnis
	public static String loadBackupDirectory()
	{
		return props.getProperty("backupDir", "");
	}

	public static void saveBackupDirectory(String path)
	{
		props.setProperty("backupDir", path != null ? path : "");
		save();

	}

	// Titelgtrafiken-Verzeichnis
	public static String loadGrafikDirectory()
	{
		return props.getProperty("titelgrafik", "");
	}

	public static void saveGrafikDirectory(String path)
	{
		props.setProperty("titelgrafik", path != null ? path : "");
		save();

	}

	/**
	 * Gibt den lokalen Konfigurationsordner zurück. Unter Windows:
	 * C:\Users\<User>\AppData\Local\MyApp
	 */
	public static File getConfigDirectory()
	{
		String base;

		// Windows
		if (System.getProperty("os.name").toLowerCase().contains("win"))
		{
			base = System.getenv("LOCALAPPDATA");
			if (base == null)
				base = System.getProperty("user.home");
		}
		// macOS
		else if (System.getProperty("os.name").toLowerCase().contains("mac"))
		{
			base = System.getProperty("user.home") + "/Library/Application Support";
		}
		// Linux
		else
		{
			base = System.getProperty("user.home") + "/.config";
		}

		File dir = new File(base, APP_FOLDER);
		dir.mkdirs(); // erzeugt den Ordner falls nicht vorhanden
		return dir;
	}

	public static String getLicenseCreateUrl()
	{
		return "https://www.pcnd.eu/jpcnd/license-api/create_license.php"; // Pfad zu create.php auf dem Server
	}

	// ----------------------------------------
	// Literatur-Filter
	// ----------------------------------------	
	public static String loadFilterStartTitel() {
	    return props.getProperty("filter.starttitel", "");
	}

	public static void saveFilterStartTitel(String value) {
	    props.setProperty("filter.starttitel", value != null ? value : "");
	    save();
	}	
	
	public static String loadFilterStartStckart() {
	    return props.getProperty("filter.startstckart", "");
	}

	public static void saveFilterStartStckart(String value) {
	    props.setProperty("filter.startstckart", value != null ? value : "");
	    save();
	}
	
	public static String loadFilterStartEdition() {
	    return props.getProperty("filter.startedition", "");
	}

	public static void saveFilterStartEdition(String value) {
	    props.setProperty("filter.startedition", value != null ? value : "");
	    save();
	}	
	public static String loadFilterStartKomponist() {
	    return props.getProperty("filter.startkomponist", "");
	}

	public static void saveFilterStartKomponist(String value) {
	    props.setProperty("filter.startkomponist", value != null ? value : "");
	    save();
	}
	
	public static String loadFilterStartDichter() {
	    return props.getProperty("filter.startdichter", "");
	}

	public static void saveFilterStartDichter(String value) {
	    props.setProperty("filter.startdichter", value != null ? value : "");
	    save();
	}
	
	public static String loadFilterStartVerlag() {
	    return props.getProperty("filter.startverlag", "");
	}

	public static void saveFilterStartVerlag(String value) {
	    props.setProperty("filter.startverlag", value != null ? value : "");
	    save();
	}
	public static String loadFilterStartWoli() {
	    return props.getProperty("filter.startwoli", "");
	}

	public static void saveFilterStartWoli(String value) {
	    props.setProperty("filter.startwoli", value != null ? value : "");
	    save();
	}
	public static String loadFilterStartThema() {
	    return props.getProperty("filter.startthema", "");
	}

	public static void saveFilterStartThema(String value) {
	    props.setProperty("filter.startthema", value != null ? value : "");
	    save();
	}
	public static String loadFilterStartNoma() {
	    return props.getProperty("filter.startnoma", "");
	}

	public static void saveFilterStartNoma(String value) {
	    props.setProperty("filter.startnoma", value != null ? value : "");
	    save();
	}
	public static String loadFilterStartBib() {
	    return props.getProperty("filter.startbib", "");
	}

	public static void saveFilterStartBib(String value) {
	    props.setProperty("filter.startbib", value != null ? value : "");
	    save();
	}
	public static String loadFilterStartGesangbuch() {
	    return props.getProperty("filter.startgesangbuch", "");
	}

	public static void saveFilterStartGesangbuch(String value) {
	    props.setProperty("filter.startgesangbuch", value != null ? value : "");
	    save();
	}

	// ----------------------------------------
	// Aktionen-Filter
	// ----------------------------------------

	public static String loadFilterAktionDatumvon() {
	    return props.getProperty("filter.aktiondatvon", "");
	}

	public static void saveFilterAktionDatumvon(String value) {
	    props.setProperty("filter.aktiondatvon", value != null ? value : "");
	    save();
	}
	
	public static String loadFilterAktionDatumbis() {
	    return props.getProperty("filter.aktiondatbis", "");
	}

	public static void saveFilterAktionDatumbis(String value) {
	    props.setProperty("filter.aktiondatbis", value != null ? value : "");
	    save();
	}
	
	public static String loadFilterAktion() {
	    return props.getProperty("filter.aktion", "");
	}

	public static void saveFilterAktion(String value) {
	    props.setProperty("filter.aktion", value != null ? value : "");
	    save();
	}
	
	public static String loadFilterAktionOrt() {
	    return props.getProperty("filter.aktionort", "");
	}

	public static void saveFilterAktionOrt(String value) {
	    props.setProperty("filter.aktionort", value != null ? value : "");
	    save();
	}
	
	public static String loadFilterAktionGruppe() {
	    return props.getProperty("filter.aktiongruppe", "");
	}

	public static void saveFilterAktionGruppe(String value) {
	    props.setProperty("filter.aktiongruppe", value != null ? value : "");
	    save();
	}
	
	public static String loadFilterAktionBeschreibung() {
	    return props.getProperty("filter.aktionbeschr", "");
	}

	public static void saveFilterAktionBeschreibung(String value) {
	    props.setProperty("filter.aktionbeschr", value != null ? value : "");
	    save();
	}
	
	// ----------------------------------------
	// AktionenPositionen-Filter
	// ----------------------------------------	
	public static String loadFilterAktionPosDatumvon() {
	    return props.getProperty("filter.aktionposdatvon", "");
	}

	public static void saveFilterAktionPosDatumvon(String value) {
	    props.setProperty("filter.aktionposdatvon", value != null ? value : "");
	    save();
	}
	
	public static String loadFilterAktionPosDatumbis() {
	    return props.getProperty("filter.aktionposdatbis", "");
	}

	public static void saveFilterAktionPosDatumbis(String value) {
	    props.setProperty("filter.aktionposdatbis", value != null ? value : "");
	    save();
	}	
	
	
	
	public static String loadFilterAktionPos() {
	    return props.getProperty("filter.aktionpos", "");
	}

	public static void saveFilterAktionPos(String value) {
	    props.setProperty("filter.aktionpos", value != null ? value : "");
	    save();
	}
	
	public static String loadFilterAktionPosOrt() {
	    return props.getProperty("filter.aktionposort", "");
	}

	public static void saveFilterAktionPosOrt(String value) {
	    props.setProperty("filter.aktionposort", value != null ? value : "");
	    save();
	}
	
	public static String loadFilterAktionPosGruppe() {
	    return props.getProperty("filter.aktionposgruppe", "");
	}

	public static void saveFilterAktionPosGruppe(String value) {
	    props.setProperty("filter.aktionposgruppe", value != null ? value : "");
	    save();
	}
	
	
	
	
	
	
	// ----------------------------------------
	// Personen-Filter
	// ----------------------------------------

	public static String loadFilterChor() {
	    return props.getProperty("filter.chor", "");
	}

	public static void saveFilterChor(String value) {
	    props.setProperty("filter.chor", value != null ? value : "");
	    save();
	}

	public static String loadFilterGruppe() {
	    return props.getProperty("filter.gruppe", "");
	}

	public static void saveFilterGruppe(String value) {
	    props.setProperty("filter.gruppe", value != null ? value : "");
	    save();
	}
	
	
	
//	public static String loadFilterMitwGruppe() {
//	    return props.getProperty("filter.mitwgruppe", "");
//	}
//
//	public static void saveFilterMitwGruppe(String value) {
//	    props.setProperty("filter.mitwgruppe", value != null ? value : "");
//	    save();
//	}

}
