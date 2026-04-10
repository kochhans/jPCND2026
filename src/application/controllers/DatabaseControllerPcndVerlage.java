package application.controllers;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.db.DBManager;
import application.models.VerlaglisteModel;
import application.uicomponents.Msgbox;
import application.utils.AktuellesDatum;


//--------------------------------------------------------------------------------
//   Datenbank starten
//--------------------------------------------------------------------------------
public class DatabaseControllerPcndVerlage extends DatabaseControllerTemplate {

    public DatabaseControllerPcndVerlage() throws SQLException {
        super(); // Connection aus DBManager
    }
   
//    
    @SuppressWarnings("unused")
	private Connection getConn() {
        return DBManager.getConnection();
    }

    // ---------------- Verlag speichern / update ----------------
    public void setVerlagSpeichern(boolean neu, String vverlag, String vort, String vbem,
                                   String verfasst, String vdb, String vverlagalt, String vid) throws SQLException {

        String sql;
        verfasst = new AktuellesDatum().getDateAsString();

        if (neu) {
            sql = "INSERT INTO tblPcndVerlag (v_id, v_verlag, v_ort, v_bem, v_erfasst, v_db, user_modified) " +
                  "VALUES ((SELECT IFNULL(MAX(v_id),0)+1 FROM tblPcndVerlag), ?, ?, ?, ?, ?, 1)";
        } else {
            sql = "UPDATE tblPcndVerlag SET v_verlag=?, v_ort=?, v_bem=?, v_erfasst=?, v_db=?, user_modified=?  WHERE v_verlag=?";
        }

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (neu) {
                pstmt.setString(1, vverlag);
                pstmt.setString(2, vort);
                pstmt.setString(3, vbem);
                pstmt.setString(4, verfasst);
                pstmt.setString(5, vdb);
            } else {
                pstmt.setString(1, vverlag);
                pstmt.setString(2, vort);
                pstmt.setString(3, vbem);
                pstmt.setString(4, verfasst);
                pstmt.setString(5, vdb);
                pstmt.setInt(6, 1);
                pstmt.setString(7, vverlagalt);
            }

            System.out.println("SQL auszuführen: " + sql);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                Msgbox.warn("Fehler beim Speichern",
                        "Der Verlag existiert bereits und kann nicht doppelt angelegt werden!");
            } else {
                System.err.println("Fehler bei der SQL-Ausführung:");
                System.err.println("SQL: " + sql);
                System.err.println("Fehlermeldung: " + e.getMessage() + " / Code: " + e.getErrorCode());
            }
        }
    }

    // ---------------- Datensatz löschen ----------------
    public void setVerlagLoeschen(String verlag) throws SQLException {
        String sql = "DELETE FROM tblPcndVerlag WHERE v_verlag=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, verlag);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                Msgbox.warn("Verlag löschen ...",
                        "ACHTUNG: Der Verlag " + verlag + " ist noch mindestens einer Notenausgabe zugewiesen und kann daher nicht gelöscht werden!\n" +
                        "Filtern Sie in der Notenausgaben-Tabelle und ändern Sie bei Bedarf dort die Zuordnung ab.");
            } else {
                e.printStackTrace();
                System.out.println(e.getErrorCode());
                System.out.println(e.getMessage());
            }
        }
    }
	public List<VerlaglisteModel> getVerlaglisteListeAlle() throws Exception
	{
		String sqlStatement = "SELECT * " + "FROM tblPcndVerlag  ORDER BY v_verlag COLLATE NOCASE";

		this.setUpStatement(sqlStatement);
		this.runStatement();

		ResultSet rs = this.getResults();
		List<VerlaglisteModel> methodResult = extractVerlaglisteListe(rs);
		return methodResult;
	}

	private List<VerlaglisteModel> extractVerlaglisteListe(ResultSet rs) throws SQLException
	{
		List<VerlaglisteModel> methodResult = new ArrayList<VerlaglisteModel>();
		while (rs.next())
		{// Modell muss genau die Argumente der Modelklasse haben
			methodResult.add(new VerlaglisteModel(
					rs.getString("v_verlag"),
					rs.getString("v_ort"),
					rs.getString("v_bem"),
					rs.getString("v_erfasst"),
					rs.getString("v_db"),
					rs.getInt("v_id")));
		}
		return methodResult;
	}

}
