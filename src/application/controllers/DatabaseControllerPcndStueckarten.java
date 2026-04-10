package application.controllers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.db.DBManager;
import application.models.StueckartlisteModel;
import application.uicomponents.Msgbox;
import javafx.stage.Stage;

/**
 * Controller für Stückarten-Daten
 */
public class DatabaseControllerPcndStueckarten extends DatabaseControllerTemplate {

    private Stage stage;

    // Konstruktor
    public DatabaseControllerPcndStueckarten() throws SQLException {
        super(); // Verbindung aus Template / DBManager
    }

    // 🔑 DB-Verbindung explizit initialisieren
    public void initConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DBManager.getConnection(); // zentrale Connection nutzen
            System.out.println("DB-Verbindung hergestellt: " + connection);
        }
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // ===============================
    // Stückarten - alles auslesen
    // ===============================
    public List<StueckartlisteModel> getStueckartListeAll() throws SQLException {
        String sql = "SELECT * FROM tblPcndStueckart ORDER BY sar_bez COLLATE NOCASE";
        PreparedStatement stmt = connection.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        List<StueckartlisteModel> result = extractStueckartListe(rs);
        rs.close();
        stmt.close();
        return result;
    }

    private List<StueckartlisteModel> extractStueckartListe(ResultSet rs) throws SQLException {
        List<StueckartlisteModel> result = new ArrayList<>();
        while (rs.next()) {
            result.add(new StueckartlisteModel(
                    rs.getString("sar_bez"),
                    rs.getString("sar_db"),
                    rs.getInt("sar_id")
            ));
        }
        return result;
    }

    // ===============================
    // Datensatz speichern
    // ===============================
    public void setStckartbezSpeichern(String bez, Boolean neu, String altBez, String db) throws SQLException {
        String sql = null;
        PreparedStatement pstmt = null;

        if (neu) {
            sql = "INSERT INTO tblPcndStueckart (sar_id, sar_bez, sar_db, user_modified) " +
                  "VALUES ((SELECT IFNULL(MAX(sar_id),0)+1 FROM tblPcndStueckart), ?, ?, 1)";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, bez);
            pstmt.setString(2, db);
        } else {
            sql = "UPDATE tblPcndStueckart SET sar_bez = ?, sar_db = ?, user_modified=1 WHERE sar_bez = ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, bez);
            pstmt.setString(2, db);
            pstmt.setString(3, altBez);
        }

        try {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                Msgbox.warn("Fehler beim Speichern",
                        "Die Stückart existiert bereits und kann nicht doppelt angelegt werden!");
            } else {
                System.err.println("Fehler bei SQL: " + sql + " - " + e.getMessage());
            }
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }

    // ===============================
    // Datensatz löschen
    // ===============================
    public void setStckartLoeschen(String stckartbez) throws SQLException {
        String sql = "DELETE FROM tblPcndStueckart WHERE sar_bez = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, stckartbez);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                Msgbox.warn("Stück-Art löschen ...",
                        "ACHTUNG: Die Stück-Art " + stckartbez +
                        " ist noch mindestens einem Literatureintrag zugewiesen und kann daher nicht gelöscht werden!");
            } else {
                throw e;
            }
        }
    }
}
