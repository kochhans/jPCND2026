package application.utils.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

public class PdfPageEvent<T> extends PdfPageEventHelper {

    private final PdfExportOptions<T> opt;

    public PdfPageEvent(PdfExportOptions<T> opt) {
        this.opt = opt;
    }

    @Override
	public void onEndPage(PdfWriter writer, Document document)//!!!!Kopf- und Fußzeile!!!!
	{
    	String timestamp = java.time.LocalDateTime.now()
				.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

        PdfContentByte cb = writer.getDirectContent();
        Font titleFont = FontFactory.getFont(FontFactory.TIMES_BOLD, 10);
        Font subFont   = FontFactory.getFont(FontFactory.TIMES, 9);
        String left = opt.title != null ? opt.title : "";
        String center = timestamp;
        String right = "Seite " + writer.getPageNumber();
        //float pageWidth = document.getPageSize().getWidth();
        float headerY = document.top() + 20;
        //KOPFZEILEN
        // 🔹 Titel
        if (opt.title != null && !opt.title.isBlank()) {
            ColumnText.showTextAligned(
                    cb,
                    Element.ALIGN_LEFT,
                    new Phrase(opt.title, titleFont),
                    document.left(),
                    headerY,//Kopfzeile1 Abstand
                    0
            );
        }

        // 🔹 Untertitel
        if (opt.subtitle != null) {
            ColumnText.showTextAligned(
                    cb,
                    Element.ALIGN_LEFT,
                    new Phrase(
                            opt.subtitle, subFont),
                    document.left(),
                    headerY - 12, //Kopfzeile2 Abstand
                    0
            );
        }

        // FUSSZEILENBEREICH ----------------------
        if (opt.showPageNumbers) {
        }
        
        if (opt.showPageDate) {
        }
        // 3 Fusszeilenbereiche
        ColumnText.showTextAligned(cb,
                Element.ALIGN_LEFT,
                new Phrase(left,subFont),
                document.left(),
                document.bottom() - 5,
                0);

        ColumnText.showTextAligned(cb,
                Element.ALIGN_CENTER,
                new Phrase(center, subFont),
                (document.left() + document.right()) / 2,
                document.bottom() - 5,
                0);

        ColumnText.showTextAligned(cb,
                Element.ALIGN_RIGHT,
                new Phrase(right, subFont),
                document.right(),
                document.bottom() - 5,
                0);
    }
}
