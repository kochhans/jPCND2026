package application.utils.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;

import javafx.scene.control.TableView;

import java.io.File;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.List;

public class PdfMasterDetailExporter<M,T>
{

    private final List<HeaderField<M>> headerFields = new ArrayList<>();


    public PdfMasterDetailExporter<M,T> header(String label, Function<M,Object> extractor)
    {
        headerFields.add(new HeaderField<>(label, extractor));
        return this;
    }


    public void export(
            M master,
            TableView<T> detailTable,
            File pdfFile,
            PdfExportOptions<T> options) throws Exception
    {

        // Dokument erzeugen
        Document document = new Document(
                options.pageSize,
                options.marginLeft,
                options.marginRight,
                options.marginTop,
                options.marginBottom);

        com.lowagie.text.pdf.PdfWriter writer =
                com.lowagie.text.pdf.PdfWriter.getInstance(document,
                        new java.io.FileOutputStream(pdfFile));

        writer.setPageEvent(new PdfPageEvent<T>(options));

        document.open();

        // Masterdaten
        writeHeader(document, master);

        document.add(Chunk.NEWLINE);

        document.close();

        // Detailtabelle exportieren
        PdfTableExporter.exportIntoExistingDocument(detailTable, document, options, null);
    }


    private void writeHeader(Document document, M master) throws Exception
    {

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 9);

        for (HeaderField<M> field : headerFields)
        {
            table.addCell(new Phrase(field.label, labelFont));

            Object v = field.extractor.apply(master);
            table.addCell(new Phrase(v == null ? "" : v.toString(), valueFont));
        }

        document.add(table);
    }


    private static class HeaderField<M>
    {
        final String label;
        final Function<M,Object> extractor;

        HeaderField(String label, Function<M,Object> extractor)
        {
            this.label = label;
            this.extractor = extractor;
        }
    }

}