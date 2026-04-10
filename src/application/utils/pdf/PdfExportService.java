package application.utils.pdf;

import application.models.AktionenListeModel;
import application.uicomponents.Msgbox;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;

import java.io.File;
import java.io.FileOutputStream;

public final class PdfExportService {

    private PdfExportService() {}

    /**
     * Erstellt einen Task, der ein Master-Detail PDF erzeugt
     */
    public static <M, D> Task<File> createMasterDetailPdfTask(
            M master,
            TableView<D> detailTable,
            File target,
            PdfExportOptions<D> opt,
            ProgressBar progressBar,
            Label statusLabel
    ) {
        return new Task<>() {
            @Override
            protected File call() throws Exception {

                // 🔹 Document und Writer
                Document document = new Document(opt.pageSize, opt.marginLeft, opt.marginRight, opt.marginTop, opt.marginBottom);
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(target));
                writer.setPageEvent(new PdfPageEvent<>(opt));
                document.open();

                // 🔹 Fonts
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, opt.headerFontSize);
                Font cellFont   = FontFactory.getFont(FontFactory.HELVETICA, opt.cellFontSize);

                // 🔹 Master-Tabelle
                PdfPTable masterTable = new PdfPTable(2);
                masterTable.setWidthPercentage(100);

                if (master instanceof AktionenListeModel aktion) {
                	PdfMasterDetailExporterUtil.addMasterRow1(masterTable, "id", String.valueOf(aktion.getCaid()), headerFont, cellFont);
                	PdfMasterDetailExporterUtil.addMasterRow1(masterTable, "Aktion", aktion.getCaakttyp(), headerFont, cellFont);
                	PdfMasterDetailExporterUtil.addMasterRow1(masterTable, "Datum", aktion.getCadatum().toString(), headerFont, cellFont);
                	PdfMasterDetailExporterUtil.addMasterRow1(masterTable, "Bem.", aktion.getCabemerkung(), headerFont, cellFont);
                }

                document.add(masterTable);
                // KEIN Paragraph/Chunk → kein Umbruch vor Detail

                // 🔹 Detail-Tabelle
                PdfTableExporter.exportIntoExistingDocument(detailTable, document, opt, progress -> updateProgress(progress, 1.0));

                document.close();

                return target;
            }
        };
    }

    /**
     * Task starten und ProgressBar + StatusLabel binden, PDF anschließend öffnen
     */
    public static <M, D> void startMasterDetailExport(
            M master,
            TableView<D> detailTable,
            PdfExportOptions<D> opt,
            ProgressBar progressBar,
            Label statusLabel,
            File target
    ) {
        Task<File> task = createMasterDetailPdfTask(master, detailTable, target, opt, progressBar, statusLabel);

        progressBar.visibleProperty().bind(task.runningProperty());
        progressBar.progressProperty().bind(task.progressProperty());
        statusLabel.setVisible(true);

        task.setOnSucceeded(e -> {
            progressBar.visibleProperty().unbind();
            progressBar.progressProperty().unbind();
            progressBar.setVisible(false);
            statusLabel.setVisible(false);

            // PDF öffnen
            PdfOpenUtil.openPdf(target);
        });

        task.setOnFailed(e -> {
            progressBar.visibleProperty().unbind();
            progressBar.progressProperty().unbind();
            progressBar.setVisible(false);
            statusLabel.setVisible(false);
            Msgbox.warn("PDF-Export fehlgeschlagen",
                    task.getException() != null ?
                            task.getException().getMessage() :
                            "Unbekannter Fehler beim PDF-Export");
        });

        new Thread(task, "PDF-MasterDetail-Export-Thread").start();
    }
}