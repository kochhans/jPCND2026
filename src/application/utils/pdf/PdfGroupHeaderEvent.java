package application.utils.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

public class PdfGroupHeaderEvent<T> extends PdfPageEventHelper {

    private PdfExportOptions<T> opt;
    private PdfPTable headerTable;
    private String currentGroupValue = null;
    private Font groupFont;

    public PdfGroupHeaderEvent(PdfExportOptions<T> opt, PdfPTable headerTable, Font groupFont) {
        this.opt = opt;
        this.headerTable = headerTable;
        this.groupFont = groupFont;
    }

    public void setCurrentGroupValue(String groupValue) {
        this.currentGroupValue = groupValue;
    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        try {
            // Header-Tabelle wiederholen
            headerTable.writeSelectedRows(0, -1, document.left(), 
                document.top() + headerTable.getTotalHeight(), writer.getDirectContent());

            // Gruppenkopf wiederholen, falls Gruppe gesetzt
            if (currentGroupValue != null) {
                PdfPTable repeatGroupTable = new PdfPTable(1);
                repeatGroupTable.setWidthPercentage(100);
                PdfPCell groupCell = new PdfPCell(new Phrase(opt.groupHeaderPrefix + currentGroupValue, groupFont));
                groupCell.setBackgroundColor(opt.groupHeaderBackground);
                groupCell.setPadding(6);
                repeatGroupTable.addCell(groupCell);
                repeatGroupTable.setTotalWidth(document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin());
                repeatGroupTable.writeSelectedRows(0, -1, document.left(), 
                    document.top() - headerTable.getTotalHeight(), writer.getDirectContent());
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}
