package application.utils.pdf;

import java.io.File;

public final class PdfOpenUtil {

    private PdfOpenUtil() {}

    /**
     * Öffnet eine PDF-Datei in einem eigenen Thread, je nach OS
     */
    public static void openPdf(File pdfFile) {
        if (pdfFile == null || !pdfFile.exists()) return;

        new Thread(() -> {
            try {
                String os = System.getProperty("os.name").toLowerCase();

                if (os.contains("linux")) {
                    new ProcessBuilder("xdg-open", pdfFile.getAbsolutePath()).start();
                } else if (os.contains("mac")) {
                    new ProcessBuilder("open", pdfFile.getAbsolutePath()).start();
                } else if (java.awt.Desktop.isDesktopSupported()) {
                    java.awt.Desktop.getDesktop().open(pdfFile);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, "PDF-Open-Thread").start();
    }
}