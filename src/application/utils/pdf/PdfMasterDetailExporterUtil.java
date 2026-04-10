package application.utils.pdf;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import application.models.AktionenListeModel;
import application.uicomponents.Msgbox;
import application.utils.AktuellesDatum;

import java.awt.Color;

import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;

public final class PdfMasterDetailExporterUtil
{
	private PdfMasterDetailExporterUtil()
	{
	}

	public static <M, D> void exportMasterDetail(
			M master, // Übergabe der Mastertabelle (z.B. aktion), um getter zu verwenden
			TableView<D> detailTable, // Übergabe der Detailtabelle 1:n
			PdfExportOptions<D> opt, // Optionen
			ProgressBar progressBar, // Progressbar anbinden
			Label statusLabel // Label aktualisieren
	) throws Exception
	{
		if (master == null || detailTable == null || opt == null)
			return; // Wenn keine Mastertabelle übergebn wurde -- beenden
		// ----------------------------------------------------------------
		// Filename vorbereiten
		final File pdfFile;
		if (master instanceof application.models.AktionenListeModel aktion)
		{
			String aktiondatum = String.valueOf(aktion.getCadatum());
			String aktionstyp = aktion.getCaakttyp() != null ? aktion.getCaakttyp() : "Report"; // Wenn Aktionstyp leer ist, wird Report vorangestellt
			AktuellesDatum stringdat = new AktuellesDatum();
			aktionstyp = aktionstyp.substring(0, 2) + stringdat.getDateTimeAsStringMitSec();
			pdfFile = PdfFilenameUtil.createActionPdfFile(aktiondatum, aktionstyp);

		}
		else
		{
			pdfFile = PdfFilenameUtil.createActionPdfFile("report", "pdf");
		}
		// pdfFile z.B.: a_2021-02-15_probe_...

		// ----------------------------------------------------------------
		// Task vorbereiten
		javafx.concurrent.Task<Void> exportTask = new javafx.concurrent.Task<>()
		{
			@Override
			protected Void call() throws Exception
			{
				// ------------------------
				// Dokument vorbereiten
				Document document = new Document(
						opt.pageSize,
						opt.marginLeft,
						opt.marginRight,
						opt.marginTop,
						opt.marginBottom);
				// -----------------------
				// PDF-Datei anlegen
				PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
				writer.setPageEvent(new PdfPageEvent<>(opt)); // ⭐ Fußzeile aktivieren
				try
				{
					document.open();
				}
				catch (DocumentException e)
				{
					Msgbox.show("PDF-Druck", "Im Moment ist das Drucken nicht möglich, weil eine gleichnamige Datei geöffnet ist");
					e.printStackTrace();
					return null;
				}
				// ------------------------------
				// Fonts
				Font headerFont = FontFactory.getFont(FontFactory.TIMES_BOLD, opt.headerFontSize);
				Font cellFont = FontFactory.getFont(FontFactory.TIMES, opt.cellFontSize);
				Font cellFontklein = FontFactory.getFont(FontFactory.TIMES_ITALIC, opt.cellFontSizeklein);
				// ======================================================================================
				// ----------- BEREICHE ------------
				// **************************************
				// --- Masterbereich (oberer Bereich) ---
				PdfPTable masterTable = new PdfPTable(2); // 2 Spalten: Label / Wert / Label / Wert
				masterTable.setWidthPercentage(100);
				masterTable.setWidths(new float[] { 60, 40 }); // Spaltenbreiten relativ
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

				if (master instanceof AktionenListeModel aktion)
				{
					String datumStr = aktion.getCadatum().format(formatter);
					// ---------------------------
					// 2 Spaltige Mastertabelle , 2 getrennte Zeilen untereinander
					Color colorTitelzelle = new Color(242, 242, 242); // Orange
					Color colorMitw = new Color(242, 242, 242);

					addMasterRow2(masterTable,
							datumStr + "  " + aktion.getCaakttyp(),
							"Treffpunkt: " + aktion.getCatreffpunkt().toString() + "  (Beginn: " + aktion.getCabeginn().toString() + ")",
							headerFont, headerFont, colorTitelzelle, colorTitelzelle);

					addMasterRow2(masterTable, aktion.getCagruppe(), "Verantwortlich: " +
							aktion.getCaverantwortlich(), cellFont, cellFont, colorMitw, colorTitelzelle);
					// ---------------------------
					// Anmerkungen, falls vorhanden über volle Breite
					if (aktion.getCabemerkung() != null && !aktion.getCabemerkung().isBlank())
					{
						PdfPCell textCell1 = new PdfPCell(new Phrase(aktion.getCabemerkung(), cellFontklein));
						textCell1.setColspan(2);
						//textCell1.setPadding(10);
						textCell1.setBorder(Rectangle.NO_BORDER);
						masterTable.addCell(textCell1);
					}
				}

				// masterTabelle an das Dokument anfügen
				document.add(masterTable);

				// ******************************************
				// --- Detailbereich (unterer Bereich) ---
				// an den Masterbereich wird nun der Detailbereich angehängt.
				// Übergabe detailTabelle (tblVw), bisheriges dokument, Optionen, progressbar)
				PdfTableExporter.exportIntoExistingDocument(detailTable, document, opt, progressBar -> {
				});
//				PdfTableExporter.exportIntoExistingDocument(detailTable, document, opt, progress -> {
//				});

				document.close(); // nun wird Kopf- Fußzeile gebildet

				
				
				
				
				
				return null;
			}
		};

		// Progress-Anzeige
		progressBar.setVisible(true);
		if (progressBar != null)
			progressBar.progressProperty().bind(exportTask.progressProperty());
		if (statusLabel != null)
			statusLabel.visibleProperty().bind(exportTask.runningProperty());

		exportTask.setOnSucceeded(e -> {
			if (progressBar != null)
				progressBar.progressProperty().unbind();
			if (progressBar != null)
				progressBar.setVisible(false);			
			if (statusLabel != null)
				statusLabel.visibleProperty().unbind();
			if (statusLabel != null)
				statusLabel.setVisible(false);
			// PDF öffnen
			PdfOpenUtil.openPdf(pdfFile);
			
			
			
		});

		exportTask.setOnFailed(e -> {
			if (progressBar != null)
				progressBar.progressProperty().unbind();
			if (progressBar != null)
				progressBar.setVisible(false);	
			if (statusLabel != null)
				statusLabel.visibleProperty().unbind();
			if (statusLabel != null)
				statusLabel.setVisible(false);
			e.getSource().getException().printStackTrace();
		});
		if (progressBar != null)
			progressBar.progressProperty().bind(exportTask.progressProperty());
		if (statusLabel != null)
			statusLabel.visibleProperty().bind(exportTask.runningProperty());

		new Thread(exportTask, "PDF-MasterDetail-Export").start();
	}

//	private static void addMasterRow2(
//			PdfPTable table,
//			String value1, String value2,
//			Font headerFont,
//			Font cellFont)
//	{
//
//		PdfPCell v1 = new PdfPCell(new Phrase(value1 != null ? value1 : "", headerFont));
//		PdfPCell v2 = new PdfPCell(new Phrase(value2 != null ? value2 : "", headerFont));
//
//		v1.setPadding(10);
//		v2.setPadding(10);
//		v1.setBackgroundColor(Color.LIGHT_GRAY);
//		v2.setBackgroundColor(Color.yellow);
//
//		v1.setBorder(Rectangle.LEFT);
//		v1.setBorder(Rectangle.TOP);
//		v1.setBorder(Rectangle.BOTTOM);
//		v2.setBorder(Rectangle.RIGHT);
//		v2.setBorder(Rectangle.TOP);
//		v2.setBorder(Rectangle.BOTTOM);
//
//		table.addCell(v1);
//		table.addCell(v2);
//
//	}
	private static void addMasterRow2(
			PdfPTable table,
			String value1, String value2,
			Font headerFont,
			Font cellFont,
			Color bgColor1,
			Color bgColor2)
	{

		PdfPCell v1 = new PdfPCell(new Phrase(value1 != null ? value1 : "", headerFont));
		PdfPCell v2 = new PdfPCell(new Phrase(value2 != null ? value2 : "", headerFont));

		v1.setPadding(10);
		v2.setPadding(10);
		

		// Farben von außen gesteuert
		v1.setBackgroundColor(bgColor1);
		v2.setBackgroundColor(bgColor2);

		v1.setBorder(Rectangle.LEFT);
		v1.setBorder(Rectangle.TOP);
		v1.setBorder(Rectangle.BOTTOM);
		v2.setBorder(Rectangle.RIGHT);
		v2.setBorder(Rectangle.TOP);
		v2.setBorder(Rectangle.BOTTOM);

		table.addCell(v1);
		table.addCell(v2);
	}

