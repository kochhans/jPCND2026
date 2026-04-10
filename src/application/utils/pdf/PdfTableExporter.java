package application.utils.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class PdfTableExporter
{

	private PdfTableExporter()
	{
	}

	/**
	 * Exportiert TableView in ein bestehendes Document
	 */
	public static <T> void exportIntoExistingDocument(
			TableView<T> tableView, // Details-Tableview
			Document document, // bisheriges Dokument
			PdfExportOptions<T> opt, // Optionen
			java.util.function.Consumer<Double> progressCallback // Fortschrittsanzeige
	) throws Exception

	{
		// 1️ sichtbare Spalten sammeln
		List<TableColumn<T, ?>> columns = tableView.getColumns().stream()
				.filter(TableColumn::isVisible)
				.filter(c -> !opt.excludedColumns.contains(c))
				.collect(Collectors.toList());
		if (columns.isEmpty())
			throw new IllegalArgumentException("Keine sichtbaren Spalten für PDF");

		// 2️ Fonts festlegen
		Font headerFont = FontFactory.getFont(FontFactory.TIMES_BOLD, opt.headerFontSize);
		Font cellFontFett = FontFactory.getFont(FontFactory.TIMES_BOLD, opt.cellFontSize);
		Font cellFontFine = FontFactory.getFont(FontFactory.TIMES, opt.cellFontSize);
		// Font cellFontklein = FontFactory.getFont(FontFactory.TIMES,
		// opt.cellFontSizeklein);
		Font groupFont = FontFactory.getFont(FontFactory.TIMES_BOLDITALIC, opt.groupFontSize);

		// 3️ Header-Berechnung
		float totalWidth = (float) columns.stream().mapToDouble(TableColumn::getWidth).sum();
		float[] widths = new float[columns.size()];
		for (int i = 0; i < columns.size(); i++)
			widths[i] = (float) (columns.get(i).getWidth() / totalWidth);

		// 4️ Zeilen vorbereiten
		List<T> rows = new ArrayList<>(tableView.getItems());

		// Filter zum anzeigen (z.B. Positionen)
		// 🔹 Filter anwenden (hier int-Feld prüfen)
		if (opt.rowFilter != null)
		{
			rows = rows.stream()
					.filter(r -> Boolean.TRUE.equals(opt.rowFilter.apply(r)))
					.collect(Collectors.toList());
		}

		if (rows.isEmpty())
		{

			PdfPTable table = new PdfPTable(columns.size());
			if (opt.columnWidths != null)
			{
				table.setWidths(opt.columnWidths);
			}
			else
			{
				table.setWidths(widths);
			}
			table.setWidthPercentage(100);
			table.setWidths(new float[] { 10, 90 }); // Spaltenbreiten relativ
			PdfPCell empty = new PdfPCell(new Phrase("Keine Positionen vorhanden", cellFontFett));
			empty.setColspan(columns.size());
			table.addCell(empty);
			document.add(table);
			return;
		}

		// 5️ Gruppierung nur, wenn die Tableview eine sortierte Spalte hat!
		if (opt.groupByColumn != null) // kommt aus der Sortierspalte des tblvw
		{
			String lastGroupValue = null;
			int rowCounter = 0;
			int totalRows = rows.size();

			for (int i = 0; i < rows.size(); i++)
			{
				T item = rows.get(i);

				TableColumn<T, ?> groupCol = (TableColumn<T, ?>) opt.groupByColumn;
				Object val = groupCol.getCellObservableValue(item).getValue();
				String currentGroupValue = val == null ? "" : val.toString();

				if (!Objects.equals(currentGroupValue, lastGroupValue))
				{
					List<T> groupRows = new ArrayList<>();
					for (int j = i; j < rows.size(); j++)
					{
						T r = rows.get(j);
						Object v = groupCol.getCellObservableValue(r).getValue();
						String s = v == null ? "" : v.toString();
						if (s.equals(currentGroupValue))
							groupRows.add(r);
						else
							break;
					}

					PdfPTable groupTable = new PdfPTable(columns.size());
					groupTable.setWidthPercentage(100);
					groupTable.setWidths(widths);

					// Gruppenkopf
					PdfPCell groupCell = new PdfPCell(new Phrase(opt.groupHeaderPrefix + currentGroupValue + " (" + groupRows.size() + ")", groupFont));
					groupCell.setColspan(columns.size());
					groupCell.setBackgroundColor(opt.groupHeaderBackground);
					groupCell.setPadding(6);
					groupCell.setBorder(Rectangle.BOTTOM);
					groupTable.addCell(groupCell);

					// Header
					for (TableColumn<T, ?> col : columns)
					{
						PdfPCell cell = new PdfPCell(new Phrase(col.getText(), headerFont));
						cell.setBackgroundColor(opt.headerBackground);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setPadding(4);
						groupTable.addCell(cell);
					}
					groupTable.setHeaderRows(1);

					// Zeilen
					for (T r : groupRows)
					{
						for (TableColumn<T, ?> col : columns)
						{
							String text = getCellText(r, col);
							PdfPCell cell = new PdfPCell(new Phrase(text, cellFontFett));
							cell.setPadding(4);
							cell.setNoWrap(!opt.autoWrap);
							// int align = opt.columnAlignment.getOrDefault(col, Element.ALIGN_LEFT);
							// cell.setHorizontalAlignment(align);
							if (opt.zebra && rowCounter % 2 == 0)
								cell.setBackgroundColor(opt.zebraColor);
							groupTable.addCell(cell);
						}
						rowCounter++;
						if (progressCallback != null)
							progressCallback.accept((double) rowCounter / totalRows);
					}

					document.add(groupTable);
					lastGroupValue = currentGroupValue;
				}
			}
		}
		else // wenn die tblvw keine Sortierte Spalte bringt
		{
			// ############ Keine Gruppierung ################################
			PdfPTable table = new PdfPTable(columns.size()); // Tabelle anlegen
			table.setWidthPercentage(100);
			table.setWidths(widths);

			// =========== Header für Daten ohne Gruppierung ===========
			if (opt.mitTabellenkopf == true)
			{
				for (TableColumn<T, ?> col : columns)
				{
					PdfPCell cell = new PdfPCell(new Phrase(col.getText(), headerFont));
					cell.setBackgroundColor(opt.headerBackground);
					cell.setPadding(4);
					// Ausrichtung der Headerzellen
					int align = opt.columnAlignment.getOrDefault(col, Element.ALIGN_LEFT);
					cell.setHorizontalAlignment(align);
					table.addCell(cell);
				}

				table.setHeaderRows(1);
			}

			// =========== Zeilenschleife für Daten ohne Gruppierung =============
			int rowCounter = 0;
			int totalRows = rows.size();
			for (T row : rows)
			{

				// rowTable direkt am Anfang der Schleife erstellen
				PdfPTable rowTable = new PdfPTable(columns.size());
				rowTable.setWidthPercentage(100);
				rowTable.setWidths(widths);
				rowTable.setKeepTogether(true); // verhindert Seitenbruch im Datensatz

				// ================= Detailtabellen-Zeilen nur bei aktiviertem Option
				if (opt.mitDetailtabelle)
				{
					for (TableColumn<T, ?> col : columns)
					{
						// Object value = col.getCellObservableValue(row).getValue();
						String text = getCellText(row, col);

						PdfPCell cell = new PdfPCell(new Phrase(text, headerFont));
						cell.setPadding(4);
						cell.setNoWrap(!opt.autoWrap);
						cell.setBorder(Rectangle.NO_BORDER);
						// AUsrichtung der Tabellenzellen
						// ✅ Alignment aus den Optionen anwenden
						int align = opt.columnAlignment.getOrDefault(col, Element.ALIGN_LEFT);
						cell.setHorizontalAlignment(align);

						if (opt.zebra && rowCounter % 2 == 0)
							cell.setBackgroundColor(opt.zebraColor);
						rowTable.addCell(cell);
					}
				}

				// ================= Zusatzzeilen – immer ausführen =================
				addExtraLine(rowTable, opt.detailLine1Extractor, row, columns.size(), cellFontFett, 10);
				addExtraLine(rowTable, opt.detailLine2Extractor, row, columns.size(), cellFontFett, 15);
				addExtraLine(rowTable, opt.detailLine3Extractor, row, columns.size(), cellFontFett, 20);

				if (opt.detailImageExtractor != null || opt.detailImageTextExtractor != null)
				{
					addExtraLineImageText(
							rowTable,
							opt.detailImageExtractor,
							opt.detailImageTextExtractor,
							row,
							columns.size(),
							cellFontFine);
				}

				// ================= Bemerkungen – immer ausführen =================
				if (opt.detailRemarkExtractor != null)
				{
					Function<T, String> extractor = (Function<T, String>) opt.detailRemarkExtractor;

					String remark = extractor.apply(row);
					if (remark != null && !remark.isBlank())
					{
						Font remarkFont = FontFactory.getFont(FontFactory.TIMES, opt.cellFontSize);
						PdfPCell remarkCell = new PdfPCell(new Phrase(remark, remarkFont));
						remarkCell.setColspan(columns.size());
						remarkCell.setPaddingLeft(40); // zusätzlich zum Blattrand links
						remarkCell.setPaddingTop(2);
						remarkCell.setPaddingBottom(10);
						remarkCell.setBorder(Rectangle.NO_BORDER);
						rowTable.addCell(remarkCell);
					}
				}

				// ================= Zeile in die Haupttabelle einfügen =================
				PdfPCell wrapper = new PdfPCell(rowTable);
				wrapper.setColspan(columns.size());
				wrapper.setPaddingBottom(3);
				wrapper.setBorder(Rectangle.TOP);
				wrapper.setBorderWidthBottom(0.3f);
				wrapper.setBorderColor(Color.gray);
				table.addCell(wrapper);

				// Zeilenzähler & Fortschritt
				rowCounter++;
				if (progressCallback != null)
					progressCallback.accept((double) rowCounter / totalRows);
			}

			document.add(table);
		}
	}

	public static <T> void druckenLiteraturlisten(TableView<T> tableView, File pdfFile, PdfExportOptions<T> opt,
			java.util.function.Consumer<Double> progressCallback) throws Exception
	{

		// 1️⃣ sichtbare Spalten
		List<TableColumn<T, ?>> columns = tableView.getColumns().stream()
				.filter(TableColumn::isVisible)
				.filter(c -> !opt.excludedColumns.contains(c))
				.toList();

		// 2️⃣ Dokument & Writer
		Document document = new Document(opt.pageSizeQuer, opt.marginLeft, opt.marginRight, opt.marginTop, opt.marginBottom);

		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
		writer.setPageEvent(new PdfPageEvent<T>(opt));
		document.open();

		// 3️⃣ Fonts
		Font headerFont = FontFactory.getFont(FontFactory.TIMES_BOLD, opt.headerFontSize);
		Font cellFont = FontFactory.getFont(FontFactory.TIMES, opt.cellFontSize);
		Font groupFont = FontFactory.getFont(FontFactory.TIMES_BOLD, opt.groupFontSize);
		// Font groupColumnHeaderFont = FontFactory.getFont(FontFactory.TIMES_BOLD,
		// opt.groupHeaderColumnFontSize);

		// 4️⃣ Header-Tabelle vorbereiten

		PdfPTable headerTable = new PdfPTable(columns.size());
		headerTable.setWidthPercentage(100);

		float totalWidth = 0;
		for (TableColumn<T, ?> c : columns)
			totalWidth += c.getWidth();
		float[] widths = new float[columns.size()];
		for (int i = 0; i < columns.size(); i++)
			widths[i] = (float) (columns.get(i).getWidth() / totalWidth);
		headerTable.setWidths(widths);

		for (TableColumn<T, ?> col : columns)
		{
			PdfPCell cell = new PdfPCell(new Phrase(col.getText(), headerFont));
			cell.setBackgroundColor(opt.headerBackground);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setPadding(4);
			headerTable.addCell(cell);
		}
		headerTable.setHeaderRows(1); // Header wiederholen

		// 5️⃣ Sortierte Kopie
		List<T> rows = new ArrayList<>(tableView.getItems());
		if (opt.groupByColumn != null)
		{
			rows.sort(Comparator.comparing(item -> {

				TableColumn<T, ?> groupCol = (TableColumn<T, ?>) opt.groupByColumn;
				Object v = groupCol.getCellObservableValue(item).getValue();
				return v == null ? "" : v.toString();
			}));
		}

		// 6️⃣ Export Zeilen & Gruppen
		String lastGroupValue = null;
		int rowCounter = 0;
		int totalRows = rows.size();

		for (int i = 0; i < rows.size(); i++)
		{
			T item = rows.get(i);

			String tempGroupValue = null;
			if (opt.groupByColumn != null)
			{
				TableColumn<T, ?> groupCol = (TableColumn<T, ?>) opt.groupByColumn;
				Object v = groupCol.getCellObservableValue(item).getValue();
				tempGroupValue = v == null ? "" : v.toString();
			}
			final String currentGroupValue = tempGroupValue;

			// Neue Gruppe?
			if (opt.groupByColumn != null && !Objects.equals(currentGroupValue, lastGroupValue))
			{

				// Items der Gruppe sammeln
				List<T> groupRows = new ArrayList<>();
				for (int j = i; j < rows.size(); j++)
				{
					T r = rows.get(j);

					TableColumn<T, ?> groupCol = (TableColumn<T, ?>) opt.groupByColumn;
					Object val = groupCol.getCellObservableValue(r).getValue();
					String s = val == null ? "" : val.toString();
					if (s.equals(currentGroupValue))
						groupRows.add(r);
					else
						break;
				}

				// Gruppentabelle erzeugen
				PdfPTable groupTable = new PdfPTable(columns.size());
				groupTable.setWidthPercentage(100);
				groupTable.setWidths(widths);
				groupTable.setKeepTogether(true); // 🔹 optional: Kopf + Zeilen

				// zusammenhalten

				// Gruppenkopf
				PdfPCell groupCell = new PdfPCell(new Phrase(
						opt.groupHeaderPrefix + currentGroupValue + " (" + groupRows.size() + ")", groupFont));
				groupCell.setColspan(columns.size());
				groupCell.setBackgroundColor(opt.groupHeaderBackground);
				groupCell.setPadding(6);
				groupCell.setBorder(Rectangle.BOTTOM);
				groupTable.addCell(groupCell);

				// 🔹 Header erneut hinzufügen
				if (opt.repeatHeaderPerGroup)
				{
					// Header in groupTable einfügen
					for (PdfPCell cell : headerTable.getRows().get(0).getCells())
					{
						PdfPCell newCell = new PdfPCell(new Phrase(cell.getPhrase()));
						newCell.cloneNonPositionParameters(cell);
						groupTable.addCell(newCell);
					}
					groupTable.setHeaderRows(1); // Header wiederholen bei Seitenumbruch

				}

				// Zeilen der Gruppe
				for (T r : groupRows)
				{
					for (TableColumn<T, ?> col : columns)
					{
						String text = getCellText(r, col);
						PdfPCell cell = new PdfPCell(new Phrase(text, cellFont));
						cell.setPadding(4);
						cell.setNoWrap(!opt.autoWrap);
						// int align = opt.columnAlignment.getOrDefault(col, Element.ALIGN_LEFT);
						// cell.setHorizontalAlignment(align);
						if (opt.zebra && rowCounter % 2 == 0)
							cell.setBackgroundColor(opt.zebraColor);
						groupTable.addCell(cell);
					}
					rowCounter++;
					if (progressCallback != null)
						progressCallback.accept((double) rowCounter / totalRows);
				}

				document.add(groupTable);
				lastGroupValue = currentGroupValue;
			}
		}

		document.close();
	}

	private static <T> String getCellText(T row, TableColumn<T, ?> col)
	{
		ObservableValue<?> ov = col.getCellObservableValue(row);
		return ov == null || ov.getValue() == null ? " " : ov.getValue().toString();
	}

	private static <T> void addExtraLine(
			PdfPTable rowTable,
			Function<T, String> extractor, // ✔ jetzt typsicher
			T row,
			int colspan,
			Font font,
			int paddingLeft)
	{

		if (extractor == null)
			return;

		String text = extractor.apply(row);
		if (text == null || text.isBlank())
			return;

		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setColspan(colspan);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setPaddingLeft(paddingLeft);
		cell.setPaddingTop(0);
		cell.setPaddingBottom(4);
		cell.setNoWrap(false);

		rowTable.addCell(cell);
	}


//	public static <T> void addExtraLineImageText(
//			PdfPTable rowTable,
//			Function<T, String> imagePathExtractor,
//			Function<T, String> textExtractor,
//			T row,
//			int totalColumns,
//			Font font)
//	{
//
//		if (rowTable == null || (imagePathExtractor == null && textExtractor == null))
//			return;
//
//		try
//		{
//			String text = textExtractor != null ? textExtractor.apply(row) : "";
//
//			// ================= Bild-Zelle =================
//			PdfPCell imgCell = new PdfPCell();
//			imgCell.setBorder(Rectangle.BOX);
//			imgCell.setPadding(2);
//			imgCell.setColspan(1);
//
//			if (imagePathExtractor != null)
//			{
//				String imagePath = imagePathExtractor.apply(row);
//				if (imagePath != null && !imagePath.isBlank())
//				{
//					try
//					{
//						Image img = Image.getInstance(imagePath);
////						float origW = img.getWidth();//
////						float origH = img.getHeight();//
//						img.scaleToFit(60, 40); // max 40px breit
//						//img.scalePercent(100); // verhindert interne Anpassung
////						float newW = img.getScaledWidth();//
////						float newH = img.getScaledHeight();//
////						System.out.println(String.format(
////							    "Bild: %s | Original: %.1f x %.1f | Skaliert: %.1f x %.1f",
////							    imagePath, origW, origH, newW, newH
////							));//
//						//imgCell.setImage(img); 
//						imgCell.setFixedHeight(62); // etwas größer als 20
//						imgCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//						imgCell.setVerticalAlignment(Element.ALIGN_TOP);
//						
//						imgCell.addElement(img);
//						
//						
//						
//					}
//					catch (Exception e)
//					{
//						// Bild konnte nicht geladen werden → leere Zelle
//						// System.err.println("Bild nicht gefunden: " + imagePath);
//					}
//				}
//			}
//
//			rowTable.addCell(imgCell);
//
//			// ================= Text-Zelle =================
//			PdfPCell textCell = new PdfPCell(new Phrase(text != null ? text : "", font));
//			textCell.setColspan(totalColumns - 1); // Restliche Spalten für Text
//			textCell.setBorder(Rectangle.NO_BORDER);
//			textCell.setPaddingLeft(10);
//			textCell.setPaddingBottom(4);
//			rowTable.addCell(textCell);
//
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
	public static <T> void addExtraLineImageText(
	        PdfPTable rowTable,
	        Function<T, String> imagePathExtractor,
	        Function<T, String> textExtractor,
	        T row,
	        int totalColumns,
	        Font font)
	{

	    if (rowTable == null || (imagePathExtractor == null && textExtractor == null))
	        return;

	    try
	    {
	        String text = textExtractor != null ? textExtractor.apply(row) : "";

	        // ================= Text-Zelle (LINKS) =================
	        PdfPCell textCell = new PdfPCell(new Phrase(text != null ? text : "", font));
	        textCell.setColspan(totalColumns - 1);
	        textCell.setBorder(Rectangle.NO_BORDER);
	        textCell.setPaddingLeft(5);
	        textCell.setPaddingRight(10);
	        textCell.setPaddingTop(2);
	        textCell.setVerticalAlignment(Element.ALIGN_TOP);

	        rowTable.addCell(textCell);

	        // ================= Bild-Zelle (RECHTS) =================
	        PdfPCell imgCell = new PdfPCell();
	        imgCell.setBorder(Rectangle.NO_BORDER);
	        //imgCell.setBorder(Rectangle.BOX);
	        imgCell.setPadding(2);
	        imgCell.setColspan(1);

	        imgCell.setFixedHeight(31); // sorgt für einheitliche Höhe
	        imgCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        imgCell.setVerticalAlignment(Element.ALIGN_TOP);

	        if (imagePathExtractor != null)
	        {
	            String imagePath = imagePathExtractor.apply(row);
	            if (imagePath != null && !imagePath.isBlank())
	            {
	                try
	                {
	                    Image img = Image.getInstance(imagePath);

	                    // 👉 saubere Skalierung
	                    img.scaleToFit(30, 30);

	                    // 👉 wichtig für "rechts oben"
	                    img.setAlignment(Image.ALIGN_RIGHT);

	                    // 👉 KEIN setImage()!
	                    imgCell.addElement(img);
	                }
	                catch (Exception e)
	                {
	                    // optional Logging
	                }
	            }
	        }

	        rowTable.addCell(imgCell);
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	    }
	}
}