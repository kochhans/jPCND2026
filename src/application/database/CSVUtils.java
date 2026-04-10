package application.database;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class CSVUtils {

    /**
     * Konvertiert eine CSV-Datei in UTF-8.
     * @param inputPfad Originaldatei
     * @param outputPfad Neue Datei in UTF-8
     * @param originalCharset Charset der Originaldatei, z.B. "Windows-1252"
     * @throws IOException bei Lese-/Schreibfehler
     */
    public static void convertToUTF8(String inputPfad, String outputPfad, Charset originalCharset) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputPfad), originalCharset));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPfad), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    // Shortcut, falls Originaldatei schon Windows-1252 ist
    public static void convertToUTF8(String inputPfad, String outputPfad) throws IOException {
        convertToUTF8(inputPfad, outputPfad, Charset.forName("Windows-1252"));
    }
}


// ANWENDUNG:
//String csvOriginal = "aktionen.csv";
//String csvUTF8 = "aktionen_utf8.csv";
//
//try {
//    CSVUtils.convertToUTF8(csvOriginal, csvUTF8);
//    System.out.println("✅ CSV erfolgreich nach UTF-8 konvertiert.");
//} catch (IOException e) {
//    e.printStackTrace();
//}