	static void addMasterRow1Rahmen(PdfPTable table, String label, String value, Font labelFont, Font valueFont)
	{
		PdfPCell v1 = new PdfPCell(new Phrase(value != null ? value : "", valueFont));
		v1.setPadding(5);
		v1.setColspan(4);
		v1.setBorder(Rectangle.BOX);

		table.addCell(v1);
	}

	static void addMasterRow1(PdfPTable table, String label, String value, Font labelFont, Font valueFont)
	{
		PdfPCell v1 = new PdfPCell(new Phrase(value != null ? value : "", valueFont));
		v1.setPadding(5);
		v1.setColspan(4);
		v1.setBorder(Rectangle.NO_BORDER);
		table.addCell(v1);
	}

//	private static void addMasterRow4(PdfPTable table, String label1, String value1, String label2, String value2,
//			Font headerFont, Font cellFont)
//	{
//		PdfPCell c1 = new PdfPCell(new Phrase(label1, headerFont));
//		c1.setPadding(4);
//		c1.setBorder(Rectangle.NO_BORDER);
//		table.addCell(c1);
//
//		PdfPCell c2 = new PdfPCell(new Phrase(value1, cellFont));
//		c2.setPadding(4);
//		c2.setBorder(Rectangle.NO_BORDER);
//		table.addCell(c2);
//
//		PdfPCell c3 = new PdfPCell(new Phrase(label2, headerFont));
//		c3.setPadding(4);
//		c3.setBorder(Rectangle.NO_BORDER);
//		table.addCell(c3);
//
//		PdfPCell c4 = new PdfPCell(new Phrase(value2, cellFont));
//		c4.setPadding(4);
//		c4.setBorder(Rectangle.NO_BORDER);
//		table.addCell(c4);
//	}

}