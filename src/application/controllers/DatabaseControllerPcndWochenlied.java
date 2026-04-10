package application.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.db.DBManager;
import application.models.LiederStueckeWoliModel;
import application.models.WochenliedlisteModel;
import application.uicomponents.Msgbox;
import javafx.stage.Stage;

public class DatabaseControllerPcndWochenlied extends DatabaseControllerTemplate {

    private Stage stage;

    public DatabaseControllerPcndWochenlied() throws SQLException {
        super(); // Connection aus DBManager
    }

    private Connection getConn() {
        return DBManager.getConnection();
    }

    // -------- Wochenlieder auslesen --------
    public List<WochenliedlisteModel> getWochenliederAlle() throws SQLException {
        String sql = "SELECT * FROM tblPcndWochenlied ORDER BY wl_rang";
        List<WochenliedlisteModel> result = new ArrayList<>();

        try (PreparedStatement pstmt = getConn().prepareStatement(sql);
             var rs = pstmt.executeQuery()) {

            while (rs.next()) {
                result.add(new WochenliedlisteModel(
                        rs.getString("wl_bez"),
                        rs.getString("wl_rang"),
                        rs.getString("wl_db"),
                        rs.getInt("wl_id")
                ));
            }
        }

        return result;
    }

    // -------- LiedStücke Wochenlied --------
    public List<LiederStueckeWoliModel> getLsWoliListe(String ls) throws SQLException {
        String sql = "SELECT * FROM tblPcndLiedstueckWochenlied WHERE lswl_ls=?";
        List<LiederStueckeWoliModel> result = new ArrayList<>();

        try (PreparedStatement pstmt = getConn().prepareStatement(sql)) {
            pstmt.setString(1, ls);
            try (var rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new LiederStueckeWoliModel(
                            rs.getString("lswl_ls"),
                            rs.getString("lswl_wl"),
                            rs.getInt("lswl_id")
                    ));
                }
            }
        }

        return result;
    }

    // -------- Wochenlied speichern / update --------
    public void setWoliHinzu(String wolibez, String wolirang, boolean neu, String wolialt, String wolidb) throws SQLException {
        String sql;
        if (neu) {
            sql = "INSERT INTO tblPcndWochenlied (wl_id, wl_bez, wl_rang, wl_db, user_modified) " +
                  "VALUES ((SELECT IFNULL(MAX(wl_id),0)+1 FROM tblPcndWochenlied), ?, ?, ?, 1)";
        } else {
            sql = "UPDATE tblPcndWochenlied SET wl_bez=?, wl_rang=?, wl_db=?, user_modified=1 WHERE wl_bez=?";
        }

        try (PreparedStatement pstmt = getConn().prepareStatement(sql)) {
            if (neu) {
                pstmt.setString(1, wolibez);
                pstmt.setString(2, wolirang);
                pstmt.setString(3, wolidb);
            } else {
                pstmt.setString(1, wolibez);
                pstmt.setString(2, wolirang);
                pstmt.setString(3, wolidb);
                pstmt.setString(4, wolialt);
            }

            System.out.println("SQL auszuführen: " + sql);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                Msgbox.warn("Fehler beim Speichern",
                        "Der Feiertag/Festtag existiert bereits und kann nicht doppelt angelegt werden!");
            } else {
                System.err.println("Fehler bei der SQL-Ausführung:");
                System.err.println("SQL: " + sql);
                System.err.println("Fehlermeldung: " + e.getMessage() + " / Code: " + e.getErrorCode());
            }
        }
    }

    // -------- Wochenlied löschen --------
    public void setWoliLoeschen(String wolibez) throws SQLException {
        String sql = "DELETE FROM tblPcndWochenlied WHERE wl_bez=?";
        try (PreparedStatement pstmt = getConn().prepareStatement(sql)) {
            pstmt.setString(1, wolibez);
            pstmt.executeUpdate();
        }
    }

    // -------- LiedStück Wochenlied zuweisen --------
    public void setLiedStckWoliZuweisen(String wolibez, String ststueck) throws SQLException {
        String sql = "INSERT INTO tblPcndLiedstueckWochenlied (lswl_wl, lswl_ls, user_modified) VALUES (?, ?, 1)";
        try (PreparedStatement pstmt = getConn().prepareStatement(sql)) {
            pstmt.setString(1, wolibez);
            pstmt.setString(2, ststueck);
            pstmt.executeUpdate();
        }
    }

    // -------- LiedStück Wochenlied entfernen --------
    public void setLiedStckWoliWegnehmen(String wolibez, String ststueck) throws SQLException {
        String sql = "DELETE FROM tblPcndLiedstueckWochenlied WHERE lswl_wl=? AND lswl_ls=?";
        try (PreparedStatement pstmt = getConn().prepareStatement(sql)) {
            pstmt.setString(1, wolibez);
            pstmt.setString(2, ststueck);
            pstmt.executeUpdate();
        }
    }
    
	public List<WochenliedlisteModel> getWochenliedlisteListeAll() throws Exception
	{
		String sqlStatement = "SELECT * " + "FROM tblPcndWochenlied " + "ORDER BY wl_rang COLLATE NOCASE";

		this.setUpStatement(sqlStatement);
		this.runStatement();

		ResultSet rs = this.getResults();
		List<WochenliedlisteModel> methodResult = extractWochenliedlisteListe(rs);
		return methodResult;
	}
	// --------------------------------------------------------------------------------------------

	private List<WochenliedlisteModel> extractWochenliedlisteListe(ResultSet rs) throws SQLException
	{
		List<WochenliedlisteModel> methodResult = new ArrayList<WochenliedlisteModel>();
		while (rs.next())
		{// Modell muss genua die Argumente der Modelklasse haben
			methodResult.add(new WochenliedlisteModel(
					rs.getString("wl_bez"),
					rs.getString("wl_rang"),
					rs.getString("wl_db"),
					rs.getInt("wl_id")

			));
		}
		return methodResult;
	}
	
	public List<LiederStueckeWoliModel> getStueckinfosWoli(String lstitel) throws Exception
	{
		String sqlStatement = "";
		sqlStatement = "SELECT lswl_ls, lswl_wl, lswl_id FROM tblPcndLiedstueckWochenlied "
				+ "WHERE lswl_ls= '" + lstitel + "' ORDER BY lswl_wl COLLATE NOCASE";
		sqlStatement += ";";

		this.setUpStatement(sqlStatement);
		this.runStatement();
		ResultSet rs = this.getResults();
		List<LiederStueckeWoliModel> methodResult = extractStckWoliinfos(rs);
		return methodResult;
	}

	private List<LiederStueckeWoliModel> extractStckWoliinfos(ResultSet rs) throws SQLException
	{
		List<LiederStueckeWoliModel> methodResult = new ArrayList<LiederStueckeWoliModel>();
		while (rs.next())
		{// Modell muss genua die Argumente der Modelklasse haben
			methodResult.add(new LiederStueckeWoliModel(
					rs.getString("lswl_wl"),
					rs.getString("lswl_ls"),
					rs.getInt("lswl_id")));
		}
		return methodResult;
	}

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
