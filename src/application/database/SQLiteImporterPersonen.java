package application.database;

import application.db.DBManager;
import application.models.AktionenListePersonenModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SQLiteImporterPersonen {

    public static void insertPersonen(List<AktionenListePersonenModel> list) throws SQLException {

        String sql = "INSERT INTO tblChoraktionenPersonen (" +
                "cape_id, cape_ca_id, cape_name, cape_vname, cape_stimme, cape_instrument) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (AktionenListePersonenModel a : list) {
                int index = 1;
                pstmt.setInt(index++, a.getCapeid());
                pstmt.setInt(index++, a.getCapecaid());
                pstmt.setString(index++, a.getCapename());
                pstmt.setString(index++, a.getCapevname());
                pstmt.setString(index++, a.getCapestimme());
                pstmt.setString(index++, a.getCapeinstrument());
                pstmt.addBatch();
            }

            pstmt.executeBatch();
        }
    }
}
