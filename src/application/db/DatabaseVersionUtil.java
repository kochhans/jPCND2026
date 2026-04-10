package application.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import application.ValuesGlobals;
import application.controllers.DatabaseControllerAllgemeinSql;
import application.models.ProgrammversionenModel;

public final class DatabaseVersionUtil {

	    private DatabaseVersionUtil() {}

	    public static String getLocalDatabaseVersion() {
	        try {
	        	DatabaseControllerAllgemeinSql db = new DatabaseControllerAllgemeinSql();
	            //DatabaseControllerAllgemeinSql db = new DatabaseControllerGeneral();
	            ProgrammversionenModel v = db.getHoechsteVersion();
	            //db.dbStop(); //hinzugefügt
	            return (v != null) ? v.getVernr() : "0.0.0";
	        } catch (Exception e) {
	            return "0.0.0";
	        }
	    }
	    public static String getLocalDatabaseStructureVersion() {
	        try {
	        	DatabaseControllerAllgemeinSql db = new DatabaseControllerAllgemeinSql();
	            //DatabaseControllerAllgemeinSql db = new DatabaseControllerGeneral();
	            ProgrammversionenModel v = db.getHoechsteVersionStruktur();
	            //db.dbStop(); //hinzugefügt
	            return (v != null) ? v.getVernr() : "0.0.0";
	        } catch (Exception e) {
	            return "0.0.0";
	        }
	    }
	    public static boolean isVersionLess(String current, String target) {
	        String[] cParts = current.split("\\.");
	        String[] tParts = target.split("\\.");

	        int length = Math.max(cParts.length, tParts.length);

	        for (int i = 0; i < length; i++) {
	            int c = i < cParts.length ? Integer.parseInt(cParts[i]) : 0;
	            int t = i < tParts.length ? Integer.parseInt(tParts[i]) : 0;

	            if (c < t) return true;
	            if (c > t) return false;
	        }
	        return false;
	    }
	    public static void setDatabaseStructureVersion(String newVersion) {
	        String sql = "INSERT INTO tblZDatenbankstruktur (dbv_version, dbv_datum) VALUES (?, datetime('now'))";

	        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + ValuesGlobals.dbPfad);
	             PreparedStatement stmt = conn.prepareStatement(sql)) {

	            stmt.setString(1, newVersion);
	            stmt.executeUpdate();

	            System.out.println("📌 DB-Version gesetzt auf " + newVersion);

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}

