package application.utils.license;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import application.ValuesGlobals;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

public class OnlineLicenseService {

    private static final Gson gson = new Gson();

    // =================== Lizenz prüfen =====================
    public static OnlineLicenseResult checkLicense(String licenseKey, String email, String hardwareId) throws Exception {
        String postData = "license=" + url(licenseKey) +
                          "&email=" + url(email) +
                          "&hardwareId=" + url(hardwareId);
        String json = post(ValuesGlobals.LICENSE_SERVER_URL, postData);
        return parseResult(json);
    }

    // =================== Lizenz aktivieren =================
    // 🔹 WICHTIG: Nur check_license.php wird aufgerufen!
    public static OnlineLicenseResult activateLicense(String licenseKey, String email, String hardwareId) throws Exception {
        String postData = "license=" + url(licenseKey) +
                          "&email=" + url(email) +
                          "&hardwareId=" + url(hardwareId);

        // 🔹 nur noch check_license.php aufrufen
        String json = post(ValuesGlobals.LICENSE_SERVER_URL, postData);
        return parseResult(json);
    }


    // =================== HTTP POST ========================
    private static String post(String urlStr, String postData) throws Exception {
        URI uri = URI.create(urlStr);
        HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();

        try {
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setConnectTimeout(5000);
            con.setReadTimeout(15000); // 🔥 erhöht

            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

            try (OutputStream os = con.getOutputStream()) {
                os.write(postData.getBytes(StandardCharsets.UTF_8));
            }

            int code = con.getResponseCode();

            InputStreamReader isr;
            if (code >= 200 && code < 300) {
                isr = new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8);
            } else {
                if (con.getErrorStream() != null) {
                    isr = new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8);
                } else {
                    throw new RuntimeException("HTTP Fehler: " + code);
                }
            }

            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            return sb.toString();

        } finally {
            con.disconnect(); // 🔥 wichtig
        }
    }

    // =================== JSON → OnlineLicenseResult ==========
    private static OnlineLicenseResult parseResult(String json) {
        try {
            JsonObject obj = gson.fromJson(json, JsonObject.class);

            OnlineLicenseResult result = new OnlineLicenseResult();

            String status = obj.has("status") ? obj.get("status").getAsString() : "SERVER_ERROR";
            result.setStatus(OnlineLicenseResult.Status.valueOf(status));

            if (obj.has("licenseKey")) {
                result.setLicenseKey(obj.get("licenseKey").getAsString());
            }

            if (obj.has("validUntil")) {
                result.setValidUntil(LocalDate.parse(obj.get("validUntil").getAsString()));
            }

            if (obj.has("email")) {
                result.setEmail(obj.get("email").getAsString());  // Mail vom Server (oder User-Mail beim ersten Mal)
            }

            return result;

        } catch (Exception e) {
            OnlineLicenseResult error = new OnlineLicenseResult();
            error.setStatus(OnlineLicenseResult.Status.SERVER_ERROR);
            return error;
        }
    }

    private static String url(String v) throws Exception {
        return URLEncoder.encode(v, StandardCharsets.UTF_8);
    }
}
