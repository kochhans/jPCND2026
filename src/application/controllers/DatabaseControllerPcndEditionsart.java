package application.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.db.DBManager;
import application.models.EditionsartenModel;
import application.uicomponents.Msgbox;

public class DatabaseControllerPcndEditionsart {

    // ---------------- Edart speichern / update ----------------
    public void setEdartSpeichern(boolean neu, String eartbez, String eartdb, String eartbezalt) throws SQLException {
        String sql;
        if (neu) {
            sql = "INSERT INTO tblPcndEditionart (ea_id, ea_bez, ea_db, user_modified) " +
                  "VALUES ((SELECT IFNULL(MAX(ea_id),0)+1 FROM tblPcndEditionart), ?, ?, 1)";
        } else {
            sql = "UPDATE tblPcndEditionart SET ea_bez=?, ea_db=?, user_modified=1 WHERE ea_bez=?";
        }

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (neu) {
                pstmt.setString(1, eartbez);
                pstmt.setString(2, eartdb);
            } else {
                pstmt.setString(1, eartbez);
                pstmt.setString(2, eartdb);
                pstmt.setString(3, eartbezalt);
            }

            System.out.println("SQL auszuführen: " + sql);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                Msgbox.warn("Fehler beim Speichern",
                        "Die Bezeichnung existiert bereits und kann nicht doppelt angelegt werden!");
            } else {
                System.err.println("Fehler bei der SQL-Ausführung:");
                System.err.println("SQL: " + sql);
                System.err.println("Fehlermeldung: " + e.getMessage() + " / Code: " + e.getErrorCode());
            }
        }
    }

    // ---------------- Datensatz löschen ----------------
    public void setEdartLoeschen(String edartbez) throws SQLException {
        String sql = "DELETE FROM tblPcndEditionart WHERE ea_bez=?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, edartbez);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                Msgbox.warn("Editionsart löschen ...",
                        "ACHTUNG: Diese Editionsart (" + edartbez + ") ist noch mindestens einer Notenausgabe zugewiesen!");
            } else {
                e.printStackTrace();
            }
        }
    }

    // ---------------- Alle Datensätze abrufen ----------------
    public List<EditionsartenModel> getEditionsartListeAlle() throws SQLException {
        String sql = "SELECT * FROM tblPcndEditionart ORDER BY ea_bez COLLATE NOCASE";

        List<EditionsartenModel> result = new ArrayList<>();

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                result.add(new EditionsartenModel(
                        rs.getString("ea_bez"),
                        rs.getString("ea_db"),
                        rs.getInt("ea_id")));
            }
        }

        return result;
    }
}
