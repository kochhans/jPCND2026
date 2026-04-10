package application.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.db.DBManager;
import application.models.AutorenlisteModel;
import application.uicomponents.Msgbox;
import application.utils.ToolsDialogHelper;

public class DatabaseControllerPcndAutoren {

    // ------- Autoren auslesen
    public List<AutorenlisteModel> getAutorenAlle() throws SQLException {
        String sql = "SELECT * FROM tblPcndAutor ORDER BY a_autor COLLATE NOCASE, a_nname, a_vname";
        List<AutorenlisteModel> resultList = new ArrayList<>();

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                resultList.add(new AutorenlisteModel(
                        rs.getString("a_autor"),
                        rs.getString("a_nname"),
                        rs.getString("a_vname"),
                        rs.getString("a_gjahr"),
                        rs.getString("a_tjahr"),
                        rs.getString("a_sonst"),
                        rs.getString("a_db"),
                        rs.getString("a_id")));
            }
        }

        return resultList;
    }

    // ---------- Datensatz speichern / update
    public void setAutorHinzu(Boolean neu, String nname, String vname, String gjahr,
                              String tjahr, String sonst, String db, String aautor, String aautoralt) {

        String sql;
        if (neu) {
            sql = "INSERT INTO tblPcndAutor (a_id, a_nname, a_vname, a_gjahr, a_tjahr, a_sonst, a_db, a_autor, user_modified) " +
                  "VALUES ((SELECT IFNULL(MAX(a_id),0)+1 FROM tblPcndAutor),?,?,?,?,?,?,?, 1)";
        } else {
            sql = "UPDATE tblPcndAutor SET a_nname=?, a_vname=?, a_gjahr=?, a_tjahr=?, a_sonst=?, a_db=?, a_autor=?, user_modified=1" +
                  "WHERE a_autor=?";
        }

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (neu) {
                pstmt.setString(1, nname);
                pstmt.setString(2, vname);
                pstmt.setString(3, gjahr);
                pstmt.setString(4, tjahr);
                pstmt.setString(5, sonst);
                pstmt.setString(6, db);
                pstmt.setString(7, aautor);
            } else {
                pstmt.setString(1, nname);
                pstmt.setString(2, vname);
                pstmt.setString(3, gjahr);
                pstmt.setString(4, tjahr);
                pstmt.setString(5, sonst);
                pstmt.setString(6, db);
                pstmt.setString(7, aautor);
                pstmt.setString(8, aautoralt);
            }

            pstmt.executeUpdate();

        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                ToolsDialogHelper.showError("Fehler beim Speichern",
                        "Autor/in - Name existiert bereits und kann nicht doppelt angelegt werden!");
            } else {
                System.err.println("Fehler bei der SQL-Ausführung:");
                System.err.println("SQL: " + sql);
                System.err.println("Fehlermeldung: " + e.getMessage() + " / Code: " + e.getErrorCode());
            }
        }
    }

    // ---------- Datensatz löschen
    public void setAutorLoeschen(String aautor) {
        String sql = "DELETE FROM tblPcndAutor WHERE a_autor=?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, aautor);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                Msgbox.error("Fehler beim Löschen",
                        "Autor ist noch mindestens einem Stück oder einem Literaturtitel zugewiesen. \n" +
                        "Diese Zuweisungen müssen vorher entfernt werden");
            } else {
                System.err.println("Fehler bei der SQL-Ausführung:");
                System.err.println("SQL: " + sql);
                System.err.println("Fehlermeldung: " + e.getMessage() + " / Code: " + e.getErrorCode());
            }
        }
    }
}
