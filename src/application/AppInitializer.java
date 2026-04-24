package application;

import application.db.DBManager;

public class AppInitializer {

    public static void init() {

        // 🔥 Config neu laden
        ConfigManager.reload();

        // 🔥 globale Werte setzen
        ValuesGlobals.databasePath = ConfigManager.loadDBPath();
        ValuesGlobals.dbPfad = ValuesGlobals.databasePath;

        // 🔥 DB sauber resetten
        //DBManager.reset();

        // 🔥 Connection wird lazy neu aufgebaut
        DBManager.getConnection();
    }
}