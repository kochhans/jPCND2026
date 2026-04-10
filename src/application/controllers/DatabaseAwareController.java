package application.controllers;

public abstract class DatabaseAwareController {

    protected DatabaseControllerTemplate dbController;

    public void initAfterStartup() {
        // wird erst nach Lizenz + DB aufgerufen
        dbController = createDatabaseController();
    }

    protected abstract DatabaseControllerTemplate createDatabaseController();
}
