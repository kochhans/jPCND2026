package application.utils;
import javafx.concurrent.Task;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.zip.*;

public class DownloadAndUnzipTask extends Task<Void> {
    private final String remoteUrl;
    private final File zipTarget;
    private final File targetDir;

    public DownloadAndUnzipTask(String remoteUrl, File zipTarget, File targetDir) {
        this.remoteUrl = remoteUrl;
        this.zipTarget = zipTarget;
        this.targetDir = targetDir;
    }

    @Override
    protected Void call() throws Exception {
        updateMessage("ZIP-Datei wird heruntergeladen...");
        
        URL url = URI.create(remoteUrl).toURL();

        
        long fileSize = url.openConnection().getContentLengthLong();
        long totalRead = 0;

        try (InputStream in = url.openStream();
             OutputStream out = new FileOutputStream(zipTarget)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                totalRead += bytesRead;
                if (fileSize > 0) {
                    updateProgress(totalRead, fileSize * 2); // Download = 0–50%
                }
            }
        }

        updateMessage("ZIP-Datei wird entpackt...");

        // Anzahl der Einträge für Fortschritt
        int totalEntries = 0;
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipTarget))) {
            while (zis.getNextEntry() != null) totalEntries++;
        }
        if (totalEntries == 0) totalEntries = 1;

        int processedEntries = 0;
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipTarget))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File outFile = new File(targetDir, entry.getName());
                if (entry.isDirectory()) outFile.mkdirs();
                else {
                    outFile.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = zis.read(buffer)) > 0) fos.write(buffer, 0, len);
                    }
                }
                processedEntries++;
                updateProgress(fileSize + processedEntries, fileSize + totalEntries); // Entpacken = 50–100%
                zis.closeEntry();
            }
        }

        zipTarget.delete();
        updateMessage("Fertig!");
        updateProgress(1, 1);
        return null;
    }
}
