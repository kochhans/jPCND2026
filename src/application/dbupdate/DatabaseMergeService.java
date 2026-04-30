package application.dbupdate;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.io.IOException;
import java.nio.file.*;
import java.util.logging.*;
import java.util.logging.Formatter;

public class DatabaseMergeService
{
	
	private final String sourceDbPath;
	private final String targetDbPath;
	private final Set<String> excludedTables = new HashSet<>(); // ← Blacklist
	
	private static final Logger LOGGER = Logger.getLogger("DatabaseMergeLogger");
	

//	public DatabaseMergeService(String sourceDbPath, String targetDbPath)
//	{
//		this.sourceDbPath = sourceDbPath;
//		this.targetDbPath = targetDbPath;
//	}
	/** Haupt-Konstruktor für Merge */
	public DatabaseMergeService(String sourceDbPath, String targetDbPath)
	{
		this.sourceDbPath = sourceDbPath;
		this.targetDbPath = targetDbPath;
		initLogger();
	}

	/** Zweiter Konstruktor für Startup-Migration (nur Ziel-DB nötig) */
	public DatabaseMergeService(String targetDbPath)
	{
		this.sourceDbPath = null;
		this.targetDbPath = targetDbPath;
	}

	/** Tabellen auf die Blacklist setzen */
	public void excludeTables(Collection<String> tables)
	{
		excludedTables.addAll(tables);
	}
	

