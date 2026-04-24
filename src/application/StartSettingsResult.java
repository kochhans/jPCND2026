package application;

public class StartSettingsResult {

    private final boolean saved;
    private final String dbPath;

    public StartSettingsResult(boolean saved, String dbPath) {
        this.saved = saved;
        this.dbPath = dbPath;
    }

    public boolean isSaved() {
        return saved;
    }

    public String getDbPath() {
        return dbPath;
    }
}