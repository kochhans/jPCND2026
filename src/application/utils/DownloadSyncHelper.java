package application.utils;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.stage.Modality;

/**
 * Synchronous download + unzip helper.
 * Verwende performFullDownloadFlow(...) um alles synchron auszuführen.
 */
public class DownloadSyncHelper {

    /**
     * Führt synchron aus:
     *  - Backup (falls gewünscht / existierende DB)
     *  - Download (REMOTE ZIP)
     *  - Entpacken in targetFolder
     *  - Löschung der ZIP
     *  - Speichern des DB-Pfads (ConfigManager / ValuesGlobals) – wird nicht von dieser Klasse durchgeführt,
     *    da ConfigManager/ValuesGlobals projekt-spezifisch sind.
     *
     * @param ownerStage Owner für das modale Wartefenster (oder null)
     * @param remoteZipUrl URL zur ZIP-Datei (z.B. https://example.com/jPCND.zip)
     * @param targetFolder Zielordner, in dem die DB später liegen soll (wenn fileIsDir==true, wird dateiname angefügt)
     * @param dbFilename gewünschter DB-Dateiname, z.B. "jPCND.db3"
     * @param makeBackupIfExists true → vor Überschreiben Backup anlegen
     * @return absoluter Pfad zur entpackten DB-Datei
     * @throws Exception bei Fehlern
     */
    public static String performFullDownloadFlow(
            Stage ownerStage,
            String remoteZipUrl,
            Path targetFolder,
            String dbFilename,
            boolean makeBackupIfExists) throws Exception {

        // 1) Zielordner sicherstellen
        if (!Files.exists(targetFolder)) {
            Files.createDirectories(targetFolder);
        }

        // Voller Pfad zur DB-Datei
        Path dbFile = targetFolder.resolve(dbFilename);
        Path zipTarget = targetFolder.resolve("jPCND_download.zip");

        // 2) Wenn DB bereits existiert → Backup (falls gewünscht) und Überschreiben
        if (Files.exists(dbFile)) {
            if (makeBackupIfExists) {
                String bakName = dbFilename + ".bak." + System.currentTimeMillis();
                Path bak = targetFolder.resolve(bakName);
                Files.copy(dbFile, bak, StandardCopyOption.REPLACE_EXISTING);
            }
            // die alte Datei wird durch den Entpackvorgang überschrieben (oder ersetzt)
        }

        // 3) Zeige modales, blockierendes "Bitte warten"-Fenster (keine Fortschrittsangabe)
        Stage waitStage = null;
        if (ownerStage != null) {
            waitStage = createAndShowBlockingDialog(ownerStage, "Datenbank wird heruntergeladen und entpackt, bitte warten...");
        }

        Exception thrown = null;
        try {
            // 4) Download (synchron, HttpClient)
            downloadZipFile(remoteZipUrl, zipTarget);

            // 5) ZIP entpacken (synchron)
            unzip(zipTarget.toFile(), targetFolder.toFile());

            // 6) ZIP entfernen
            try {
                Files.deleteIfExists(zipTarget);
            } catch (Exception ex) {
                // kein Abbruch falls Löschung fehlschlägt
                System.err.println("Warnung: ZIP konnte nicht gelöscht werden: " + ex.getMessage());
            }

            // 7) Rückgabe des absoluten DB-Pfads
            return dbFile.toAbsolutePath().toString();

        } catch (Exception ex) {
            thrown = ex;
            throw ex;
        } finally {
            // 8) Dialog schließen (falls geöffnet)
            if (waitStage != null) {
                try { waitStage.close(); } catch (Exception ignore) {}
            }
            if (thrown != null) {
                // optional Logging
                System.err.println("Fehler im Download/Unzip-Prozess: " + thrown.getMessage());
            }
        }
    }

    // -----------------------------
    // Hilfsmethoden
    // -----------------------------

    private static Stage createAndShowBlockingDialog(Stage owner, String message) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setResizable(false);
        dialog.setTitle("Bitte warten...");

        VBox vb = new VBox(10);
        vb.setPadding(new Insets(16));
        vb.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(message);
        vb.getChildren().add(lbl);

        Scene sc = new Scene(vb, 460, 120);
        dialog.setScene(sc);

        // Wichtig: show() hier, nicht showAndWait() — showAndWait würde blockieren und evtl. UI-Paint verhindern,
        // aber da wir synchron arbeiten und beabsichtigen den Aufrufer zu blockieren, ist show() ausreichend.
        dialog.show();

        return dialog;
    }

    private static void downloadZipFile(String url, Path target) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        // BodyHandler schreibt direkt in die Datei (überschreibt)
        client.send(request, HttpResponse.BodyHandlers.ofFile(
                target,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        ));
    }

    private static void unzip(File zipFile, File targetDir) throws IOException {
        if (!targetDir.exists()) targetDir.mkdirs();

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            byte[] buffer = new byte[8192];

            while ((entry = zis.getNextEntry()) != null) {
                File outFile = new File(targetDir, entry.getName());

                // Sicherheitscheck: Zip-Slip vermeiden
                String destPath = outFile.getCanonicalPath();
                String destDirPath = targetDir.getCanonicalPath();
                if (!destPath.startsWith(destDirPath + File.separator) && !destPath.equals(destDirPath)) {
                    throw new IOException("Ungültiger ZIP-Eintrag (Zip-Slip-Verdacht): " + entry.getName());
                }

                if (entry.isDirectory()) {
                    outFile.mkdirs();
                } else {
                    File parent = outFile.getParentFile();
                    if (parent != null && !parent.exists()) parent.mkdirs();

                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }
}

