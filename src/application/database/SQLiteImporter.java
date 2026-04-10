package application.database;

import application.models.AktionenListeModel;
import application.models.AktionenListePositionenModel;
import application.models.CvwPersonenModel;
import application.db.DBManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class SQLiteImporter {

    // ---------------- Aktionen importieren ----------------
    public static void insertAktionen(List<AktionenListeModel> list) throws SQLException {
        String sql = "INSERT INTO tblChoraktionen (" +
                "ca_id, ca_akttyp, ca_datum, ca_beschreibung, ca_treffpunkt, ca_beginn, " +
                "ca_anwesend, ca_bemerkung, ca_verantwortlich, ca_gruppe, ca_aktionsort, " +
                "ca_veranstalter, ca_auftrittstermin, ca_gema) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (AktionenListeModel a : list) {
                pstmt.setInt(1, a.getCaid());
                pstmt.setString(2, a.getCaakttyp());
                pstmt.setString(3, a.getCadatum() != null ? a.getCadatum().toString() : null);
                pstmt.setString(4, a.getCabeschreibung());
                pstmt.setString(5, a.getCatreffpunkt() != null ? a.getCatreffpunkt().toString() : null);
                pstmt.setString(6, a.getCabeginn() != null ? a.getCabeginn().toString() : null);
                pstmt.setInt(7, a.getCaanwesend());
                pstmt.setString(8, a.getCabemerkung());
                pstmt.setString(9, a.getCaverantwortlich());
                pstmt.setString(10, a.getCagruppe());
                pstmt.setString(11, a.getCaaktionsort());
                pstmt.setString(12, a.getCaveranstalter());
                pstmt.setInt(13, a.getCaauftrittstermin());
                pstmt.setInt(14, a.getCagema());

                pstmt.addBatch();
            }

            pstmt.executeBatch();
        }
    }

    // ---------------- Capo-Positionen importieren ----------------
    public static void insertCapoPositionen(List<AktionenListePositionenModel> list) throws SQLException {
        String sql = "INSERT INTO tblChoraktionenPositionen (" +
                "capo_id, capo_pos, capo_bem, capo_ca_id, capo_stcktitel,"
                + " capo_edition, capo_art, capo_dauermin, capo_dauersec, capo_sonstiges,"
                + " capo_nr, capo_seite, capo_besetzung, capo_tonart, capo_komponist,"
                + " capo_zeilentyp, capo_titelbild, capo_bearbeiter, capo_auspcnd, capo_zwischentext,"
                + " capo_lit_ic) " +
                "VALUES ("
                + " ?, ?, ?, ?, ?,"
                + " ?, ?, ?, ?, ?,"
                + " ?, ?, ?, ?, ?,"
                + " ?, ?, ?, ?, ?,"
                + " ?)";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (AktionenListePositionenModel p : list) {
                pstmt.setInt(1, p.getCapoId());
                pstmt.setInt(2, p.getCapoPos());
                pstmt.setString(3, p.getCapoBem());
                pstmt.setInt(4, p.getCapoCaId());
                pstmt.setString(5, p.getCapoStcktitel());
                pstmt.setString(6, p.getCapoEdition());
                pstmt.setString(7, p.getCapoArt());
                pstmt.setInt(8, p.getCapoDauermin());
                pstmt.setInt(9, p.getCapoDauersec());
                pstmt.setString(10, p.getCapoSonstiges());
                pstmt.setString(11, p.getCapoNr());
                pstmt.setString(12, p.getCapoSeite());               
                pstmt.setString(13, p.getCapoBesetzung());
                pstmt.setString(14, p.getCapoTonart());
                pstmt.setString(15, p.getCapoKomponist());
                pstmt.setString(16, p.getCapoZeilentyp());
                pstmt.setString(17, p.getCapoTitelbild());
                pstmt.setString(18, p.getCapoBearbeiter());
                pstmt.setInt(19, p.isCapoLiteratur());
                pstmt.setInt(20, p.isCapoZwischentext());
                pstmt.setInt(21, p.getCapoLitId());

                pstmt.addBatch();
            }

            pstmt.executeBatch();
        }
    }

    
    
    // ---------------- Alles auf einmal importieren ----------------
    public static void importAktionenMitPositionen(
            List<AktionenListeModel> aktionen,
            List<AktionenListePositionenModel> positionen) throws SQLException {

        // Zuerst Aktionen
        insertAktionen(aktionen);

        // Danach die zugehörigen Positionen
        insertCapoPositionen(positionen);
    }
    
    
    public static void insertPositionen(List<AktionenListePositionenModel> list) throws SQLException {
        if (list == null || list.isEmpty()) return;

        String sql = "INSERT INTO tblChoraktionenPositionen (" +
                "capo_id, capo_pos, capo_bem, capo_ca_id, capo_stcktitel, " +
                "capo_edition, capo_art, capo_dauermin, capo_dauersec, capo_sonstiges, " +
                "capo_nr, capo_seite, capo_besetzung, capo_tonart, capo_komponist,"
                + " capo_zeilentyp, capo_titelbild, capo_bearbeiter, capo_auspcnd, capo_zwischentext,"
                + " capo_lit_ic)"
                + " VALUES ("
                + " ?, ?, ?, ?, ?,"
                + " ?, ?, ?, ?, ?,"
                + " ?, ?, ?, ?, ?,"
                + " ?, ?, ?, ?, ?,"
                + " ?)";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (AktionenListePositionenModel pos : list) {
                pstmt.setInt(1, pos.getCapoId());
                pstmt.setInt(2, pos.getCapoPos());
                pstmt.setString(3, pos.getCapoBem());
                pstmt.setInt(4, pos.getCapoCaId());
                pstmt.setString(5, pos.getCapoStcktitel());
                pstmt.setString(6, pos.getCapoEdition());
                pstmt.setString(7, pos.getCapoArt());
                pstmt.setInt(8, pos.getCapoDauermin());
                pstmt.setInt(9, pos.getCapoDauersec());
                pstmt.setString(10, pos.getCapoSonstiges());
                pstmt.setString(11, pos.getCapoNr());
                pstmt.setString(12, pos.getCapoSeite());
                pstmt.setString(13, pos.getCapoBesetzung());
                pstmt.setString(14, pos.getCapoTonart());
                pstmt.setString(15, pos.getCapoKomponist());
                pstmt.setString(16, pos.getCapoZeilentyp());
                pstmt.setString(17, pos.getCapoTitelbild());
                pstmt.setString(18, pos.getCapoBearbeiter());
                pstmt.setInt(19, pos.isCapoLiteratur());
                pstmt.setInt(20, pos.isCapoZwischentext());
                pstmt.setInt(21, pos.getCapoLitId());

                pstmt.addBatch();
            }

            pstmt.executeBatch();
            System.out.println("✅ Positionen erfolgreich in SQLite importiert!");
        }
    }
    
    // ---------------- Personen aus CSV  importieren ----------------
