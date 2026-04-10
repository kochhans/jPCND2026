package application.controllers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.models.EditionenlisteKompaktModel;
import application.models.NotenmappeInhaltModel;
import application.models.NotenmappeModel;

public class DatabaseControllerPcndNotenmappen extends DatabaseControllerTemplate {

    public DatabaseControllerPcndNotenmappen() throws SQLException {
        super(); // Verbindung aus DBManager
    }

    // ---------------- Notenmappen ----------------
    public List<NotenmappeModel> getNomaListeAll() throws SQLException {
        String sql = "SELECT * FROM tblNotenmappe ORDER BY nm_bezeichnung COLLATE NOCASE";
        List<NotenmappeModel> result = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                result.add(new NotenmappeModel(
                        rs.getInt("nm_id"),
                        rs.getString("nm_bezeichnung"),
                        rs.getString("nm_bemerkung")
                ));
            }
        }

        return result;
    }

    public void setNomaListeNeu(String bez, String bem) throws SQLException {
        String sql = "INSERT INTO tblNotenmappe (nm_bezeichnung, nm_bemerkung, user_modified) VALUES (?, ?, 1)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, bez);
            pstmt.setString(2, bem);
            pstmt.executeUpdate();
        }
    }

    public void setNomaListeEdit(String bez, String bem, int id) throws SQLException {
        String sql = "UPDATE tblNotenmappe SET nm_bezeichnung=?, nm_bemerkung=?, user_modified=1 WHERE nm_id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, bez);
            pstmt.setString(2, bem);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        }
    }

    public void setNomaListeDelete(int id) throws SQLException {
        String sql = "DELETE FROM tblNotenmappe WHERE nm_id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // ---------------- Notenmappen-Inhalt ----------------
    public List<NotenmappeInhaltModel> getNomaListeInhaltAll(String filternoma) throws SQLException {
        String sql = "SELECT * FROM tblNotenmappeEdition WHERE nmed_nm_bez LIKE ?";
        List<NotenmappeInhaltModel> result = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, filternoma);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new NotenmappeInhaltModel(
                            rs.getInt("nmed_id"),
                            rs.getString("nmed_e_lt"),
                            rs.getString("nmed_nm_bez"),
                            rs.getString("nmed_bem"),
                            rs.getString("nmed_lager1"),
                            rs.getString("nmed_lager2"),
                            rs.getString("nmed_lager3"),
                            rs.getString("nmed_titelgrafik"),
                            rs.getInt("nmed_anz")
                    ));
                }
            }
        }

        return result;
    }

    public void setNomaInhaltHinzu(String bez, String inhalt, String titelgrafik) throws SQLException {
        String sql = "INSERT INTO tblNotenmappeEdition (nmed_nm_bez, nmed_e_lt, nmed_titelgrafik, user_modified) VALUES (?, ?, ?, 1)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, bez);
            pstmt.setString(2, inhalt);
            pstmt.setString(3, titelgrafik);
            pstmt.executeUpdate();
        }
    }

    public void setNomaInhaltEdit(int id, String anz, String bem, String lager1, String lager2, String lager3) throws SQLException {
        String sql = "UPDATE tblNotenmappeEdition SET nmed_anz=?, nmed_bem=?, nmed_lager1=?, nmed_lager2=?, nmed_lager3=?, user_modified=1 WHERE nmed_id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, anz);
            pstmt.setString(2, bem);
            pstmt.setString(3, lager1);
            pstmt.setString(4, lager2);
            pstmt.setString(5, lager3);
            pstmt.setInt(6, id);
            pstmt.executeUpdate();
        }
    }

    public void setNomaInhaltLoeschen(int nmid) throws SQLException {
        String sql = "DELETE FROM tblNotenmappeEdition WHERE nmed_id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, nmid);
            pstmt.executeUpdate();
        }
    }

    // ---------------- Editionenliste ----------------
    public List<EditionenlisteKompaktModel> getNomaEditionenliste(String filterlt, String filterverlag) throws SQLException {
        String sql = "SELECT * FROM tblPcndEdition WHERE e_lt LIKE ? AND e_verlag LIKE ?";
        List<EditionenlisteKompaktModel> result = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, filterlt + "%");
            pstmt.setString(2, filterverlag + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new EditionenlisteKompaktModel(
                            rs.getString("e_db"),
                            rs.getString("e_lt"),
                            rs.getString("e_titelgrafikpfad"),
                            rs.getString("e_verlag")
                    ));
                }
            }
        }

        return result;
    }

    // ---------------- Gesamte Listen ----------------
    public List<NotenmappeModel> getNomaAll() throws SQLException {
        String sql = "SELECT * FROM tblNotenmappe";
        List<NotenmappeModel> result = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                result.add(new NotenmappeModel(
                        rs.getInt("nm_id"),
                        rs.getString("nm_bezeichnung"),
                        rs.getString("nm_bemerkung")
                ));
            }
        }

        return result;
    }

    public List<NotenmappeInhaltModel> getNomainhaltAlle() throws SQLException {
        String sql = "SELECT * FROM tblNotenmappeEdition";
        List<NotenmappeInhaltModel> result = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                result.add(new NotenmappeInhaltModel(
                        rs.getInt("nmed_id"),
                        rs.getString("nmed_e_lt"),
                        rs.getString("nmed_nm_bez"),
                        rs.getString("nmed_bem"),
                        rs.getString("nmed_lager1"),
                        rs.getString("nmed_lager2"),
                        rs.getString("nmed_lager3"),
                        rs.getString("nmed_titelgrafik"),
                        rs.getInt("nmed_anz")
                ));
            }
        }

        return result;
    }
}
