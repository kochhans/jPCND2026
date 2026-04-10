package application.db;

import java.sql.Connection;

import java.sql.DriverManager;

import java.sql.SQLException;

import application.ValuesGlobals;

public class DBManager
{
	private static Connection connection;
	public static synchronized Connection getConnection()
	{
		try
		{
			Class.forName("org.sqlite.JDBC");
			//System.out.println("✅ SQLite JDBC Treiber geladen!");
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
			throw new RuntimeException("SQLite JDBC Treiber konnte nicht geladen werden!", e);
		}
		try
		{
			if (connection == null || connection.isClosed())
			{
				if (ValuesGlobals.dbPfad == null || ValuesGlobals.dbPfad.isBlank())
				{
					throw new IllegalStateException("DB-Pfad ist leer!");
				}
				String url = "jdbc:sqlite:" + ValuesGlobals.dbPfad;
				//System.out.println("📂 Öffne SQLite DB: " + ValuesGlobals.dbPfad);
				try
				{
					connection = DriverManager.getConnection(url);
					//System.out.println("✅ SQLite Connection erfolgreich");
					connection.createStatement().execute("PRAGMA foreign_keys = ON");
					//System.out.println("✅ PRAGMA foreign_keys gesetzt");
				}
				catch (SQLException e)
				{
					System.err.println("❌ SQLite Connection fehlgeschlagen!");
					e.printStackTrace();
					throw e; // Fehler weiterwerfen
				}
			}
			return connection;
		}
		catch (Exception e)
		{
			System.err.println("❌ DBManager.getConnection hat einen Fehler ausgelöst");
			e.printStackTrace();
			throw new RuntimeException("DB-Verbindung konnte nicht geöffnet werden", e);
		}
	}
	public static void close()
	{
		try
		{
			if (connection != null && !connection.isClosed())
			{
				connection.close();
				connection = null;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}

