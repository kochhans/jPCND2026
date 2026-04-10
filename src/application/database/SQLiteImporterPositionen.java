package application.database;

import application.db.DBManager;
import application.models.AktionenListePositionenModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SQLiteImporterPositionen {

    public static void insertPositionen(List<AktionenListePositionenModel> list) throws SQLException {

        String sql = "INSERT INTO tblChoraktionenPositionen (" +
                "capo_id, capo_pos, capo_bem, capo_ca_id, capo_stcktitel, capo_edition, capo_art, " +
                "capo_dauermin, capo_dauersec, capo_sonstiges, capo_nr, capo_seite, capo_besetzung, " +
                "capo_tonart, capo_komponist, capo_zeilentyp, capo_titelbild, capo_bearbeiter, " +
                "capo_auspcnd, capo_zwischentext, capo_lit_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (AktionenListePositionenModel a : list) {
                int index = 1;
                pstmt.setInt(index++, a.getCapoId());
                pstmt.setInt(index++, a.getCapoPos());
                pstmt.setString(index++, a.getCapoBem());
                pstmt.setInt(index++, a.getCapoCaId());
                pstmt.setString(index++, a.getCapoStcktitel());
                pstmt.setString(index++, a.getCapoEdition());
                pstmt.setString(index++, a.getCapoArt());
                pstmt.setInt(index++, a.getCapoDauermin());
                pstmt.setInt(index++, a.getCapoDauersec());
                pstmt.setString(index++, a.getCapoSonstiges());
                pstmt.setString(index++, a.getCapoNr());
                pstmt.setString(index++, a.getCapoSeite());               
                pstmt.setString(index++, a.getCapoBesetzung());
                pstmt.setString(index++, a.getCapoTonart());
                pstmt.setString(index++, a.getCapoKomponist());
                pstmt.setString(index++, a.getCapoZeilentyp());
                pstmt.setString(index++, a.getCapoTitelbild());
                pstmt.setString(index++, a.getCapoBearbeiter());
                pstmt.setInt(index++, a.isCapoLiteratur());
                pstmt.setInt(index++, a.isCapoZwischentext());
                pstmt.setInt(index++, a.getCapoLitId());

                pstmt.addBatch();
            }

            pstmt.executeBatch();
        }
    }
}
