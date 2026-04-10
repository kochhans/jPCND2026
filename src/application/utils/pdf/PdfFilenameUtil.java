package application.utils.pdf;

import java.io.File;



public final class PdfFilenameUtil {

    private static final int MAX_LENGTH = 50;

    private PdfFilenameUtil() {} // privater Konstruktor

    /**
     * Bestehende Methode: nur String zurückgeben, sicher für Dateinamen
     */
    public static String toSafePdfName(String title, String date) {

        String base = title;//.substring(0,5);

        String safe = base.toLowerCase()
                .replace("ä", "ae")
                .replace("ö", "oe")
                .replace("ü", "ue")
                .replace("ß", "ss")
                .replaceAll("[^a-z0-9_\\-]", "_")
                .replaceAll("_+", "_");

        String suffix = "_" + date + ".pdf";

        int maxBaseLength = MAX_LENGTH - suffix.length();
        if (maxBaseLength < 1) maxBaseLength = 1;

        if (safe.length() > maxBaseLength) {
            safe = safe.substring(0, maxBaseLength);
        }

        return safe + suffix;
    }

    /**
     * Neue Methode: liefert direkt ein File mit sicherem PDF-Namen
     * Inklusive Aktion-Nummer, aktuellem Datum und Ort
     */
    public static File createActionPdfFile(String aktionDatum, String typ) {

        // Zielordner für Reports
        File pdfDir = PdfPathUtil.getPdfDirectory();
        if (!pdfDir.exists()) {
            pdfDir.mkdirs(); // Ordner ggf. anlegen
        }

        // aktuelles Datum
        //String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Basisname zusammenbauen
        String base = "aktion_" + aktionDatum + "_"  + typ + "";// + dateStr;

        // Umlaute & Sonderzeichen ersetzen
        String safe = base.toLowerCase()
                .replace("ä", "ae")
                .replace("ö", "oe")
                .replace("ü", "ue")
                .replace("ß", "ss")
                .replaceAll("[^a-z0-9_\\-]", "_")
                .replaceAll("_+", "_");

        // Max Länge berücksichtigen
        int maxLength = MAX_LENGTH - 4; // ".pdf"
        if (safe.length() > maxLength) safe = safe.substring(0, maxLength);

        // File im Reports-Ordner erzeugen
        return new File(pdfDir, safe + ".pdf");
    }

}