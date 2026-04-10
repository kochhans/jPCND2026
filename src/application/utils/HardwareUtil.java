package application.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class HardwareUtil {

    public static String getHardwareHash() {
        try {
            String serial = readHardwareId();

            if (serial == null || serial.isBlank()) {
                throw new IllegalStateException(
                    "Hardware-ID konnte nicht ermittelt werden"
                );
            }

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(serial.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(hash);

        } catch (Exception e) {
            throw new RuntimeException("HardwareHash fehlgeschlagen", e);
        }
    }

    private static String readHardwareId() throws Exception {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            return execAndRead("wmic", "csproduct", "get", "uuid");
        }

        if (os.contains("linux")) {
            return execAndRead("cat", "/etc/machine-id");
        }

        if (os.contains("mac")) {
            return execAndRead(
                "ioreg",
                "-rd1",
                "-c",
                "IOPlatformExpertDevice"
            );
        }

        throw new UnsupportedOperationException("Unbekanntes OS: " + os);
    }

    private static String execAndRead(String... cmd) throws Exception {
        Process p = new ProcessBuilder(cmd)
                .redirectErrorStream(true)
                .start();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line.trim());
            }

            return sb.toString();
        }
    }
}
