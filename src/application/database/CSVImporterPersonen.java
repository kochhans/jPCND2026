package application.database;

import application.models.AktionenListePersonenModel;

import org.apache.commons.csv.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CSVImporterPersonen
{

	// ===================== PUBLIC API =====================

	public static List<AktionenListePersonenModel> readCSV(String path) throws Exception
	{
		List<AktionenListePersonenModel> result = new ArrayList<>();

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

	private static AktionenListePersonenModel parseRecord(CSVRecord r)
	{
		return new AktionenListePersonenModel(//Feldnamen der CSV!!!!
				parseIntSafe(r.get("cape_ID")),
				parseIntSafe(r.get("cape_ca_ID")), // FK zu Aktionen
				r.get("cape_Name"),
				r.get("cape_VName"),
				r.get("cape_Stimme"),
				r.get("cape_Instrument"));
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

}
