package application.utils;

import javax.swing.text.*;
import javax.swing.text.rtf.*;
import java.io.*;

public final class RtfUtils {

    private RtfUtils() {}

    /**
     * Wandelt RTF aus CSV-Zellen in reinen Plain Text um.
     * - Zeilenumbrüche bleiben erhalten
     * - Alle RTF-Steuerzeichen und Fonts werden entfernt
     * - Leere RTF-Zellen liefern leeren String
     */
    public static String rtfCsvToPlainText(String input) {
        if (input == null || input.isBlank()) return "";

        // Prüfen, ob es sich um RTF handelt
        if (!input.trim().startsWith("{\\rtf")) {
            return input.trim();  // Kein RTF, normal zurückgeben
        }

        try {
            RTFEditorKit rtfKit = new RTFEditorKit();
            Document doc = rtfKit.createDefaultDocument();
            rtfKit.read(new StringReader(input), doc, 0);

            String text = doc.getText(0, doc.getLength());
            return normalizeTypography(text).trim();
        } catch (Exception e) {
            // Bei Fehlern einfach leeren Text zurückgeben
            return "";
        }
    }

    /**
     * Typografie optional normalisieren (Anführungszeichen, Apostrophe)
     */
    private static String normalizeTypography(String s) {
        if (s == null) return null;
        return s
                .replace('\u0084', '„')
                .replace('\u0093', '“')
                .replace('\u0094', '”')
                .replace('\u0091', '‚')
                .replace('\u0092', '’')
                .trim();
    }
}