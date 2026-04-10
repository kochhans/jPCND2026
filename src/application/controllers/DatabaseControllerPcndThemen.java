package application.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import application.db.DBManager;
import application.models.ThemenlisteModel;
import application.models.ThemenlisteStueckModel;
import application.utils.ToolsDialogHelper;
import javafx.stage.Stage;

public class DatabaseControllerPcndThemen extends DatabaseControllerTemplate {

    private Stage stage;

    public DatabaseControllerPcndThemen() throws SQLException {
        super(); // Connection aus DBManager
    }

    private Connection getConn() {
        return DBManager.getConnection();
    }

    // ---------------- Themen auslesen ----------------
    public List<ThemenlisteModel> getThemenListeAll() throws SQLException {
        String sql = "SELECT * FROM tblPcndThema";
        List<ThemenlisteModel> result = new ArrayList<>();

        try (PreparedStatement pstmt = getConn().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                result.add(new ThemenlisteModel(
                        rs.getString("th_bez"),
                        rs.getString("th_db"),
                        rs.getInt("th_id")
                ));
            }
        }

        return result;
    }

    // ---------------- Datensatz speichern / update ----------------
    public void setThemaHinzu(boolean neu, String themabez, String themaalt, String db, int id) throws SQLException {
        String sql;

        if (neu) {
            sql = "INSERT INTO tblPcndThema (th_id, th_bez, th_db, user_modified) " +
                  "VALUES ((SELECT IFNULL(MAX(th_id),0)+1 FROM tblPcndThema), ?, ?, 1)";
        } else {
            sql = "UPDATE tblPcndThema SET th_bez=?, th_db=?, user_modified=1 WHERE th_bez=?";
        }

        try (PreparedStatement pstmt = getConn().prepareStatement(sql)) {
            if (neu) {
                pstmt.setString(1, themabez);
                pstmt.setString(2, db);
            } else {
                pstmt.setString(1, themabez);
                pstmt.setString(2, db);
                pstmt.setString(3, themaalt);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                ToolsDialogHelper.showError("Fehler beim Speichern",
                        "Das Thema existiert bereits und kann nicht doppelt angelegt werden!");
            } else {
                System.err.println("Fehler bei der SQL-Ausführung:");
                System.err.println("SQL: " + sql);
                System.err.println("Fehlermeldung: " + e.getMessage() + " / Code: " + e.getErrorCode());
            }
        }
    }

//    // ---------------- Datensatz löschen ----------------
//    public void setThemaLoeschen(String themabez) throws SQLException {
//        String sql = "DELETE FROM tblPcndThema WHERE th_bez=?";
//        try (PreparedStatement pstmt = getConn().prepareStatement(sql)) {
//            pstmt.setString(1, themabez);
//            pstmt.executeUpdate();
//        }
//    }
    
    
	// --------------------------------------------------------------------------------------------
	// Alle Themen laden
	// --------------------------------------------------------------------------------------------
	public List<ThemenlisteModel> getAlleThemen() throws SQLException
	{

		String sql = """
            SELECT th_bez, th_db, th_id
            FROM tblPcndThema
            ORDER BY th_bez COLLATE NOCASE
            """;

		List<ThemenlisteModel> result = new ArrayList<>();

		try (PreparedStatement ps = getConn().prepareStatement(sql);
				ResultSet rs = ps.executeQuery())
		{

			while (rs.next())
			{
				result.add(new ThemenlisteModel(
						rs.getString("th_bez"),
						rs.getString("th_db"),
						rs.getInt("th_id")));
			}
		}
		return result;
	}

	// --------------------------------------------------------------------------------------------
	// Themen eines Liedes / Stückes laden
	// --------------------------------------------------------------------------------------------
	public List<ThemenlisteStueckModel> getThemenFuerStueck(String stueck) throws SQLException
	{

		Objects.requireNonNull(stueck, "stueck darf nicht null sein");
		if (stueck.isBlank())
		{
			throw new IllegalArgumentException("stueck darf nicht leer sein");
		}

		String sql = """
            SELECT lsth_th
            FROM tblPcndLiedstueckThema
            WHERE lsth_ls = ?
            ORDER BY lsth_th COLLATE NOCASE
            """;

		List<ThemenlisteStueckModel> result = new ArrayList<>();

		try (PreparedStatement ps = getConn().prepareStatement(sql))
		{
			ps.setString(1, stueck);

			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					result.add(new ThemenlisteStueckModel(
							rs.getString("lsth_th")));
				}
			}
		}
		return result;
	}

	// --------------------------------------------------------------------------------------------
	// Thema einem Stück zuweisen
	// --------------------------------------------------------------------------------------------
	public void addThemaZuStueck(String thema, String stueck) throws SQLException
	{
		String sql = "";
		try
		{
			Objects.requireNonNull(thema, "thema darf nicht null sein");
			Objects.requireNonNull(stueck, "stueck darf nicht null sein");

			 sql = """
			    INSERT INTO tblPcndLiedstueckThema (lsth_ls, lsth_th, user_modified)
			    VALUES (?, ?, 1)
			    """;

			try (PreparedStatement ps = getConn().prepareStatement(sql))
			{
				ps.setString(1, stueck);
				ps.setString(2, thema);
				ps.executeUpdate();
			}
		}
		catch (SQLException e)
		{
			if (e.getErrorCode() == 19)
			{
				ToolsDialogHelper.showError("Fehler beim Speichern",
						"Das Thema wuede dem Stück bereits zugewiesen!");
			}

			else
			{
				System.err.println("Fehler bei der SQL-Ausführung:");
				System.err.println("SQL: " + sql);
				System.err.println("Fehlermeldung: " + e.getMessage() + " / Code: " + e.getErrorCode());
			}
		}

	}

	// --------------------------------------------------------------------------------------------
	// Thema von Stück entfernen
	// --------------------------------------------------------------------------------------------
	public void removeThemaVonStueck(String thema, String stueck) throws SQLException
	{

		Objects.requireNonNull(thema, "thema darf nicht null sein");
		Objects.requireNonNull(stueck, "stueck darf nicht null sein");

		String sql = """
            DELETE FROM tblPcndLiedstueckThema
            WHERE lsth_th = ? AND lsth_ls = ?
            """;

		try (PreparedStatement ps = getConn().prepareStatement(sql))
		{
			ps.setString(1, thema);
			ps.setString(2, stueck);
			ps.executeUpdate();
		}
	}

	// --------------------------------------------------------------------------------------------
	// Thema komplett löschen
	// --------------------------------------------------------------------------------------------
	public void deleteThema(String themabez) throws SQLException
	{

		Objects.requireNonNull(themabez, "themabez darf nicht null sein");

		String sql = """
            DELETE FROM tblPcndThema
            WHERE th_bez = ?
            """;

		try (PreparedStatement ps = getConn().prepareStatement(sql))
		{
			ps.setString(1, themabez);
			ps.executeUpdate();
		}
	}

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
