package application.database;

import application.models.AktionenListeModel;
import application.utils.RtfUtils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CSVImporterAktionen {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static List<AktionenListeModel> readCSV(String path) throws Exception {
        List<AktionenListeModel> list = new ArrayList<>();

        try (Reader reader = new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8)) {

            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setDelimiter(';')
                    .setQuote('"')
                    .setIgnoreSurroundingSpaces(true)
                    .setTrim(true)
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .get();

            try (CSVParser parser = CSVParser.parse(reader, format)) {
                int gema = 0;
                int auftritt = 0;

                for (CSVRecord r : parser) {

                    LocalDate datum = parseDatum(r.get("ca_datum"));
                    LocalTime beginn = parseZeit(r.get("ca_Beginn"));
                    LocalTime treffpunkt = parseZeit(r.get("ca_Treffpunkt"));

                    auftritt = "True".equalsIgnoreCase(r.get("ca_Auftrittstermin")) ? 1 : 0;

                    // ⭐ Hier wird jetzt sauberer Text aus RTF erzeugt
                    String bemerkungen = RtfUtils.rtfCsvToPlainText(r.get("ca_Bemerkungen"));

                    AktionenListeModel a = new AktionenListeModel(
                            parseInt(r.get("ca_id")),
                            r.get("ca_akttyp"),
                            datum,
                            r.get("ca_beschreibung"),
                            treffpunkt,
                            beginn,
                            parseInt(r.get("ca_anwesend")),
                            bemerkungen,
                            r.get("ca_verantwortlich"),
                            r.get("ca_Gruppe"),
                            r.get("ca_Aktionsort"),
                            r.get("ca_Veranstalter"),
                            auftritt,
                            parseInt(r.get("ca_Prog_oberer_Rand")),
                            parseInt(r.get("ca_Prog_linker_Rand")),
                            parseInt(r.get("ca_Prog_unterer_Rand")),
                            parseInt(r.get("ca_Prog_Kopfabstand")),
                            parseInt(r.get("ca_Prog_Positionsabstand")),
                            gema
                    );

                    list.add(a);
                }
            }
        }

        return list;
    }

    private static LocalDate parseDatum(String s) {
        if (s == null || s.isBlank()) return null;
        String datePart = s.split(" ")[0];
        if (datePart.equals("30.12.1899") || datePart.equals("01.01.1900")) return null;
        return LocalDate.parse(datePart, DATE_FMT);
    }

    private static LocalTime parseZeit(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return LocalTime.parse(s.split(" ")[1], TIME_FMT);
        } catch (Exception e) {
            return null;
        }
    }

    private static int parseInt(String s) {
        try { return Integer.parseInt(s); }
        catch (Exception e) { return 0; }
    }
}