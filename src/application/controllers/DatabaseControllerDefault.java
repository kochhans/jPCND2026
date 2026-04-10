package application.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import application.db.DBManager;

public class DatabaseControllerDefault {

    protected Connection connection;

    // Standard-Konstruktor → holt die Connection automatisch von DBManager
    public DatabaseControllerDefault() throws SQLException {
        this.connection = DBManager.getConnection();
        if (this.connection == null) {
            throw new SQLException("Datenbank-Verbindung konnte nicht hergestellt werden!");
        }
    }

    // Konstruktor mit eigener Connection (optional)
    public DatabaseControllerDefault(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection darf nicht null sein");
        }
        this.connection = connection;
    }

}
