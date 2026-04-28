package application;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ValuesGlobals
{
	// ###### ALLG. EINSTELLUNG #########
	public static int progAdmin = 1; // 0 oder 1
	public static String progTitel = "jPCND ";
	public static String progVersion = "Version 1.26.04283";//"Version 1.26.01251" letzte Ziffer für Tagesversion
	public static String dbVersion ="";
	//public static StrinMg dbStructureVersion ="";
	public static String Versionsinfo = "JRT: " + System.getProperty("java.runtime.version") + " // JFX: " + System.getProperty("javafx.version");
	public static Color defaultBackgroundColor = new Color(0.1, 0.1, 0.1, 1);
	public static Font headlineFont = new Font(20);
	public static Font defaultFont = new Font(10);
	public static String progIconLizenz ="Icons: Fatcow Web Hostimg, \n"
			+ "https://creativecommons.org/licenses/by/4.0/\n"
			+ "FatCow Farm Fresh Icons 03.29.2013, v3.5.0 \n"
			+ "Creative Commons Attribution 3.0 License.\r\n"
			+ "http://creativecommons.org/licenses/by/3.0/us/\n"
			+ "\nOpen Icon Library";
	
	// ###### PFADE #########
	public static String dbPfad = ""; // Kompletter Pfad der Datenbank mit Datenbankdatei z.B. C:\daten\jpcnd.db3
	
	public static String progPfad = System.getProperty("user.dir");
	public static String progPfadGrafik = "";// wird gefüllt durch main 12/2025// ValuesGlobals.dbverbindung_adb
											 // +"/titelgrafik/"; //System.getProperty("user.dir")+"/db/titelgrafik/";
	public static String progPfadGrafikFehlt = "titelgrafikfehlt.jpg";
	
	// ###### DB-Verbindungsdaten #########
	//public static Connection dbverbindung_adb;
	public static String databasePath;
	public static String dbverbindung_xx = "";// "jdbc:sqlite:db/jpcnd_pdb.sqlite3" ;
	// EnforceFKConstraints=True
	// public static final String foreignKeyOptionString = "foreign
	// keys=true";//"foreign keys=true";
	public static final boolean defaultEnableForeignKeys = true;
	
	// ###### DOWNLOADS #########
	//public static final String REMOTE_DB_URL = "https://www.pcnd.eu/jpcnd/00download/jpcnd.db3"; // ← Link zur Datenbankdatei
	//Link zur gepackten Datenbankdatei (erstinstall - leere Datenbank oder Demos) :::
	public static final String REMOTE_ZIP_URL = "https://www.pcnd.eu/jpcnd/00download/db/jpcnd-db-start.zip"; 
	// Link zur Updatedatei ZIP mit Datenbankupdatedatei (für Arbeitsordner) und titelgrafik (Unterordner) jpcnd-db-update-voll.zip	
	public static final String REMOTE_DBUPDATE_URL = "https://www.pcnd.eu/jpcnd/00download/db/jpcnd-db-update-voll.zip"; 
	
	public static final String DOWNLOADZIP = "jpcnd-db-download.zip"; // ← Dateiname zum zwischenspeichern des Downloads

	// ###### LIZENZIERUNG #########
	// URLs für Online-Lizenz-Erstellung u. Update / Lizenz checken
	public static final String CREATE_LICENSE_URL = "https://www.pcnd.eu/jpcnd/license-api/create_license.php";
	public static final String LICENSE_SERVER_URL = "https://www.pcnd.eu/jpcnd/license-api/check_license.php"; // ysdlgfkjsdfgölkjs
	//Lokale Lizenzdatei
	public static final String LICENSE_FILENAME = System.getProperty("user.home") + "/.jpcnd/lizenz.txt"; //c:\USERS\xxxx\.jpcnd
	// AES-Schlüssel (16 Bytes = 128 Bit)
	public static final String SECRET_KEY = "1234567890abcdef";
	//Lizenzzeiten
	public static final int OFFLINE_GRACE_DAYS = 30;
	//public static final int GRACE_DAYS = 7;
	public static final long OFFLINE_GRACE_SECONDS = OFFLINE_GRACE_DAYS * 24L * 60L * 60L;
	public static final int OFFLINE_WARN_DAYS = 5;
	public static final long DAY_SECONDS = 24L * 60L * 60L;
	public static final int AUTO_RENEW_DAYS = 5;

	
	// ###### Programmvariablen #########	
	public static final int filtermax = 70000;

	public byte tblvwzeile = -1;
	public static String zentralEingabe = "ADB Original";// ACHTUNG-- in Access eingetragen!
	public static String privatEingabe = "neu priv.";
	public static String privatEingabeedit = "edit priv.";
	public static String zentralEingabeedit = "edit ADB";
	public static String filteruebergabeStueck;
	public static Integer geklickteZeileTblView = 0;
	public boolean dsaendernfrei = false;
	public static String Uebergabewert1 = "";
	public static String Uebergabewert2 = "";
	public static boolean updatecheck = true;
	public static String updateprogramm = "";
	public static String updatedatenbank = "";

//	public static void init()
//	{
//		dbverbindung_adb = DBManager.getConnection();
//		dbVersion=DatabaseVersionUtil.getLocalDatabaseVersion();
//	}

}

