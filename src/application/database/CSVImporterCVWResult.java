package application.database;

public class CSVImporterCVWResult
{
    public final int inserted;
    public final int updated;
    public final int unchanged;

    public CSVImporterCVWResult(int inserted, int updated, int unchanged) {
        this.inserted = inserted;
        this.updated = updated;
        this.unchanged = unchanged;
    }
}
