package application.controllers;

import application.utils.HardwareUtil;
import application.utils.license.LicenseCheckResult;
import application.utils.license.LicenseManager;
import application.utils.license.OnlineLicenseResult;
import application.utils.license.OnlineLicenseService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LicenseDialogController {

    @FXML
    private TextField txtLicenseKey;

    @FXML
    private TextField txtEmail;

    @FXML
    private Label lblStatus;

    @FXML
    private Button btnActivate;
    


    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void onCancel() {
        System.exit(0);
    }

    @FXML
    private void onActivate() {
        String key = txtLicenseKey.getText().trim();
        String email = txtEmail.getText().trim();

        if (key.isEmpty() || email.isEmpty()) {
            lblStatus.setText("Bitte Lizenzschlüssel und E-Mail eingeben.");
            return;
        }

        lblStatus.setText("Lizenzprüfung läuft...");

        Task<LicenseCheckResult> task = new Task<>() {
            @Override
            protected LicenseCheckResult call() {
                try {
                    String hwid = HardwareUtil.getHardwareHash();

                    // Aktivierung → ruft nur check_license.php auf
                    OnlineLicenseResult result = OnlineLicenseService.activateLicense(key, email, hwid);

                    // Server-Mail prüfen
                    String finalEmail = (result.getEmail() != null && !result.getEmail().isBlank())
                            ? result.getEmail()
                            : email;

                    // Lizenz lokal speichern
                    return LicenseManager.getInstance().activate(key, finalEmail);

                } catch (Exception e) {
                    e.printStackTrace();
                    return LicenseCheckResult.SERVER_ERROR;
                }
            }
        };

        task.setOnSucceeded(e -> {
            LicenseCheckResult result = task.getValue();
            switch (result) {
                case VALID -> stage.close();
                case INVALID -> lblStatus.setText("Die eingegebene Lizenznummer ist nicht korrekt!");
                case WRONG_MACHINE -> lblStatus.setText("Lizenz ist an ein anderes Gerät gebunden.");
                case SERVER_ERROR -> lblStatus.setText("Serverfehler. Bitte prüfen Sie die Verbindung.");
                case SAVE_FAILED -> lblStatus.setText("Lizenz konnte nicht gespeichert werden.");
                default -> lblStatus.setText("Lizenz konnte nicht aktiviert werden.");
            }
        });

        task.setOnFailed(e -> {
            lblStatus.setText("Fehler bei Lizenzprüfung.");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    /**
     * Optional: Aktuelle Lizenzinfo anzeigen
     */
    public String getCurrentLicenseStatus() {
        var lic = LicenseManager.getInstance().getCurrentLicense();
        if (lic == null) {
            return "Keine Lizenz vorhanden";
        }

        if (!HardwareUtil.getHardwareHash().equals(lic.getHardwareId())) {
            return "Lizenz gehört zu einem anderen Gerät";
        }

        if (lic.getValidUntil() != null && java.time.LocalDate.now().isAfter(lic.getValidUntil())) {
            return "Lizenz abgelaufen";
        }

        return "Lizenz gültig bis: " + (lic.getValidUntil() != null ? lic.getValidUntil() : "unbegrenzt");
    }
    
    
    
}
