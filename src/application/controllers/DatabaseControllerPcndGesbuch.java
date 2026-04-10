package application.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.db.DBManager;
import application.models.GesangbuchModel;
import application.models.LiederStueckeGesbModel;
import application.uicomponents.Msgbox;

public class DatabaseControllerPcndGesbuch {

    // ------- Gesangbuch auslesen
    public List<GesangbuchModel> getGbListeAll() throws SQLException {
        String sql = "SELECT * FROM tblPcndGesangbuch";
        List<GesangbuchModel> resultList = new ArrayList<>();

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                resultList.add(new GesangbuchModel(
                        rs.getString("g_bez"),
                        rs.getString("g_kurz"),
                        rs.getString("g_db"),
                        rs.getString("g_bem")
                ));
            }
        }

        return resultList;
    }

    // ---------- Datensatz speichern / update
    public void setGbHinzu(String gbbez, String gbkurz, String gbdb, String gbbem, boolean neu, String gbalt) {
        String sql = null;

        try (Connection conn = DBManager.getConnection()) {

            PreparedStatement pstmt;
            if (neu) {
                sql = "INSERT INTO tblPcndGesangbuch (g_id, g_bez, g_kurz, g_db, g_bem, user_modified) " +
                      "VALUES ((SELECT IFNULL(MAX(g_id),0)+1 FROM tblPcndGesangbuch), ?, ?, ?, ?, 1)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, gbbez);
                pstmt.setString(2, gbkurz);
                pstmt.setString(3, gbdb);
                pstmt.setString(4, gbbem);
            } else {
                sql = "UPDATE tblPcndGesangbuch SET g_bez=?, g_kurz=?, g_db=?, g_bem=?, user_modified=1 WHERE g_bez=?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, gbbez);
                pstmt.setString(2, gbkurz);
                pstmt.setString(3, gbdb);
                pstmt.setString(4, gbbem);
                pstmt.setString(5, gbalt);
            }

            pstmt.executeUpdate();

        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                Msgbox.warn("Fehler beim Speichern",
                    "Das Gesangbuch bzw. das Kürzel existiert bereits und kann nicht doppelt angelegt werden!");
            } else {
                e.printStackTrace();
            }
        }
    }

    // ---------- Datensatz löschen
    public void setGesbuchLoeschen(String gesbuch) {
        String sql = "DELETE FROM tblPcndGesangbuch WHERE g_bez=?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, gesbuch);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------- LiedStückGesangbuch
    public List<LiederStueckeGesbModel> getLsGbuchListe(String ls) throws SQLException {
        String sql = "SELECT * FROM tblPcndLiedstueckGesangbuch WHERE lsgb_ls=?";
        List<LiederStueckeGesbModel> resultList = new ArrayList<>();

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ls);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    resultList.add(new LiederStueckeGesbModel(
                            rs.getString("lsgb_g"),
                            rs.getString("lsgb_nr"),
                            rs.getInt("lsgb_nrsort"),
                            rs.getString("lsgb_ls"),
                            rs.getInt("lsgb_id")
                    ));
                }
            }
        }

        return resultList;
    }

    public void setLiedStckGesbuchNeu(String gbuch, String gnr, String ststueck, Integer gnrsort) throws SQLException {
        String sql = "INSERT INTO tblPcndLiedstueckGesangbuch (lsgb_ls, lsgb_g, lsgb_nr, lsgb_nrsort, user_modified) VALUES (?, ?, ?, ?, 1)";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ststueck);
            pstmt.setString(2, gbuch);
            pstmt.setString(3, gnr);
            pstmt.setInt(4, gnrsort);
            pstmt.executeUpdate();
        }
    }

    public void setLiedStckGesbuchWegnehmen(int lsgbid) throws SQLException {
        String sql = "DELETE FROM tblPcndLiedstueckGesangbuch WHERE lsgb_id=?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, lsgbid);
            pstmt.executeUpdate();
        }
    }

	public List<GesangbuchModel> getGesAll()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
