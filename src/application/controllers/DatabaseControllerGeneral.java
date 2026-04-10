package application.controllers;

import java.sql.SQLException;



//--------------------------------------------------------------------------------
//Allgemeine Datenbank ADB  starten
//--------------------------------------------------------------------------------
public class DatabaseControllerGeneral extends DatabaseControllerDefault
{
    public DatabaseControllerGeneral() throws SQLException {
        super(); // holt Connection aus DBManager
        System.out.println("GeneralDatabaseController()");
    }
}

