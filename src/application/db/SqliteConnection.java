package application.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.sqlite.SQLiteConfig;


public class SqliteConnection
{
	//Komplettdatenbank
	public static final String DB_URLADB = "jdbc:sqlite:db/jpcnd_adb.sqlite3;foreign keys=true";// " jdbc:data.sqlite3;foreign keys=true
	public static final String DRIVER = "org.sqlite.JDBC";
	public static Connection getConnectionAdb(String db) throws ClassNotFoundException
	{
		Class.forName(DRIVER);
		Connection connection = null;
		try
		{
			SQLiteConfig config = new SQLiteConfig();
			config.enforceForeignKeys(true);
			connection = DriverManager.getConnection(db, config.toProperties());
			//connection = DriverManager.getConnection(DB_URLADB, config.toProperties());
		} catch (SQLException ex)
		{
		}
		return connection;
	}
	
	public static void init() {
	    try {
	        Class.forName("org.sqlite.JDBC");
	    } catch (ClassNotFoundException e) {
	        throw new RuntimeException("SQLite JDBC Driver fehlt", e);
	    }
	}



}
