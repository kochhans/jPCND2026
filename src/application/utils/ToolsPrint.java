package application.utils;

import application.models.LiteraturlisteModel;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.control.TableView;
import javafx.scene.transform.Scale;

public class ToolsPrint
{


  public void doPrintAction(TableView<LiteraturlisteModel> tableView)
 {
     Printer printer = Printer.getDefaultPrinter();
     PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.HARDWARE_MINIMUM);

     double scaleX = pageLayout.getPrintableWidth() / tableView.getBoundsInParent().getWidth();
     double scaleY = pageLayout.getPrintableHeight() / tableView.getBoundsInParent().getHeight();
     tableView.getTransforms().add(new Scale(scaleX, scaleY));

     PrinterJob printerJob = PrinterJob.createPrinterJob();
     if (printerJob != null && printerJob.showPrintDialog(tableView.getScene().getWindow()))
     {
         boolean success = printerJob.printPage(pageLayout,tableView);
         if (success)
         {
             printerJob.endJob();

         }
     }

     tableView.getTransforms().clear();
 }
}
