package application.utils;

import javafx.concurrent.Task;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DownloadTask extends Task<Void> {

    private final String url;
    private final String targetPath;

    public DownloadTask(String url, String targetPath) {
        this.url = url;
        this.targetPath = targetPath;
    }

    @Override
    protected Void call() throws Exception {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        // HEAD Request, um Dateigröße zu erfahren
        HttpRequest headReq = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();

        long fileSize = client.send(headReq, HttpResponse.BodyHandlers.discarding())
                .headers()
                .firstValueAsLong("Content-Length")
                .orElse(-1L);

        updateMessage("Starte Download ...");

        HttpResponse<InputStream> response =
                client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        try (InputStream in = response.body();
             FileOutputStream out = new FileOutputStream(targetPath)) {

            byte[] buffer = new byte[8192];
            long totalRead = 0;
            int read;

            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
                totalRead += read;

                if (fileSize > 0) {
                    updateProgress(totalRead, fileSize);
                    updateMessage(String.format("Heruntergeladen: %.2f %%", 
                            (100.0 * totalRead / fileSize)));
                }
            }
        }

        updateMessage("Download abgeschlossen.");
        updateProgress(1, 1);

        return null;
    }
}
