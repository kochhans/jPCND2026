package application.database;

import application.models.AktionenListePositionenModel;
import application.utils.RtfUtils;

import org.apache.commons.csv.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CSVImporterPositionen
{

	// ===================== PUBLIC API =====================

	public static List<AktionenListePositionenModel> readCSV(String path) throws Exception
	{
		List<AktionenListePositionenModel> result = new ArrayList<>();

		try (Reader reader = new InputStreamReader(
				new FileInputStream(path), StandardCharsets.UTF_8))
		{

			CSVFormat format = CSVFormat.DEFAULT.builder()
					.setDelimiter(';')
					.setQuote('"')
					.setIgnoreSurroundingSpaces(true)
					.setTrim(true)
					.setHeader()
					.setSkipHeaderRecord(true)
					.get();

			try (CSVParser parser = format.parse(reader))
			{
				for (CSVRecord r : parser)
				{
					result.add(parseRecord(r));
				}
			}
		}

		return result;
	}

	// ===================== RECORD → MODEL =====================

	private static AktionenListePositionenModel parseRecord(CSVRecord r)
	{

		String[] nrSeite = splitNrSeite(r.get("capo_nrseite"));
		String capoNr = nrSeite[0];
		String capoSeite = nrSeite[1];

		return new AktionenListePositionenModel(
				parseIntSafe(r.get("capo_id")),
				parseIntSafe(r.get("capo_nr")), // neuer Feldname capo_pos
				RtfUtils.rtfCsvToPlainText(r.get("capo_bem")),
				parseIntSafe(r.get("capo_ca_id")),
				r.get("capo_stcktitel"),
				r.get("capo_edition"),
				r.get("capo_Art"),
				parseIntSafe(r.get("capo_dauermin")),
				parseIntSafe(r.get("capo_dauersec")),
				RtfUtils.rtfCsvToPlainText(r.get("capo_sonstiges")),

				capoNr, // 👉 capo_nr (aus N:)
				capoSeite, // 👉 capo_seite (aus S:)

				r.get("capo_besetzung"),
				r.get("capo_tonart"),
				r.get("capo_komponist"),
				r.get("capo_zeilentyp"),
				r.get("capo_Titelbild"),
				r.get("capo_Bearbeiter"),
				parseBooleanAsInt(r.get("capo_Literatur")),
				parseBooleanAsInt(r.get("capo_Zwischentext")),
				parseIntSafe(r.get("capo_Literatur_ID")));
	}

	// ===================== PARSER =====================

	private static int parseIntSafe(String s)
	{
		try
		{
			return s != null && !s.isBlank() ? Integer.parseInt(s.trim()) : 0;
		}
		catch (Exception e)
		{
			return 0;
		}
	}

	private static int parseBooleanAsInt(String s)
	{
		if (s == null)
			return 0;
		s = s.trim();
		return (s.equals("1") || s.equalsIgnoreCase("true")) ? 1 : 0;
	}

	// ===================== NR/SEITE SPLITTER =====================
	// ===================== REGEX (Performance + robust) =====================

	private static final java.util.regex.Pattern NR_SEITE_PATTERN =
	        java.util.regex.Pattern.compile(
	                "(?i)\\b(Nr|N|S)\\b\\s*[:\\.]?\\s*(\\d+)"
	        );

	private static String[] splitNrSeite(String value)
	{
	    String capoNr = null;
	    String capoSeite = null;

	    if (value == null || value.isBlank())
	    {
	        return new String[] { null, null };
	    }

	    // Geschützte Leerzeichen entfernen + trimmen
	    value = value.replace('\u00A0', ' ').trim();

	    java.util.regex.Matcher matcher = NR_SEITE_PATTERN.matcher(value);

	    while (matcher.find())
	    {
	        String typ = matcher.group(1).toUpperCase();
	        String zahl = matcher.group(2);

	        if (typ.equals("S"))
	        {
	            capoSeite = zahl;
	        }
	        else if (typ.equals("N") || typ.equals("NR"))
	        {
	            capoNr = zahl;
	        }
	    }

	    return new String[] { capoNr, capoSeite };
	}
}