//    public static void insertCvwPersonen(List<CvwPersonenModel> list) throws SQLException {
//        String sql = "INSERT INTO tblChorPersonen (" +
//                "pe_id, pe_name, pe_vorname, pe_instrument, pe_chor, " +
//                "pe_stimme, pe_gruppe, pe_telefon, pe_mail) " +
//                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
//        
//        
//
//        try (Connection conn = DBManager.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            for (CvwPersonenModel a : list) {
//                pstmt.setInt(1, a.getPeid());
//                pstmt.setString(2, a.getPename());
//                pstmt.setString(3, a.getPevname());
//                pstmt.setString(4, a.getPeinstrument());
//                pstmt.setString(5, a.getPechor());
//                pstmt.setString(6, a.getPestimme());
//                pstmt.setString(7, a.getPegruppe());
//                pstmt.setString(8, a.getPetelefon());
//                pstmt.setString(9, a.getPemail());   
//                
//                pstmt.addBatch();
//            }
//
//            pstmt.executeBatch();
//        }
//    }
    public static CSVImporterCVWResult insertCvwPersonen(List<CvwPersonenModel> list) throws SQLException {

        int inserted = 0;
        int updated = 0;
        int unchanged = 0;

        String selectSql = """
            SELECT pe_instrument, pe_stimme, pe_telefon, pe_mail
            FROM tblChorPersonen
            WHERE pe_name = ?
              AND pe_vorname = ?
              AND pe_chor = ?
              AND pe_gruppe = ?
            """;

        String insertSql = """
            INSERT INTO tblChorPersonen (
                pe_id, pe_name, pe_vorname, pe_instrument, pe_chor,
                pe_stimme, pe_gruppe, pe_telefon, pe_mail
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        String updateSql = """
            UPDATE tblChorPersonen SET
                pe_instrument = ?,
                pe_stimme = ?,
                pe_telefon = ?,
                pe_mail = ?
            WHERE pe_name = ?
              AND pe_vorname = ?
              AND pe_chor = ?
              AND pe_gruppe = ?
            """;

        try (Connection conn = DBManager.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            conn.setAutoCommit(false);

            for (CvwPersonenModel a : list) {

                // ---- Existiert Person?
                selectStmt.setString(1, a.getPename());
                selectStmt.setString(2, a.getPevname());
                selectStmt.setString(3, a.getPechor());
                selectStmt.setString(4, a.getPegruppe());

                ResultSet rs = selectStmt.executeQuery();

                if (!rs.next()) {
                    // ===== INSERT =====
                    insertStmt.setInt(1, a.getPeid());
                    insertStmt.setString(2, a.getPename());
                    insertStmt.setString(3, a.getPevname());
                    insertStmt.setString(4, a.getPeinstrument());
                    insertStmt.setString(5, a.getPechor());
                    insertStmt.setString(6, a.getPestimme());
                    insertStmt.setString(7, a.getPegruppe());
                    insertStmt.setString(8, a.getPetelefon());
                    insertStmt.setString(9, a.getPemail());
                    insertStmt.executeUpdate();
                    inserted++;
                }
                else {
                    // ===== Vergleich =====
                    boolean changed =
                            !Objects.equals(rs.getString("pe_instrument"), a.getPeinstrument()) ||
                            !Objects.equals(rs.getString("pe_stimme"), a.getPestimme()) ||
                            !Objects.equals(rs.getString("pe_telefon"), a.getPetelefon()) ||
                            !Objects.equals(rs.getString("pe_mail"), a.getPemail());

                    if (changed) {
                        // ===== UPDATE =====
                        updateStmt.setString(1, a.getPeinstrument());
                        updateStmt.setString(2, a.getPestimme());
                        updateStmt.setString(3, a.getPetelefon());
                        updateStmt.setString(4, a.getPemail());
                        updateStmt.setString(5, a.getPename());
                        updateStmt.setString(6, a.getPevname());
                        updateStmt.setString(7, a.getPechor());
                        updateStmt.setString(8, a.getPegruppe());
                        updateStmt.executeUpdate();
                        updated++;
                    } else {
                        unchanged++;
                    }
                }
            }

            conn.commit();
        }

        return new CSVImporterCVWResult(inserted, updated, unchanged);
    }
}
