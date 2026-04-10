package application.database;

import application.models.CvwPersonenModel;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CSVImporterCVW {
	//Personen importieren

     public static List<CvwPersonenModel> readCSV(String path) throws Exception {
        List<CvwPersonenModel> list = new ArrayList<>();

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
		        	
                for (CSVRecord r : parser) {
                    CvwPersonenModel a = new CvwPersonenModel(
                            parseInt(r.get("peid")),
                            parseInt(r.get("peid")),
                            r.get("pename"),
                            r.get("pevname"),
                            r.get("peinstrument"),
                            r.get("pechor"),
                            r.get("pestimme"),
                            r.get("pegruppe"),
                            r.get("petelefon"),
                            r.get("pemail")                            
                    );
                    list.add(a);
                }
            }
        }

        return list;
    }
    private static int parseInt(String s) {
        try { return Integer.parseInt(s); }
        catch (Exception e) { return 0; }
    }
}