	/** Liefert alle Tabellen der Quelle */
	public List<String> getTableNames() throws SQLException
	{
		List<String> tables = new ArrayList<>();
		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + sourceDbPath))
		{
			ResultSet rs = conn.getMetaData().getTables(null, null, "%", new String[] { "TABLE" });
			while (rs.next())
				tables.add(rs.getString("TABLE_NAME"));
		}
		return tables;
	}

	private void initLogger() {
	    try {
	        Path dbPath = Paths.get(targetDbPath); // deine Ziel-DB
	        Path logPath = dbPath.getParent().resolve("merge.log");

	        FileHandler fileHandler = new FileHandler(logPath.toString(), true);
	        
	        fileHandler.setFormatter(new Formatter() {
	            @Override
	            public String format(LogRecord record) {
	                return String.format("[%1$tF %1$tT] [%2$s] %3$s%n",
	                        record.getMillis(),
	                        record.getLevel(),
	                        record.getMessage());
	            }
	        });

	        LOGGER.addHandler(fileHandler);
	        LOGGER.setUseParentHandlers(false);

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}	

	public void mergeAllTables(Connection sourceConn, Connection targetConn, List<String> tables) throws SQLException {

		for (String table : tables) {
	        try {
	            int added = mergeTable(sourceConn, targetConn, table);

	            LOGGER.info("Tabelle " + table + " übernommen: " + added + " neue Datensätze");

	        } catch (SQLException e) {

	            LOGGER.log(Level.WARNING, "Fehler bei Tabelle " + table, e);

	            // 👉 kein throw → Merge läuft weiter
	        }
	    }
	}

	
	/** Nur für tblAdbVersion: Insert der neuen Version, wenn sie noch nicht existiert */
//	private void mergeTblAdbVersion(Connection sourceConn, Connection targetConn) throws SQLException {
//	    String sql = "SELECT ver_id, ver_nr, ver_dat FROM tblAdbVersion ORDER BY ver_id DESC LIMIT 1";
//	    try (Statement stmt = sourceConn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
//	        if (rs.next()) {
//	            int verId = rs.getInt("ver_id");
//	            String verNr = rs.getString("ver_nr");
//	            String verDat = rs.getString("ver_dat");
//
//	            // Prüfen, ob ver_id schon in Ziel-DB existiert
//	            String checkSql = "SELECT COUNT(*) FROM tblAdbVersion WHERE ver_id = ?";
//	            try (PreparedStatement checkStmt = targetConn.prepareStatement(checkSql)) {
//	                checkStmt.setInt(1, verId);
//	                ResultSet checkRs = checkStmt.executeQuery();
//	                if (checkRs.next() && checkRs.getInt(1) == 0) {
//	                    // Insert ausführen
//	                    String insertSql = "INSERT INTO tblAdbVersion (ver_id, ver_nr, ver_dat) VALUES (?, ?, ?)";
//	                    try (PreparedStatement insertStmt = targetConn.prepareStatement(insertSql)) {
//	                        insertStmt.setInt(1, verId);
//	                        insertStmt.setString(2, verNr);
//	                        insertStmt.setString(3, verDat);
//	                        insertStmt.executeUpdate();
//	                        System.out.println("✅ Neue Version " + verNr + " in tblAdbVersion eingetragen");
//	                    }
//	                } else {
//	                    System.out.println("ℹ Version " + verNr + " existiert bereits in Ziel-DB, Insert übersprungen");
//	                }
//	            }
//	        }
//	    }
//	}

	/**
	 * Merge einer einzelnen Tabelle → gibt Anzahl hinzugefügter Datensätze zurück
	 */
	/**
	 * Schneller Merge einer einzelnen Tabelle: nur neue Datensätze oder user_modified=0 aktualisieren
	 * @param sourceConn Quelle
	 * @param targetConn Ziel
	 * @param table Tabellenname
	 * @return Anzahl hinzugefügter Datensätze
	 */
//	private int mergeTable(Connection sourceConn, Connection targetConn, String table) throws SQLException {
//	    String pkColumn = getPrimaryKeyColumn(targetConn, table);
//	    if (pkColumn == null) return 0;
//
//	    int affectedCount = 0;
//
//	    try (Statement stmt = sourceConn.createStatement();
//	         ResultSet rs = stmt.executeQuery("SELECT * FROM " + table)) {
//
//	        ResultSetMetaData meta = rs.getMetaData();
//	        int columnCount = meta.getColumnCount();
//
//	        // Spaltenliste
//	        List<String> colNames = new ArrayList<>();
//	        for (int i = 1; i <= columnCount; i++) {
//	            colNames.add(meta.getColumnName(i));
//	        }
//	        colNames.add("user_modified"); // sicherstellen
//
//	        // 🔥 UPSERT SQL (INSERT + UPDATE)
//	        String insertSQL = "INSERT INTO " + table + " (" + String.join(",", colNames) + ") VALUES ("
//	                + "?,".repeat(colNames.size());
//	        insertSQL = insertSQL.substring(0, insertSQL.length() - 1) + ")";
//
//	        insertSQL += " ON CONFLICT(" + pkColumn + ") DO UPDATE SET ";
//
//	        for (int i = 0; i < columnCount; i++) {
//	            insertSQL += colNames.get(i) + " = excluded." + colNames.get(i) + ",";
//	        }
//
//	        insertSQL = insertSQL.substring(0, insertSQL.length() - 1);
//
//	        // 🔥 nur überschreiben wenn NICHT user-modified
//	        insertSQL += " WHERE " + table + ".user_modified = 0";
//
//	        targetConn.setAutoCommit(false);
//
//	        try (PreparedStatement pstmt = targetConn.prepareStatement(insertSQL)) {
//
//	            int batchSize = 0;
//
//	            while (rs.next()) {
//
//	                for (int i = 1; i <= columnCount; i++) {
//	                    pstmt.setObject(i, rs.getObject(i));
//	                }
//
//	                pstmt.setInt(columnCount + 1, 0); // user_modified = 0
//	                pstmt.addBatch();
//	                batchSize++;
//	                affectedCount++;
//
//	                if (batchSize % 5000 == 0) {
//	                    try {
//	                        pstmt.executeBatch();
//	                    } catch (BatchUpdateException e) {
//	                        System.err.println("⚠️ Batch-Fehler in Tabelle " + table + ": " + e.getMessage());
//	                    }
//	                }
//	            }
//
//	            try {
//	                pstmt.executeBatch();
//	            } catch (BatchUpdateException e) {
//	                System.err.println("⚠️ Batch-Endfehler in Tabelle " + table + ": " + e.getMessage());
//	            }
//
//	            targetConn.commit();
//
//	        } finally {
//	            targetConn.setAutoCommit(true);
//	        }
//	    }
//
//	    return affectedCount;
//	}
	
	private int mergeTable(Connection sourceConn, Connection targetConn, String table) throws SQLException {

	    String pkColumn = getPrimaryKeyColumn(targetConn, table);
	    if (pkColumn == null) return 0;

	    int inserted = 0;
	    int updated = 0;
	    int skipped = 0;
	    int errors = 0;

	    try (Statement stmt = sourceConn.createStatement();
	         ResultSet rs = stmt.executeQuery("SELECT * FROM " + table)) {

	        ResultSetMetaData meta = rs.getMetaData();
	        int columnCount = meta.getColumnCount();

	        List<String> colNames = new ArrayList<>();
	        for (int i = 1; i <= columnCount; i++) {
	            colNames.add(meta.getColumnName(i));
	        }
	        colNames.add("user_modified");

	        String insertSQL = "INSERT INTO " + table + " (" + String.join(",", colNames) + ") VALUES ("
	                + "?,".repeat(colNames.size());
	        insertSQL = insertSQL.substring(0, insertSQL.length() - 1) + ")";

	        insertSQL += " ON CONFLICT(" + pkColumn + ") DO UPDATE SET ";

	        for (int i = 0; i < columnCount; i++) {
	            insertSQL += colNames.get(i) + " = excluded." + colNames.get(i) + ",";
	        }

	        insertSQL = insertSQL.substring(0, insertSQL.length() - 1);

	        insertSQL += " WHERE " + table + ".user_modified = 0";

	        targetConn.setAutoCommit(false);

	        try (PreparedStatement pstmt = targetConn.prepareStatement(insertSQL)) {

	            int batchSize = 0;

	            while (rs.next()) {

	                for (int i = 1; i <= columnCount; i++) {
	                    pstmt.setObject(i, rs.getObject(i));
	                }

	                pstmt.setInt(columnCount + 1, 0);
	                pstmt.addBatch();
	                batchSize++;

	                if (batchSize % 5000 == 0) {
	                    int[] results = executeBatchSafe(pstmt, table);

	                    for (int r : results) {
	                        if (r > 0) {
	                            inserted++; // oder updated → SQLite unterscheidet hier leider nicht sauber
	                        } else if (r == Statement.SUCCESS_NO_INFO) {
	                            updated++;
	                        } else if (r == Statement.EXECUTE_FAILED) {
	                            errors++;
	                        } else {
	                            skipped++;
	                        }
	                    }
	                }
	            }

	            // Restbatch
	            int[] results = executeBatchSafe(pstmt, table);

	            for (int r : results) {
	                if (r > 0) {
	                    inserted++;
	                } else if (r == Statement.SUCCESS_NO_INFO) {
	                    updated++;
	                } else if (r == Statement.EXECUTE_FAILED) {
	                    errors++;
	                } else {
	                    skipped++;
	                }
	            }

	            targetConn.commit();

	        } catch (Exception e) {
	            targetConn.rollback();
	            throw e;
	        } finally {
	            targetConn.setAutoCommit(true);
	        }
	    }

	    System.out.println("📊 Tabelle " + table +
	            " | inserted=" + inserted +
	            " updated=" + updated +
	            " skipped=" + skipped +
	            " errors=" + errors);
	    
	    LOGGER.info("Tabelle " + table +
	            " | inserted=" + inserted +
	            " updated=" + updated +
	            " skipped=" + skipped +
	            " errors=" + errors);

	    return inserted + updated;
	}
	
	private int[] executeBatchSafe(PreparedStatement pstmt, String table) throws SQLException {

	    try {
	        return pstmt.executeBatch();

	    } catch (BatchUpdateException e) {

	        int[] results = e.getUpdateCounts();

	        System.err.println("⚠️ Batch-Fehler in Tabelle " + table + ": " + e.getMessage());
	        LOGGER.warning("Batch-Fehler in Tabelle " + table + ": " + e.getMessage());
	        

	        for (int i = 0; i < results.length; i++) {
	            if (results[i] == Statement.EXECUTE_FAILED) {
	                System.err.println("❌ Fehler bei Datensatz #" + i);
	                LOGGER.fine("Fehler bei Datensatz #" + i);
	            }
	        }

	        return results; // 👉 extrem wichtig!
	    }
	}

	/**
	 * Merge aller Tabellen aus sourceDB in targetDB
	 */
	public int mergeAllTables(List<String> tables, Connection sourceConn, Connection targetConn,
	                          BiConsumer<Integer, Integer> progressCallback,
	                          Consumer<String> messageCallback) throws SQLException {

	    int totalTables = tables.size();
	    int currentTable = 0;
	    int totalAdded = 0;

	    for (String table : tables) {
	        currentTable++;
	        messageCallback.accept("⏳ Merge Tabelle: " + table);
	        int added = mergeTable(sourceConn, targetConn, table);
	        totalAdded += added;

	        messageCallback.accept("📦 " + table + ": " + added + " Datensätze verarbeitet");
	        progressCallback.accept(currentTable, totalTables);
	    }

	    return totalAdded;
	}

	/** Liefert die PK-Spalte einer Tabelle */
	private String getPrimaryKeyColumn(Connection conn, String table) throws SQLException
	{
		ResultSet rs = conn.getMetaData().getPrimaryKeys(null, null, table);
		if (rs.next())
			return rs.getString("COLUMN_NAME");
		return null;
	}

	/** Sortiert Tabellen nach FK-Abhängigkeiten → Tabellen ohne FK zuerst */
//	private List<String> sortTablesByDependencies(List<String> tables) throws SQLException
//	{
//		List<String> sorted = new ArrayList<>();
//		Set<String> handled = new HashSet<>();
//
//		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + sourceDbPath))
//		{
//			while (sorted.size() < tables.size())
//			{
//				for (String table : tables)
//				{
//					if (handled.contains(table))
//						continue;
//					List<String> deps = getForeignKeys(conn, table);
//					if (handled.containsAll(deps))
//					{
//						sorted.add(table);
//						handled.add(table);
//					}
//				}
//			}
//		}
//		return sorted;
//	}

	/** Liefert alle Tabellen, auf die die gegebene Tabelle per FK verweist */
//	private List<String> getForeignKeys(Connection conn, String table) throws SQLException
//	{
//		List<String> fkTables = new ArrayList<>();
//		String sql = "PRAGMA foreign_key_list(" + table + ")";
//		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql))
//		{
//			while (rs.next())
//				fkTables.add(rs.getString("table"));
//		}
//		return fkTables;
//	}

//==========================================================
//      Spalten in Tabellen ergänzen user_modified
//==========================================================
	/** Alle relevanten Tabellen ermitteln (ohne Blacklist) */
	public List<String> getAllRelevantTables(Connection conn) throws SQLException
	{
		List<String> tables = new ArrayList<>();
		DatabaseMetaData meta = conn.getMetaData();
		try (ResultSet rs = meta.getTables(null, null, "%", new String[] { "TABLE" }))
		{
			while (rs.next())
			{
				String table = rs.getString("TABLE_NAME");
				if (!excludedTables.contains(table))
				{
					tables.add(table);
				}
			}
		}
		return tables;
	}

	/** Prüft alle Tabellen und fügt user_modified hinzu, falls fehlt */
	public void ensureUserModifiedColumnAllTables(Connection conn, List<String> tables) throws SQLException
	{
		for (String table : tables)
		{
			try (Statement stmt = conn.createStatement())
			{
				// Prüfen ob Spalte existiert
				boolean exists = false;
				try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(" + table + ")"))
				{
					while (rs.next())
					{
						String colName = rs.getString("name");
						if ("user_modified".equalsIgnoreCase(colName))
						{
							exists = true;
							break;
						}
					}
				}

				if (!exists)
				{
					stmt.execute("ALTER TABLE " + table + " ADD COLUMN user_modified INTEGER DEFAULT 0");
				}
			}
		}
	}

	/** Setzt alle NULL-Werte in user_modified auf 0 */
	public void migrateUserModifiedNotNull(Connection conn, List<String> tables) throws SQLException
	{
		for (String table : tables)
		{
			try (Statement stmt = conn.createStatement())
			{
				stmt.executeUpdate("UPDATE " + table + " SET user_modified = 0 WHERE user_modified IS NULL");
			}
		}
	}

	public void ensureUserModifiedEverywhere(Connection conn) throws SQLException
	{

		List<String> tables = getAllRelevantTables(conn);

		for (String table : tables)
		{

			// 🔍 Prüfen ob Spalte existiert
			boolean hasColumn = false;

			try (Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery("PRAGMA table_info(" + table + ")"))
			{

				while (rs.next())
				{
					if ("user_modified".equalsIgnoreCase(rs.getString("name")))
					{
						hasColumn = true;
						break;
					}
				}
			}

			// ➕ Falls fehlt → hinzufügen
			if (!hasColumn)
			{
				try (Statement stmt = conn.createStatement())
				{
					stmt.execute("ALTER TABLE " + table + " ADD COLUMN user_modified INTEGER DEFAULT 0");
					System.out.println("➕ user_modified hinzugefügt in " + table);
				}
			}

			// 🧹 NULL-Werte bereinigen
			try (Statement stmt = conn.createStatement())
			{
				stmt.executeUpdate("UPDATE " + table + " SET user_modified = 0 WHERE user_modified IS NULL");
			}
		}
	}

	// ==========================================================
}
