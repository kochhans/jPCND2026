package application.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import application.ValuesGlobals;

public class DBManager {

    private static Connection connection;

    // ---------------------------------------------------------
    // Verbindung holen (Lazy Init)
    // ---------------------------------------------------------
    public static synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {

                String dbPath = ValuesGlobals.dbPfad;

                if (dbPath == null || dbPath.isBlank()) {
                    throw new IllegalStateException("DB-Pfad ist leer!");
                }

                String url = "jdbc:sqlite:" + dbPath;

                System.out.println("📂 Öffne SQLite DB: " + dbPath);

                connection = DriverManager.getConnection(url);

                // Foreign Keys aktivieren
                connection.createStatement().execute("PRAGMA foreign_keys = ON");

                System.out.println("✅ SQLite Connection erfolgreich");
            }
            else {
                // optional:
                System.out.println("♻️ bestehende DB-Verbindung wird genutzt");
            }

            return connection;

        } catch (Exception e) {
            System.err.println("❌ DBManager.getConnection Fehler");
            e.printStackTrace();
            throw new RuntimeException("DB-Verbindung konnte nicht geöffnet werden", e);
        }
    }

    // ---------------------------------------------------------
    // Verbindung sauber schließen
    // ---------------------------------------------------------
    public static synchronized void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔒 DB Verbindung geschlossen");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection = null;
        }
    }

//    // ---------------------------------------------------------
//    // 🔥 NEU: Reset für Soft-Restart
//    // ---------------------------------------------------------
//    public static synchronized void reset() {
//        System.out.println("♻️ DBManager Reset");
//
//        close(); // alte Verbindung schließen
//
//        // optional: globale Werte zurücksetzen
//        // ValuesGlobals.dbPfad bleibt bewusst erhalten!
//    }

    // ---------------------------------------------------------
    // 🔥 NEU: explizit neue DB setzen (optional)
    // ---------------------------------------------------------
    public static synchronized void setDatabasePath(String newPath) {

        if (newPath == null || newPath.isBlank()) {
            throw new IllegalArgumentException("Neuer DB-Pfad ist ungültig!");
        }

        System.out.println("🔄 Setze neuen DB-Pfad: " + newPath);

        // alte Verbindung schließen
        close();

        // neuen Pfad setzen
        ValuesGlobals.dbPfad = newPath;
    }
}


//package application.db;
//
//import java.sql.Connection;
//
//import java.sql.DriverManager;
//
//import java.sql.SQLException;
//
//import application.ValuesGlobals;
//
//public class DBManager
//{
//	private static Connection connection;
//	public static synchronized Connection getConnection()
//	{
//		try
//		{
//			Class.forName("org.sqlite.JDBC");
//			//System.out.println("✅ SQLite JDBC Treiber geladen!");
//		}
//		catch (ClassNotFoundException e)
//		{
//			e.printStackTrace();
//			throw new RuntimeException("SQLite JDBC Treiber konnte nicht geladen werden!", e);
//		}
//		try
//		{
//			if (connection == null || connection.isClosed())
//			{
//				if (ValuesGlobals.dbPfad == null || ValuesGlobals.dbPfad.isBlank())
//				{
//					throw new IllegalStateException("DB-Pfad ist leer!");
//				}
//				String url = "jdbc:sqlite:" + ValuesGlobals.dbPfad;
//				//System.out.println("📂 Öffne SQLite DB: " + ValuesGlobals.dbPfad);
//				try
//				{
//					connection = DriverManager.getConnection(url);
//					//System.out.println("✅ SQLite Connection erfolgreich");
//					connection.createStatement().execute("PRAGMA foreign_keys = ON");
//					//System.out.println("✅ PRAGMA foreign_keys gesetzt");
//				}
//				catch (SQLException e)
//				{
//					System.err.println("❌ SQLite Connection fehlgeschlagen!");
//					e.printStackTrace();
//					throw e; // Fehler weiterwerfen
//				}
//			}
//			return connection;
//		}
//		catch (Exception e)
//		{
//			System.err.println("❌ DBManager.getConnection hat einen Fehler ausgelöst");
//			e.printStackTrace();
//			throw new RuntimeException("DB-Verbindung konnte nicht geöffnet werden", e);
//		}
//	}
//	public static void close()
//	{
//		try
//		{
//			if (connection != null && !connection.isClosed())
//			{
//				connection.close();
//				connection = null;
//			}
//		}
//		catch (SQLException e)
//		{
//			e.printStackTrace();
//		}
//	}
//}
//
