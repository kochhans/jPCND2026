package application.controllers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.stage.Stage;

public abstract class DatabaseControllerTemplate extends DatabaseControllerDefault {

    protected PreparedStatement stmt;
    protected ResultSet rs;
    private Stage stage;

    public DatabaseControllerTemplate() throws SQLException {
        super(); // holt Connection aus DBManager
    }

    public void setUpStatement(String sql) throws SQLException {
        stmt = connection.prepareStatement(sql);
    }

    public ResultSet runStatement() throws SQLException {
        if (stmt != null) {
            rs = stmt.executeQuery();
            return rs;
        }
        throw new SQLException("PreparedStatement ist null! setUpStatement() vorher aufrufen.");
    }

    public ResultSet getResults() throws SQLException {
        return rs;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
