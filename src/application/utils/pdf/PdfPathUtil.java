package application.utils.pdf;

import java.io.File;

import application.ValuesGlobals;


public final class PdfPathUtil {

    private static final String PDF_SUBDIR = "REPORTS";

    private PdfPathUtil() {}

    /** Liefert das Verzeichnis der Datenbank */
    private static File getDatabaseDirectory() {
        File dbFile = new File(ValuesGlobals.dbPfad);
        if (!dbFile.exists()) {
            throw new IllegalStateException("Datenbankdatei existiert nicht: " + dbFile);
        }

        // 🔥 WICHTIG: Parent-Verzeichnis verwenden
        File dbDir = dbFile.getParentFile();

        if (dbDir == null) {
            throw new IllegalStateException(
                "Ungültiger Datenbankpfad: " + ValuesGlobals.dbPfad
            );
        }

        return dbDir;
    }

    /** Liefert das PDF-Verzeichnis (wird bei Bedarf angelegt) */
    public static File getPdfDirectory() {
        File pdfDir = new File(getDatabaseDirectory(), PDF_SUBDIR);

        if (!pdfDir.exists()) {
            pdfDir.mkdirs();
        }

        return pdfDir;
    }

    /** Liefert vollständigen PDF-Dateipfad */
    public static File resolvePdfPath(String fileName) {
        return new File(getPdfDirectory(), fileName);
    }
}
