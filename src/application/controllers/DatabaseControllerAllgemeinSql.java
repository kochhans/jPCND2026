package application.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import application.db.DBManager;
import application.models.ProgrammversionenModel;

public class DatabaseControllerAllgemeinSql {

    // ---------------- DB VERSIONEN ----------------
    public ProgrammversionenModel getHoechsteVersion() {
        String sql = "SELECT * FROM tblAdbVersion ORDER BY ver_id DESC LIMIT 1";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String verNr = rs.getString("ver_nr");
                String verDat = rs.getString("ver_dat");
                //System.out.println("Gefundene Version: " + verNr + " / " + verDat);
                return new ProgrammversionenModel(verNr, verDat);
            } else {
                System.out.println("Keine Version in tblAdbVersion gefunden");
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public ProgrammversionenModel getHoechsteVersionStruktur() {
        String sql = "SELECT * FROM tblZDatenbankstruktur ORDER BY dbv_id DESC LIMIT 1";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String verNr = rs.getString("dbv_version");
                String verDat = rs.getString("dbv_datum");
                //System.out.println("Gefundene Version: " + verNr + " / " + verDat);
                return new ProgrammversionenModel(verNr, verDat);
            } else {
                System.out.println("Keine Version in tblZDatenbankstruktur gefunden");
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Weitere Methoden folgen demselben Muster:
    // - Connection lokal aus DBManager
    // - PreparedStatement in try-with-resources
    // - ResultSet sofort verarbeiten
}
