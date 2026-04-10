package application.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.db.DBManager;
import application.models.BibellisteModel;
import application.models.LiederStueckeBibelModel;
import application.uicomponents.Msgbox;

public class DatabaseControllerPcndBibel {

    // ------- Bibelbuecher auslesen
    public List<BibellisteModel> getBibelListeAll() throws SQLException {
        String sql = "SELECT * FROM tblPcndBibel ORDER BY b_birang";
        List<BibellisteModel> resultList = new ArrayList<>();

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                resultList.add(new BibellisteModel(
                        rs.getString("b_buch"),
                        rs.getString("b_kuerzel"),
                        rs.getString("b_birang"),
                        rs.getString("b_db")
                ));
            }
        }

        return resultList;
    }

    // ---------- Datensatz speichern / update
    public void setBuchHinzu(String buchbez, String buchkurz, String buchrang, boolean neu, String buchalt, String bidb) {
        String sql = null;

        try (Connection conn = DBManager.getConnection()) {

            PreparedStatement pstmt;
            if (neu) {
                sql = "INSERT INTO tblPcndBibel (b_id, b_buch, b_kuerzel, b_birang, b_db, user_modified) " +
                      "VALUES ((SELECT IFNULL(MAX(b_id), 0) + 1 FROM tblPcndBibel), ?, ?, ?, ?, 1)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, buchbez);
                pstmt.setString(2, buchkurz);
                pstmt.setString(3, buchrang);
                pstmt.setString(4, bidb);
            } else {
                sql = "UPDATE tblPcndBibel SET b_buch = ?, b_kuerzel = ?, b_birang = ?, b_db = ?, user_modified=1 WHERE b_buch = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, buchbez);
                pstmt.setString(2, buchkurz);
                pstmt.setString(3, buchrang);
                pstmt.setString(4, bidb);
                pstmt.setString(5, buchalt);
            }

            System.out.println("SQL auszuführen: " + sql);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                Msgbox.warn("Fehler beim Speichern",
                        "Das Bibel-Buch existiert bereits und kann nicht doppelt angelegt werden!");
            } else {
                System.err.println("SQL: " + sql);
                System.err.println("Fehlermeldung: " + e.getMessage() + " / Code: " + e.getErrorCode());
            }
        }
    }

    // ---------- Datensatz löschen
    public void setBibelbuchLoeschen(String bibuch) {
        String sql = "DELETE FROM tblPcndBibel WHERE b_buch = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bibuch);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------- Liedstueck-Bibel Beziehung hinzufügen
    public void setLiedStckBibelNeu(String bibuch, String biverse, String ststueck) throws SQLException {
        String sql = "INSERT INTO tblPcndLiedstueckBibel (lsbi_ls, lsbi_bi, lsbi_versangabe, user_modified) VALUES (?, ?, ?, 1)";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ststueck);
            pstmt.setString(2, bibuch);
            pstmt.setString(3, biverse);
            pstmt.executeUpdate();
        }
    }

    // ---------- Liedstueck-Bibel Beziehung löschen
    public void setLiedStckBibelWegnehmen(String biid, String ststueck) throws SQLException {
        String sql = "DELETE FROM tblPcndLiedstueckBibel WHERE lsbi_id = ? AND lsbi_ls = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, biid);
            pstmt.setString(2, ststueck);
            pstmt.executeUpdate();
        }
    }

    // ---------- Zugeordnete Bibelstellen zu Liedstueck
    public List<LiederStueckeBibelModel> getBibellisteZugew(String liedstueck) throws SQLException {
        String sql = "SELECT lsbi_id, lsbi_ls, lsbi_bi, b_birang, " +
                     "IFNULL(lsbi_bi, '') || ' ' || IFNULL(lsbi_versangabe, '') AS lsbi_versang, " +
                     "lsbi_versangabe " +
                     "FROM tblPcndLiedstueckBibel " +
                     "INNER JOIN tblPcndBibel ON tblPcndBibel.b_buch = tblPcndLiedstueckBibel.lsbi_bi " +
                     "WHERE lsbi_ls = ? " +
                     "ORDER BY b_birang, lsbi_versang COLLATE NOCASE";

        List<LiederStueckeBibelModel> result = new ArrayList<>();

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, liedstueck);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new LiederStueckeBibelModel(
                            rs.getString("lsbi_versang"),
                            rs.getString("lsbi_id"),
                            rs.getString("lsbi_ls")
                    ));
                }
            }
        }

        return result;
    }
}
